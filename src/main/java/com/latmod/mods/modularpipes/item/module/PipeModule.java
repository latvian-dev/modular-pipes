package com.latmod.mods.modularpipes.item.module;

import com.latmod.mods.modularpipes.tile.PipeNetwork;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class PipeModule implements ICapabilityProvider
{
	@CapabilityInject(PipeModule.class)
	public static Capability<PipeModule> CAP;

	public TilePipeModularMK1 pipe = null;
	public ItemStack stack = ItemStack.EMPTY;

	public void writeData(NBTTagCompound nbt)
	{
	}

	public void readData(NBTTagCompound nbt)
	{
	}

	public boolean canInsert(EntityPlayer player, @Nullable EnumFacing facing)
	{
		return true;
	}

	public void onInserted(EntityPlayer player, @Nullable EnumFacing facing)
	{
	}

	public boolean canRemove(EntityPlayer player)
	{
		return true;
	}

	public void onRemoved(EntityPlayer player)
	{
	}

	public void onPipeBroken()
	{
	}

	public boolean canUpdate()
	{
		return false;
	}

	public void updateModule()
	{
	}

	public void clearCache()
	{
	}

	public boolean onModuleRightClick(EntityPlayer player, EnumHand hand)
	{
		return false;
	}

	public boolean isConnected(EnumFacing facing)
	{
		return false;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CAP;
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		return capability == CAP ? (T) this : null;
	}

	public final void refreshNetwork()
	{
		if (pipe != null && pipe.hasWorld())
		{
			PipeNetwork.get(pipe.getWorld()).refresh();
		}
	}

	public String toString()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		writeData(nbt);
		return stack.getItem().getRegistryName() + (nbt.isEmpty() ? "" : ("+" + nbt));
	}
}