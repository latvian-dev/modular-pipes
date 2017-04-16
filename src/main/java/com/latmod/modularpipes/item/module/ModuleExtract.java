package com.latmod.modularpipes.item.module;

import com.latmod.modularpipes.api.Module;
import com.latmod.modularpipes.api.ModuleContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public class ModuleExtract extends Module
{
    @Override
    public void update(ModuleContainer container)
    {
    }

    @Override
    public boolean onRightClick(ModuleContainer container, EntityPlayer player, EnumHand hand)
    {
        return true;
    }
}