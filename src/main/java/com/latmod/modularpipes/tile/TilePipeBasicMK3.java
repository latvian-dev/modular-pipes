package com.latmod.modularpipes.tile;

/**
 * @author LatvianModder
 */
public class TilePipeBasicMK3 extends TilePipeBasicMK2
{
	@Override
	public void moveItem(PipeItem item)
	{
		item.pos += item.speed;
		float pipeSpeed = 0.25F;

		if (item.speed < pipeSpeed)
		{
			item.speed *= 1.3F;

			if (item.speed > pipeSpeed)
			{
				item.speed = pipeSpeed;
			}
		}
	}
}