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
	public static final int[] POS_X = new int[7];
	public static final int[] POS_Y = new int[7];
	public static final int[] POS_Z = new int[7];
	public static final double[] ROT_X = new double[7];
	public static final double[] ROT_Y = new double[7];
	public static final int[] OPPOSITE = new int[7];
	public static final char[] DIR_CHAR = new char[7];

	static {
		for (int i = 0; i < 6; i++) {
			POS_X[i] = DIRECTIONS[i].getStepX();
			POS_Y[i] = DIRECTIONS[i].getStepY();
			POS_Z[i] = DIRECTIONS[i].getStepZ();
			OPPOSITE[i] = DIRECTIONS[i].getOpposite().get3DDataValue();
			DIR_CHAR[i] = Character.toUpperCase(DIRECTIONS[i].getSerializedName().charAt(0));
		}

		POS_X[6] = 0;
		POS_Y[6] = 0;
		POS_Z[6] = 0;
		OPPOSITE[6] = 6;
		DIR_CHAR[6] = 'X';

		//ROT_X[0] = 90D;
		//ROT_Y[0] = 0D;
		//ROT_X[1] = 270D;
		//ROT_Y[1] = 180D;
		ROT_X[0] = 0D;
		ROT_Y[0] = 180D;
		ROT_X[1] = 0D;
		ROT_Y[1] = 0D;
		ROT_X[2] = 0D;
		ROT_Y[2] = 180D;
		ROT_X[3] = 0D;
		ROT_Y[3] = 0D;
		ROT_X[4] = 0D;
		ROT_Y[4] = 270D;
		ROT_X[5] = 0D;
		ROT_Y[5] = 90D;
		ROT_X[6] = 0D;
		ROT_Y[6] = 180D;
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

	public static boolean translate(double cx, double cy, double cz, int pos, int steps, int from, int to, double[] position) {
		if (from == 6) {
			double p = pos / (steps * 4D);
			double r = p * 0.5D;
			position[0] = cx + POS_X[to] * r;
			position[1] = cy + POS_Y[to] * r;
			position[2] = cz + POS_Z[to] * r;
			position[3] = 0D;
			position[4] = 0D;
			return false;
		}

		if (pos < steps * 2) {
			double p = pos / (steps * 2D);
			double r = 0.5D - p * 0.5D;
			position[0] = cx + POS_X[from] * r;
			position[1] = cy + POS_Y[from] * r;
			position[2] = cz + POS_Z[from] * r;
			position[3] = ROT_X[OPPOSITE[from]];
			position[4] = ROT_Y[OPPOSITE[from]];
		} else {
			double p = (pos - steps * 2) / (steps * 2D);
			double r = p * 0.5D;
			position[0] = cx + POS_X[to] * r;
			position[1] = cy + POS_Y[to] * r;
			position[2] = cz + POS_Z[to] * r;
			position[3] = ROT_X[to];
			position[4] = ROT_Y[to];
		}

		return false;
	}
}