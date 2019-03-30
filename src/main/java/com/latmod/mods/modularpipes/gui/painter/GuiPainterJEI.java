package com.latmod.mods.modularpipes.gui.painter;

import com.latmod.mods.modularpipes.item.ItemPainter;
import com.latmod.mods.modularpipes.net.MessageSendPaint;
import com.latmod.mods.modularpipes.net.ModularPipesNet;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public enum GuiPainterJEI implements IGhostIngredientHandler<GuiPainter>, IAdvancedGuiHandler<GuiPainter>
{
	INSTANCE;

	@Override
	public <I> List<Target<I>> getTargets(GuiPainter gui, I ingredient, boolean doStart)
	{
		if (ingredient instanceof ItemStack)
		{
			return Collections.singletonList(new Target<I>()
			{
				@Override
				public Rectangle getArea()
				{
					return new Rectangle(gui.buttonPaint.x, gui.buttonPaint.y, gui.buttonPaint.width, gui.buttonPaint.height);
				}

				@Override
				public void accept(I i)
				{
					if (ItemPainter.setPaint(gui.container.stack, (ItemStack) i))
					{
						ModularPipesNet.NET.sendToServer(new MessageSendPaint((ItemStack) ingredient));
					}
				}
			});
		}

		return Collections.emptyList();
	}

	@Override
	public void onComplete()
	{
	}

	@Override
	public Class<GuiPainter> getGuiContainerClass()
	{
		return GuiPainter.class;
	}

	@Override
	@Nullable
	public Object getIngredientUnderMouse(GuiPainter gui, int x, int y)
	{
		if (x >= gui.buttonPaint.x && x < gui.buttonPaint.x + gui.buttonPaint.width && y >= gui.buttonPaint.y && y < gui.buttonPaint.y + gui.buttonPaint.height)
		{
			ItemStack stack = ItemPainter.getPaint(gui.container.stack);

			if (!stack.isEmpty())
			{
				return stack;
			}
		}

		return null;
	}
}
