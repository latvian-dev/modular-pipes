package com.latmod.modularpipes.block;

import com.feed_the_beast.ftbl.lib.block.ItemBlockBase;
import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.data.IPipeBlock;
import com.latmod.modularpipes.data.NodeType;
import com.latmod.modularpipes.data.TransportedItem;
import com.latmod.modularpipes.item.ItemModule;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class BlockModularPipe extends BlockPipeBase
{
    public enum Tier implements IStringSerializable
    {
        TIER_0("0", 0, NodeType.TILES),
        TIER_1("1", 1, NodeType.TILES),
        TIER_2("2", 2, NodeType.TILES),
        TIER_3("3", 3, NodeType.TILES),
        TIER_4("4", 4, NodeType.TILES),
        TIER_5("5", 5, NodeType.TILES),
        TIER_6("6", 6, NodeType.TILES),
        TIER_7("7", 7, NodeType.TILES),
        NODE_BASIC("node_basic", 0, NodeType.SIMPLE),
        NODE_SPEED("node_speed", 0, NodeType.SIMPLE);

        public static final Tier[] VALUES = values();

        public static Tier getFromMeta(int m)
        {
            return m < 0 || m >= VALUES.length ? TIER_0 : VALUES[m];
        }

        private final String name;
        public final int tier;
        public IBlockState node = Blocks.AIR.getDefaultState();
        public final NodeType nodeType;

        Tier(String n, int t, NodeType nt)
        {
            name = n;
            tier = t;
            nodeType = nt;
        }

        @Override
        public String getName()
        {
            return name;
        }

        public boolean hasNode()
        {
            return node != Blocks.AIR.getDefaultState();
        }
    }

    public static final PropertyEnum<Tier> TIER = PropertyEnum.create("tier", Tier.class);
    public static final PropertyInteger CON_D = PropertyInteger.create("con_d", 0, 2);
    public static final PropertyInteger CON_U = PropertyInteger.create("con_u", 0, 2);
    public static final PropertyInteger CON_N = PropertyInteger.create("con_n", 0, 2);
    public static final PropertyInteger CON_S = PropertyInteger.create("con_s", 0, 2);
    public static final PropertyInteger CON_W = PropertyInteger.create("con_w", 0, 2);
    public static final PropertyInteger CON_E = PropertyInteger.create("con_e", 0, 2);
    public static final PropertyInteger[] CONNECTIONS = {CON_D, CON_U, CON_N, CON_S, CON_W, CON_E};

    public BlockModularPipe(String id)
    {
        super(id, MapColor.GRAY);
        setDefaultState(blockState.getBaseState()
                .withProperty(TIER, Tier.TIER_0)
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
        return this.getDefaultState().withProperty(TIER, Tier.getFromMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(TIER).ordinal();
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        Tier tier = state.getValue(TIER);

        if(tier.hasNode())
        {
            return tier.node.getBlock().getItemDropped(tier.node, rand, fortune);
        }

        return super.getItemDropped(state, rand, fortune);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        Tier tier = state.getValue(TIER);

        if(tier.hasNode())
        {
            return tier.node.getBlock().damageDropped(tier.node);
        }

        return getMetaFromState(state);
    }

    @Override
    @Deprecated
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(getItemDropped(state, worldIn.rand, 0), 1, damageDropped(state));
    }

    @Override
    public ItemBlock createItemBlock()
    {
        return new ItemBlockBase(this, true);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return !state.getValue(TIER).hasNode();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileModularPipe(state.getValue(TIER).tier);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for(Tier t : Tier.VALUES)
        {
            if(!t.hasNode())
            {
                list.add(new ItemStack(itemIn, 1, t.ordinal()));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        Tier tier = Tier.getFromMeta(stack.getMetadata());
        tooltip.add(StringUtils.translate("tile.modularpipes.pipe_modular.tier", tier.tier));
        tooltip.add(StringUtils.translate("tile.modularpipes.pipe_modular.slots", Math.min(6, tier.tier)));

        if(tier.tier >= 7)
        {
            tooltip.add(StringUtils.translate("tile.modularpipes.pipe_modular.super_boost", StringUtils.formatDouble(ModularPipesConfig.SUPER_BOOST.getAsDouble()) + "x"));
        }
        else if(tier == Tier.TIER_0)
        {
            tooltip.add(StringUtils.translate("tile.modularpipes.pipe_modular.tier_0"));
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(state.getValue(TIER).hasNode())
        {
            return false;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if(tileEntity instanceof TileModularPipe)
        {
            ((TileModularPipe) tileEntity).onRightClick(playerIn, hand);
        }

        return true;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if(state.getValue(TIER).hasNode())
        {
            for(int i = 0; i < 6; i++)
            {
                state = state.withProperty(CONNECTIONS[i], canConnectTo(state, worldIn, pos, EnumFacing.VALUES[i]) ? 1 : 0);
            }

            return state;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if(tileEntity instanceof TileModularPipe)
        {
            TileModularPipe pipe = (TileModularPipe) tileEntity;

            for(int i = 0; i < 6; i++)
            {
                int j = pipe.modules[i].hasModule() ? 2 : 0;
                state = state.withProperty(CONNECTIONS[i], j == 0 ? (pipe.canConnectTo(EnumFacing.VALUES[i]) ? 1 : 0) : j);
            }
        }

        return state;
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        Tier tier = state.getValue(TIER);

        if(tier.hasNode())
        {
            int sum = 0;

            for(EnumFacing facing : EnumFacing.VALUES)
            {
                if(canConnectTo(state, worldIn, pos, facing))
                {
                    sum++;
                }
            }

            if(sum == 0 || sum == 2)
            {
                worldIn.setBlockState(pos, tier.node);
            }

            return;
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if(tileEntity instanceof TileModularPipe)
        {
            ((TileModularPipe) tileEntity).onNeighborChange();
        }
    }

    @Override
    @Nullable
    @Deprecated
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        Vec3d start1 = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        Vec3d end1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
        RayTraceResult ray1 = null;
        EntityPlayer player = worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5D, false);
        boolean holdingModule = player != null && (player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemModule || player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemModule);
        double dist = Double.POSITIVE_INFINITY;

        for(int i = 0; i < BlockPipeBase.BOXES.length; i++)
        {
            if(i < 6 && !(holdingModule || canConnectTo(blockState, worldIn, pos, EnumFacing.VALUES[i])))
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
    public boolean canConnectTo(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        if(state.getValue(TIER).hasNode())
        {
            BlockPos pos1 = pos.offset(facing);
            IBlockState state1 = worldIn.getBlockState(pos1);
            Block block1 = state1.getBlock();
            return block1 instanceof IPipeBlock && ((IPipeBlock) block1).canPipeConnect(worldIn, pos1, state1, facing.getOpposite());
        }

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        return tileEntity instanceof TileModularPipe && ((TileModularPipe) tileEntity).canConnectTo(facing);
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
            c |= Math.min(1, state.getValue(CONNECTIONS[facing])) << facing;
        }

        return c;
    }

    @Override
    public NodeType getNodeType(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return state.getValue(TIER).nodeType;
    }

    @Override
    public double getSpeedModifier(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return state.getValue(TIER) == Tier.NODE_SPEED ? ModularPipesConfig.SPEED_PIPE_BOOST.getAsDouble() : 1D;
    }

    @Override
    public boolean superBoost(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return state.getValue(TIER).tier >= 7;
    }

    @Override
    public EnumFacing getItemDirection(IBlockAccess world, BlockPos pos, IBlockState state, TransportedItem item, EnumFacing source)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof TileModularPipe)
        {
            return ((TileModularPipe) tileEntity).getItemDirection(item, source);
        }

        return source;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if(tileEntity instanceof TileModularPipe)
        {
            ((TileModularPipe) tileEntity).onBroken();
        }

        super.breakBlock(worldIn, pos, state);
    }
}