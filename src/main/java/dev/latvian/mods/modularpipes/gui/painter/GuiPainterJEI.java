package dev.latvian.mods.modularpipes.gui.painter;

import dev.latvian.mods.modularpipes.item.ItemPainter;
import dev.latvian.mods.modularpipes.net.MessageSendPaint;
import dev.latvian.mods.modularpipes.net.ModularPipesNet;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public enum GuiPainterJEI implements IGhostIngredientHandler<GuiPainter> {
	INSTANCE;

	@Override
	public <I> List<Target<I>> getTargets(GuiPainter gui, I ingredient, boolean doStart) {
		if (ingredient instanceof ItemStack) {
			return Collections.singletonList(new Target<I>() {
				@Override
				public Rectangle2d getArea() {
					return new Rectangle2d(gui.buttonPaint.x, gui.buttonPaint.y, gui.buttonPaint.getWidth(), gui.buttonPaint.getHeight());
				}

				@Override
				public void accept(I i) {
					if (ItemPainter.setPaint(gui.container.stack, (ItemStack) i)) {
						ModularPipesNet.NET.sendToServer(new MessageSendPaint(ItemPainter.getPaint(gui.container.stack)));
					}
				}
			});
		}

		return Collections.emptyList();
	}

	@Override
	public void onComplete() {
	}
}