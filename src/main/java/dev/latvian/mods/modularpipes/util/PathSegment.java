package dev.latvian.mods.modularpipes.util;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class PathSegment {
	public final int x, y, z, from, to, steps;

	public PathSegment(int _x, int _y, int _z, int _from, int _to, int _steps) {
		x = _x;
		y = _y;
		z = _z;
		from = _from;
		to = _to;
		steps = _steps;
	}

	public PathSegment(int[] ai) {
		this(ai[0], ai[1], ai[2], ai[3], ai[4], ai[5]);
	}

	public PathSegment(FriendlyByteBuf buf) {
		x = buf.readVarInt();
		y = buf.readVarInt();
		z = buf.readVarInt();
		from = buf.readByte();
		to = buf.readByte();
		steps = buf.readVarInt();
	}

	public int[] toIntArray() {
		return new int[]{x, y, z, from, to, steps};
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(x);
		buf.writeVarInt(y);
		buf.writeVarInt(z);
		buf.writeByte(from);
		buf.writeByte(to);
		buf.writeVarInt(steps);
	}

	@Override
	public String toString() {
		return "PathSegment{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				", from=" + ModularPipesUtils.DIRECTIONS[from].getSerializedName() +
				", to=" + ModularPipesUtils.DIRECTIONS[to].getSerializedName() +
				", steps=" + steps +
				'}';
	}

	public void translate(int pos, double[] position) {
		if (pos < steps * 2) {
			double p = pos / (steps * 2D);
			double r = 0.5D - p * 0.5D;
			position[0] = x + 0.5D + ModularPipesUtils.POS_X[from] * r;
			position[1] = y + 0.5D + ModularPipesUtils.POS_Y[from] * r;
			position[2] = z + 0.5D + ModularPipesUtils.POS_Z[from] * r;
			position[3] = ModularPipesUtils.ROT_X[ModularPipesUtils.OPPOSITE[from]];
			position[4] = ModularPipesUtils.ROT_Y[ModularPipesUtils.OPPOSITE[from]];
		} else {
			double p = (pos - steps * 2) / (steps * 2D);
			double r = (p * 0.5D);
			position[0] = x + 0.5D + ModularPipesUtils.POS_X[to] * r;
			position[1] = y + 0.5D + ModularPipesUtils.POS_Y[to] * r;
			position[2] = z + 0.5D + ModularPipesUtils.POS_Z[to] * r;
			position[3] = ModularPipesUtils.ROT_X[to];
			position[4] = ModularPipesUtils.ROT_Y[to];
		}
	}

	public boolean contains(BlockPos pos) {
		return x == pos.getX() && y == pos.getY() && z == pos.getZ();
	}

	public PathSegment chain(int newTo) {
		return new PathSegment(x + ModularPipesUtils.POS_X[to], y + ModularPipesUtils.POS_Y[to], z + ModularPipesUtils.POS_Z[to], ModularPipesUtils.OPPOSITE[to], newTo, steps);
	}
}
