package dev.latvian.mods.modularpipes.util;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class PrioritizedPipeItem {
	public static final PrioritizedPipeItem EMPTY = new PrioritizedPipeItem(null, 0);

	public final PipeItem item;
	public final int priority;

	public PrioritizedPipeItem(@Nullable PipeItem i, int p) {
		item = i;
		priority = p;
	}
}