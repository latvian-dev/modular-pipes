package com.latmod.modularpipes.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
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
        UPDATE,
        HIDE;

        public static final Action[] VALUES = values();
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

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Item", stack.serializeNBT());
        nbt.setInteger("Dim", dimension);

        NBTTagList pathTag = new NBTTagList();

        for(BlockPos p : path)
        {
            pathTag.appendTag(new NBTTagIntArray(new int[] {p.getX(), p.getY(), p.getZ()}));
        }

        nbt.setTag("Path", pathTag);
        nbt.setInteger("Filters", filters);
        nbt.setFloat("Speed", speed);
        nbt.setFloat("Progress", progress);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        action = Action.NONE;
        stack = new ItemStack(nbt.getCompoundTag("Item"));
        dimension = nbt.getInteger("Dim");

        path.clear();
        NBTTagList pathTag = nbt.getTagList("Path", Constants.NBT.TAG_INT_ARRAY);
        for(int i = 0; i < pathTag.tagCount(); i++)
        {
            int pos[] = pathTag.getIntArrayAt(i);

            if(pos.length >= 3)
            {
                path.add(new BlockPos(pos[0], pos[1], pos[2]));
            }
        }

        filters = nbt.getInteger("Filters");
        speed = nbt.getFloat("Speed");
        progress = nbt.getFloat("Progress");

        if(stack.getCount() == 0)
        {
            action = Action.REMOVE;
        }
    }

    public void writeToByteBuf(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeByte(action.ordinal());

        if(action == Action.REMOVE)
        {
            return;
        }

        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(dimension);
        buf.writeInt(path.size());

        for(BlockPos p : path)
        {
            buf.writeInt(p.getX());
            buf.writeInt(p.getY());
            buf.writeInt(p.getZ());
        }

        buf.writeShort(filters);
        buf.writeFloat(speed);
        buf.writeFloat(progress);
        buf.writeFloat(prevProgress);
    }

    public void readFromByteBuf(ByteBuf buf)
    {
        id = buf.readInt();
        action = Action.VALUES[buf.readUnsignedByte()];

        if(action == Action.REMOVE)
        {
            return;
        }

        stack = ByteBufUtils.readItemStack(buf);
        dimension = buf.readInt();
        path.clear();
        int s = buf.readInt();

        while(--s >= 0)
        {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            path.add(new BlockPos(x, y, z));
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
}