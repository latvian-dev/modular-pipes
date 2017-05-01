package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.math.MathUtils;
import com.feed_the_beast.ftbl.lib.util.NetUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public final class Link
{
    public static final Comparator<Link> COMPARATOR = Comparator.comparing(link -> link.length);

    public final PipeNetwork network;
    public final UUID uuid;
    public Node start, end;
    public List<BlockPos> path;
    public float length;
    public int actualLength;

    public Link(PipeNetwork n, UUID id)
    {
        network = n;
        uuid = id;
    }

    public Link(PipeNetwork n, NBTTagCompound nbt)
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

    public Link(PipeNetwork n, ByteBuf buf)
    {
        network = n;
        uuid = NetUtils.readUUID(buf);
        int s = buf.readUnsignedShort();
        path = new ArrayList<>(s);
        while(--s >= 0)
        {
            path.add(NetUtils.readPos(buf));
        }
        actualLength = buf.readInt();
        length = buf.readFloat();
    }

    public static void writeToBuf(ByteBuf buf, Link link)
    {
        NetUtils.writeUUID(buf, link.uuid);
        buf.writeShort(link.path.size());
        for(BlockPos pos : link.path)
        {
            NetUtils.writePos(buf, pos);
        }
        buf.writeInt(link.actualLength);
        buf.writeFloat(link.length);
    }

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

    public Link copyForItem(Node start, Node end, BlockPos pathStart)
    {
        List<BlockPos> list = new ArrayList<>();
        list.add(start.pos);
        //FIXME
        list.add(end.pos);
        Link link = new Link(network, uuid);
        link.start = start;
        link.end = end;
        link.path = list;
        link.actualLength = actualLength + 2;
        link.length = length + 2F;
        return link;
    }

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
}