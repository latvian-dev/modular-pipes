package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.ModularPipesConfig;
import dev.latvian.mods.modularpipes.block.PipeBlock;
import dev.latvian.mods.modularpipes.block.PipeTier;
import dev.latvian.mods.modularpipes.item.ModularPipesItems;
import dev.latvian.mods.modularpipes.item.WrenchItem;
import dev.latvian.mods.modularpipes.item.module.PipeModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class PipeBlockEntity extends BlockEntity {
	private boolean sync = false;
	private boolean changed = false;
	public List<PipeItem> items = new ArrayList<>(0);
	private PipeTier cachedTier;
	public final PipeSideData[] sideData;

	public PipeBlockEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		sideData = new PipeSideData[6];

		for (int i = 0; i < 6; i++) {
			sideData[i] = new PipeSideData(this, Direction.from3DDataValue(i));
		}
	}

	public void writeData(CompoundTag nbt) {
		for (PipeSideData data : sideData) {
			if (data.shouldWrite()) {
				nbt.put("Data" + data.direction.getSerializedName().substring(0, 1).toUpperCase(), data.write(new CompoundTag()));
			}
		}

		if (!items.isEmpty()) {
			ListTag list = new ListTag();

			for (PipeItem item : items) {
				list.add(item.serializeNBT());
			}

			nbt.put("Items", list);
		}
	}

	public void readData(CompoundTag nbt) {
		ListTag list = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
		items = new ArrayList<>(list.size());

		for (int i = 0; i < list.size(); i++) {
			CompoundTag nbt1 = list.getCompound(i);
			PipeItem item = new PipeItem();
			item.deserializeNBT(nbt1);

			if (!item.stack.isEmpty()) {
				items.add(item);
			}
		}

		for (PipeSideData data : sideData) {
			data.read(nbt.getCompound("Data" + data.direction.getSerializedName().substring(0, 1).toUpperCase()));
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt) {
		writeData(nbt);
		return super.save(nbt);
	}

	@Override
	public void load(BlockState state, CompoundTag nbt) {
		super.load(state, nbt);
		readData(nbt);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return save(new CompoundTag());
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundTag tag) {
		load(state, tag);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag nbt = new CompoundTag();
		writeData(nbt);
		return new ClientboundBlockEntityDataPacket(worldPosition, 0, nbt);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
		int c0 = getModelIndex();
		readData(packet.getTag());

		if (c0 != getModelIndex()) {
			sync();
		}
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		return new ModelDataMap.Builder().withInitial(PipeBlock.PIPE, this).build();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (side != null && sideData[side.get3DDataValue()].module != null) {
			LazyOptional<T> o = sideData[side.get3DDataValue()].module.getCapability(cap, side);

			if (o.isPresent()) {
				return o;
			}
		}

		return super.getCapability(cap, side);
	}

	@Override
	public void setRemoved() {
		if (hasLevel()) {
			PipeNetwork.get(getLevel()).refresh();
		}

		super.setRemoved();
	}

	@Override
	public void setLevelAndPosition(Level world, BlockPos pos) {
		super.setLevelAndPosition(world, pos);

		if (hasLevel()) {
			PipeNetwork.get(getLevel()).refresh();
		}
	}

	public void moveItem(PipeItem item) {
		if (getTier() != PipeTier.BASIC) {
			item.pos += item.speed;
			return;
		}

		item.pos += Math.min(item.speed, 0.99F);
		float pipeSpeed = (float) ModularPipesConfig.pipes.base_speed;

		if (item.speed > pipeSpeed) {
			item.speed *= 0.99F;

			if (item.speed < pipeSpeed) {
				item.speed = pipeSpeed;
			}
		} else if (item.speed < pipeSpeed) {
			item.speed *= 1.3F;

			if (item.speed > pipeSpeed) {
				item.speed = pipeSpeed;
			}
		}
	}

	@Override
	public void setChanged() {
		changed = true;

		if (this instanceof TransportPipeBlockEntity) {
			sendUpdates();
		}
	}

	public void sync() {
		sync = true;
		setChanged();
	}

	public final void sendUpdates() {
		if (changed) {
			changed = false;
			level.blockEntityChanged(worldPosition, this);

			if (sync) {
				sync = false;
				level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 11);
			}
		}
	}

	public void dropItems() {
		for (PipeItem item : items) {
			Block.popResource(level, worldPosition, item.stack);
		}

		for (PipeSideData data : sideData) {
			if (data.module != null) {
				data.module.onPipeBroken();
				Block.popResource(level, worldPosition, data.module.moduleItem);
			}

			if (data.light) {
				Block.popResource(level, worldPosition, new ItemStack(ModularPipesItems.LIGHT.get()));
			}
		}
	}

	private static int coord(double v) {
		return v < PipeBlock.S0 ? -1 : v > PipeBlock.S1 ? 1 : 0;
	}

	private static Direction coordDir(int x, int y, int z, Direction def) {
		for (Direction d : Direction.values()) {
			if (d.getStepX() == x && d.getStepY() == y && d.getStepZ() == z) {
				return d;
			}
		}

		return def;
	}

	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		int x = coord(hit.getLocation().x - worldPosition.getX());
		int y = coord(hit.getLocation().y - worldPosition.getY());
		int z = coord(hit.getLocation().z - worldPosition.getZ());
		Direction side = coordDir(x, y, z, hit.getDirection());
		PipeSideData data = sideData[side.get3DDataValue()];
		boolean isWrench = stack.getHarvestLevel(WrenchItem.WRENCH_TYPE, player, null) >= 0;

		if (isWrench || stack.isEmpty()) {
			if (level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}

			if (isWrench || player.isCrouching()) {
				if (data.light) {
					data.light = false;

					data.updateConnection();
					sync();

					if (!player.isCreative()) {
						ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(ModularPipesItems.LIGHT.get()));
					}

					int lights = 0;

					for (PipeSideData data1 : sideData) {
						if (data1.light) {
							lights++;
						}
					}

					if (lights == 0) {
						level.setBlock(worldPosition, getBlockState().setValue(PipeBlock.LIGHT, false), 3);
						clearCache();
					}
				} else if (data.module != null) {
					if (!data.module.canRemove(player, hand)) {
						player.displayClientMessage(new TextComponent("Can't remove this module!"), true);
						return InteractionResult.SUCCESS;
					}

					data.module.onRemoved(player, hand);

					if (!player.isCreative()) {
						ItemHandlerHelper.giveItemToPlayer(player, data.module.moduleItem);
					}

					data.module = null;
					data.updateConnection();
					sync();
				} else if (isWrench) {
					data.setDisabled(!data.disabled);

					if (data.disabled) {
						player.displayClientMessage(new TextComponent("Side disabled!"), true);
					} else {
						player.displayClientMessage(new TextComponent("Side enabled!"), true);
					}

					BlockEntity entity = level.getBlockEntity(worldPosition.relative(data.direction));

					if (entity instanceof PipeBlockEntity) {
						// ((PipeBlockEntity) entity).sideData[data.direction.getOpposite().get3DDataValue()].setDisabled(data.disabled);
						((PipeBlockEntity) entity).sideData[data.direction.getOpposite().get3DDataValue()].updateConnection();
					}
				}
			}

			return InteractionResult.SUCCESS;
		}

		if (stack.getItem() == ModularPipesItems.LIGHT.get()) {
			if (data.light) {
				player.displayClientMessage(new TextComponent("This slot already has a light in it!"), true);
				return InteractionResult.SUCCESS;
			} else if (data.module != null) {
				player.displayClientMessage(new TextComponent("This slot already has a module in it!"), true);
				return InteractionResult.SUCCESS;
			}

			data.light = true;

			if (!player.isCreative()) {
				stack.shrink(1);
			}

			data.updateConnection();
			sync();

			int lights = 0;

			for (PipeSideData data1 : sideData) {
				if (data1.light) {
					lights++;
				}
			}

			if (lights == 1) {
				level.setBlock(worldPosition, getBlockState().setValue(PipeBlock.LIGHT, true), 3);
				clearCache();
			}
			return InteractionResult.SUCCESS;
		} else if (getTier().maxModules == 0) {
			return InteractionResult.PASS;
		}

		PipeModule module = stack.getCapability(PipeModule.CAP).orElse(null);

		if (module == null) {
			return InteractionResult.PASS;
		} else if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (data.light) {
			player.displayClientMessage(new TextComponent("This slot already has a light in it!"), true);
			return InteractionResult.SUCCESS;
		}

		if (data.module != null) {
			player.displayClientMessage(new TextComponent("Module slot already occupied!"), true);
			return InteractionResult.SUCCESS;
		} else {
			int currentModuleCount = 0;

			for (PipeSideData data1 : sideData) {
				if (data1.module != null) {
					currentModuleCount++;
				}
			}

			if (currentModuleCount >= getTier().maxModules) {
				player.displayClientMessage(new TextComponent("All module slots occupied!"), true);
				return InteractionResult.SUCCESS;
			}
		}

		module.sideData = data;
		module.moduleItem = ItemHandlerHelper.copyStackWithSize(stack, 1);

		if (!module.canInsert(player, hand)) {
			player.displayClientMessage(new TextComponent("Module slot already occupied!"), true);
			return InteractionResult.SUCCESS;
		}

		data.module = module;
		module.onInserted(player, hand);

		if (!player.isCreative()) {
			stack.shrink(1);
		}

		clearCache();
		data.updateConnection();
		sync();
		return InteractionResult.SUCCESS;
	}

	public PipeTier getTier() {
		if (cachedTier == null) {
			cachedTier = ((PipeBlock) getBlockState().getBlock()).tier;
		}

		return cachedTier;
	}

	public int getModelIndex() {
		int index = 0;

		for (int i = 0; i < 6; i++) {
			index |= sideData[i].getModelIndex() << (i * 5);
		}

		return index;
	}
}