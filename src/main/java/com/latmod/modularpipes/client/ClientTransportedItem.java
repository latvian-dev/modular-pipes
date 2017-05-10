package com.latmod.modularpipes.client;

import com.latmod.modularpipes.data.TransportedItem;
import net.minecraft.item.ItemSkull;

/**
 * @author LatvianModder
 */
public class ClientTransportedItem extends TransportedItem
{
    public float rotationY, scale;

    public ClientTransportedItem(int _id)
    {
        super(ClientPipeNetwork.get());
        id = _id;
    }

    @Override
    public ClientTransportedItem client()
    {
        return this;
    }

    @Override
    public void copyFrom(TransportedItem item)
    {
        super.copyFrom(item);
        rotationY = 0F;
        scale = 0.49F;

        boolean render3D = ClientPipeNetwork.RENDER_ITEM.shouldRenderItemIn3D(stack);

        if(!render3D || stack.getItem() instanceof ItemSkull)
        {
            rotationY = 180F;
        }

        if(render3D)
        {
            scale = 0.749F;
        }
    }

    public boolean visible()
    {
        return !action.invisible();
    }
}