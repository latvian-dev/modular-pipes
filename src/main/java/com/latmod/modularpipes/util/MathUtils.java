package com.latmod.modularpipes.util;

import net.minecraft.block.BlockLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Mirrored from FTBLib
 *
 * @author LatvianModder
 */
public class MathUtils
{
    public static final double RAD = Math.PI / 180D;
    public static final double DEG = 180D / Math.PI;
    public static final float PI_F = (float) Math.PI;
    public static final float RAD_F = (float) RAD;
    public static final float DEG_F = (float) DEG;

    public static final int FACING_BIT_DOWN = 1;
    public static final int FACING_BIT_UP = 2;
    public static final int FACING_BIT_NORTH = 4;
    public static final int FACING_BIT_SOUTH = 8;
    public static final int FACING_BIT_WEST = 16;
    public static final int FACING_BIT_EAST = 32;
    public static final int FACING_BIT[] = {FACING_BIT_DOWN, FACING_BIT_UP, FACING_BIT_NORTH, FACING_BIT_SOUTH, FACING_BIT_WEST, FACING_BIT_EAST};
    public static final int OPPOSITE[] = {1, 0, 3, 2, 5, 4};
    public static final int OPPOSITE_BIT[] = {2, 1, 8, 4, 32, 16};

    @Nullable
    public static EnumFacing getFacing(int i)
    {
        return i < 0 || i > 5 ? null : EnumFacing.VALUES[i];
    }

    public static int getFacingIndex(@Nullable EnumFacing facing)
    {
        return facing == null ? -1 : facing.getIndex();
    }

    private static boolean isNumberBetween(int num, int num1, int num2)
    {
        int min = Math.min(num1, num2);
        int max = Math.max(num1, num2);
        return num >= min && num <= max;
    }

    public static BlockLog.EnumAxis getAxis(BlockPos pos1, BlockPos pos2)
    {
        int x = pos1.getX() - pos2.getX();
        int y = pos1.getY() - pos2.getY();
        int z = pos1.getZ() - pos2.getZ();

        if(x != 0 && y == 0 && z == 0)
        {
            return BlockLog.EnumAxis.X;
        }
        else if(x == 0 && y != 0 && z == 0)
        {
            return BlockLog.EnumAxis.Y;
        }
        else if(x == 0 && y == 0 && z != 0)
        {
            return BlockLog.EnumAxis.Z;
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
                return pos1.equals(pos) || pos2.equals(pos);
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

    public static Vec3d getEyePosition(EntityPlayer ep)
    {
        return new Vec3d(ep.posX, ep.world.isRemote ? ep.posY : (ep.posY + ep.getEyeHeight()), ep.posZ);
    }

    @Nullable
    public static RayTraceResult rayTrace(EntityPlayer playerIn, boolean useLiquids, double dist)
    {
        Vec3d vec3d = new Vec3d(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ);
        float f2 = MathHelper.cos(-playerIn.rotationYaw * RAD_F - PI_F);
        float f3 = MathHelper.sin(-playerIn.rotationYaw * RAD_F - PI_F);
        float f4 = -MathHelper.cos(-playerIn.rotationPitch * RAD_F);
        Vec3d vec3d1 = vec3d.addVector(f3 * f4 * dist, MathHelper.sin(-playerIn.rotationPitch * RAD_F) * dist, f2 * f4 * dist);
        return playerIn.world.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }

    @Nullable
    public static RayTraceResult rayTrace(EntityPlayer playerIn, boolean useLiquids)
    {
        double dist = 5D;

        if(playerIn instanceof EntityPlayerMP)
        {
            dist = ((EntityPlayerMP) playerIn).interactionManager.getBlockReachDistance();
        }

        return rayTrace(playerIn, useLiquids, dist);
    }

    @Nullable
    public static RayTraceResult collisionRayTrace(World w, BlockPos blockPos, Vec3d start, Vec3d end, AxisAlignedBB[] boxes)
    {
        RayTraceResult current = null;
        double dist = Double.POSITIVE_INFINITY;

        for(int i = 0; i < boxes.length; i++)
        {
            if(boxes[i] != null)
            {
                RayTraceResult mop = collisionRayTrace(w, blockPos, start, end, boxes[i]);

                if(mop != null)
                {
                    double d1 = mop.hitVec.squareDistanceTo(start);
                    if(current == null || d1 < dist)
                    {
                        current = mop;
                        current.subHit = i;
                        dist = d1;
                    }
                }
            }
        }

        return current;
    }

    @Nullable
    public static RayTraceResult collisionRayTrace(World w, BlockPos blockPos, Vec3d start, Vec3d end, AxisAlignedBB aabb)
    {
        Vec3d pos = start.addVector(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
        Vec3d rot = end.addVector(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());

        Vec3d xmin = pos.getIntermediateWithXValue(rot, aabb.minX);
        Vec3d xmax = pos.getIntermediateWithXValue(rot, aabb.maxX);
        Vec3d ymin = pos.getIntermediateWithYValue(rot, aabb.minY);
        Vec3d ymax = pos.getIntermediateWithYValue(rot, aabb.maxY);
        Vec3d zmin = pos.getIntermediateWithZValue(rot, aabb.minZ);
        Vec3d zmax = pos.getIntermediateWithZValue(rot, aabb.maxZ);

        if(!isVecInsideYZBounds(xmin, aabb))
        {
            xmin = null;
        }
        if(!isVecInsideYZBounds(xmax, aabb))
        {
            xmax = null;
        }
        if(!isVecInsideXZBounds(ymin, aabb))
        {
            ymin = null;
        }
        if(!isVecInsideXZBounds(ymax, aabb))
        {
            ymax = null;
        }
        if(!isVecInsideXYBounds(zmin, aabb))
        {
            zmin = null;
        }
        if(!isVecInsideXYBounds(zmax, aabb))
        {
            zmax = null;
        }
        Vec3d v = null;

        if(xmin != null && (v == null || pos.squareDistanceTo(xmin) < pos.squareDistanceTo(v)))
        {
            v = xmin;
        }
        if(xmax != null && (v == null || pos.squareDistanceTo(xmax) < pos.squareDistanceTo(v)))
        {
            v = xmax;
        }
        if(ymin != null && (v == null || pos.squareDistanceTo(ymin) < pos.squareDistanceTo(v)))
        {
            v = ymin;
        }
        if(ymax != null && (v == null || pos.squareDistanceTo(ymax) < pos.squareDistanceTo(v)))
        {
            v = ymax;
        }
        if(zmin != null && (v == null || pos.squareDistanceTo(zmin) < pos.squareDistanceTo(v)))
        {
            v = zmin;
        }
        if(zmax != null && (v == null || pos.squareDistanceTo(zmax) < pos.squareDistanceTo(v)))
        {
            v = zmax;
        }
        if(v == null)
        {
            return null;
        }
        else
        {
            EnumFacing side = null;

            if(v == xmin)
            {
                side = EnumFacing.WEST;
            }
            if(v == xmax)
            {
                side = EnumFacing.EAST;
            }
            if(v == ymin)
            {
                side = EnumFacing.DOWN;
            }
            if(v == ymax)
            {
                side = EnumFacing.UP;
            }
            if(v == zmin)
            {
                side = EnumFacing.NORTH;
            }
            if(v == zmax)
            {
                side = EnumFacing.SOUTH;
            }

            return new RayTraceResult(v.addVector(blockPos.getX(), blockPos.getY(), blockPos.getZ()), side, blockPos);
        }
    }

    private static boolean isVecInsideYZBounds(@Nullable Vec3d v, AxisAlignedBB aabb)
    {
        return v != null && (v.yCoord >= aabb.minY && v.yCoord <= aabb.maxY && v.zCoord >= aabb.minZ && v.zCoord <= aabb.maxZ);
    }

    private static boolean isVecInsideXZBounds(@Nullable Vec3d v, AxisAlignedBB aabb)
    {
        return v != null && (v.xCoord >= aabb.minX && v.xCoord <= aabb.maxX && v.zCoord >= aabb.minZ && v.zCoord <= aabb.maxZ);
    }

    private static boolean isVecInsideXYBounds(@Nullable Vec3d v, AxisAlignedBB aabb)
    {
        return v != null && (v.xCoord >= aabb.minX && v.xCoord <= aabb.maxX && v.yCoord >= aabb.minY && v.yCoord <= aabb.maxY);
    }
}
