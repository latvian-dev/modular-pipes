package dev.latvian.mods.modularpipes.block;

import dev.latvian.mods.modularpipes.block.entity.PipeBlockEntity;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class PipeBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
	public static final double SIZE = 4D;
	public static final double S0 = SIZE / 16D;
	public static final double S1 = 1D - S0;
	private static final VoxelShape[] BOXES_64 = new VoxelShape[1 << 6];
	public static final ModelProperty<PipeBlockEntity> PIPE = new ModelProperty<>();
	public static final BooleanProperty LIGHT = BooleanProperty.create("light");

	public static VoxelShape getBox(int i) {
		if (BOXES_64[i] == null) {
			List<VoxelShape> extra = new ArrayList<>();

			if ((i & (1 << Direction.WEST.get3DDataValue())) != 0) {
				extra.add(Shapes.box(0D, S0, S0, S0, S1, S1));
			}

			if ((i & (1 << Direction.EAST.get3DDataValue())) != 0) {
				extra.add(Shapes.box(S1, S0, S0, 1D, S1, S1));
			}

			if ((i & (1 << Direction.DOWN.get3DDataValue())) != 0) {
				extra.add(Shapes.box(S0, 0D, S0, S1, S0, S1));
			}

			if ((i & (1 << Direction.UP.get3DDataValue())) != 0) {
				extra.add(Shapes.box(S0, S1, S0, S1, 1D, S1));
			}

			if ((i & (1 << Direction.NORTH.get3DDataValue())) != 0) {
				extra.add(Shapes.box(S0, S0, 0D, S1, S1, S0));
			}

			if ((i & (1 << Direction.SOUTH.get3DDataValue())) != 0) {
				extra.add(Shapes.box(S0, S0, S1, S1, S1, 1D));
			}

			BOXES_64[i] = Shapes.or(Shapes.box(S0, S0, S0, S1, S1, S1), extra.toArray(new VoxelShape[0]));
		}

		return BOXES_64[i];
	}

	public final PipeTier tier;

	public PipeBlock(PipeTier t) {
		super(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
						.strength(0.6F)
						.sound(SoundType.METAL)
						.noOcclusion()
						.lightLevel(value -> value.getValue(LIGHT) ? 15 : 0)
				//.harvestTool(ToolType.PICKAXE)
		);

		registerDefaultState(getStateDefinition().any().setValue(LIGHT, false).setValue(BlockStateProperties.WATERLOGGED, false));
		tier = t;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return tier.blockEntity.create(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> def) {
		def.add(LIGHT, BlockStateProperties.WATERLOGGED);
	}

	// FIXME: @Override
	// public int getLightValue(BlockState state, BlockGetter level, BlockPos pos) {
	// 	return state.getValue(LIGHT) ? 15 : ModularPipes.PROXY.getPipeLightValue(level);
	// }

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		BlockEntity tileEntity = level.getBlockEntity(pos);

		if (tileEntity instanceof PipeBlockEntity) {
			PipeBlockEntity pipe = (PipeBlockEntity) tileEntity;

			int id = 0;

			for (int i = 0; i < 6; i++) {
				if (pipe.sideData[i].extendShape()) {
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
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean b) {
		super.onPlace(state, level, pos, oldState, b);

		if (!oldState.is(state.getBlock())) {
			BlockEntity blockEntity = level.getBlockEntity(pos);

			if (blockEntity instanceof PipeBlockEntity) {
				for (int i = 0; i < 6; i++) {
					((PipeBlockEntity) blockEntity).sideData[i].updateConnection();
				}
			}
		}
	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction face, BlockState nstate, LevelAccessor level, BlockPos pos, BlockPos npos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity instanceof PipeBlockEntity) {
			((PipeBlockEntity) blockEntity).sideData[face.get3DDataValue()].updateConnection();
		}

		BlockEntity blockEntity2 = level.getBlockEntity(npos);

		if (blockEntity2 instanceof PipeBlockEntity) {
			((PipeBlockEntity) blockEntity2).sideData[face.getOpposite().get3DDataValue()].updateConnection();
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
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean b) {
		if (!level.isClientSide() && !state.is(oldState.getBlock())) {
			BlockEntity blockEntity = level.getBlockEntity(pos);

			if (blockEntity instanceof PipeBlockEntity) {
				((PipeBlockEntity) blockEntity).dropItems();
			}
		}

		super.onRemove(state, level, pos, oldState, b);
	}

	@Override
	@Deprecated
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (blockEntity instanceof PipeBlockEntity) {
			return ((PipeBlockEntity) blockEntity).rightClick(player, hand, hit);
		}

		return InteractionResult.PASS;
	}
}