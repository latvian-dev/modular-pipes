package dev.latvian.mods.modularpipes.client;

import net.minecraft.world.level.Level;

public interface PipeParticleFactory {
	void create(Level level, double x, double y, double z);
}
