package dev.latvian.mods.modularpipes.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class PipeItem implements INBTSerializable<CompoundTag> {
	public static final Predicate<PipeItem> IS_DEAD = item -> item.age == Integer.MAX_VALUE || item.age > item.lifespan;

	public ItemStack stack = ItemStack.EMPTY;
	public float pos = 0F, prevPos = 0F;
	public int from = 6;
	public int to = 6;
	public float speed = 0.05F;
	public int age = 0;
	public int lifespan = 6000;
	private float scale = -1F;

	public PipeItem copyForTransfer(@Nullable ItemStack newStack) {
		PipeItem item = new PipeItem();
		item.stack = newStack == null ? stack.copy() : newStack;
		item.pos = pos % 1F;
		item.prevPos = item.pos - (pos - prevPos);
		item.from = from;
		item.to = to;
		item.speed = speed;
		item.age = age;
		item.lifespan = lifespan;
		item.scale = scale;
		return item;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = stack.serializeNBT();
		nbt.putInt("Age", age);
		nbt.putFloat("Pos", pos);
		nbt.putFloat("Prevpos", prevPos);
		nbt.putByte("Dir", (byte) (from | (to << 4)));

		if (speed != 0.05F) {
			nbt.putFloat("Speed", speed);
		}

		if (lifespan != 6000) {
			nbt.putInt("Lifespan", 6000);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		stack = ItemStack.of(nbt);
		age = nbt.getInt("Age");
		pos = nbt.getFloat("Pos");
		prevPos = nbt.getFloat("Prevpos");
		int dir = nbt.getByte("Dir") & 0xFF;
		from = dir & 0xF;
		to = (dir >> 4) & 0xF;
		speed = nbt.contains("Speed") ? Mth.clamp(nbt.getFloat("Speed"), 0.01F, 1F) : 0.05F;
		lifespan = nbt.contains("Lifespan") ? nbt.getInt("Lifespan") : 6000;
	}

	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack poseStack, ItemRenderer renderItem, MultiBufferSource multiBufferSource, int light, int overlay) {
		renderItem.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlay, poseStack, multiBufferSource);
	}

	@OnlyIn(Dist.CLIENT)
	public float getScale(Minecraft mc, ItemRenderer renderItem) {
		if (scale <= 0F) {
			scale = 0.4F;

			// if (renderItem.shouldRenderItemIn3D(stack)) {
			// 	scale = 0.72F;
			// }

			scale += mc.level.random.nextFloat() * 0.01F;
		}

		return scale;
	}
}