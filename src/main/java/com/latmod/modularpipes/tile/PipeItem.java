package com.latmod.modularpipes.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class PipeItem implements INBTSerializable<NBTTagCompound>
{
	public ItemStack stack = ItemStack.EMPTY;
	public float pos = 0F;
	public float speed = 0.05F;
	public long created = 0L;
	public int lifespan = -1;
	private float scale = -1F;

	public int getLifespan(World world)
	{
		if (lifespan == -1)
		{
			lifespan = stack.getItem().getEntityLifespan(stack, world);
		}

		return lifespan;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = stack.serializeNBT();
		nbt.setLong("created", created);
		nbt.setFloat("pos", pos);

		if (speed != 0.05F)
		{
			nbt.setFloat("speed", speed);
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		stack = new ItemStack(nbt);
		created = nbt.getLong("created");
		pos = nbt.getFloat("pos");
		speed = nbt.hasKey("speed") ? MathHelper.clamp(nbt.getFloat("speed"), 0.01F, 1F) : 0.05F;
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

	public void move(float pipeSpeed)
	{
		pos += speed;

		if (speed > pipeSpeed)
		{
			speed *= 0.98F;

			if (speed < pipeSpeed)
			{
				speed = pipeSpeed;
			}
		}
		else if (speed < pipeSpeed)
		{
			speed *= 1.3F;

			if (speed > pipeSpeed)
			{
				speed = pipeSpeed;
			}
		}
	}
}