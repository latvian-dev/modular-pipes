package com.latmod.modularpipes.data;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.client.ClientTransportedItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TransportedItem implements ITickable, INBTSerializable<NBTTagCompound>
{
	public static final DataOut.Serializer<TransportedItem> SERIALIZER = (data, item) ->
	{
		data.writeInt(item.id);
		data.writeByte(item.action.ordinal());

		if (!item.action.remove())
		{
			data.writeItemStack(item.stack);
			data.writePos(item.start);
			data.writePos(item.pos);
			byte[] b = PathPoint.toArray(item.path);
			data.writeByte(b.length / 2);
			data.writeBytes(b);
			data.writeShort(item.filters);
			data.writeDouble(item.progress);
			data.writeDouble(item.speedModifier);
		}
	};

	public static final DataIn.Deserializer<TransportedItem> DESERIALIZER = data ->
	{
		TransportedItem item = new TransportedItem(null);

		item.id = data.readInt();
		item.action = TransportedItem.Action.VALUES[data.readUnsignedByte()];

		if (!item.action.remove())
		{
			item.stack = data.readItemStack();
			item.start = data.readPos();
			item.pos = data.readMutablePos();
			byte[] b = new byte[data.readUnsignedByte() * 2];
			data.readBytes(b);
			PathPoint.fromArray(item.path, b);
			item.filters = data.readUnsignedShort();
			item.progress = data.readDouble();
			item.speedModifier = data.readDouble();
		}

		return item;
	};

	public enum Action
	{
		NONE,
		REMOVE,
		DROP,
		UPDATE;

		public static final Action[] VALUES = values();

		public boolean remove()
		{
			return this == REMOVE || this == DROP;
		}

		public boolean update()
		{
			return this == UPDATE || remove();
		}
	}

	public static class PathPoint
	{
		public final EnumFacing facing;
		public final int length;

		public PathPoint(EnumFacing f, int l)
		{
			facing = f;
			length = l;
		}

		public int hashCode()
		{
			return facing.getIndex() * 31 + length;
		}

		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			else if (o instanceof PathPoint)
			{
				PathPoint p = (PathPoint) o;
				return p.facing == facing && p.length == length;
			}
			return false;
		}

		public String toString()
		{
			return "[" + facing + ':' + length + ']';
		}

		public static void fromArray(List<PathPoint> path, byte[] arr)
		{
			path.clear();
			for (int i = 0; i < arr.length; i += 2)
			{
				path.add(new PathPoint(EnumFacing.VALUES[arr[i]], arr[i + 1] & 0xFF));
			}
		}

		public static byte[] toArray(List<PathPoint> path)
		{
			byte[] b = new byte[path.size() * 2];
			for (int i = 0; i < path.size(); i++)
			{
				PathPoint p = path.get(i);
				b[i * 2] = (byte) p.facing.getIndex();
				b[i * 2 + 1] = (byte) p.length;
			}
			return b;
		}
	}

	public final PipeNetwork network;
	public int id;
	public BlockPos start;
	public BlockPos.MutableBlockPos pos;
	public List<PathPoint> path;
	public ItemStack stack = ItemStack.EMPTY;
	public int filters = 0;
	public double progress = 0D;
	public Action action = Action.NONE;
	public double speedModifier = 1D;
	public double prevX, prevY, prevZ;
	public double posX, posY, posZ;

	public TransportedItem(PipeNetwork n)
	{
		network = n;
		path = new ArrayList<>();
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Item", stack.serializeNBT());

		if (start != null)
		{
			nbt.setIntArray("Start", new int[] {start.getX(), start.getY(), start.getZ()});
		}

		if (pos != null)
		{
			nbt.setIntArray("Pos", new int[] {pos.getX(), pos.getY(), pos.getZ()});
		}

		nbt.setByteArray("Path", PathPoint.toArray(path));
		nbt.setInteger("Filters", filters);
		nbt.setDouble("Progress", progress);
		nbt.setDouble("Speed", speedModifier);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		action = TransportedItem.Action.NONE;
		stack = new ItemStack(nbt.getCompoundTag("Item"));
		int[] ai = nbt.getIntArray("Start");
		start = (ai.length >= 3) ? new BlockPos(ai[0], ai[1], ai[2]) : null;
		ai = nbt.getIntArray("Pos");
		pos = (ai.length >= 3) ? new BlockPos.MutableBlockPos(ai[0], ai[1], ai[2]) : null;
		PathPoint.fromArray(path, nbt.getByteArray("Path"));
		filters = nbt.getInteger("Filters");
		progress = nbt.getDouble("Progress");
		speedModifier = nbt.hasKey("Speed") ? nbt.getDouble("Speed") : 1D;
	}

	public ClientTransportedItem client()
	{
		throw new IllegalStateException();
	}

	public void addToNetwork()
	{
		network.addItem(this);
	}

	public void setPath(List<BlockPos> l)
	{
		path.clear();

		if (l.isEmpty())
		{
			return;
		}

		Collection<BlockPos> set = new HashSet<>();
		start = new BlockPos(l.get(0));
		pos = new BlockPos.MutableBlockPos(start);

		for (BlockPos pos1 : l)
		{
			if (set.contains(pos1))
			{
				continue;
			}

			EnumFacing facing = MathUtils.getFacing(pos, pos1);
			int dist = (int) MathUtils.sqrt(pos.distanceSq(pos1));

			if (facing != null && dist > 0)
			{
				path.add(new PathPoint(facing, dist));
				pos.setPos(pos1);
				set.add(pos1);
			}
		}

		pos.setPos(start);
	}

	@Override
	public void update()
	{
		updatePrevData();
		if (!updatePosition())
		{
			return;
		}

		if (path.isEmpty())
		{
			action = Action.DROP;
			return;
		}

		BlockPos pos = new BlockPos(posX, posY, posZ);

		IBlockState state = network.world.getBlockState(pos);

		if (state.getBlock().isAir(state, network.world, pos))
		{
			action = Action.DROP;
		}
		/*
		else if(state.getBlock() instanceof IPipeBlock)
        {
            IPipeBlock pipe = (IPipeBlock) state.getBlock();
            s *= pipe.getItemSpeedModifier(network.world, pos, state, this);

            if(pipe.superBoost(network.world, pos, state))
            {
                boost = true;
            }
        }

        if(boost)
        {
            s *= ModularPipesConfig.SUPER_BOOST.getAsDouble();
        }
        */

		progress += speedModifier * ModularPipesConfig.pipes.base_speed;
	}

	public void updatePrevData()
	{
		prevX = posX;
		prevY = posY;
		prevZ = posZ;
	}

	public boolean updatePosition()
	{
		if (remove())
		{
			return false;
		}

		PathPoint p = path.get(0);
		double pr = Math.min(progress, p.length);
		posX = pos.getX() + p.facing.getFrontOffsetX() * pr + 0.5D;
		posY = pos.getY() + p.facing.getFrontOffsetY() * pr + 0.5D;
		posZ = pos.getZ() + p.facing.getFrontOffsetZ() * pr + 0.5D;

		if (progress > p.length)
		{
			progress -= p.length;
			pos.move(p.facing, p.length);
			path.remove(0);
		}

		return true;
	}

	public void copyFrom(TransportedItem item)
	{
		action = item.action;

		if (action.remove())
		{
			return;
		}

		start = new BlockPos(item.start);
		pos = new BlockPos.MutableBlockPos(item.pos);
		path = new ArrayList<>(item.path);
		stack = item.stack.copy();
		filters = item.filters;
		progress = item.progress;
		speedModifier = item.speedModifier;

		updatePosition();
		updatePrevData();
	}

	public void postUpdate()
	{
		action = Action.NONE;
	}

	public boolean remove()
	{
		return path.isEmpty() || start == null || pos == null || action.remove() || stack.isEmpty();
	}

	public void setRemoved()
	{
		action = Action.REMOVE;
	}

	public boolean generatePath(ModuleContainer container)
	{
		Node node = network.getNode(container.tile.getPos());
		if (node == null || node.linkedWith.isEmpty())
		{
			return false;
		}

		//TODO: Fix the path finding
		return false;
	}
}