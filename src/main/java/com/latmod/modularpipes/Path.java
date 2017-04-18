package com.latmod.modularpipes;

import com.latmod.modularpipes.util.BlockDimPos;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public final class Path
{
    public static final Comparator<Path> COMPARATOR = (p1, p2) -> Float.compare(p2.efficiency, p1.efficiency);

    private static boolean isNumberBetween(int num, int num1, int num2)
    {
        return num == num1 || num == num2 || (num1 <= num && num <= num2) || (num2 <= num && num <= num1);
    }

    public static BlockLog.EnumAxis getAxis(BlockPos pos1, BlockPos pos2)
    {
        if(pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY())
        {
            return BlockLog.EnumAxis.Z;
        }
        else if(pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ())
        {
            return BlockLog.EnumAxis.X;
        }
        else if(pos1.getZ() == pos2.getZ() && pos1.getX() == pos2.getX())
        {
            return BlockLog.EnumAxis.Y;
        }

        return BlockLog.EnumAxis.NONE;
    }

    public static boolean isPosBetween(BlockPos pos, BlockPos pos1, BlockPos pos2)
    {
        switch(getAxis(pos1, pos2))
        {
            case X:
                return isNumberBetween(pos.getX(), pos1.getX(), pos2.getX());
            case Y:
                return isNumberBetween(pos.getY(), pos1.getY(), pos2.getY());
            case Z:
                return isNumberBetween(pos.getZ(), pos1.getZ(), pos2.getZ());
            default:
                return false;
        }
    }

    @Nullable
    public static EnumFacing getFacing(BlockPos pos1, BlockPos pos2)
    {
        if(pos1.getY() > pos2.getY())
        {
            return EnumFacing.DOWN;
        }
        else if(pos1.getY() < pos2.getY())
        {
            return EnumFacing.UP;
        }
        else if(pos1.getZ() > pos2.getZ())
        {
            return EnumFacing.NORTH;
        }
        else if(pos1.getZ() < pos2.getZ())
        {
            return EnumFacing.SOUTH;
        }
        else if(pos1.getX() > pos2.getX())
        {
            return EnumFacing.WEST;
        }
        else if(pos1.getX() < pos2.getX())
        {
            return EnumFacing.EAST;
        }
        return null;
    }

    private final UUID uuid;
    private final int dimension;
    private final List<BlockPos> path;
    private final float efficiency;

    public Path(int dim, List<BlockPos> p, float e)
    {
        uuid = UUID.randomUUID();
        dimension = dim;
        path = p;
        efficiency = e;
    }

    public Path(NBTTagCompound nbt)
    {
        uuid = nbt.getUniqueId("ID");
        dimension = nbt.getInteger("Dim");
        path = new ArrayList<>();
        NBTTagList list = nbt.getTagList("Path", Constants.NBT.TAG_INT_ARRAY);

        for(int i = 0; i < list.tagCount(); i++)
        {
            int[] pos = list.getIntArrayAt(i);

            if(pos.length >= 3)
            {
                path.add(new BlockPos(pos[0], pos[1], pos[2]));
            }
        }

        efficiency = nbt.getFloat("Efficiency");
    }

    public Path(ByteBuf buf)
    {
        long msb = buf.readLong();
        long lsb = buf.readLong();
        uuid = new UUID(msb, lsb);
        dimension = buf.readInt();
        int s = buf.readUnsignedShort();
        path = new ArrayList<>(s);
        while(--s >= 0)
        {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            path.add(new BlockPos(x, y, z));
        }
        efficiency = buf.readFloat();
    }

    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setUniqueId("ID", uuid);
        nbt.setInteger("Dim", dimension);
        NBTTagList list = new NBTTagList();

        for(BlockPos pos : path)
        {
            list.appendTag(new NBTTagIntArray(new int[] {pos.getX(), pos.getY(), pos.getZ()}));
        }

        nbt.setTag("Path", list);
        nbt.setFloat("Efficiency", efficiency);
        return nbt;
    }

    public void writeToBuf(ByteBuf buf)
    {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        buf.writeInt(dimension);
        buf.writeShort(path.size());
        for(BlockPos pos : path)
        {
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
        }
        buf.writeFloat(efficiency);
    }

    public boolean contains(BlockDimPos pos)
    {
        if(pos.dim != dimension)
        {
            return false;
        }

        for(int i = 0; i < path.size() - 1; i++)
        {
            if(isPosBetween(pos.pos, path.get(i), path.get(i + 1)))
            {
            }
        }

        return false;
    }
}