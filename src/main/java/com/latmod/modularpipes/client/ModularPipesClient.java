package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.block.BlockModularPipe;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.item.ModularPipesItems;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = ModularPipes.MOD_ID, value = Side.CLIENT)
public class ModularPipesClient extends ModularPipesCommon
{
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		ModelLoader.setCustomStateMapper(ModularPipesItems.PIPE_MODULAR, new StateMap.Builder().ignore(BlockModularPipe.TIER).build());

		registerModel(ModularPipesItems.PIPE_BASIC, 0, ModularPipes.MOD_ID + ":pipe_item#variant=basic");

		for (int m = 0; m < 8; m++)
		{
			registerModel(ModularPipesItems.PIPE_MODULAR, m, ModularPipes.MOD_ID + ":pipe_item#variant=tier_" + (m & 7));
		}

		registerModel(ModularPipesItems.PIPE_NODE, 0, ModularPipesItems.PIPE_BASIC.getRegistryName() + "#model=none");

		registerModel(ModularPipesItems.MODULE, 0, "inventory");
		registerModel(ModularPipesItems.DEBUG, 0, "inventory");

		for (Item m : MODULE_LIST)
		{
			registerModel(m, 0, m.getRegistryName().toString().replace("module_", "module/") + "#inventory");
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileModularPipe.class, new RenderModularPipe());
	}

	private static void registerModel(Block block, int meta, String variant)
	{
		registerModel(Item.getItemFromBlock(block), meta, variant);
	}

	private static void registerModel(Item item, int meta, String variant)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, variant.indexOf('#') != -1 ? new ModelResourceLocation(variant) : new ModelResourceLocation(item.getRegistryName(), variant));
	}

	@Override
	public PipeNetwork getClientNetwork(World world)
	{
		if (ClientPipeNetwork.INSTANCE == null || world != ClientPipeNetwork.INSTANCE.world)
		{
			ClientPipeNetwork.INSTANCE = new ClientPipeNetwork(world);
		}

		return ClientPipeNetwork.INSTANCE;
	}
}