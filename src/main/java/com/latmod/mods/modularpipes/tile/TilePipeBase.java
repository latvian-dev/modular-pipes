package com.latmod.mods.modularpipes.tile;

import com.latmod.mods.modularpipes.ModularPipesConfig;
import com.latmod.mods.modularpipes.block.PipeSkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public class TilePipeBase extends TileBase
{
	private boolean isDirty = false;
	public boolean sync = false;
	public PipeSkin skin = PipeSkin.NONE;
	public boolean invisible = false;

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		if (skin != PipeSkin.NONE)
		{
			nbt.setString("skin", skin.name);
		}

		if (invisible)
		{
			nbt.setBoolean("invisible", true);
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		skin = PipeSkin.byName(nbt.getString("skin"));
		invisible = nbt.getBoolean("invisible");
	}

	@Override
	public void invalidate()
	{
		if (hasWorld())
		{
			PipeNetwork.get(getWorld()).refresh();
		}

		super.invalidate();
	}

	@Override
	public void setWorld(World world)
	{
		super.setWorld(world);

		if (hasWorld())
		{
			PipeNetwork.get(getWorld()).refresh();
		}
	}

	public void moveItem(PipeItem item)
	{
		item.pos += Math.min(item.speed, 0.99F);
		float pipeSpeed = (float) ModularPipesConfig.pipes.base_speed;

		if (item.speed > pipeSpeed)
		{
			item.speed *= 0.99F;

			if (item.speed < pipeSpeed)
			{
				item.speed = pipeSpeed;
			}
		}
		else if (item.speed < pipeSpeed)
		{
			item.speed *= 1.3F;

			if (item.speed > pipeSpeed)
			{
				item.speed = pipeSpeed;
			}
		}
	}

	@Override
	public void markDirty()
	{
		isDirty = true;
		sync = true;
	}

	public final void sendUpdates()
	{
		if (isDirty)
		{
			super.markDirty();

			if (!world.isRemote && sync)
			{
				IBlockState state = world.getBlockState(pos);
				world.notifyBlockUpdate(pos, state, state, 11);
			}

			isDirty = false;
		}
	}

	public boolean canPipesConnect(PipeSkin s)
	{
		return skin == s || skin == PipeSkin.NONE || s == PipeSkin.NONE;
	}

	public boolean isConnected(EnumFacing facing)
	{
		return false;
	}
}