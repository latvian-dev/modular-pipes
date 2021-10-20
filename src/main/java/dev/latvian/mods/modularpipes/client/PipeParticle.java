package dev.latvian.mods.modularpipes.client;

import dev.latvian.mods.modularpipes.item.ModularPipesItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;

public enum PipeParticle {
	SPARK(false, (level, x, y, z) -> {
		float shift = level.random.nextFloat() * 0.2F - 0.1F;
		float size = 1.4F + level.random.nextFloat() * 0.6F;
		level.addParticle(new DustParticleOptions(0.9F - shift, 0.9F + shift, 0F, size), x, y, z, 0D, 0D, 0D);
	}),
	EXPLOSION(false, (level, x, y, z) -> level.addParticle(ParticleTypes.EXPLOSION, x, y, z, 0D, 0D, 0D)),
	DEBUG_EXPLOSION(true, (level, x, y, z) -> level.addParticle(ParticleTypes.EXPLOSION, x, y, z, 0D, 0D, 0D));

	public static final PipeParticle[] VALUES = values();

	public final boolean debug;
	public final PipeParticleFactory factory;

	PipeParticle(boolean d, PipeParticleFactory f) {
		debug = d;
		factory = f;
	}

	public boolean canSee(ServerPlayer player, double x, double y, double z) {
		if (player.distanceToSqr(x, y, z) > 24D * 24D) {
			return false;
		}

		return !debug || player.getMainHandItem().getItem() == ModularPipesItems.VISUALIZER.get();
	}
}
