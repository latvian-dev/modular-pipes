package dev.latvian.mods.modularpipes.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
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
	@CapabilityInject(PipeNetwork.class)
	public static Capability<PipeNetwork> CAP;

	static {
		Direction[] directions = Direction.values();
		for (int i = 0; i < 6; i++) {
			POS_X[i] = directions[i].getXOffset();
			POS_Y[i] = directions[i].getYOffset();
			POS_Z[i] = directions[i].getZOffset();
			OPPOSITE[i] = directions[i].getOpposite().getIndex();
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

	public final World world;
	public final List<TilePipeModularMK1> pipes = new ArrayList<>();
	protected LazyOptional<?> thisOptional = LazyOptional.of(() -> this);
	private boolean refresh = true;

	public PipeNetwork(World w) {
		world = w;
	}

	@Nullable
	public static PipeNetwork get(@Nullable World world) {
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
		if (refresh) {
			pipes.clear();

			for (TileEntity tileEntity : world.loadedTileEntityList) {
				if (!tileEntity.isRemoved() && tileEntity instanceof TilePipeModularMK1) {
					pipes.add((TilePipeModularMK1) tileEntity);
				}
			}

			for (TilePipeBase pipe : pipes) {
				pipe.updateContainingBlockInfo();
			}

			refresh = false;
		}

		for (TilePipeModularMK1 pipe : pipes) {
			for (PipeItem item : pipe.items) {
				item.prevPos = item.pos;
				pipe.moveItem(item);
			}
		}

		for (TilePipeModularMK1 pipe : pipes) {
			pipe.tickPipe();
			pipe.sendUpdates();
		}
	}

	private boolean shouldRender() {
		if (pipes.isEmpty()) {
			return false;
		}

		for (TilePipeModularMK1 pipe : pipes) {
			if (!pipe.items.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	public void render(float partialTicks) {
		if (!shouldRender()) {
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		ItemRenderer renderItem = mc.getItemRenderer();
		double renderDistanceSq = 64 * 64;
		float pos;
		float rx, ry, rz;
		float scale, rotX, rotY;
		double px = TileEntityRendererDispatcher.staticPlayerX;
		double py = TileEntityRendererDispatcher.staticPlayerY;
		double pz = TileEntityRendererDispatcher.staticPlayerZ;
		Frustum frustum = new Frustum();
		frustum.setPosition(px, py, pz);
		GlStateManager.pushMatrix();
		GlStateManager.translated(-px, -py, -pz);
		GlStateManager.disableLighting();
		RenderHelper.enableStandardItemLighting();

		for (TilePipeModularMK1 pipe : pipes) {
			if (pipe.items.isEmpty()) {
				continue;
			}

			BlockPos p = pipe.getPos();

			if (p.distanceSq(px, py, pz, true) > renderDistanceSq || !frustum.isBoxInFrustum(p.getX(), p.getY(), p.getZ(), p.getX() + 1, p.getY() + 1, p.getZ() + 1)) {
				continue;
			}

			GlStateManager.pushMatrix();
			GlStateManager.translated(p.getX() + 0.5D, p.getY() + 0.5D, p.getZ() + 0.5D);

			for (PipeItem item : pipe.items) {
				if (item.from == 6 || item.to == 6) {
					continue;
				}

				pos = (item.prevPos + (item.pos - item.prevPos) * partialTicks);

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

				GlStateManager.pushMatrix();
				GlStateManager.translatef(rx, ry, rz);
				GlStateManager.rotatef(rotY, 0F, 1F, 0F);
				GlStateManager.rotatef(rotX, 1F, 0F, 0F);
				GlStateManager.scalef(scale, scale, scale);
				item.render(renderItem);
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
		}

		RenderHelper.disableStandardItemLighting();
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}