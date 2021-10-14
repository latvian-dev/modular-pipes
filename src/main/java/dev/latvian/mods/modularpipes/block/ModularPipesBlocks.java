package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.ModularPipes;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public interface ModularPipesBlocks {
	DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, ModularPipes.MOD_ID);

	Supplier<Block> TRANSPORT_PIPE = REGISTRY.register("transport_pipe", () -> new PipeBlock(PipeTier.BASIC));
	Supplier<Block> MODULAR_PIPE_MK1 = REGISTRY.register("modular_pipe_mk1", () -> new PipeBlock(PipeTier.MK1));
	Supplier<Block> MODULAR_PIPE_MK2 = REGISTRY.register("modular_pipe_mk2", () -> new PipeBlock(PipeTier.MK2));
	Supplier<Block> MODULAR_PIPE_MK3 = REGISTRY.register("modular_pipe_mk3", () -> new PipeBlock(PipeTier.MK3));
}