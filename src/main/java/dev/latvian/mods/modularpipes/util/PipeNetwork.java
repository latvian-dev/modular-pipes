package dev.latvian.mods.modularpipes.util;

import dev.latvian.mods.modularpipes.net.RemovePipeItemMessage;
import dev.latvian.mods.modularpipes.net.UpdatePipeItemMessage;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * @author LatvianModder
 */
public abstract class PipeNetwork implements ICapabilityProvider {
	@CapabilityInject(PipeNetwork.class)
	public static Capability<PipeNetwork> CAP;

	public final Level level;
	public final Long2ObjectOpenHashMap<PipeItem> pipeItems;
	protected LazyOptional<?> thisOptional;

	public PipeNetwork(Level w) {
		level = w;
		pipeItems = new Long2ObjectOpenHashMap<>();
		thisOptional = LazyOptional.of(() -> this);
	}

	@Nullable
	public static PipeNetwork get(@Nullable Level world) {
		return world == null ? null : world.getCapability(CAP).orElse(null);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		return capability == CAP ? thisOptional.cast() : LazyOptional.empty();
	}

	public void refresh() {
	}

	public boolean canTick() {
		return !pipeItems.isEmpty();
	}

	public final void tick() {
		if (canTick()) {
			ProfilerFiller profiler = level.getProfiler();
			profiler.push("Modular Pipes");
			actuallyTick(profiler);
			profiler.pop();
		}
	}

	public void actuallyTick(ProfilerFiller profiler) {
		if (!pipeItems.isEmpty()) {
			profiler.push("Pipe Items");

			Iterator<PipeItem> iterator = pipeItems.values().iterator();

			while (iterator.hasNext()) {
				PipeItem item = iterator.next();
				item.tick();

				if (item.ttl <= 0) {
					iterator.remove();

					if (level instanceof ServerLevel) {
						new RemovePipeItemMessage(item.id).sendToLevel((ServerLevel) level);
					}
				} else if (item.update) {
					item.update = false;

					if (level instanceof ServerLevel) {
						new UpdatePipeItemMessage(item).sendToLevel((ServerLevel) level);
					}
				}
			}

			profiler.pop();
		}
	}
}