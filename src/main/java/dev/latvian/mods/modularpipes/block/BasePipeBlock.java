package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.block.entity.BasePipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.ToolType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BasePipeBlock extends Block implements SimpleWaterloggedBlock {
	public static final double SIZE = 4D;
	private static final VoxelShape[] BOXES_64 = new VoxelShape[1 << 6];
	public static final ModelProperty<BasePipeBlockEntity> PIPE = new ModelProperty<>();

	public static VoxelShape getBox(int i) {
		if (BOXES_64[i] == null) {
			double d0 = SIZE / 16D;
			double d1 = 1D - d0;

			List<VoxelShape> extra = new ArrayList<>();

			if ((i & (1 << Direction.WEST.get3DDataValue())) != 0) {
				extra.add(Shapes.box(0D, d0, d0, d0, d1, d1));
			}

			if ((i & (1 << Direction.EAST.get3DDataValue())) != 0) {
				extra.add(Shapes.box(d1, d0, d0, 1D, d1, d1));
			}

			if ((i & (1 << Direction.DOWN.get3DDataValue())) != 0) {
				extra.add(Shapes.box(d0, 0D, d0, d1, d0, d1));
			}

			if ((i & (1 << Direction.UP.get3DDataValue())) != 0) {
				extra.add(Shapes.box(d0, d1, d0, d1, 1D, d1));
			}

			if ((i & (1 << Direction.NORTH.get3DDataValue())) != 0) {
				extra.add(Shapes.box(d0, d0, 0D, d1, d1, d0));
			}

			if ((i & (1 << Direction.SOUTH.get3DDataValue())) != 0) {
				extra.add(Shapes.box(d0, d0, d1, d1, d1, 1D));
			}

			BOXES_64[i] = Shapes.or(Shapes.box(d0, d0, d0, d1, d1, d1), extra.toArray(new VoxelShape[0]));
		}

		return BOXES_64[i];
	}

	public final PipeTier tier;

	public BasePipeBlock(PipeTier t) {
		super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(0.6F).sound(SoundType.METAL).noOcclusion().harvestTool(ToolType.PICKAXE));
		registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false));
		tier = t;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
		return tier.blockEntity.get();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> def) {
		def.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public int getLightValue(BlockState state, BlockGetter level, BlockPos pos) {
		return ModularPipes.PROXY.getPipeLightValue(level);
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity tileEntity = level.getBlockEntity(pos);

		if (tileEntity instanceof BasePipeBlockEntity) {
			BasePipeBlockEntity pipe = (BasePipeBlockEntity) tileEntity;

			int id = 0;

			for (int i = 0; i < 6; i++) {
				if (pipe.getConnection(i) > 0) {
					id |= 1 << i;
				}
			}

			return getBox(id);
		}

		return getBox(0);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(BlockStateProperties.WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction face, BlockState nstate, LevelAccessor level, BlockPos pos, BlockPos npos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity instanceof BasePipeBlockEntity) {
			blockEntity.clearCache();
		}

		return super.updateShape(state, face, nstate, level, pos, npos);
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	@Deprecated
	public boolean isPathfindable(BlockState arg, BlockGetter arg2, BlockPos arg3, PathComputationType arg4) {
		return false;
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state1, boolean b) {
		if (!level.isClientSide()) {
			BlockEntity blockEntity = level.getBlockEntity(pos);

			if (blockEntity instanceof BasePipeBlockEntity) {
				((BasePipeBlockEntity) blockEntity).dropItems();
			}
		}

		super.onRemove(state, level, pos, state1, b);
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity instanceof ModularPipeBlockEntity) {
			return ((ModularPipeBlockEntity) blockEntity).rightClick(player, hand, hit);
		}

		return InteractionResult.PASS;
	}
}