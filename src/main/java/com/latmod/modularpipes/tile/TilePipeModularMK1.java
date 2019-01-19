package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.block.EnumMK;
import com.latmod.modularpipes.item.module.PipeModule;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TilePipeModularMK1 extends TilePipeBase
{
	public List<PipeItem> items = new ArrayList<>(0);
	public List<PipeModule> modules = new ArrayList<>(0);
	public final CachedTileEntity[] cachedTiles = new CachedTileEntity[6];

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		super.writeData(nbt);

		if (!modules.isEmpty())
		{
			NBTTagList list = new NBTTagList();

			for (PipeModule module : modules)
			{
				NBTTagCompound nbt1 = module.stack.serializeNBT();
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
				module.stack = stack;
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
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side)
	{
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
		for (PipeModule module : modules)
		{
			if (module.canUpdate())
			{
				module.updateModule();
			}
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
				cachedTiles[f] = new CachedTileEntity(tileEntity, 1);
			}
			else if (tileEntity instanceof TilePipeTransport)
			{
				cachedTiles[f] = ((TilePipeTransport) tileEntity).findNextOne(facing.getOpposite(), 1);

				if (cachedTiles[f].tile == this)
				{
					cachedTiles[f] = CachedTileEntity.NONE;
				}
			}
			else if (tileEntity != null)
			{
				cachedTiles[f] = new CachedTileEntity(tileEntity, 1);
			}

			if (cachedTiles[f].hasTile())
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
			return canPipesConnect(((TilePipeBase) tileEntity).skin);
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
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), module.stack);
		}
	}
	
	/*
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		PipeItem item = new PipeItem();
		item.stack = stack;
		item.speed = 0.2F;

		if (insertPipeItem(item, simulate))
		{
			if (!simulate)
			{
				pipe.markDirty();
			}

			return ItemStack.EMPTY;
		}

		return stack;
	}

	public boolean insertPipeItem(PipeItem item, boolean simulate)
	{
		if (simulate)
		{
			return true;
		}

		item.from = facing.getIndex();
		item.to = getDirection(item);
		item.lifespan = item.stack.getItem().getEntityLifespan(item.stack, pipe.getWorld());
		pipe.items.add(item);
		pipe.markDirty();
		pipe.sync = false;
		return true;
	}

	private int getDirection(PipeItem item)
	{
		int[] dirs = new int[6];
		int pos = 0;

		for (int i = 0; i < 6; i++)
		{
			if (i != facing.getIndex() && !pipe.inventories[i].module.isEmpty() && pipe.inventories[i].module.getItem() != ItemFiltersAPI.NULL_ITEM && ItemFiltersAPI.filter(pipe.inventories[i].module, item.stack))
			{
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(EnumFacing.VALUES[i]));

				if (tileEntity instanceof TilePipeBase && !pipe.canPipesConnect(((TilePipeBase) tileEntity).skin))
				{
					continue;
				}

				IItemHandler handler = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.VALUES[i].getOpposite());

				if (handler != null && (handler instanceof ModularPipeInventory || !ItemFiltersAPI.areItemStacksEqual(item.stack, ItemHandlerHelper.insertItem(handler, item.stack.getCount() == 1 ? item.stack : ItemHandlerHelper.copyStackWithSize(item.stack, 1), true))))
				{
					dirs[pos] = i;
					pos++;
				}
			}
		}

		if (pos > 0)
		{
			return dirs[pipe.getWorld().rand.nextInt(pos)];
		}

		for (int i = 0; i < 6; i++)
		{
			if (i != facing.getIndex() && pipe.inventories[i].module.isEmpty())
			{
				TileEntity tileEntity = pipe.getWorld().getTileEntity(pipe.getPos().offset(EnumFacing.VALUES[i]));

				if (tileEntity instanceof TilePipeBase && !pipe.canPipesConnect(((TilePipeBase) tileEntity).skin))
				{
					continue;
				}

				IItemHandler handler = tileEntity == null ? null : tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.VALUES[i].getOpposite());

				if (handler != null && (handler instanceof ModularPipeInventory || !ItemFiltersAPI.areItemStacksEqual(item.stack, ItemHandlerHelper.insertItem(handler, item.stack.getCount() == 1 ? item.stack : ItemHandlerHelper.copyStackWithSize(item.stack, 1), true))))
				{
					dirs[pos] = i;
					pos++;
				}
			}
		}

		return pos == 0 ? facing.getIndex() : dirs[pipe.getWorld().rand.nextInt(pos)];
	}
	*/
}