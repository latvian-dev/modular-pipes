package com.latmod.mods.modularpipes.integration;

import com.latmod.mods.modularpipes.ModularPipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
@JeiPlugin
public class ModularPipesJEIIntegration implements IModPlugin
{
	@Override
	public ResourceLocation getPluginUid()
	{
		return new ResourceLocation(ModularPipes.MOD_ID, "jei");
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration)
	{
		//		registration.addGhostIngredientHandler(GuiPainter.class, GuiPainterJEI.INSTANCE);
	}
}