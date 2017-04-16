package com.latmod.modularpipes.block;

import com.latmod.modularpipes.api.IPipeConnection;
import com.latmod.modularpipes.tile.TilePipe;
import com.latmod.modularpipes.util.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockPipe extends BlockBase implements IPipeConnection
{
    public static final float SIZE = 4F;
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, 7);

    public static final PropertyEnum<BlockLog.EnumAxis> MODEL = PropertyEnum.create("model", BlockLog.EnumAxis.class);
    public static final PropertyBool CON_DOWN = PropertyBool.create("con_down");
    public static final PropertyBool CON_UP = PropertyBool.create("con_up");
    public static final PropertyBool CON_NORTH = PropertyBool.create("con_north");
    public static final PropertyBool CON_SOUTH = PropertyBool.create("con_south");
    public static final PropertyBool CON_WEST = PropertyBool.create("con_west");
    public static final PropertyBool CON_EAST = PropertyBool.create("con_east");
    public static final PropertyBool[] CONNECTIONS = {CON_DOWN, CON_UP, CON_NORTH, CON_SOUTH, CON_WEST, CON_EAST};
    public static final int AXIS_X = 48;//1 << EnumFacing.WEST.ordinal() | 1 << EnumFacing.EAST.ordinal();
    public static final int AXIS_Y = 3;//1 << EnumFacing.DOWN.ordinal() | 1 << EnumFacing.UP.ordinal();
    public static final int AXIS_Z = 12;//1 << EnumFacing.NORTH.ordinal() | 1 << EnumFacing.SOUTH.ordinal();

    public static final AxisAlignedBB[] BOXES = new AxisAlignedBB[7];
    private static final AxisAlignedBB BOXES_COMBINED[] = new AxisAlignedBB[64];

    static
    {
        double d0 = SIZE / 16D;
        double d1 = 1D - d0;

        for(int i = 0; i < BOXES_COMBINED.length; i++)
        {
            boolean x0 = (i & MathUtils.FACING_BIT_WEST) != 0;
            boolean x1 = (i & MathUtils.FACING_BIT_EAST) != 0;
            boolean y0 = (i & MathUtils.FACING_BIT_DOWN) != 0;
            boolean y1 = (i & MathUtils.FACING_BIT_UP) != 0;
            boolean z0 = (i & MathUtils.FACING_BIT_NORTH) != 0;
            boolean z1 = (i & MathUtils.FACING_BIT_SOUTH) != 0;
            BOXES_COMBINED[i] = new AxisAlignedBB(x0 ? 0D : d0, y0 ? 0D : d0, z0 ? 0D : d0, x1 ? 1D : d1, y1 ? 1D : d1, z1 ? 1D : d1);
        }

        BOXES[0] = new AxisAlignedBB(d0, 0D, d0, d1, d0, d1);
        BOXES[1] = new AxisAlignedBB(d0, d1, d0, d1, 1D, d1);
        BOXES[2] = new AxisAlignedBB(d0, d0, 0D, d1, d1, d0);
        BOXES[3] = new AxisAlignedBB(d0, d0, d1, d1, d1, 1D);
        BOXES[4] = new AxisAlignedBB(0D, d0, d0, d0, d1, d1);
        BOXES[5] = new AxisAlignedBB(d1, d0, d0, 1D, d1, d1);
        BOXES[6] = BOXES_COMBINED[0];
    }

    public BlockPipe(String id)
    {
        super(id, Material.ROCK, MapColor.GRAY);
        setDefaultState(blockState.getBaseState()
                .withProperty(TIER, 0)
                .withProperty(MODEL, BlockLog.EnumAxis.NONE)
                .withProperty(CON_DOWN, false)
                .withProperty(CON_UP, false)
                .withProperty(CON_NORTH, false)
                .withProperty(CON_SOUTH, false)
                .withProperty(CON_WEST, false)
                .withProperty(CON_EAST, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TIER, MODEL, CON_DOWN, CON_UP, CON_NORTH, CON_SOUTH, CON_WEST, CON_EAST);
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TIER, meta & 7);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(TIER);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return state.getValue(TIER) > 0;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TilePipe(state.getValue(TIER));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for(int meta = 0; meta < 8; meta++)
        {
            list.add(new ItemStack(itemIn, 1, meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add("Tier " + stack.getMetadata());
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(state.getValue(TIER) == 0)
        {
            return false;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if(tileEntity instanceof TilePipe)
        {
            ((TilePipe) tileEntity).onRightClick(playerIn, hand);
        }

        return true;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @SideOnly(Side.CLIENT)
    @Deprecated
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        boolean d = canConnectTo(worldIn, pos, EnumFacing.DOWN);
        boolean u = canConnectTo(worldIn, pos, EnumFacing.UP);
        boolean n = canConnectTo(worldIn, pos, EnumFacing.NORTH);
        boolean s = canConnectTo(worldIn, pos, EnumFacing.SOUTH);
        boolean w = canConnectTo(worldIn, pos, EnumFacing.WEST);
        boolean e = canConnectTo(worldIn, pos, EnumFacing.EAST);

        if(state.getValue(TIER) == 0)
        {
            if(!d && !u && !n && !s && w && e)
            {
                return state.withProperty(MODEL, BlockLog.EnumAxis.X);
            }
            else if(d && u && !n && !s && !w && !e)
            {
                return state.withProperty(MODEL, BlockLog.EnumAxis.Y);
            }
            else if(!d && !u && n && s && !w && !e)
            {
                return state.withProperty(MODEL, BlockLog.EnumAxis.Z);
            }
        }

        return state.withProperty(CON_DOWN, d).withProperty(CON_UP, u).withProperty(CON_NORTH, n).withProperty(CON_SOUTH, s).withProperty(CON_WEST, w).withProperty(CON_EAST, e);
    }

    @Override
    @Nullable
    @Deprecated
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        Vec3d start1 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        Vec3d end1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
        RayTraceResult ray1 = null;

        double dist = Double.POSITIVE_INFINITY;

        for(int i = 0; i < BOXES.length; i++)
        {
            if(i < 6 && !canConnectTo(worldIn, pos, EnumFacing.VALUES[i]))
            {
                continue;
            }

            RayTraceResult ray = BOXES[i].calculateIntercept(start1, end1);

            if(ray != null)
            {
                double dist1 = ray.hitVec.squareDistanceTo(start1);

                if(dist >= dist1)
                {
                    dist = dist1;
                    ray1 = ray;
                    ray1.subHit = i;
                }
            }
        }

        if(ray1 != null)
        {
            RayTraceResult ray2 = new RayTraceResult(ray1.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ()), ray1.sideHit, pos);
            ray2.subHit = ray1.subHit;
            return ray2;
        }

        return null;
    }

    @Deprecated
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[6]);

        for(EnumFacing facing : EnumFacing.VALUES)
        {
            if(canConnectTo(worldIn, pos, facing))
            {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[facing.ordinal()]);
            }
        }
    }

    @Override
    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BOXES_COMBINED[getConnectionsFromState(state)];
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        if(state.getValue(TIER) == 0)
        {
            state = getActualState(state, worldIn, pos);

            switch(state.getValue(MODEL))
            {
                case NONE:
                    return BOXES_COMBINED[getConnectionsFromState(state)].offset(pos);
                case X:
                    return BOXES_COMBINED[AXIS_X].offset(pos);
                case Y:
                    return BOXES_COMBINED[AXIS_Y].offset(pos);
                case Z:
                    return BOXES_COMBINED[AXIS_Z].offset(pos);
            }
        }

        RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;

        if(mop != null && mop.subHit >= 0 && mop.subHit < BOXES.length)
        {
            return BOXES[mop.subHit].offset(pos);
        }

        return BOXES_COMBINED[0].offset(pos);
    }

    public static boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        BlockPos pos1 = pos.offset(facing);
        IBlockState state1 = worldIn.getBlockState(pos1);
        Block block = state1.getBlock();

        if(block instanceof IPipeConnection)
        {
            return ((IPipeConnection) block).canPipeConnect(worldIn, pos1, state1, facing.getOpposite());
        }
        else if(block.hasTileEntity(state1))
        {
            if(worldIn.getBlockState(pos).getValue(TIER) == 0)
            {
                return false;
            }

            TileEntity tileEntity = worldIn.getTileEntity(pos1);

            if(tileEntity != null && tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()))
            {
                return true;
            }
        }

        return false;
    }

    public static int getConnectionsFromState(@Nullable IBlockState state)
    {
        if(state == null)
        {
            return 0;
        }

        int c = 0;

        for(int facing = 0; facing < 6; facing++)
        {
            if(state.getValue(CONNECTIONS[facing]))
            {
                c |= MathUtils.FACING_BIT[facing];
            }
        }

        return c;
    }

    @Override
    public boolean canPipeConnect(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing)
    {
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if(!worldIn.isRemote && state.getValue(TIER) > 0)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);

            if(tileEntity instanceof TilePipe)
            {
                ((TilePipe) tileEntity).onBroken();
            }
        }

        super.breakBlock(worldIn, pos, state);
    }
}