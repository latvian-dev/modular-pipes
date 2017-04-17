package com.latmod.modularpipes.block;

import com.latmod.modularpipes.api.IPipeBlock;
import com.latmod.modularpipes.api.TransportedItem;
import com.latmod.modularpipes.tile.TilePipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockPipe extends BlockPipeBase
{
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, 7);
    public static final PropertyInteger CON_D = PropertyInteger.create("con_d", 0, 1);
    public static final PropertyInteger CON_U = PropertyInteger.create("con_u", 0, 1);
    public static final PropertyInteger CON_N = PropertyInteger.create("con_n", 0, 1);
    public static final PropertyInteger CON_S = PropertyInteger.create("con_s", 0, 1);
    public static final PropertyInteger CON_W = PropertyInteger.create("con_w", 0, 1);
    public static final PropertyInteger CON_E = PropertyInteger.create("con_e", 0, 1);
    public static final PropertyInteger[] CONNECTIONS = {CON_D, CON_U, CON_N, CON_S, CON_W, CON_E};

    public BlockPipe(String id)
    {
        super(id, MapColor.GRAY);
        setDefaultState(blockState.getBaseState()
                .withProperty(TIER, 0)
                .withProperty(CON_D, 0)
                .withProperty(CON_U, 0)
                .withProperty(CON_N, 0)
                .withProperty(CON_S, 0)
                .withProperty(CON_W, 0)
                .withProperty(CON_E, 0));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TIER, CON_D, CON_U, CON_N, CON_S, CON_W, CON_E);
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
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TilePipe(world.provider.getDimension(), state.getValue(TIER));
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
        int m = stack.getMetadata() & 7;

        tooltip.add("Tier " + m);

        switch(m)
        {
            case 0:
                tooltip.add("Not very modular");
                break;
            case 7:
                tooltip.add("Available module slots: 6");
                tooltip.add("Permanent speed modifier: 10x");
                break;
            default:
                tooltip.add("Available module slots: " + m);
                break;
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if(tileEntity instanceof TilePipe)
        {
            ((TilePipe) tileEntity).onRightClick(playerIn, hand);
        }

        return true;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        int d = canConnectTo(worldIn, pos, EnumFacing.DOWN) ? 1 : 0;
        int u = canConnectTo(worldIn, pos, EnumFacing.UP) ? 1 : 0;
        int n = canConnectTo(worldIn, pos, EnumFacing.NORTH) ? 1 : 0;
        int s = canConnectTo(worldIn, pos, EnumFacing.SOUTH) ? 1 : 0;
        int w = canConnectTo(worldIn, pos, EnumFacing.WEST) ? 1 : 0;
        int e = canConnectTo(worldIn, pos, EnumFacing.EAST) ? 1 : 0;
        return state.withProperty(CON_D, d).withProperty(CON_U, u).withProperty(CON_N, n).withProperty(CON_S, s).withProperty(CON_W, w).withProperty(CON_E, e);
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

        for(int i = 0; i < BlockPipeBase.BOXES.length; i++)
        {
            if(i < 6 && !canConnectTo(worldIn, pos, EnumFacing.VALUES[i]))
            {
                continue;
            }

            RayTraceResult ray = BlockPipeBase.BOXES[i].calculateIntercept(start1, end1);

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

    @Override
    public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        BlockPos pos1 = pos.offset(facing);
        IBlockState state1 = worldIn.getBlockState(pos1);
        Block block1 = state1.getBlock();

        if(block1 instanceof IPipeBlock)
        {
            return ((IPipeBlock) block1).canPipeConnect(worldIn, pos1, state1, facing.getOpposite());
        }
        else if(block1.hasTileEntity(state1))
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos1);

            if(tileEntity != null)
            {
                if(tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite()))
                {
                    return true;
                }
                else if(tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int getConnectionIdFromState(@Nullable IBlockState state)
    {
        if(state == null)
        {
            return 0;
        }

        int c = 0;

        for(int facing = 0; facing < 6; facing++)
        {
            c |= state.getValue(CONNECTIONS[facing]) << facing;
        }

        return c;
    }

    @Override
    public EnumFacing getItemDirection(IBlockAccess world, BlockPos pos, IBlockState state, TransportedItem item, EnumFacing source)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof TilePipe)
        {
            return ((TilePipe) tileEntity).getItemDirection(item, source);
        }

        return source;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if(tileEntity instanceof TilePipe)
        {
            ((TilePipe) tileEntity).onBroken();
        }

        super.breakBlock(worldIn, pos, state);
    }
}