package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.client.ClientTransportedItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TransportedItem implements ITickable, INBTSerializable<NBTTagCompound>
{
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
            if(o == this)
            {
                return true;
            }
            else if(o instanceof PathPoint)
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
            for(int i = 0; i < arr.length; i += 2)
            {
                path.add(new PathPoint(EnumFacing.VALUES[arr[i]], arr[i + 1] & 0xFF));
            }
        }

        public static byte[] toArray(List<PathPoint> path)
        {
            byte[] b = new byte[path.size() * 2];
            for(int i = 0; i < path.size(); i++)
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
    public boolean boost = false;
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

        if(start != null)
        {
            nbt.setIntArray("Start", new int[] {start.getX(), start.getY(), start.getZ()});
        }

        if(pos != null)
        {
            nbt.setIntArray("Pos", new int[] {pos.getX(), pos.getY(), pos.getZ()});
        }

        nbt.setByteArray("Path", PathPoint.toArray(path));
        nbt.setInteger("Filters", filters);
        nbt.setDouble("Progress", progress);
        if(boost)
        {
            nbt.setBoolean("Boost", true);
        }
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
        boost = nbt.getBoolean("Boost");
    }

    public void writeToByteBuf(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeByte(action.ordinal());

        if(!action.remove())
        {
            ByteBufUtils.writeItemStack(buf, stack);
            NetUtils.writePos(buf, start);
            NetUtils.writePos(buf, pos);
            byte[] b = PathPoint.toArray(path);
            buf.writeByte(b.length / 2);
            buf.writeBytes(b);
            buf.writeShort(filters);
            buf.writeDouble(progress);
            buf.writeBoolean(boost);
        }
    }

    public void readFromByteBuf(ByteBuf buf)
    {
        id = buf.readInt();
        action = TransportedItem.Action.VALUES[buf.readUnsignedByte()];

        if(!action.remove())
        {
            stack = ByteBufUtils.readItemStack(buf);
            start = NetUtils.readPos(buf);
            pos = NetUtils.readMutablePos(buf);
            byte[] b = new byte[buf.readUnsignedByte() * 2];
            buf.readBytes(b);
            PathPoint.fromArray(path, b);
            filters = buf.readUnsignedShort();
            progress = buf.readDouble();
            boost = buf.readBoolean();
        }
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

        if(l.isEmpty())
        {
            return;
        }

        Collection<BlockPos> set = new HashSet<>();
        start = new BlockPos(l.get(0));
        pos = new BlockPos.MutableBlockPos(start);

        for(BlockPos pos1 : l)
        {
            if(set.contains(pos1))
            {
                continue;
            }

            EnumFacing facing = MathUtils.getFacing(pos, pos1);
            int dist = (int) MathUtils.sqrt(pos.distanceSq(pos1));

            if(facing != null && dist > 0)
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
        if(!updatePosition())
        {
            return;
        }

        if(path.isEmpty())
        {
            action = Action.DROP;
            return;
        }

        BlockPos pos = new BlockPos(posX, posY, posZ);

        double s = ModularPipesConfig.ITEM_BASE_SPEED.getAsDouble();
        IBlockState state = network.world.getBlockState(pos);

        if(state.getBlock().isAir(state, network.world, pos))
        {
            action = Action.DROP;
        }
        else if(state.getBlock() instanceof IPipeBlock)
        {
            IPipeBlock pipe = (IPipeBlock) state.getBlock();
            s *= pipe.getSpeedModifier(network.world, pos, state);

            if(pipe.superBoost(network.world, pos, state))
            {
                boost = true;
            }
        }

        if(boost)
        {
            s *= ModularPipesConfig.SUPER_BOOST.getAsDouble();
        }

        progress += s;
    }

    public void updatePrevData()
    {
        prevX = posX;
        prevY = posY;
        prevZ = posZ;
    }

    public boolean updatePosition()
    {
        if(remove())
        {
            return false;
        }

        PathPoint p = path.get(0);
        double pr = Math.min(progress, p.length);
        posX = pos.getX() + p.facing.getFrontOffsetX() * pr + 0.5D;
        posY = pos.getY() + p.facing.getFrontOffsetY() * pr + 0.5D;
        posZ = pos.getZ() + p.facing.getFrontOffsetZ() * pr + 0.5D;

        if(progress > p.length)
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

        if(action.remove())
        {
            return;
        }

        start = new BlockPos(item.start);
        pos = new BlockPos.MutableBlockPos(item.pos);
        path = new ArrayList<>(item.path);
        stack = item.stack.copy();
        filters = item.filters;
        progress = item.progress;
        boost = item.boost;

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
        if(node == null || node.linkedWith.isEmpty())
        {
            return false;
        }

        List<BlockPos> list = new ArrayList<>();

        for(Link link : node.linkedWith)
        {
            if(link.invalid())
            {
                continue;
            }
            
            /*for(BlockPos pos : link.path)
            {
                //PipeNetwork.test(container.getTile().getWorld(), pos);
            }*/

            list.add(node.offset(container.facing));

            if(link.start.equals(node))
            {
                list.addAll(link.path);
            }
            else
            {
                for(int i = link.path.size() - 1; i >= 0; i--)
                {
                    list.add(link.path.get(i));
                }
            }

            break;
        }

        if(list.size() >= 2)
        {
            setPath(list);
            return true;
        }

        return false;
    }
}