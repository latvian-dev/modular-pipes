package com.latmod.mods.modularpipes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipesUtils
{
	public static <E> List<E> optimize(List<E> list)
	{
		if (list.isEmpty())
		{
			return Collections.emptyList();
		}
		else if (list.size() == 1)
		{
			return Collections.singletonList(list.get(0));
		}

		return Arrays.asList(list.toArray((E[]) new Object[0]));
	}

	public static <E> List<E> combineAndOptimize(List<E> a, List<E> b)
	{
		if (a.isEmpty())
		{
			return optimize(b);
		}
		else if (b.isEmpty())
		{
			return optimize(a);
		}

		Object[] combined = new Object[a.size() + b.size()];
		System.arraycopy(a.toArray(), 0, combined, 0, a.size());
		System.arraycopy(b.toArray(), 0, combined, a.size(), b.size());
		return Arrays.asList((E[]) combined);
	}
}