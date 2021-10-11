package dev.latvian.mods.modularpipes.integration;

import dev.latvian.mods.modularpipes.ModularPipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

/**
 * @author LatvianModder
 */
@JeiPlugin
public class ModularPipesJEIIntegration implements IModPlugin {
	public static final ResourceLocation ID = new ResourceLocation(ModularPipes.MOD_ID, "jei");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		//		registration.addGhostIngredientHandler(GuiPainter.class, GuiPainterJEI.INSTANCE);
	}
}