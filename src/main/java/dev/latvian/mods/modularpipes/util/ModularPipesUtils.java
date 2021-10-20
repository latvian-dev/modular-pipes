package dev.latvian.mods.modularpipes.util;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipesUtils {
	public static final Direction[] DIRECTIONS = Direction.values();
	public static final int[] POS_X = new int[6];
	public static final int[] POS_Y = new int[6];
	public static final int[] POS_Z = new int[6];
	public static final int[] ROT_X = new int[6];
	public static final int[] ROT_Y = new int[6];
	public static final int[] OPPOSITE = new int[7];

	static {
		for (int i = 0; i < 6; i++) {
			POS_X[i] = DIRECTIONS[i].getStepX();
			POS_Y[i] = DIRECTIONS[i].getStepY();
			POS_Z[i] = DIRECTIONS[i].getStepZ();
			OPPOSITE[i] = DIRECTIONS[i].getOpposite().get3DDataValue();
		}

		ROT_X[0] = 90;
		ROT_Y[0] = 0;
		ROT_X[1] = 270;
		ROT_Y[1] = 180;
		ROT_X[2] = 0;
		ROT_Y[2] = 180;
		ROT_X[3] = 0;
		ROT_Y[3] = 0;
		ROT_X[4] = 0;
		ROT_Y[4] = 270;
		ROT_X[5] = 0;
		ROT_Y[5] = 90;

		OPPOSITE[6] = 6;
	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> optimize(@Nullable List<E> list) {
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		} else if (list.size() == 1) {
			return Collections.singletonList(list.get(0));
		}

		return Arrays.asList(list.toArray((E[]) new Object[0]));
	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> combineAndOptimize(@Nullable List<E> a, @Nullable List<E> b) {
		if (a == null || a.isEmpty()) {
			return optimize(b);
		} else if (b == null || b.isEmpty()) {
			return optimize(a);
		}

		Object[] combined = new Object[a.size() + b.size()];
		System.arraycopy(a.toArray(), 0, combined, 0, a.size());
		System.arraycopy(b.toArray(), 0, combined, a.size(), b.size());
		return Arrays.asList((E[]) combined);
	}
}