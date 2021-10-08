package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.block.entity.TilePipeBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class BlockPipeBase extends Block {
	public static final float SIZE = 4F;
	public static final AxisAlignedBB[] BOXES_64 = new AxisAlignedBB[1 << 6];
	public static final AxisAlignedBB[] BOXES = new AxisAlignedBB[7];
	public static final ModelProperty<TilePipeBase> PIPE = new ModelProperty<>();

	static {
		double d0 = SIZE / 16D;
		double d1 = 1D - d0;

		for (int i = 0; i < BOXES_64.length; i++) {
			boolean x0 = (i & (1 << Direction.WEST.getIndex())) != 0;
			boolean x1 = (i & (1 << Direction.EAST.getIndex())) != 0;
			boolean y0 = (i & (1 << Direction.DOWN.getIndex())) != 0;
			boolean y1 = (i & (1 << Direction.UP.getIndex())) != 0;
			boolean z0 = (i & (1 << Direction.NORTH.getIndex())) != 0;
			boolean z1 = (i & (1 << Direction.SOUTH.getIndex())) != 0;
			BOXES_64[i] = new AxisAlignedBB(x0 ? 0D : d0, y0 ? 0D : d0, z0 ? 0D : d0, x1 ? 1D : d1, y1 ? 1D : d1, z1 ? 1D : d1);
		}

		BOXES[0] = new AxisAlignedBB(d0, 0D, d0, d1, d0, d1);
		BOXES[1] = new AxisAlignedBB(d0, d1, d0, d1, 1D, d1);
		BOXES[2] = new AxisAlignedBB(d0, d0, 0D, d1, d1, d0);
		BOXES[3] = new AxisAlignedBB(d0, d0, d1, d1, d1, 1D);
		BOXES[4] = new AxisAlignedBB(0D, d0, d0, d0, d1, d1);
		BOXES[5] = new AxisAlignedBB(d1, d0, d0, 1D, d1, d1);
		BOXES[6] = new AxisAlignedBB(d0, d0, d0, d1, d1, d1);
	}

	public BlockPipeBase(Block.Properties properties) {
		super(properties);
	}

	public boolean isModular() {
		return false;
	}


	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public abstract TileEntity createTileEntity(BlockState state, IBlockReader world);

	@Override
	public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof TilePipeBase) {
			TilePipeBase pipe = (TilePipeBase) tileEntity;

			int id = 0;

			for (int i = 0; i < 6; i++) {
				if (pipe.isConnected(Direction.values()[i])) {
					id |= 1 << i;
				}
			}

			return VoxelShapes.create(BOXES_64[id]);
		}

		return VoxelShapes.create(BOXES_64[0]);
	}
	//	@Override
	//	@Deprecated
	//	public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader world, BlockPos pos)
	//	{
	//		TileEntity tileEntity = world.getTileEntity(pos);
	//
	//		if (tileEntity instanceof TilePipeBase)
	//		{
	//			TilePipeBase pipe = (TilePipeBase) tileEntity;
	//
	//			int id = 0;
	//
	//			for (int i = 0; i < 6; i++)
	//			{
	//				if (pipe.isConnected(Direction.values()[i]))
	//				{
	//					id |= 1 << i;
	//				}
	//			}
	//
	//			return BOXES_64[id];
	//		}
	//
	//		return BOXES_64[0];
	//	}
	//
	//	@Override
	//	@Deprecated
	//	public void addCollisionBoxToList(BlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
	//	{
	//		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES_64[0]);
	//		TileEntity tileEntity = world.getTileEntity(pos);
	//
	//		if (tileEntity instanceof TilePipeBase)
	//		{
	//			TilePipeBase pipe = (TilePipeBase) tileEntity;
	//
	//			for (int i = 0; i < 6; i++)
	//			{
	//				if (pipe.isConnected(Direction.VALUES[i]))
	//				{
	//					addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES_64[1 << i]);
	//				}
	//			}
	//		}
	//	}
}