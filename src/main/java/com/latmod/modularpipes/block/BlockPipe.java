package com.latmod.modularpipes.block;

import com.latmod.modularpipes.tile.TilePipe;
import net.minecraft.block.Block;
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
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockPipe extends BlockBase
{
    public static final PropertyEnum<EnumPipeTier> TIER = PropertyEnum.create("tier", EnumPipeTier.class);
    public static final PropertyBool OPAQUE = PropertyBool.create("opaque");

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
                .withProperty(CON_EAST, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TIER, OPAQUE, CON_DOWN, CON_UP, CON_NORTH, CON_SOUTH, CON_WEST, CON_EAST);
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
        return state
                .withProperty(CON_DOWN, canConnectTo(worldIn, pos, EnumFacing.DOWN))
                .withProperty(CON_UP, canConnectTo(worldIn, pos, EnumFacing.UP))
                .withProperty(CON_NORTH, canConnectTo(worldIn, pos, EnumFacing.NORTH))
                .withProperty(CON_SOUTH, canConnectTo(worldIn, pos, EnumFacing.SOUTH))
                .withProperty(CON_WEST, canConnectTo(worldIn, pos, EnumFacing.WEST))
                .withProperty(CON_EAST, canConnectTo(worldIn, pos, EnumFacing.EAST));
    }

    public static boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        pos = pos.offset(facing);
        IBlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();

        if(block instanceof BlockPipe)
        {
            return true;
        }
        else if(block.hasTileEntity(state))
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);

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
                c |= 1 << facing;
            }
        }

        return c;
    }
}