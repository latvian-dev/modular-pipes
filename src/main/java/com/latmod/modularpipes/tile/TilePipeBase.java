package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.block.PipeSkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TilePipeBase extends TileBase
{
	private static final IItemHandler[] TEMP_HANDLERS = new IItemHandler[6];
	private static final int[] TEMP_HANDLER_FLAGS = new int[6];

	public List<PipeItem> items = new ArrayList<>(0);
	private boolean isDirty = false;
	public boolean sync = false;
	public PipeSkin skin = PipeSkin.NONE;
	public boolean invisible = false;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		if (!items.isEmpty())
		{
			NBTTagList list = new NBTTagList();

			for (PipeItem item : items)
			{
				list.appendTag(item.serializeNBT());
			}

			nbt.setTag("items", list);
		}

		if (skin != PipeSkin.NONE)
		{
			nbt.setString("skin", skin.name);
		}

		if (invisible)
		{
			nbt.setBoolean("invisible", true);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		items = new ArrayList<>(0);
		NBTTagList list = nbt.getTagList("items", Constants.NBT.TAG_COMPOUND);

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

		skin = PipeSkin.byName(nbt.getString("skin"));
		invisible = nbt.getBoolean("invisible");
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null && hasPipeItemHandler(side) || super.hasCapability(capability, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side != null ? (T) getPipeItemHandler(side) : super.getCapability(capability, side);
	}

	@Override
	public void invalidate()
	{
		if (hasWorld())
		{
			PipeNetwork.get(getWorld()).refresh();
		}

		super.invalidate();
	}

	@Override
	public void setWorld(World world)
	{
		super.setWorld(world);

		if (hasWorld())
		{
			PipeNetwork.get(getWorld()).refresh();
		}
	}

	public boolean hasPipeItemHandler(EnumFacing side)
	{
		return false;
	}

	@Nullable
	public IPipeItemHandler getPipeItemHandler(EnumFacing side)
	{
		return null;
	}

	public void tickPipe()
	{
		if (items.isEmpty())
		{
			return;
		}

		Arrays.fill(TEMP_HANDLERS, null);
		Arrays.fill(TEMP_HANDLER_FLAGS, 0);

		for (PipeItem item : items.size() == 1 ? Collections.singletonList(items.get(0)) : new ArrayList<>(items))
		{
			item.age++;

			if (item.to == 6 || item.from == 6)
			{
				item.age = Integer.MAX_VALUE;
			}
			else if (item.pos >= 1F)
			{
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

				item.age = Integer.MAX_VALUE;
			}
		}

		if (items.removeIf(PipeItem.IS_DEAD))
		{
			markDirty();
			sync = false;
		}
	}

	@Nullable
	private IItemHandler getInventory(World world, int to)
	{
		if (TEMP_HANDLER_FLAGS[to] == 0)
		{
			TEMP_HANDLER_FLAGS[to] = 1;

			BlockPos p = pos.offset(EnumFacing.VALUES[to]);

			if (world.isBlockLoaded(p))
			{
				TileEntity tileEntity = world.getTileEntity(p);

				if (tileEntity != null)
				{
					TEMP_HANDLERS[to] = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.VALUES[to].getOpposite());
				}
			}
		}

		return TEMP_HANDLERS[to];
	}

	public void moveItem(PipeItem item)
	{
		item.pos += Math.min(item.speed, 0.99F);
		float pipeSpeed = (float) ModularPipesConfig.pipes.base_speed;

		if (item.speed > pipeSpeed)
		{
			item.speed *= 0.99F;

			if (item.speed < pipeSpeed)
			{
				item.speed = pipeSpeed;
			}
		}
		else if (item.speed < pipeSpeed)
		{
			item.speed *= 1.3F;

			if (item.speed > pipeSpeed)
			{
				item.speed = pipeSpeed;
			}
		}
	}

	@Override
	public void markDirty()
	{
		isDirty = true;
		sync = true;
	}

	public final void sendUpdates()
	{
		if (isDirty)
		{
			super.markDirty();

			if (!world.isRemote && sync)
			{
				IBlockState state = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, state, state, 11);
			}

			isDirty = false;
		}
	}

	public void dropItems()
	{
		for (PipeItem item : items)
		{
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), item.stack);
		}
	}

	public boolean canPipesConnect(PipeSkin s)
	{
		return skin == s || skin == PipeSkin.NONE || s == PipeSkin.NONE;
	}

	public boolean isConnected(EnumFacing facing)
	{
		if (!hasPipeItemHandler(facing))
		{
			return false;
		}

		TileEntity tileEntity = world.getTileEntity(pos.offset(facing));

		if (tileEntity instanceof TilePipeBase && !canPipesConnect(((TilePipeBase) tileEntity).skin))
		{
			return false;
		}

		return tileEntity instanceof TileController || tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
	}
}