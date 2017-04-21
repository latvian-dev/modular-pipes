package com.latmod.modularpipes.api_impl;

import com.latmod.modularpipes.api.ILink;
import com.latmod.modularpipes.api.INode;
import com.latmod.modularpipes.api.IPipeNetwork;
import com.latmod.modularpipes.util.MathUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public final class Link implements ILink
{
    private final IPipeNetwork network;
    private final UUID uuid;
    private INode start, end;
    private List<BlockPos> path;
    private float length;
    private int actualLength;

    public Link(IPipeNetwork n, UUID id)
    {
        network = n;
        uuid = id;
    }

    public Link(IPipeNetwork n, NBTTagCompound nbt)
    {
        network = n;
        uuid = nbt.getUniqueId("ID");
        path = new ArrayList<>();
        NBTTagList list = nbt.getTagList("Link", Constants.NBT.TAG_INT_ARRAY);

        for(int i = 0; i < list.tagCount(); i++)
        {
            int[] pos = list.getIntArrayAt(i);

            if(pos.length >= 3)
            {
                path.add(new BlockPos(pos[0], pos[1], pos[2]));
            }
        }

        actualLength = nbt.getInteger("ActualLength");
        length = nbt.getFloat("Length");
    }

    public Link(IPipeNetwork n, ByteBuf buf)
    {
        network = n;
        long msb = buf.readLong();
        long lsb = buf.readLong();
        uuid = new UUID(msb, lsb);
        int s = buf.readUnsignedShort();
        path = new ArrayList<>(s);
        while(--s >= 0)
        {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            path.add(new BlockPos(x, y, z));
        }
        actualLength = buf.readInt();
        length = buf.readFloat();
    }

    public static void writeToBuf(ByteBuf buf, ILink link)
    {
        buf.writeLong(link.getId().getMostSignificantBits());
        buf.writeLong(link.getId().getLeastSignificantBits());
        buf.writeShort(link.getPath().size());
        for(BlockPos pos : link.getPath())
        {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
        }
        buf.writeInt(link.getActualLength());
        buf.writeFloat(link.getLength());
    }

    @Override
    public void simplify()
    {
        if(path.size() <= 2)
        {
            return;
        }

        List<BlockPos> newPath = new ArrayList<>();
        BlockPos prevPos = path.get(0);
        BlockLog.EnumAxis axis = MathUtils.getAxis(prevPos, path.get(1));

        for(BlockPos pos : path)
        {
            BlockLog.EnumAxis a = MathUtils.getAxis(prevPos, pos);

            if(axis != a)
            {
                axis = a;

                if(!newPath.contains(prevPos))
                {
                    newPath.add(prevPos);
                }
            }

            prevPos = pos;
        }

        prevPos = path.get(path.size() - 1);

        if(!newPath.contains(prevPos))
        {
            newPath.add(prevPos);
        }

        path = newPath;
    }

    public Link copyForItem(INode start, INode end, BlockPos pathStart)
    {
        List<BlockPos> list = new ArrayList<>();
        list.add(start.getPos());
        //FIXME
        list.add(end.getPos());
        Link link = new Link(network, uuid);
        link.setStart(start);
        link.setEnd(end);
        link.setPath(list);
        link.setActualLength(actualLength + 2);
        link.setLength(length + 2F);
        return link;
    }

    @Override
    public boolean contains(BlockPos pos)
    {
        for(int i = 0; i < path.size() - 1; i++)
        {
            if(MathUtils.isPosBetween(pos, path.get(i), path.get(i + 1)))
            {
                return true;
            }
        }

        return false;
    }

    public int hashCode()
    {
        return uuid.hashCode();
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder(uuid.toString());
        builder.append(":[");

        for(int i = 0; i < path.size(); i++)
        {
            BlockPos pos = path.get(i);
            builder.append('[');
            builder.append(pos.getX());
            builder.append(',');
            builder.append(pos.getY());
            builder.append(',');
            builder.append(pos.getZ());
            builder.append(']');
            if(i != path.size() - 1)
            {
                builder.append(',');
            }
        }

        builder.append(']');
        return builder.toString();
    }

    public boolean equals(Object o)
    {
        return o == this || (o instanceof Link && ((Link) o).uuid.equals(uuid));
    }

    @Override
    public IPipeNetwork getNetwork()
    {
        return network;
    }

    @Override
    public UUID getId()
    {
        return uuid;
    }

    @Override
    public INode getStart()
    {
        return start;
    }

    public void setStart(INode node)
    {
        start = node;
    }

    @Override
    public INode getEnd()
    {
        return end;
    }

    public void setEnd(INode node)
    {
        end = node;
    }

    @Override
    public List<BlockPos> getPath()
    {
        return path;
    }

    public void setPath(List<BlockPos> p)
    {
        path = p;
    }

    @Override
    public int getActualLength()
    {
        return actualLength;
    }

    public void setActualLength(int l)
    {
        actualLength = l;
    }

    @Override
    public float getLength()
    {
        return length;
    }

    public void setLength(float l)
    {
        length = l;
    }
}