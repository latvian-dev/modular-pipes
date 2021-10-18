package dev.latvian.mods.modularpipes.item.module;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @author LatvianModder
 */
public class FluidExtractModule extends PipeModule implements IFluidHandler {
	@Override
	public boolean isThisCapability(Capability<?> capability) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@NotNull
	@Override
	public FluidStack getFluidInTank(int i) {
		return FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int i) {
		return 1000;
	}

	@Override
	public boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}