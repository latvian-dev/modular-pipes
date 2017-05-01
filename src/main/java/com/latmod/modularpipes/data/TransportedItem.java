package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.util.NetUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class TransportedItem implements ITickable
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
    }

    public int id;
    public int dimension;
    public final List<BlockPos> path = new ArrayList<>();
    public ItemStack stack = ItemStack.EMPTY;
    public int filters = 0;
    public float speed = 0F, progress = 0F, prevProgress = 0F;
    public Action action = Action.NONE;

    public void update()
    {
        action = Action.NONE;
        prevProgress = progress;
        progress += speed;
    }

    public void readFromByteBuf(ByteBuf buf)
    {
        id = buf.readInt();
        action = TransportedItem.Action.VALUES[buf.readUnsignedByte()];

        if(action.remove())
        {
            return;
        }

        stack = ByteBufUtils.readItemStack(buf);
        dimension = buf.readInt();
        path.clear();
        int s1 = buf.readUnsignedShort();

        while(--s1 >= 0)
        {
            path.add(NetUtils.readPos(buf));
        }

        filters = buf.readUnsignedShort();
        speed = buf.readFloat();
        progress = buf.readFloat();
        prevProgress = buf.readFloat();
    }

    public void copyFrom(TransportedItem item)
    {
        dimension = item.dimension;
        path.clear();
        path.addAll(item.path);
        stack = item.stack.copy();
        filters = item.filters;
        speed = item.speed;
        progress = item.progress;
        prevProgress = item.prevProgress;
        action = item.action;
    }

    public void writeToByteBuf(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeByte(action.ordinal());

        if(action.remove())
        {
            return;
        }

        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(dimension);
        buf.writeShort(path.size());

        for(BlockPos p : path)
        {
            NetUtils.writePos(buf, p);
        }

        buf.writeShort(filters);
        buf.writeFloat(speed);
        buf.writeFloat(progress);
        buf.writeFloat(prevProgress);
    }
}