package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.math.MathUtils;
import net.minecraft.block.BlockLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public final class Link implements INBTSerializable<NBTTagCompound>
{
    public static final Comparator<Link> COMPARATOR = Comparator.<Link>comparingDouble(link -> link.length).thenComparingInt(value -> value.actualLength);

    public static class PosPredicate implements Predicate<Link>
    {
        private final BlockPos pos;
        private final boolean contains;
        private final boolean endpoint;

        public PosPredicate(BlockPos p, boolean c, boolean e)
        {
            pos = p;
            contains = c;
            endpoint = e;
        }

        @Override
        public boolean test(Link link)
        {
            return link.invalid() || contains == (endpoint ? link.isEndpoint(pos) : link.contains(pos));
        }
    }

    public final PipeNetwork network;
    public Node start, end;
    public List<BlockPos> path;
    public double length;
    public int actualLength;

    public Link(PipeNetwork n)
    {
        network = n;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList list1 = new NBTTagList();

        for(BlockPos pos : path)
        {
            list1.appendTag(new NBTTagIntArray(new int[] {pos.getX(), pos.getY(), pos.getZ()}));
        }

        nbt.setTag("Link", list1);
        nbt.setDouble("Length", length);
        nbt.setInteger("ActualLength", actualLength);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
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

        setPath(path, false);
        length = nbt.getDouble("Length");
        actualLength = nbt.getInteger("ActualLength");
    }

    public void setPath(List<BlockPos> p, boolean copy)
    {
        start = end = null;

        if(copy)
        {
            if(path == null)
            {
                path = new ArrayList<>();
            }

            path.clear();
        }

        if(p.size() >= 2)
        {
            if(copy)
            {
                path.addAll(p);
            }
            else
            {
                path = p;
            }

            start = network.getNode(path.get(0));
            end = network.getNode(path.get(path.size() - 1));

            if(start == null || end == null || start.equals(end))
            {
                start = null;
                end = null;
                path.clear();
            }
        }
    }

    public boolean invalid()
    {
        return start == null || end == null;
    }

    public void simplify()
    {
        if(invalid())
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

        setPath(newPath, false);
    }

    public boolean isEndpoint(BlockPos pos)
    {
        return !invalid() && (start.equals(pos) || end.equals(pos));
    }

    public boolean contains(BlockPos pos)
    {
        if(invalid())
        {
            return false;
        }
        else if(isEndpoint(pos))
        {
            return true;
        }

        for(int i = 0; i < path.size() - 1; i++)
        {
            if(MathUtils.isPosBetween(pos, path.get(i), path.get(i + 1)))
            {
                return true;
            }
        }

        return false;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder("[");

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
}