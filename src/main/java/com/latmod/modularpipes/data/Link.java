package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.math.MathUtils;
import net.minecraft.block.BlockLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author LatvianModder
 */
public final class Link
{
    public static final Comparator<Link> COMPARATOR = Comparator.comparingInt(link -> link.actualLength);

    public static List<BlockPos> simplify(List<BlockPos> path)
    {
        if(path.size() <= 2)
        {
            return path;
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

        return newPath;
    }

    public final PipeNetwork network;
    public final List<BlockPos> path;
    public final Node start, end;
    public final int actualLength;

    public Link(PipeNetwork n, List<BlockPos> p, int a)
    {
        network = n;
        path = p;
        start = path.size() >= 2 ? network.getNode(path.get(0)) : null;
        end = start != null ? network.getNode(path.get(path.size() - 1)) : null;
        actualLength = a;
    }

    public Link(PipeNetwork n, NBTTagCompound nbt)
    {
        network = n;
        path = new ArrayList<>();
        int[] ai = nbt.getIntArray("Path");

        for(int i = 0; i < ai.length; i += 3)
        {
            path.add(new BlockPos(ai[i], ai[i + 1], ai[i + 2]));
        }

        if(path.size() >= 2)
        {
            start = network.getNode(path.get(0));
            end = network.getNode(path.get(path.size() - 1));

            if(start != null && end != null && !start.equals(end))
            {
                start.linkedWith.add(this);
                end.linkedWith.add(this);
            }
            else
            {
                path.clear();
            }
        }
        else
        {
            start = end = null;
        }

        actualLength = nbt.hasKey("ActualLength") ? nbt.getInteger("ActualLength") : nbt.getInteger("Length");
    }

    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        int[] ai = new int[path.size() * 3];

        for(int i = 0; i < path.size(); i++)
        {
            BlockPos pos = path.get(i);
            ai[i * 3] = pos.getX();
            ai[i * 3 + 1] = pos.getY();
            ai[i * 3 + 2] = pos.getZ();
        }

        nbt.setIntArray("Path", ai);
        nbt.setInteger("Length", actualLength);
        return nbt;
    }

    public boolean invalid()
    {
        return start == null || end == null || path.size() < 2;
    }

    public boolean isEndpoint(BlockPos pos)
    {
        return !invalid() && (start == pos || end == pos || start.equals(pos) || end.equals(pos));
    }

    public boolean contains(BlockPos pos)
    {
        if(invalid())
        {
            return false;
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

    public int hashCode()
    {
        return start.hashCode() ^ end.hashCode();
    }

    public boolean equals(Object o)
    {
        if(o == this)
        {
            return true;
        }
        else if(o instanceof Link)
        {
            Link l = (Link) o;
            if(isEndpoint(l.start) && isEndpoint(l.end))
            {
                for(BlockPos pos : l.path)
                {
                    if(!contains(pos))
                    {
                        return false;
                    }
                }

                return true;
            }
        }
        return false;
    }

    public String toString()
    {
        return "[#" + Integer.toHexString(path.hashCode()) + ' ' + start + "->" + end + ']';
    }
}