package dev.latvian.mods.modularpipes.block.entity;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class PipeNetwork implements ICapabilityProvider {
	public static final float[] POS_X = new float[6];
	public static final float[] POS_Y = new float[6];
	public static final float[] POS_Z = new float[6];
	public static final float[] ROT_X = new float[6];
	public static final float[] ROT_Y = new float[6];
	public static final int[] OPPOSITE = new int[7];

	public static final Capability<PipeNetwork> CAP = CapabilityManager.get(new CapabilityToken<>() {
	});

	static {
		Direction[] directions = Direction.values();
		for (int i = 0; i < 6; i++) {
			POS_X[i] = directions[i].getStepX();
			POS_Y[i] = directions[i].getStepY();
			POS_Z[i] = directions[i].getStepZ();
			OPPOSITE[i] = directions[i].getOpposite().get3DDataValue();
		}

		OPPOSITE[6] = 6;

		ROT_X[0] = 90F;
		ROT_Y[0] = 0F;
		ROT_X[1] = 270F;
		ROT_Y[1] = 180F;
		ROT_X[2] = 0F;
		ROT_Y[2] = 180F;
		ROT_X[3] = 0F;
		ROT_Y[3] = 0F;
		ROT_X[4] = 0F;
		ROT_Y[4] = 270F;
		ROT_X[5] = 0F;
		ROT_Y[5] = 90F;
	}

	public final Level world;
	public final List<ModularPipeBlockEntity> pipes = new ArrayList<>();
	protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);
	private boolean refresh = true;

	public PipeNetwork(Level w) {
		world = w;
	}

	@Nullable
	public static PipeNetwork get(@Nullable Level world) {
		return world == null ? null : world.getCapability(CAP, null).orElse(null);
	}

	@Nullable
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		return capability == CAP ? thisOptional.cast() : LazyOptional.empty();
	}

	public void refresh() {
		refresh = true;
	}

	public void tick() {
		ProfilerFiller profiler = world.getProfiler();
		profiler.push("ModularPipes");

		if (refresh) {
			pipes.clear();

			/*
			for (BlockEntity tileEntity : world.blockEntityList) {
				if (!tileEntity.isRemoved() && tileEntity instanceof ModularPipeBlockEntity) {
					pipes.add((ModularPipeBlockEntity) tileEntity);
				}
			}
			 */

			for (PipeBlockEntity pipe : pipes) {
				pipe.clearCache();
			}

			refresh = false;
		}

		if (!pipes.isEmpty()) {
			profiler.push("ItemMoving");

			for (ModularPipeBlockEntity pipe : pipes) {
				if (!pipe.items.isEmpty()) {
					for (PipeItem item : pipe.items) {
						item.prevPos = item.pos;
						pipe.moveItem(item);
					}
				}
			}

			profiler.popPush("PipeTick");

			for (ModularPipeBlockEntity pipe : pipes) {
				pipe.tickPipe();
				pipe.sendUpdates();
			}

			profiler.pop();
		}

		profiler.pop();
	}

	private boolean shouldRender() {
		if (pipes.isEmpty()) {
			return false;
		}

		for (ModularPipeBlockEntity pipe : pipes) {
			if (!pipe.items.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	public void render(RenderWorldLastEvent event) {
		if (!shouldRender()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		ItemRenderer renderItem = mc.getItemRenderer();
		PoseStack matrix = event.getMatrixStack();
		float delta = event.getPartialTicks();
		MultiBufferSource multiBufferSource = mc.renderBuffers().bufferSource();

		double renderDistanceSq = 64 * 64;
		float pos;
		float rx, ry, rz;
		float scale, rotX, rotY;
		double px = mc.getBlockEntityRenderDispatcher().camera.getPosition().x;
		double py = mc.getBlockEntityRenderDispatcher().camera.getPosition().y;
		double pz = mc.getBlockEntityRenderDispatcher().camera.getPosition().z;
		Frustum frustum = new Frustum(matrix.last().pose(), event.getProjectionMatrix());
		frustum.prepare(px, py, pz);
		matrix.pushPose();
		matrix.translate(-px, -py, -pz);
		// FIXME: RenderSystem.disableLighting();
		Lighting.setupFor3DItems();

		for (ModularPipeBlockEntity pipe : pipes) {
			if (pipe.items.isEmpty()) {
				continue;
			}

			BlockPos p = pipe.getBlockPos();

			if (p.distSqr(px, py, pz, true) > renderDistanceSq || !frustum.isVisible(new AABB(p))) {
				continue;
			}

			matrix.pushPose();
			matrix.translate(p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D);

			for (PipeItem item : pipe.items) {
				if (item.from == 6 || item.to == 6) {
					continue;
				}

				pos = (item.prevPos + (item.pos - item.prevPos) * delta);

				if (pos < 0.5D) {
					rx = POS_X[item.from] * (0.5F - pos);
					ry = POS_Y[item.from] * (0.5F - pos);
					rz = POS_Z[item.from] * (0.5F - pos);
					rotX = ROT_X[item.from];
					rotY = ROT_Y[item.from];
				} else {
					rx = POS_X[item.to] * (pos - 0.5F);
					ry = POS_Y[item.to] * (pos - 0.5F);
					rz = POS_Z[item.to] * (pos - 0.5F);
					rotX = ROT_X[OPPOSITE[item.to]];
					rotY = ROT_Y[OPPOSITE[item.to]];
				}

				scale = item.getScale(mc, renderItem);

				matrix.pushPose();
				matrix.translate(rx, ry, rz);
				matrix.mulPose(Vector3f.YP.rotationDegrees(rotY));
				matrix.mulPose(Vector3f.XP.rotationDegrees(rotX));
				matrix.scale(scale, scale, scale);
				item.render(matrix, renderItem, multiBufferSource, 15728880, OverlayTexture.NO_OVERLAY);
				matrix.popPose();
			}

			matrix.popPose();
		}

		// Lighting.setupForFlatItems();
		// FIXME: RenderSystem.enableLighting();
		matrix.popPose();
	}
}