package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.TilePipe;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockPipe extends BlockBase
{
    public static final PropertyEnum<EnumPipeTier> TIER = PropertyEnum.create("tier", EnumPipeTier.class);
    public static final PropertyBool OPAQUE = PropertyBool.create("opaque");

    private static final PropertyBool CON_DOWN = PropertyBool.create("con_down");
    private static final PropertyBool CON_UP = PropertyBool.create("con_up");
    private static final PropertyBool CON_NORTH = PropertyBool.create("con_north");
    private static final PropertyBool CON_SOUTH = PropertyBool.create("con_south");
    private static final PropertyBool CON_WEST = PropertyBool.create("con_west");
    private static final PropertyBool CON_EAST = PropertyBool.create("con_east");
    private static final PropertyEnum<BlockLog.EnumAxis> AXIS = PropertyEnum.create("axis", BlockLog.EnumAxis.class);
    private static final int AXIS_X = 48;//1 << EnumFacing.WEST.ordinal() | 1 << EnumFacing.EAST.ordinal();
    private static final int AXIS_Y = 3;//1 << EnumFacing.DOWN.ordinal() | 1 << EnumFacing.UP.ordinal();
    private static final int AXIS_Z = 12;//1 << EnumFacing.NORTH.ordinal() | 1 << EnumFacing.SOUTH.ordinal();

    public BlockPipe(String id)
    {
        super(id, Material.ROCK, MapColor.GRAY);
        setDefaultState(blockState.getBaseState()
                .withProperty(TIER, EnumPipeTier.MK0)
                .withProperty(OPAQUE, false)
                .withProperty(CON_DOWN, false)
                .withProperty(CON_UP, false)
                .withProperty(CON_NORTH, false)
                .withProperty(CON_SOUTH, false)
                .withProperty(CON_WEST, false)
                .withProperty(CON_EAST, false)
                .withProperty(AXIS, BlockLog.EnumAxis.NONE));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TIER, OPAQUE, CON_DOWN, CON_UP, CON_NORTH, CON_SOUTH, CON_WEST, CON_EAST, AXIS);
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TIER, EnumPipeTier.getFromMeta(meta)).withProperty(OPAQUE, meta > 7);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(TIER).ordinal() + (state.getValue(OPAQUE) ? 8 : 0);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return state.getValue(TIER).ordinal() > 0;
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
        for(int meta = 0; meta < 16; meta++)
        {
            list.add(new ItemStack(itemIn, 1, meta));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
        tooltip.add("Tier: " + (stack.getMetadata() & 7));
        tooltip.add("Opaque: " + (stack.getMetadata() > 7));
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
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == (state.getValue(OPAQUE) ? BlockRenderLayer.CUTOUT : BlockRenderLayer.TRANSLUCENT);
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        int c = getConnections(worldIn, pos);
        switch(c)
        {
            case 0:
                return state;
            case AXIS_X:
                return state.withProperty(AXIS, BlockLog.EnumAxis.X);
            case AXIS_Y:
                return state.withProperty(AXIS, BlockLog.EnumAxis.Y);
            case AXIS_Z:
                return state.withProperty(AXIS, BlockLog.EnumAxis.Z);
            default:
                return state
                        .withProperty(CON_DOWN, con(c, EnumFacing.DOWN))
                        .withProperty(CON_UP, con(c, EnumFacing.UP))
                        .withProperty(CON_NORTH, con(c, EnumFacing.NORTH))
                        .withProperty(CON_SOUTH, con(c, EnumFacing.SOUTH))
                        .withProperty(CON_WEST, con(c, EnumFacing.WEST))
                        .withProperty(CON_EAST, con(c, EnumFacing.EAST));
        }
    }

    private static boolean con(int c, EnumFacing facing)
    {
        return ((c >> facing.ordinal()) & 1) != 0;
    }

    public static int getConnections(IBlockAccess worldIn, BlockPos pos)
    {
        int c = 0;

        for(EnumFacing facing : EnumFacing.VALUES)
        {
            if(canConnectTo(worldIn, pos, facing))
            {
                c |= 1 << facing.ordinal();
            }
        }

        return c;
    }

    public static boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        return worldIn.getBlockState(pos.offset(facing)).getBlock() instanceof BlockPipe;
    }
}