package com.latmod.mods.modularpipes.tile;

import com.latmod.mods.modularpipes.block.ModularPipesTiles;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class TileTank extends TileBase implements ITickableTileEntity
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

	public TileTank()
	{
		super(ModularPipesTiles.TANK);
	}

	@Override
	public void writeData(CompoundNBT nbt)
	{
		if (tank.getFluid() != null)
		{
			tank.getFluid().writeToNBT(nbt);
		}
	}

	@Override
	public void readData(CompoundNBT nbt)
	{
		tank.setFluid(FluidStack.loadFluidStackFromNBT(nbt));
		prevAmountPart = tank.getFluidAmount() * 255 / tank.getCapacity();
		prevFluid = prevAmountPart > 0 ? tank.getFluid().getFluid() : null;
	}

	@Override
	public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.Direction facing)
	{
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? thisOptional.cast() : super.getCapability(capability, facing);
	}

	@Override
	public void markDirty()
	{
		isDirty = true;
	}

	@Override
	public void tick()
	{
		if (!world.isRemote)
		{
			int amount = tank.getFluidAmount() * 255 / tank.getCapacity();
			Fluid fluid = amount > 0 ? tank.getFluid().getFluid() : null;

			if (amount > 0 && (prevFluid != fluid || prevAmountPart != amount || world.getWorldInfo().getGameTime() % 25L == 3L))
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
				BlockState state = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, state, state, 11);
			}
			else if (amount != prevAmountPart)
			{
				world.addBlockEvent(pos, getBlockState().getBlock(), 0, amount);
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