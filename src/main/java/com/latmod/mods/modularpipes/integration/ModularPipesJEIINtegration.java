package com.latmod.mods.modularpipes.integration;

import com.latmod.mods.modularpipes.gui.painter.GuiPainter;
import com.latmod.mods.modularpipes.gui.painter.GuiPainterJEI;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

/**
 * @author LatvianModder
 */
@JEIPlugin
public class ModularPipesJEIINtegration implements IModPlugin
{
	@Override
	public void register(IModRegistry registry)
	{
		registry.addGhostIngredientHandler(GuiPainter.class, GuiPainterJEI.INSTANCE);
	}
}