package com.latmod.modularpipes.data;

import com.latmod.modularpipes.client.ModularPipesClientEventHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemSkull;
import net.minecraft.util.math.BlockPos;

/**
 * @author LatvianModder
 */
public class TransportedItemClient extends TransportedItem
{
    private double posX, posY, posZ;
    private double prevX, prevY, prevZ;
    private float rotationY;

    public TransportedItemClient()
    {
    }

    @Override
    public void copyFrom(TransportedItem item)
    {
        super.copyFrom(item);

        if(stack.getItem() instanceof ItemSkull || !ModularPipesClientEventHandler.RENDER_ITEM.shouldRenderItemIn3D(stack))
        {
            rotationY = 180F;
        }

        updatePosition();
    }

    @Override
    public void update()
    {
        super.update();
        updatePosition();
    }

    public void render()
    {
        if(action == TransportedItem.Action.HIDE || path.isEmpty())
        {
            return;
        }

        double x = prevX + (posX - prevX) * ModularPipesClientEventHandler.partialTicks;
        double y = prevY + (posY - prevY) * ModularPipesClientEventHandler.partialTicks;
        double z = prevZ + (posZ - prevZ) * ModularPipesClientEventHandler.partialTicks;
        float s = 0.5F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(s, s, s);
        GlStateManager.rotate(rotationY, 0F, 1F, 0F);
        ModularPipesClientEventHandler.RENDER_ITEM.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();
    }

    public void updatePosition()
    {
        prevX = posX;
        prevY = posY;
        prevZ = posZ;

        BlockPos pos = path.get(0);
        posX = pos.getX() + 0.5D;
        posY = pos.getY() + 0.5D;
        posZ = pos.getZ() + 0.5D;
    }
}