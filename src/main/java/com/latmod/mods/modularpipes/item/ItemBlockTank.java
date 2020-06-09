package com.latmod.mods.modularpipes.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemBlockTank extends BlockItem
{
	public static class TankCapProvider implements ICapabilityProvider, IFluidHandlerItem, INBTSerializable<CompoundNBT>
	{
		public final ItemStack stack;
		public final FluidTank tank;
		protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);

		public TankCapProvider(ItemStack is)
		{
			stack = is;
			tank = new FluidTank(16 * Fluid.BUCKET_VOLUME);
		}

		@Nullable
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
		{
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? thisOptional.cast() : null;
		}

		@Nonnull
		@Override
		public ItemStack getContainer()
		{
			return stack;
		}

		@Override
		public IFluidTankProperties[] getTankProperties()
		{
			return tank.getTankProperties();
		}

		@Override
		public int fill(FluidStack resource, boolean doFill)
		{
			return tank.fill(resource, doFill);
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain)
		{
			return tank.drain(resource, doDrain);
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain)
		{
			return tank.drain(maxDrain, doDrain);
		}

		@Override
		public CompoundNBT serializeNBT()
		{
			CompoundNBT nbt = new CompoundNBT();
			tank.writeToNBT(nbt);
			return nbt;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt)
		{
			tank.readFromNBT(nbt);
		}
	}

	public ItemBlockTank(Block block, Properties properties)
	{
		super(block, properties);
	}

	public static TankCapProvider getData(ItemStack stack)
	{
		TankCapProvider tank = (TankCapProvider) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(null);

		if (stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
		{
			tank.deserializeNBT(stack.getChildTag("BlockEntityTag"));
			stack.setTag(null);
		}

		return tank;
	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		TankCapProvider data = getData(stack);
		return data == null || data.tank.getFluidAmount() <= 0 ? 64 : 1;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
	{
		return new TankCapProvider(stack);
	}

	@Override
	public CompoundNBT getShareTag(ItemStack stack)
	{
		CompoundNBT nbt = new CompoundNBT();

		if (stack.hasTag())
		{
			nbt.put("nbt", stack.getTag());
		}

		TankCapProvider data = getData(stack);

		if (data != null && data.tank.getFluidAmount() > 0)
		{
			nbt.put("fluid", data.tank.writeToNBT(new CompoundNBT()));
		}

		return nbt;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt)
	{
		stack.setTag(nbt == null ? null : nbt.getCompound("nbt"));
		getData(stack).tank.setFluid(FluidStack.loadFluidStackFromNBT(nbt == null ? null : nbt.getCompound("fluid")));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		TankCapProvider data = getData(stack);

		if (data != null && data.tank.getFluidAmount() > 0)
		{
			tooltip.add(new StringTextComponent(TextFormatting.YELLOW.toString() + data.tank.getFluidAmount() + TextFormatting.DARK_GRAY + " mB of " + TextFormatting.YELLOW + data.tank.getFluid().getLocalizedName()));
		}
	}
}