package dev.latvian.mods.modularpipes.block.entity;

import dev.latvian.mods.modularpipes.ModularPipesUtils;
import dev.latvian.mods.modularpipes.item.ItemKey;
import dev.latvian.mods.modularpipes.item.ModuleItem;
import dev.latvian.mods.modularpipes.item.module.PipeModule;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ModularPipeBlockEntity extends BasePipeBlockEntity {
	public final CachedBlockEntity[] cachedTiles = new CachedBlockEntity[6];
	public PipeModule[] modules = new PipeModule[6];
	public Object2IntOpenHashMap<ItemKey> itemDirections = new Object2IntOpenHashMap<>(0);
	public int storedEnergy = 0;
	private int powerOutputIndex = -1;
	private List<ModularPipeBlockEntity> cachedNetwork = null;

	public ModularPipeBlockEntity() {
		super(ModularPipesBlockEntities.MODULAR_PIPE.get());
	}

	@Override
	public void writeData(CompoundTag nbt) {
		super.writeData(nbt);

		ListTag list = new ListTag();

		for (PipeModule module : modules) {
			if (module != null) {
				CompoundTag nbt1 = module.moduleItem.serializeNBT();
				CompoundTag nbt2 = new CompoundTag();
				module.writeData(nbt2);

				if (!nbt2.isEmpty()) {
					nbt1.put("Module", nbt2);
				}

				list.add(nbt1);
			}
		}

		if (!list.isEmpty()) {
			nbt.put("Modules", list);
		}

		if (storedEnergy > 0) {
			nbt.putInt("Energy", storedEnergy);
		}
	}

	@Override
	public void readData(CompoundTag nbt) {
		super.readData(nbt);

		ListTag list = nbt.getList("Modules", Constants.NBT.TAG_COMPOUND);
		Arrays.fill(modules, null);

		for (int i = 0; i < list.size(); i++) {
			CompoundTag nbt1 = list.getCompound(i);
			ItemStack stack = ItemStack.of(nbt1);
			PipeModule module = stack.getCapability(PipeModule.CAP, null).orElse(null);

			if (module != null) {
				module.pipe = this;
				module.moduleItem = stack;
				module.readData(nbt1.getCompound("Module"));
				modules[module.side.get3DDataValue()] = module;
			}
		}

		storedEnergy = nbt.getInt("Energy");
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
		for (PipeModule module : modules) {
			LazyOptional<T> t = module.getCapability(capability, side);

			if (t.isPresent()) {
				return t.cast();
			}
		}

		return super.getCapability(capability, side);
	}

	public void tickPipe() {
		if (!level.isClientSide()) {
			for (PipeModule module : modules) {
				if (module != null && module.canUpdate()) {
					module.updateModule();
				}
			}
		}

		if (!level.isClientSide() && storedEnergy > 0 && level.getGameTime() % 5L == 0L) {
			if (powerOutputIndex == -1) {
				powerOutputIndex = hashCode() % 6;

				if (powerOutputIndex < 0) {
					powerOutputIndex = 6 - powerOutputIndex;
				}
			}

			CachedBlockEntity tileEntity = getBlockEntity(Direction.from3DDataValue(powerOutputIndex));

			if (tileEntity.blockEntity instanceof ModularPipeBlockEntity) {
				ModularPipeBlockEntity pipe = (ModularPipeBlockEntity) tileEntity.blockEntity;

				if (Math.abs(storedEnergy - pipe.storedEnergy) > 1) {
					int a = (storedEnergy + pipe.storedEnergy) / 2;

					storedEnergy = a;
					setChanged();

					pipe.storedEnergy = a;
					pipe.setChanged();
				}
			}

			powerOutputIndex++;
		}

		if (items.isEmpty()) {
			return;
		}

		for (PipeItem item : items.size() == 1 ? Collections.singletonList(items.get(0)) : new ArrayList<>(items)) {
			item.age++;

			if (item.to == 6 || item.from == 6) {
				item.age = Integer.MAX_VALUE;
			} else if (item.pos >= 1F) {
				/*
				IItemHandler handler = getInventory(world, item.to);

				if (handler instanceof IPipeItemHandler && ((IPipeItemHandler) handler).insertPipeItem(item.copyForTransfer(null), false))
				{
					markDirty();
					sync = false;
				}
				else
				{
					ItemStack stack = ItemHandlerHelper.insertItem(handler, item.stack, world.isRemote);

					if (!stack.isEmpty())
					{
						IPipeItemHandler opposite = getPipeItemHandler(Direction.VALUES[item.to].getOpposite());

						if (opposite != null)
						{
							if (!opposite.insertPipeItem(item.copyForTransfer(stack), false) && !world.isRemote)
							{
								InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
							}
						}
						else if (!world.isRemote)
						{
							InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
						}

						markDirty();

						if (handler == opposite)
						{
							sync = false;
						}
					}
				}
				*/

				item.age = Integer.MAX_VALUE;
			}
		}

		if (items.removeIf(PipeItem.IS_DEAD)) {
			setChanged();
		}
	}

	@Override
	public void clearCache() {
		super.clearCache();
		Arrays.fill(cachedTiles, null);
		itemDirections = new Object2IntOpenHashMap<>(0);

		for (PipeModule module : modules) {
			if (module != null) {
				module.clearCache();
			}
		}

		cachedNetwork = null;
	}

	public CachedBlockEntity getBlockEntity(Direction facing) {
		int f = facing.get3DDataValue();

		if (cachedTiles[f] == null) {
			cachedTiles[f] = CachedBlockEntity.NONE;

			BlockPos pos1 = worldPosition.relative(facing);
			BlockEntity tileEntity = level.getBlockEntity(pos1);

			if (tileEntity instanceof BasePipeBlockEntity) {
				if (canPipesConnect()) {
					cachedTiles[f] = new CachedBlockEntity(tileEntity, 1);
				}
			} else if (tileEntity != null) {
				cachedTiles[f] = new CachedBlockEntity(tileEntity, 1);
			}

			if (cachedTiles[f].blockEntity != null) {
				for (int i = 0; i < 6; i++) {
					if (i != f && cachedTiles[i] != null && cachedTiles[f].blockEntity == cachedTiles[i].blockEntity) {
						if (cachedTiles[f].distance < cachedTiles[i].distance) {
							cachedTiles[i] = CachedBlockEntity.NONE;
						} else {
							cachedTiles[f] = CachedBlockEntity.NONE;
						}

						break;
					}
				}
			}
		}

		return cachedTiles[f];
	}

	@Override
	public int updateConnection(int face) {
		if (modules[face] != null) {
			return 2;
		}

		// Check for covers and return 3

		return super.updateConnection(face);
	}

	@Override
	public void dropItems() {
		super.dropItems();

		for (PipeModule module : modules) {
			if (module != null) {
				module.onPipeBroken();
				Block.popResource(level, worldPosition, module.moduleItem);
			}
		}
	}

	public List<ModularPipeBlockEntity> getPipeNetwork() {
		if (cachedNetwork == null) {
			HashSet<ModularPipeBlockEntity> set = new HashSet<>();
			getNetwork(set);
			cachedNetwork = new ArrayList<>(set);

			if (cachedNetwork.size() > 1) {
				cachedNetwork.sort(Comparator.comparingDouble(value -> worldPosition.distSqr(value.worldPosition)));
			}

			cachedNetwork = ModularPipesUtils.optimize(cachedNetwork);
		}

		return cachedNetwork;
	}

	private void getNetwork(HashSet<ModularPipeBlockEntity> set) {
		for (Direction facing : Direction.values()) {
			BlockEntity tileEntity = getBlockEntity(facing).blockEntity;

			if (tileEntity instanceof ModularPipeBlockEntity && !set.contains(tileEntity)) {
				set.add((ModularPipeBlockEntity) tileEntity);
				((ModularPipeBlockEntity) tileEntity).getNetwork(set);
			}
		}
	}

	public InteractionResult rightClick(Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);

		if (stack.isEmpty()) {
			return InteractionResult.SUCCESS;
		} else if (stack.getItem() instanceof ModuleItem) {
			if (level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}

			Direction side = hit.getDirection();

			if (modules[side.get3DDataValue()] != null) {
				player.displayClientMessage(new TextComponent("Module slot already occupied!"), true);
				return InteractionResult.SUCCESS;
			} else {
				int currentModuleCount = 0;

				for (int i = 0; i < 6; i++) {
					if (modules[i] != null) {
						currentModuleCount++;
					}
				}

				if (currentModuleCount >= getTier().maxModules) {
					player.displayClientMessage(new TextComponent("All module slots occupied!"), true);
					return InteractionResult.SUCCESS;
				}
			}

			PipeModule module = new PipeModule();
			module.pipe = this;
			module.moduleItem = ItemHandlerHelper.copyStackWithSize(stack, 1);
			module.side = side;

			if (!module.canInsert(player, hand)) {
				player.displayClientMessage(new TextComponent("Module slot already occupied!"), true);
				return InteractionResult.SUCCESS;
			}

			modules[module.side.get3DDataValue()] = module;
			module.onInserted(player, hand);
			stack.shrink(1);
			clearCache();
			getConnections();
			sync();
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}
}