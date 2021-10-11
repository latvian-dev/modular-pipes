package dev.latvian.mods.modularpipes;

import dev.latvian.mods.modularpipes.client.ModularPipesClient;
import dev.latvian.mods.modularpipes.item.ModularPipesItems;
import dev.latvian.mods.modularpipes.net.ModularPipesNet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModularPipes.MOD_ID)
public class ModularPipes {
	public static final String MOD_ID = "modularpipes";
	public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModularPipesItems.MODULAR_PIPE_MK1);
		}
	};

	public static ModularPipesCommon PROXY;

	public ModularPipes() {
		PROXY = DistExecutor.safeRunForDist(() -> ModularPipesClient::new, () -> ModularPipesCommon::new);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, ModularPipesEventHandler::registerBlocks);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModularPipesEventHandler::registerItems);
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(BlockEntityType.class, ModularPipesEventHandler::registerBlockEntities);
		ModularPipesNet.init();
	}
}