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
		addModel(item, "inventory"); //FIXME: Pipe Model
		//ModelLoader.setCustomStateMapper(ModularPipesBlocks.PIPE_MODULAR, ModelPipe.INSTANCE);
		//ModelLoader.setCustomModelResourceLocation(ModularPipesItems.PIPE_MODULAR, 0, ModelPipe.INSTANCE.ID);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		//ModelLoaderRegistry.registerLoader(ModelPipe.INSTANCE);

		addModel(ModularPipesItems.CONTROLLER, "error=false");
		addPipeModel(ModularPipesBlocks.PIPE_MODULAR, ModularPipesItems.PIPE_MODULAR);
		addPipeModel(ModularPipesBlocks.PIPE_COBBLESTONE, ModularPipesItems.PIPE_COBBLESTONE);
		addPipeModel(ModularPipesBlocks.PIPE_GRANITE, ModularPipesItems.PIPE_GRANITE);
		addPipeModel(ModularPipesBlocks.PIPE_DIORITE, ModularPipesItems.PIPE_DIORITE);
		addPipeModel(ModularPipesBlocks.PIPE_ANDESITE, ModularPipesItems.PIPE_ANDESITE);
		addPipeModel(ModularPipesBlocks.PIPE_BRICK, ModularPipesItems.PIPE_BRICK);
		addPipeModel(ModularPipesBlocks.PIPE_QUARTZ, ModularPipesItems.PIPE_QUARTZ);
		addPipeModel(ModularPipesBlocks.PIPE_ENDSTONE, ModularPipesItems.PIPE_ENDSTONE);
		addPipeModel(ModularPipesBlocks.PIPE_GOLD, ModularPipesItems.PIPE_GOLD);
		addPipeModel(ModularPipesBlocks.PIPE_GLOWSTONE, ModularPipesItems.PIPE_GLOWSTONE);
		addPipeModel(ModularPipesBlocks.PIPE_MAGMA, ModularPipesItems.PIPE_MAGMA);
		addPipeModel(ModularPipesBlocks.PIPE_DIAMOND, ModularPipesItems.PIPE_DIAMOND);

		addModel(ModularPipesItems.MODULE, "inventory");
		addModel(ModularPipesItems.MODULE_EXTRACT, "inventory");
		addModel(ModularPipesItems.MODULE_RIGHTCLICK_EXTRACT, "inventory");
		addModel(ModularPipesItems.MODULE_CRAFTING, "inventory");
		addModel(ModularPipesItems.MODULE_ITEM_STORAGE, "inventory");
		addModel(ModularPipesItems.MODULE_FLUID_STORAGE, "inventory");
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