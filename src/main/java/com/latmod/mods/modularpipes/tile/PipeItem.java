package com.latmod.mods.modularpipes.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class PipeItem implements INBTSerializable<CompoundNBT>
{
	public static final Predicate<PipeItem> IS_DEAD = item -> item.age == Integer.MAX_VALUE || item.age > item.lifespan;

	public ItemStack stack = ItemStack.EMPTY;
	public float pos = 0F, prevPos = 0F;
	public int from = 6;
	public int to = 6;
	public float speed = 0.05F;
	public int age = 0;
	public int lifespan = 6000;
	private float scale = -1F;

	public PipeItem copyForTransfer(@Nullable ItemStack newStack)
	{
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
	public CompoundNBT serializeNBT()
	{
		CompoundNBT nbt = stack.serializeNBT();
		nbt.putInt("age", age);
		nbt.putFloat("pos", pos);
		nbt.putFloat("prevpos", prevPos);
		nbt.putByte("dir", (byte) (from | (to << 4)));

		if (speed != 0.05F)
		{
			nbt.putFloat("speed", speed);
		}

		if (lifespan != 6000)
		{
			nbt.putInt("lifespan", 6000);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt)
	{
		stack = ItemStack.read(nbt);
		age = nbt.getInt("age");
		pos = nbt.getFloat("pos");
		prevPos = nbt.getFloat("prevpos");
		int dir = nbt.getByte("dir") & 0xFF;
		from = dir & 0xF;
		to = (dir >> 4) & 0xF;
		speed = nbt.contains("speed") ? MathHelper.clamp(nbt.getFloat("speed"), 0.01F, 1F) : 0.05F;
		lifespan = nbt.contains("lifespan") ? nbt.getInt("lifespan") : 6000;
	}

	@OnlyIn(Dist.CLIENT)
	public void render(ItemRenderer renderItem)
	{
		renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
	}

	@OnlyIn(Dist.CLIENT)
	public float getScale(Minecraft mc, ItemRenderer renderItem)
	{
		if (scale <= 0F)
		{
			scale = 0.4F;

			if (renderItem.shouldRenderItemIn3D(stack))
			{
				scale = 0.72F;
			}

			scale += mc.world.rand.nextFloat() * 0.01F;
		}

		return scale;
	}
}