package com.latmod.mods.modularpipes.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class PipeItem implements INBTSerializable<NBTTagCompound>
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
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = stack.serializeNBT();
		nbt.setInteger("age", age);
		nbt.setFloat("pos", pos);
		nbt.setFloat("prevpos", prevPos);
		nbt.setByte("dir", (byte) (from | (to << 4)));

		if (speed != 0.05F)
		{
			nbt.setFloat("speed", speed);
		}

		if (lifespan != 6000)
		{
			nbt.setInteger("lifespan", 6000);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		stack = new ItemStack(nbt);
		age = nbt.getInteger("age");
		pos = nbt.getFloat("pos");
		prevPos = nbt.getFloat("prevpos");
		int dir = nbt.getByte("dir") & 0xFF;
		from = dir & 0xF;
		to = (dir >> 4) & 0xF;
		speed = nbt.hasKey("speed") ? MathHelper.clamp(nbt.getFloat("speed"), 0.01F, 1F) : 0.05F;
		lifespan = nbt.hasKey("lifespan") ? nbt.getInteger("lifespan") : 6000;
	}

	@SideOnly(Side.CLIENT)
	public void render(RenderItem renderItem)
	{
		renderItem.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
	}

	@SideOnly(Side.CLIENT)
	public float getScale(Minecraft mc, RenderItem renderItem)
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