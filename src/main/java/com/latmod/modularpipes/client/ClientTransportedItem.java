package com.latmod.modularpipes.client;

import com.feed_the_beast.ftbl.lib.client.ClientUtils;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import net.minecraft.item.ItemSkull;

/**
 * @author LatvianModder
 */
public class ClientTransportedItem extends TransportedItem
{
	public float rotationY, scale;
	public int renderTick;
	public boolean visible = true;

	public ClientTransportedItem(PipeNetwork n, int _id)
	{
		super(n);
		id = _id;
	}

	@Override
	public ClientTransportedItem client()
	{
		return this;
	}

	@Override
	public void update()
	{
		super.update();
		renderTick++;
	}

	@Override
	public void copyFrom(TransportedItem item)
	{
		super.copyFrom(item);
		rotationY = 0F;
		scale = 0.49F;

		boolean render3D = ClientUtils.MC.getRenderItem().shouldRenderItemIn3D(stack);

		if (!render3D || stack.getItem() instanceof ItemSkull)
		{
			rotationY = 180F;
		}

		if (render3D)
		{
			scale = 0.749F;
		}
	}
}