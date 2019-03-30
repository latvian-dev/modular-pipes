package com.latmod.mods.modularpipes.tile;

import com.latmod.mods.modularpipes.ModularPipesConfig;
import com.latmod.mods.modularpipes.ModularPipesUtils;
import com.latmod.mods.modularpipes.block.EnumMK;
import com.latmod.mods.modularpipes.item.ItemKey;
import com.latmod.mods.modularpipes.item.module.PipeModule;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TilePipeModularMK1 extends TilePipeBase implements IEnergyStorage
{
	public List<PipeItem> items = new ArrayList<>(0);
	public List<PipeModule> modules = new ArrayList<>(0);
	public final CachedTileEntity[] cachedTiles = new CachedTileEntity[6];
	public Object2IntOpenHashMap<ItemKey> itemDirections = new Object2IntOpenHashMap<>(0);
	public int storedPower = 0;
	private int powerOutputIndex = -1;
	private List<TilePipeModularMK1> cachedNetwork = null;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (!modules.isEmpty())
		{
			NBTTagList list = new NBTTagList();

			for (PipeModule module : modules)
			{
				NBTTagCompound nbt1 = module.moduleItem.serializeNBT();
				NBTTagCompound nbt2 = new NBTTagCompound();
				module.writeData(nbt2);

				if (!nbt2.isEmpty())
				{
					nbt1.setTag("module", nbt2);
				}

				list.appendTag(nbt1);
			}

			nbt.setTag("modules", list);
		}

		if (!items.isEmpty())
		{
			NBTTagList list = new NBTTagList();

			for (PipeItem item : items)
			{
				list.appendTag(item.serializeNBT());
			}

			nbt.setTag("items", list);
		}

		if (storedPower > 0)
		{
			nbt.setInteger("power", storedPower);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		super.readData(nbt);

		NBTTagList list = nbt.getTagList("modules", Constants.NBT.TAG_COMPOUND);
		modules = new ArrayList<>(list.tagCount());

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
			ItemStack stack = new ItemStack(nbt1);
			PipeModule module = stack.getCapability(PipeModule.CAP, null);

			if (module != null)
			{
				module.pipe = this;
				module.moduleItem = stack;
				module.readData(nbt1.getCompoundTag("module"));
				modules.add(module);
			}
		}

		list = nbt.getTagList("items", Constants.NBT.TAG_COMPOUND);
		items = new ArrayList<>(list.tagCount());

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);
			PipeItem item = new PipeItem();
			item.deserializeNBT(nbt1);

			if (!item.stack.isEmpty())
			{
				items.add(item);
			}
		}

		storedPower = nbt.getInteger("power");
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side)
	{
		if (capability == CapabilityEnergy.ENERGY && side == null)
		{
			return true;
		}

		for (PipeModule module : modules)
		{
			if (module.hasCapability(capability, side))
			{
				return true;
			}
		}

		return super.hasCapability(capability, side);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side)
	{
		if (capability == CapabilityEnergy.ENERGY && side == null)
		{
			return (T) this;
		}

		for (PipeModule module : modules)
		{
			T t = module.getCapability(capability, side);

			if (t != null)
			{
				return t;
			}
		}

		return super.getCapability(capability, side);
	}

	public EnumMK getMK()
	{
		return EnumMK.MK1;
	}

	@Override
	public void moveItem(PipeItem item)
	{
		item.pos += item.speed;
	}

	public void tickPipe()
	{
		if (!modules.isEmpty())
		{
			for (PipeModule module : modules)
			{
				if (module.canUpdate())
				{
					module.updateModule();
				}
			}
		}

		if (!world.isRemote && storedPower > 0 && world.getTotalWorldTime() % 5L == 0L)
		{
			if (powerOutputIndex == -1)
			{
				powerOutputIndex = hashCode() % 6;

				if (powerOutputIndex < 0)
				{
					powerOutputIndex = 6 - powerOutputIndex;
				}
			}

			CachedTileEntity tileEntity = getTile(EnumFacing.byIndex(powerOutputIndex));

			if (tileEntity.tile instanceof TilePipeModularMK1)
			{
				TilePipeModularMK1 pipe = (TilePipeModularMK1) tileEntity.tile;

				if (Math.abs(storedPower - pipe.storedPower) > 1)
				{
					int a = (storedPower + pipe.storedPower) / 2;

					storedPower = a;
					markDirty();
					sync = false;

					pipe.storedPower = a;
					pipe.markDirty();
					pipe.sync = false;
				}
			}

			powerOutputIndex++;
		}

		if (items.isEmpty())
		{
			return;
		}

		for (PipeItem item : items.size() == 1 ? Collections.singletonList(items.get(0)) : new ArrayList<>(items))
		{
			item.age++;

			if (item.to == 6 || item.from == 6)
			{
				item.age = Integer.MAX_VALUE;
			}
			else if (item.pos >= 1F)
			{
				/*
				IItemHandler handler = getInventory(world, item.to);

				if (handler instanceof IPipeItemHandler && ((IPipeItemHandler) handler).insertPipeItem(item.copyForTransfer(null), false))
				{
					markDirty();
					sync = false;
				}
				else
				{
					ItemStack stack = ItemHandlerHelper.insertItem(handler, item.stack, world.isRemote);

					if (!stack.isEmpty())
					{
						IPipeItemHandler opposite = getPipeItemHandler(EnumFacing.VALUES[item.to].getOpposite());

						if (opposite != null)
						{
							if (!opposite.insertPipeItem(item.copyForTransfer(stack), false) && !world.isRemote)
							{
								InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
							}
						}
						else if (!world.isRemote)
						{
							InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
						}

						markDirty();

						if (handler == opposite)
						{
							sync = false;
						}
					}
				}
				*/

				item.age = Integer.MAX_VALUE;
			}
		}

		if (items.removeIf(PipeItem.IS_DEAD))
		{
			markDirty();
			sync = false;
		}
	}

	@Override
	public void updateContainingBlockInfo()
	{
		super.updateContainingBlockInfo();
		Arrays.fill(cachedTiles, null);
		itemDirections = new Object2IntOpenHashMap<>(0);

		for (PipeModule module : modules)
		{
			module.clearCache();
		}

		cachedNetwork = null;
	}

	public CachedTileEntity getTile(EnumFacing facing)
	{
		int f = facing.getIndex();

		if (cachedTiles[f] == null)
		{
			cachedTiles[f] = CachedTileEntity.NONE;

			BlockPos pos1 = pos.offset(facing);
			TileEntity tileEntity = world.getTileEntity(pos1);

			if (tileEntity instanceof TilePipeModularMK1)
			{
				if (canPipesConnect(((TilePipeModularMK1) tileEntity).paint))
				{
					cachedTiles[f] = new CachedTileEntity(tileEntity, 1);
				}
			}
			else if (tileEntity instanceof TilePipeTransport)
			{
				if (canPipesConnect(((TilePipeTransport) tileEntity).paint))
				{
					cachedTiles[f] = ((TilePipeTransport) tileEntity).findNextOne(facing.getOpposite(), 1);

					if (cachedTiles[f].tile == this)
					{
						cachedTiles[f] = CachedTileEntity.NONE;
					}
				}
			}
			else if (tileEntity != null)
			{
				cachedTiles[f] = new CachedTileEntity(tileEntity, 1);
			}

			if (cachedTiles[f].tile != null)
			{
				for (int i = 0; i < 6; i++)
				{
					if (i != f && cachedTiles[i] != null && cachedTiles[f].tile == cachedTiles[i].tile)
					{
						if (cachedTiles[f].distance < cachedTiles[i].distance)
						{
							cachedTiles[i] = CachedTileEntity.NONE;
						}
						else
						{
							cachedTiles[f] = CachedTileEntity.NONE;
						}

						break;
					}
				}
			}
		}

		return cachedTiles[f];
	}

	@Override
	public boolean isConnected(EnumFacing facing)
	{
		TileEntity tileEntity = world.getTileEntity(pos.offset(facing));

		if (tileEntity instanceof TilePipeBase)
		{
			return canPipesConnect(((TilePipeBase) tileEntity).paint);
		}

		for (PipeModule module : modules)
		{
			if (module.isConnected(facing))
			{
				return true;
			}
		}

		return false;
	}

	public void dropItems()
	{
		for (PipeItem item : items)
		{
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), item.stack);
		}

		for (PipeModule module : modules)
		{
			module.onPipeBroken();
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), module.moduleItem);
		}
	}

	public List<TilePipeModularMK1> getPipeNetwork()
	{
		if (cachedNetwork == null)
		{
			HashSet<TilePipeModularMK1> set = new HashSet<>();
			getNetwork(set);
			cachedNetwork = new ArrayList<>(set);

			if (cachedNetwork.size() > 1)
			{
				cachedNetwork.sort(Comparator.comparingDouble(value -> getDistanceSq(value.pos.getX() + 0.5D, value.pos.getY() + 0.5D, value.pos.getZ() + 0.5D)));
			}

			cachedNetwork = ModularPipesUtils.optimize(cachedNetwork);
		}

		return cachedNetwork;
	}

	private void getNetwork(HashSet<TilePipeModularMK1> set)
	{
		for (EnumFacing facing : EnumFacing.VALUES)
		{
			TileEntity tileEntity = getTile(facing).tile;

			if (tileEntity instanceof TilePipeModularMK1 && !set.contains(tileEntity))
			{
				set.add((TilePipeModularMK1) tileEntity);
				((TilePipeModularMK1) tileEntity).getNetwork(set);
			}
		}
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return 0;
	}

	@Override
	public int getEnergyStored()
	{
		return storedPower;
	}

	@Override
	public int getMaxEnergyStored()
	{
		return ModularPipesConfig.pipes.max_energy_stored;
	}

	@Override
	public boolean canExtract()
	{
		return false;
	}

	@Override
	public boolean canReceive()
	{
		return false;
	}
}