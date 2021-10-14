package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.block.ModularPipesBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public interface ModularPipesBlockEntities {
	DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ModularPipes.MOD_ID);

	Supplier<BlockEntityType<?>> TRANSPORT_PIPE = REGISTRY.register("transport_pipe", () -> BlockEntityType.Builder.of(TransportPipeBlockEntity::new, ModularPipesBlocks.TRANSPORT_PIPE.get()).build(null));
	Supplier<BlockEntityType<?>> MODULAR_PIPE = REGISTRY.register("modular_pipe", () -> BlockEntityType.Builder.of(ModularPipeBlockEntity::new, ModularPipesBlocks.MODULAR_PIPE_MK1.get(), ModularPipesBlocks.MODULAR_PIPE_MK2.get(), ModularPipesBlocks.MODULAR_PIPE_MK3.get()).build(null));
}