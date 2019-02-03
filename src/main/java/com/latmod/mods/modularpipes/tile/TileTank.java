package com.latmod.mods.modularpipes.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class TileTank extends TileBase implements ITickable
{
	public boolean brokenByCreative = false;
	private Fluid prevFluid = null;
	private int prevAmountPart = 0;
	private boolean isDirty = false;

	public final FluidTank tank = new FluidTank(16 * Fluid.BUCKET_VOLUME)
	{
		@Override
		protected void onContentsChanged()
		{
			markDirty();
		}
	};

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		if (tank.getFluid() != null)
		{
			tank.getFluid().writeToNBT(nbt);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		tank.setFluid(FluidStack.loadFluidStackFromNBT(nbt));
		prevAmountPart = tank.getFluidAmount() * 255 / tank.getCapacity();
		prevFluid = prevAmountPart > 0 ? tank.getFluid().getFluid() : null;
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing)
	{
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing)
	{
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) tank : super.getCapability(capability, facing);
	}

	@Override
	public void markDirty()
	{
		isDirty = true;
	}

	@Override
	public void update()
	{
		if (!world.isRemote)
		{
			int amount = tank.getFluidAmount() * 255 / tank.getCapacity();
			Fluid fluid = amount > 0 ? tank.getFluid().getFluid() : null;

			if (amount > 0 && (prevFluid != fluid || prevAmountPart != amount || world.getTotalWorldTime() % 25L == 3L))
			{
				TileEntity tileEntity = world.getTileEntity(pos.down());

				if (tileEntity instanceof TileTank)
				{
					FluidTank t = ((TileTank) tileEntity).tank;

					if (t.getFluidAmount() < t.getCapacity())
					{
						FluidStack stack = tank.drain(t.getCapacity() - t.getFluidAmount(), false);

						if (stack != null && stack.amount > 0)
						{
							int a = t.fill(stack, true);

							if (a > 0)
							{
								tank.drain(a, true);
							}
						}
					}
				}
			}

			if (fluid != prevFluid)
			{
				IBlockState state = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, state, state, 11);
			}
			else if (amount != prevAmountPart)
			{
				world.addBlockEvent(pos, getBlockType(), 0, amount);
			}

			prevFluid = fluid;
			prevAmountPart = amount;
		}

		if (isDirty)
		{
			isDirty = false;

			if (world != null)
			{
				world.markChunkDirty(pos, this);
			}
		}
	}
}