package com.latmod.modularpipes.item.module;

import com.latmod.modularpipes.PipeNetwork;
import com.latmod.modularpipes.api.Module;
import com.latmod.modularpipes.api.ModuleContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * @author LatvianModder
 */
public class ModuleExtract extends Module
{
    @Override
    public void update(ModuleContainer container)
    {
        if(container.getTile().getWorld().isRemote && container.getTick() % 20 == 0)
        {
            BlockPos pos = container.getTile().getPos();
            Vec3i p1 = container.getFacing().getDirectionVec();
            double d = 0.4D;

            for(int i = 0; i < 10; i++)
            {
                container.getTile().getWorld().spawnParticle(EnumParticleTypes.REDSTONE, pos.getX() + 0.5D + p1.getX() * d, pos.getY() + 0.5D + p1.getY() * d, pos.getZ() + 0.5D + p1.getZ() * d, 0D, 0D, 0D);
            }
        }
    }

    @Override
    public boolean onRightClick(ModuleContainer container, EntityPlayer player, EnumHand hand)
    {
        PipeNetwork.sendItemFrom(container, new ItemStack(Items.APPLE));
        return true;
    }
}