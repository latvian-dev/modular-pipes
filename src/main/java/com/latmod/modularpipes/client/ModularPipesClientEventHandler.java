package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.block.ModularPipesBlocks;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.tile.PipeNetwork;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID, value = Side.CLIENT)
public class ModularPipesClientEventHandler
{
	private static void addModel(Item item, String variant)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), variant));
	}

	private static void addPipeModel(Block block, Item item)
	{
		ModelLoader.setCustomStateMapper(block, ModelPipeLoader.INSTANCE);
		ModelLoader.setCustomModelResourceLocation(item, 0, ModelPipeLoader.ID);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(ModelPipeLoader.INSTANCE);

		addPipeModel(ModularPipesBlocks.PIPE_TRANSPORT, ModularPipesItems.PIPE_TRANSPORT);
		addPipeModel(ModularPipesBlocks.PIPE_MODULAR_MK1, ModularPipesItems.PIPE_MODULAR_MK1);
		addPipeModel(ModularPipesBlocks.PIPE_MODULAR_MK2, ModularPipesItems.PIPE_MODULAR_MK2);
		addPipeModel(ModularPipesBlocks.PIPE_MODULAR_MK3, ModularPipesItems.PIPE_MODULAR_MK3);
		addModel(ModularPipesItems.TANK, "normal");
		addModel(ModularPipesItems.MODULAR_STORAGE, "normal");
		addModel(ModularPipesItems.MODULAR_TANK, "normal");

		addModel(ModularPipesItems.PAINTER, "inventory");
		addModel(ModularPipesItems.MODULE, "inventory");
		addModel(ModularPipesItems.MODULE_ITEM_STORAGE, "inventory");
		addModel(ModularPipesItems.MODULE_ITEM_BASE, "inventory");
		addModel(ModularPipesItems.MODULE_ITEM_EXTRACT, "inventory");
		addModel(ModularPipesItems.MODULE_ITEM_INSERT, "inventory");
		addModel(ModularPipesItems.MODULE_FLUID_STORAGE, "inventory");
		addModel(ModularPipesItems.MODULE_FLUID_BASE, "inventory");
		addModel(ModularPipesItems.MODULE_FLUID_EXTRACT, "inventory");
		addModel(ModularPipesItems.MODULE_FLUID_INSERT, "inventory");
		addModel(ModularPipesItems.MODULE_CRAFTING, "inventory");
	}

	@SubscribeEvent
	public static void tickClientWorld(TickEvent.ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();

		if (event.phase == TickEvent.Phase.END && mc.world != null && !mc.isGamePaused())
		{
			PipeNetwork.get(mc.world).tick();
		}
	}

	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event)
	{
		PipeNetwork.get(Minecraft.getMinecraft().world).render(event.getPartialTicks());
	}
}