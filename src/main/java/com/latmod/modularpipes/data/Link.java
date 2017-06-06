package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.math.MathUtils;
import net.minecraft.block.BlockLog;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public final class Link
{
    public static final Comparator<Link> COMPARATOR = Comparator.comparingInt(link -> link.length);

    public static List<BlockPos> simplify(Collection<BlockPos> path)
    {
        if(path.size() < 2)
        {
            return Collections.emptyList();
        }
        else if(path.size() == 2)
        {
            return new ArrayList<>(path);
        }

        Collection<BlockPos> newPath = new LinkedHashSet<>();
        BlockPos prevPos = null;
        BlockLog.EnumAxis axis = null;

        for(BlockPos pos : path)
        {
            if(prevPos == null)
            {
                prevPos = pos;
            }

            BlockLog.EnumAxis a = MathUtils.getAxis(prevPos, pos);

            if(axis != a)
            {
                axis = a;
                newPath.add(prevPos);
            }

            prevPos = pos;
        }

        newPath.add(prevPos);
        return new ArrayList<>(newPath);
    }

    public final PipeNetwork network;
    public final List<BlockPos> path;
    public final Node start, end;
    public final int length;
    private boolean invalid = false;

    public Link(PipeNetwork n, List<BlockPos> p, Node s, Node e, int l)
    {
        network = n;
        path = p;
        start = s;
        end = e;
        length = l;
    }

    public boolean invalid()
    {
        return invalid;
    }

    public void setInvalid()
    {
        invalid = true;
    }

    public boolean contains(BlockPos pos, boolean endpointOnly)
    {
        if(invalid())
        {
            return false;
        }
        else if(start == pos || end == pos || start.equals(pos) || end.equals(pos))
        {
            return true;
        }
        else if(endpointOnly)
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
            if(contains(l.start, true) && contains(l.end, true))
            {
                for(BlockPos pos : l.path)
                {
                    if(!contains(pos, false))
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
        return "[#" + Integer.toHexString(path.hashCode()) + ' ' + length + 'x' + start + "->" + end + ']';
    }
}