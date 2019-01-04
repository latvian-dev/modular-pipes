package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.tile.PipeNetwork;
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

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		//ModelLoaderRegistry.registerLoader(ModelPipe.INSTANCE);

		addModel(ModularPipesItems.CONTROLLER, "error=false");
		//ModelLoader.setCustomStateMapper(ModularPipesBlocks.PIPE_MODULAR, ModelPipe.INSTANCE);
		//ModelLoader.setCustomModelResourceLocation(ModularPipesItems.PIPE_MODULAR, 0, ModelPipe.INSTANCE.ID);
		addModel(ModularPipesItems.PIPE_MODULAR, "inventory");//FIXME
		addModel(ModularPipesItems.PIPE_COBBLESTONE, "inventory");
		addModel(ModularPipesItems.PIPE_DIAMOND, "inventory");

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