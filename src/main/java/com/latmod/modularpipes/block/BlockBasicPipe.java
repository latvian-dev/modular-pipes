package com.latmod.modularpipes.block;

import com.feed_the_beast.ftbl.lib.block.ItemBlockBase;
import com.latmod.modularpipes.ModularPipesConfig;
import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockBasicPipe extends BlockPipeBase
{
    public enum Variant implements IStringSerializable
    {
        BASIC("basic", MapColor.GRAY),
        SPEED("speed", MapColor.GOLD);

        public static final Variant[] VALUES = values();
        private final String name;
        public final MapColor color;
        public final String uname;

        Variant(String n, MapColor c)
        {
            name = n;
            color = c;
            uname = "tile.modularpipes.pipe_basic." + n;
        }

        @Override
        public String getName()
        {
            return name;
        }

        public static Variant getFromMeta(int meta)
        {
            return (meta < 0 || meta >= VALUES.length || VALUES[meta] == null) ? BASIC : VALUES[meta];
        }
    }

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
        S_E("s_e", EnumFacing.SOUTH, EnumFacing.EAST),
        D("d", EnumFacing.DOWN, null),
        U("u", EnumFacing.UP, null),
        N("n", EnumFacing.NORTH, null),
        S("s", EnumFacing.SOUTH, null),
        W("w", EnumFacing.WEST, null),
        E("e", EnumFacing.EAST, null);

        public static final Model[] VALUES = values();
        public static final TIntIntHashMap CONNECTION_MAP = new TIntIntHashMap();

        static
        {
            for(Model m : VALUES)
            {
                CONNECTION_MAP.put(m.connectionId, m.ordinal());
            }
        }

        private final String name;
        public final EnumFacing facing1, facing2;
        public final int connectionId;

        Model(String n, @Nullable EnumFacing f1, @Nullable EnumFacing f2)
        {
            name = n;
            facing1 = f1;
            facing2 = f2;
            connectionId = (facing1 == null ? 0 : (1 << facing1.getIndex())) | (facing2 == null ? 0 : (1 << facing2.getIndex()));
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

    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
    public static final PropertyEnum<Model> MODEL = PropertyEnum.create("model", Model.class);

    public BlockBasicPipe(String id)
    {
        super(id, MapColor.GRAY);
        setDefaultState(blockState.getBaseState().withProperty(VARIANT, Variant.BASIC).withProperty(MODEL, Model.NONE));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, VARIANT, MODEL);
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(VARIANT, Variant.getFromMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return getMetaFromState(state);
    }

    @Override
    public ItemBlock createItemBlock()
    {
        return new ItemBlockBase(this, true)
        {
            @Override
            public String getUnlocalizedName(ItemStack stack)
            {
                return Variant.getFromMeta(stack.getMetadata()).uname;
            }
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for(Variant v : Variant.VALUES)
        {
            list.add(new ItemStack(itemIn, 1, v.ordinal()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
    }

    @Override
    @Deprecated
    public MapColor getMapColor(IBlockState state)
    {
        return state.getValue(VARIANT).color;
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
        return state.withProperty(MODEL, Model.VALUES[Model.CONNECTION_MAP.get(d | u << 1 | n << 2 | s << 3 | w << 4 | e << 5)]);
    }

    @Override
    public int getConnectionIdFromState(@Nullable IBlockState state)
    {
        return state == null ? 0 : state.getValue(MODEL).connectionId;
    }

    @Override
    public double getSpeedModifier(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return state.getValue(VARIANT) == Variant.SPEED ? ModularPipesConfig.SPEED_PIPE_MODIFIER.getAsDouble() : 1D;
    }

    @Override
    public EnumFacing getPipeFacing(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing source)
    {
        return getActualState(state, world, pos).getValue(MODEL).getItemDirection(source);
    }
}