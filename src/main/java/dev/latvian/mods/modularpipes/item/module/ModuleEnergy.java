package dev.latvian.mods.modularpipes.item.module;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModuleEnergy extends PipeModule {
	@Override
	public boolean isThisCapability(Capability<?> capability) {
		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	public void clearCache() {
		super.clearCache();
	}

	@Nullable
	public IEnergyStorage getFacingEnergyStorage() {
		BlockEntity tileEntity = getFacingTile();
		return tileEntity == null ? null : tileEntity.getCapability(CapabilityEnergy.ENERGY, sideData.direction.getOpposite()).orElse(null);
	}

	@Override
	public Component canInsert(Player player, InteractionHand hand) {
		return new TextComponent("WIP!");
	}
}