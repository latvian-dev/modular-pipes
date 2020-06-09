package com.latmod.mods.modularpipes.item.module;

import com.latmod.mods.modularpipes.net.MessageParticle;
import com.latmod.mods.modularpipes.net.ModularPipesNet;
import com.latmod.mods.modularpipes.tile.PipeNetwork;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class PipeModule implements ICapabilityProvider
{
	@CapabilityInject(PipeModule.class)
	public static Capability<PipeModule> CAP;
	public TilePipeModularMK1 pipe = null;
	public ItemStack moduleItem = ItemStack.EMPTY;
	protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);

	public void writeData(CompoundNBT nbt)
	{
	}

	public void readData(CompoundNBT nbt)
	{
	}

	public boolean canInsert(PlayerEntity player, @Nullable Direction facing)
	{
		return true;
	}

	public void onInserted(PlayerEntity player, @Nullable Direction facing)
	{
	}

	public boolean canRemove(PlayerEntity player)
	{
		return true;
	}

	public void onRemoved(PlayerEntity player)
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

	public boolean onModuleRightClick(PlayerEntity player, Hand hand)
	{
		return false;
	}

	public boolean isConnected(Direction facing)
	{
		return false;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing)
	{
		return capability == CAP ? thisOptional.cast() : LazyOptional.empty();
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
		CompoundNBT nbt = new CompoundNBT();
		writeData(nbt);
		return moduleItem.getItem().getRegistryName() + (nbt.isEmpty() ? "" : ("+" + nbt));
	}

	public void spawnParticle(int type)
	{
		if (pipe.hasWorld() && !pipe.getWorld().isRemote)
		{
			double x = pipe.getPos().getX() + 0.5D;
			double y = pipe.getPos().getY() + 0.5D;
			double z = pipe.getPos().getZ() + 0.5D;
			ModularPipesNet.NET.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, y, z, 24, pipe.getWorld().getDimension().getType())), new MessageParticle(pipe.getPos(), null, type));
		}
	}
}