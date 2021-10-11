package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.block.entity.BasePipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelProperty;

/**
 * @author LatvianModder
 */
public abstract class BasePipeBlock extends Block {
	public static final float SIZE = 4F;
	public static final VoxelShape[] BOXES_64 = new VoxelShape[1 << 6];
	public static final VoxelShape[] BOXES = new VoxelShape[7];
	public static final ModelProperty<BasePipeBlockEntity> PIPE = new ModelProperty<>();

	static {
		double d0 = SIZE / 16D;
		double d1 = 1D - d0;

		for (int i = 0; i < BOXES_64.length; i++) {
			boolean x0 = (i & (1 << Direction.WEST.get3DDataValue())) != 0;
			boolean x1 = (i & (1 << Direction.EAST.get3DDataValue())) != 0;
			boolean y0 = (i & (1 << Direction.DOWN.get3DDataValue())) != 0;
			boolean y1 = (i & (1 << Direction.UP.get3DDataValue())) != 0;
			boolean z0 = (i & (1 << Direction.NORTH.get3DDataValue())) != 0;
			boolean z1 = (i & (1 << Direction.SOUTH.get3DDataValue())) != 0;
			BOXES_64[i] = Shapes.box(x0 ? 0D : d0, y0 ? 0D : d0, z0 ? 0D : d0, x1 ? 1D : d1, y1 ? 1D : d1, z1 ? 1D : d1);
		}

		BOXES[0] = Shapes.box(d0, 0D, d0, d1, d0, d1);
		BOXES[1] = Shapes.box(d0, d1, d0, d1, 1D, d1);
		BOXES[2] = Shapes.box(d0, d0, 0D, d1, d1, d0);
		BOXES[3] = Shapes.box(d0, d0, d1, d1, d1, 1D);
		BOXES[4] = Shapes.box(0D, d0, d0, d0, d1, d1);
		BOXES[5] = Shapes.box(d1, d0, d0, 1D, d1, d1);
		BOXES[6] = Shapes.box(d0, d0, d0, d1, d1, d1);
	}

	public BasePipeBlock(Block.Properties properties) {
		super(properties);
	}

	public boolean isModular() {
		return false;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	// @Override
	// public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
	// 	return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.CUTOUT;
	// }

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity tileEntity = level.getBlockEntity(pos);

		if (tileEntity instanceof BasePipeBlockEntity) {
			BasePipeBlockEntity pipe = (BasePipeBlockEntity) tileEntity;

			int id = 0;

			for (int i = 0; i < 6; i++) {
				if (pipe.isConnected(Direction.values()[i])) {
					id |= 1 << i;
				}
			}

			return BOXES_64[id];
		}

		return BOXES_64[0];
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