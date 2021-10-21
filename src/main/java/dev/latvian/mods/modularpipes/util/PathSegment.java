package dev.latvian.mods.modularpipes.util;

import dev.latvian.mods.modularpipes.block.entity.PipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.TransportPipeBlockEntity;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PathSegment {
	public int x, y, z, steps, from, to;
	public PathSegment next;

	public PathSegment() {
	}

	public PathSegment(int[] ai) {
		x = ai[0];
		y = ai[1];
		z = ai[2];
		steps = ai[3];
		from = ai[4];
		to = ai[5];

		PathSegment s = this;

		for (int i = 6; i < ai.length; i += 2) {
			s.next = s.chain(ai[i], ai[i + 1]);
			s = s.next;
		}
	}

	public int[] toIntArray() {
		IntArrayList list = new IntArrayList();
		list.add(x);
		list.add(y);
		list.add(z);
		list.add(steps);
		list.add(from);
		list.add(to);
		PathSegment n = next;

		while (n != null) {
			list.add(n.steps);
			list.add(n.to);
			n = n.next;
		}

		return list.toIntArray();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Path{");
		sb.append(x);
		sb.append(",");
		sb.append(y);
		sb.append(",");
		sb.append(z);
		sb.append(",");
		sb.append(ModularPipesUtils.DIR_CHAR[from]);
		sb.append("=>");
		sb.append(steps);
		sb.append(ModularPipesUtils.DIR_CHAR[to]);

		PathSegment n = next;

		while (n != null) {
			sb.append('+');
			sb.append(n.steps);
			sb.append(ModularPipesUtils.DIR_CHAR[n.to]);
			n = n.next;
		}

		return sb.toString();
	}

	public boolean translate(int pos, double[] position) {
		return ModularPipesUtils.translate(x + 0.5D, y + 0.5D, z + 0.5D, pos, steps, from, to, position);
	}

	public boolean contains(BlockPos pos) {
		return x == pos.getX() && y == pos.getY() && z == pos.getZ();
	}

	public PathSegment chain(int newSteps, int newTo) {
		PathSegment s = new PathSegment();
		s.x = x + ModularPipesUtils.POS_X[to];
		s.y = y + ModularPipesUtils.POS_Y[to];
		s.z = z + ModularPipesUtils.POS_Z[to];
		s.steps = newSteps;
		s.from = ModularPipesUtils.OPPOSITE[to];
		s.to = newTo;
		return s;
	}

	public void findNextSegment(PipeBlockEntity entity, int ignore, int steps, int limit) {
		if (limit <= 0) {
			return;
		}

		PipeBlockEntity nextEntity = null;
		int nextDir = 6;

		for (int i = 0; i < 6; i++) {
			if (i != ignore) {
				BlockEntity e = entity.getLevel().getBlockEntity(entity.getBlockPos().relative(ModularPipesUtils.DIRECTIONS[i]));

				if (e instanceof TransportPipeBlockEntity && ((TransportPipeBlockEntity) e).isValidSegment(ModularPipesUtils.OPPOSITE[i])) {
					if (nextEntity != null) {
						return;
					}

					nextEntity = (TransportPipeBlockEntity) e;
					nextDir = i;
				}
			}

			//if (i != ModularPipesUtils.OPPOSITE[current.to] && (pipeEntity.sideData[i].connect || pipeEntity.sideData[i].module != null && pipeEntity.sideData[i].module.canExit(item, pipeEntity.sideData[i].connect))) {
			// BlockPos pos = new BlockPos(current.x + ModularPipesUtils.POS_X[i], current.y + ModularPipesUtils.POS_Y[i], current.z + ModularPipesUtils.POS_Z[i]);
			// BlockEntity e = currentEntity.getLevel().getBlockEntity(pos);
			//}
		}

		if (nextEntity != null) {
			next = chain(steps, nextDir);
			next.findNextSegment(nextEntity, ModularPipesUtils.OPPOSITE[nextDir], steps, limit - 1);
		}
	}
}
