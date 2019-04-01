package com.latmod.mods.modularpipes.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemBlockTank extends ItemBlock
{
	public static TankCapProvider getData(ItemStack stack)
	{
		TankCapProvider tank = (TankCapProvider) stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);

		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag"))
		{
			tank.deserializeNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
			stack.setTagCompound(null);
		}

		return tank;
	}

	public static class TankCapProvider implements ICapabilityProvider, IFluidHandlerItem, INBTSerializable<NBTTagCompound>
	{
		public final ItemStack stack;
		public final FluidTank tank;

		public TankCapProvider(ItemStack is)
		{
			stack = is;
			tank = new FluidTank(16 * Fluid.BUCKET_VOLUME);
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
		{
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
		{
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
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
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			tank.writeToNBT(nbt);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt)
		{
			tank.readFromNBT(nbt);
		}
	}

	public ItemBlockTank(Block block)
	{
		super(block);
	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		TankCapProvider data = getData(stack);
		return data == null || data.tank.getFluidAmount() <= 0 ? 64 : 1;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
	{
		return new TankCapProvider(stack);
	}

	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack)
	{
		NBTTagCompound nbt = new NBTTagCompound();

		if (stack.hasTagCompound())
		{
			nbt.setTag("nbt", stack.getTagCompound());
		}

		TankCapProvider data = getData(stack);

		if (data != null && data.tank.getFluidAmount() > 0)
		{
			nbt.setTag("fluid", data.tank.writeToNBT(new NBTTagCompound()));
		}

		return nbt;
	}

	@Override
	public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
	{
		stack.setTagCompound(nbt == null ? null : (NBTTagCompound) nbt.getTag("nbt"));
		getData(stack).tank.setFluid(FluidStack.loadFluidStackFromNBT(nbt == null ? null : (NBTTagCompound) nbt.getTag("fluid")));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		TankCapProvider data = getData(stack);

		if (data != null && data.tank.getFluidAmount() > 0)
		{
			tooltip.add(TextFormatting.YELLOW.toString() + data.tank.getFluidAmount() + TextFormatting.DARK_GRAY + " mB of " + TextFormatting.YELLOW + data.tank.getFluid().getLocalizedName());
		}
	}
}