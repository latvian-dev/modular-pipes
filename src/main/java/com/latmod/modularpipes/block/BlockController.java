package com.latmod.modularpipes.block;

import com.latmod.modularpipes.data.IPipeBlock;
import com.latmod.modularpipes.tile.TileController;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class BlockController extends BlockBase implements IPipeBlock
{
    public static final PropertyBool ERROR = PropertyBool.create("error");

    public BlockController(String id)
    {
        super(id, Material.IRON, MapColor.BLUE);
        setDefaultState(blockState.getBaseState().withProperty(ERROR, false));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, ERROR);
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileController(world.provider.getDimension());
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);

        if(tileEntity instanceof TileController)
        {
            return state.withProperty(ERROR, false);
        }

        return state;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        return true;
    }

    @Override
    public boolean isNode(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return false;
    }

    @Override
    public float getSpeedModifier(IBlockAccess world, BlockPos pos, IBlockState state)
    {
        return 0F;
    }

    @Override
    public boolean canPipeConnect(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing facing)
    {
        return true;
    }

    @Override
    public EnumFacing getPipeFacing(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing source)
    {
        return source;
    }
}