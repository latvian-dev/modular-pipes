package com.latmod.modularpipes.tile;

import com.feed_the_beast.ftblib.lib.tile.EnumSaveType;
import com.feed_the_beast.ftblib.lib.tile.TileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class TileController extends TileBase implements ITickable, IModularPipeNetworkTile
{
	private boolean hasError = false;

	@Override
	protected void writeData(NBTTagCompound nbt, EnumSaveType type)
	{
		nbt.setBoolean("Error", hasError);
	}

	@Override
	protected void readData(NBTTagCompound nbt, EnumSaveType type)
	{
		hasError = nbt.getBoolean("Error");
	}

	@Override
	public void update()
	{
		checkIfDirty();
	}

	@Override
	public void updateNetworkTile()
	{
	}

	@Override
	public TileController getController()
	{
		return this;
	}

	@Override
	public void setControllerPosition(BlockPos pos)
	{
	}

	@Override
	public boolean hasError()
	{
		return hasError || isInvalid();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}

	public void onRightClick(EntityPlayer player, EnumHand hand, EnumFacing facing)
	{
		hasError = !hasError;
		markDirty();
	}
}