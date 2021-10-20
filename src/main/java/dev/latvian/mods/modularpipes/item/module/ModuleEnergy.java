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
import java.util.Optional;

/**
 * @author LatvianModder
 */
public class ModuleEnergy extends PipeModule {
	private Optional<IEnergyStorage> cachedEnergyStorage = null;

	@Override
	public boolean isThisCapability(Capability<?> capability) {
		return capability == CapabilityEnergy.ENERGY;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		cachedEnergyStorage = null;
	}

	@Nullable
	public IEnergyStorage getFacingEnergyStorage() {
		if (cachedEnergyStorage == null) {
			BlockEntity tileEntity = getFacingTile();
			cachedEnergyStorage = Optional.ofNullable(tileEntity == null ? null : tileEntity.getCapability(CapabilityEnergy.ENERGY, sideData.direction.getOpposite()).orElse(null));
		}

		return cachedEnergyStorage.orElse(null);
	}

	@Override
	public Component canInsert(Player player, InteractionHand hand) {
		return new TextComponent("WIP!");
	}
}