package com.latmod.modularpipes.block;

import com.latmod.modularpipes.item.ModularPipesItems;
import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BlockPipeBasic extends BlockPipeBase
{
    public enum Model implements IStringSerializable
    {
        NONE("none", null, null),
        X("x", EnumFacing.WEST, EnumFacing.EAST),
        Y("y", EnumFacing.DOWN, EnumFacing.UP),
        Z("z", EnumFacing.NORTH, EnumFacing.SOUTH),
        D_N("d_n", EnumFacing.DOWN, EnumFacing.NORTH),
        D_S("d_s", EnumFacing.DOWN, EnumFacing.SOUTH),
        D_W("d_w", EnumFacing.DOWN, EnumFacing.WEST),
        D_E("d_e", EnumFacing.DOWN, EnumFacing.EAST),
        U_N("u_n", EnumFacing.UP, EnumFacing.NORTH),
        U_S("u_s", EnumFacing.UP, EnumFacing.SOUTH),
        U_W("u_w", EnumFacing.UP, EnumFacing.WEST),
        U_E("u_e", EnumFacing.UP, EnumFacing.EAST),
        N_W("n_w", EnumFacing.NORTH, EnumFacing.WEST),
        N_E("n_e", EnumFacing.NORTH, EnumFacing.EAST),
        S_W("s_w", EnumFacing.SOUTH, EnumFacing.WEST),
        S_E("s_e", EnumFacing.SOUTH, EnumFacing.EAST);

        public static final Model[] VALUES = values();
        public static final TIntIntHashMap CONNECTION_MAP = new TIntIntHashMap();

        private static int getConnectionId(@Nullable EnumFacing facing1, @Nullable EnumFacing facing2)
        {
            return (facing1 == null ? 0 : (1 << facing1.getIndex())) | (facing2 == null ? 0 : (1 << facing2.getIndex()));
        }

        static
        {
            for(Model m : VALUES)
            {
                CONNECTION_MAP.put(m.connectionId, m.ordinal());
            }

            CONNECTION_MAP.put(getConnectionId(EnumFacing.DOWN, null), Y.ordinal());
            CONNECTION_MAP.put(getConnectionId(EnumFacing.UP, null), Y.ordinal());
            CONNECTION_MAP.put(getConnectionId(EnumFacing.NORTH, null), Z.ordinal());
            CONNECTION_MAP.put(getConnectionId(EnumFacing.SOUTH, null), Z.ordinal());
            CONNECTION_MAP.put(getConnectionId(EnumFacing.WEST, null), X.ordinal());
            CONNECTION_MAP.put(getConnectionId(EnumFacing.EAST, null), X.ordinal());
        }

        private final String name;
        public final EnumFacing facing1, facing2;
        public final int connectionId;

        Model(String n, @Nullable EnumFacing f1, @Nullable EnumFacing f2)
        {
            name = n;
            facing1 = f1;
            facing2 = f2;
            connectionId = getConnectionId(facing1, facing2);
        }

        @Override
        public String getName()
        {
            return name;
        }

        public EnumFacing getItemDirection(EnumFacing source)
        {
            if(facing2 != null && source == facing1)
            {
                return facing2;
            }
            if(facing1 != null && source == facing2)
            {
                return facing1;
            }
            return source;
        }
    }

    public static final PropertyEnum<Model> MODEL = PropertyEnum.create("model", Model.class);

    public BlockPipeBasic(String id, MapColor color)
    {
        super(id, color);
        setDefaultState(blockState.getBaseState().withProperty(MODEL, Model.NONE));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, MODEL);
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(MODEL, Model.VALUES[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(MODEL).ordinal();
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 0;
    }

    @Override
    @Deprecated
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        checkState(state, worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if(checkState(state, worldIn, pos))
        {
            super.onBlockAdded(worldIn, pos, state);
        }
    }

    public boolean checkState(IBlockState state, World worldIn, BlockPos pos)
    {
        int d = canConnectTo(state, worldIn, pos, EnumFacing.DOWN) ? 1 : 0;
        int u = canConnectTo(state, worldIn, pos, EnumFacing.UP) ? 1 : 0;
        int n = canConnectTo(state, worldIn, pos, EnumFacing.NORTH) ? 1 : 0;
        int s = canConnectTo(state, worldIn, pos, EnumFacing.SOUTH) ? 1 : 0;
        int w = canConnectTo(state, worldIn, pos, EnumFacing.WEST) ? 1 : 0;
        int e = canConnectTo(state, worldIn, pos, EnumFacing.EAST) ? 1 : 0;
        int sum = d + u + n + s + w + e;

        if(sum == 0 || sum == 2)
        {
            worldIn.setBlockState(pos, state.withProperty(MODEL, Model.VALUES[Model.CONNECTION_MAP.get(d | u << 1 | n << 2 | s << 3 | w << 4 | e << 5)]));
            return true;
        }
        else
        {
            worldIn.setBlockState(pos, getNodeState());
            return false;
        }
    }

    @Override
    public int getConnectionIdFromState(@Nullable IBlockState state)
    {
        return state == null ? 0 : state.getValue(MODEL).connectionId;
    }

    @Override
    public EnumFacing getPipeFacing(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing source)
    {
        return state.getValue(MODEL).getItemDirection(source);
    }

    public IBlockState getNodeState()
    {
        return ModularPipesItems.PIPE_MODULAR.getDefaultState().withProperty(BlockModularPipe.TIER, BlockModularPipe.Tier.NODE_BASIC);
    }
}