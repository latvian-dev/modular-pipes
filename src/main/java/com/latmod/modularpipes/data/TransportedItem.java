package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbl.lib.util.InvUtils;
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
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class TransportedItem implements ITickable, INBTSerializable<NBTTagCompound>
{
    public enum Action
    {
        NONE,
        REMOVE,
        UPDATE,
        HIDE;

        public static final Action[] VALUES = values();

        public boolean remove()
        {
            return this == REMOVE;
        }

        public boolean update()
        {
            return this == UPDATE || this == REMOVE;
        }

        public boolean invisible()
        {
            return this == HIDE || this == REMOVE;
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

        public PathPoint(ByteBuf buf)
        {
            facing = EnumFacing.VALUES[buf.readUnsignedByte()];
            length = buf.readUnsignedByte();
        }

        public void writeToByteBuf(ByteBuf buf)
        {
            buf.writeByte(facing.getIndex());
            buf.writeByte(length);
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

    public static final Predicate<TransportedItem> REMOVE_PREDICATE = TransportedItem::remove;
    public static final Consumer<TransportedItem> FOREACH_POST_UPDATE = TransportedItem::postUpdate;

    public final PipeNetwork network;
    public int id;
    public BlockPos start;
    public BlockPos.MutableBlockPos pos;
    public List<PathPoint> path;
    public ItemStack stack = ItemStack.EMPTY;
    public int filters = 0;
    public double progress = 0D;
    public Action action = Action.NONE;
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
        }
    }

    public void addToNetwork()
    {
        network.addItem(this);
    }

    public void resetPath(Vec3i v)
    {
        start = new BlockPos(v);
        pos = new BlockPos.MutableBlockPos(start);
        path.clear();
    }

    public void addPath(Link link)
    {
        resetPath(link.start);

        for(BlockPos pos : link.path)
        {
            if(!link.isEndpoint(pos))
            {
                addPath(pos);
            }
        }

        addPath(link.end);
    }

    public void addPath(BlockPos pos1)
    {
        path.add(new PathPoint(MathUtils.getFacing(pos, pos1), (int) MathUtils.sqrt(pos.distanceSq(pos1))));
        pos.setPos(pos1);
    }

    public void setPath()
    {
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

        /*
        if(prevX != posX || prevY != posY || prevZ != posZ)
        {
            BlockPos oldPos = new BlockPos(prevX, prevY, prevZ);
            //check for block change
        }
        */

        BlockPos pos = new BlockPos(posX, posY, posZ);
        double s = ModularPipesConfig.ITEM_BASE_SPEED.getAsDouble();
        IBlockState state = network.world.getBlockState(pos);

        if(state.getBlock().isAir(state, network.world, pos))
        {
            action = Action.REMOVE;

            if(!network.world.isRemote)
            {
                InvUtils.dropItem(network.world, pos, stack, 12);
            }
        }
        else if(state.getBlock() instanceof IPipeBlock)
        {
            s *= ((IPipeBlock) state.getBlock()).getSpeedModifier(network.world, pos, state);
        }

        progress += s;

        /*
        if(!network.world.isRemote)
        {
            network.world.spawnEntity(new EntityFireworkRocket(network.world, posX, posY, posZ, new ItemStack(Items.FIREWORKS)));
        }
        */
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
        posX = pos.getX() + p.facing.getFrontOffsetX() * progress + 0.5D;
        posY = pos.getY() + p.facing.getFrontOffsetY() * progress + 0.5D;
        posZ = pos.getZ() + p.facing.getFrontOffsetZ() * progress + 0.5D;

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

    public ClientTransportedItem client()
    {
        throw new IllegalStateException();
    }
}