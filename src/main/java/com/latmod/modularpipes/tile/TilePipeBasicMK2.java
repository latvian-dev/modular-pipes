package com.latmod.modularpipes.tile;

/**
 * @author LatvianModder
 */
public class TilePipeBasicMK2 extends TilePipeBasicMK1
{
	@Override
	public void moveItem(PipeItem item)
	{
		item.pos += item.speed;
	}
}