package com.latmod.modularpipes.item.module;

import com.latmod.modularpipes.data.ModuleContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * @author LatvianModder
 */
public class ModuleRightClickExtract extends ModuleExtract
{
    @Override
    public void update(ModuleContainer container)
    {
    }

    @Override
    public boolean onRightClick(ModuleContainer container, EntityPlayer player, EnumHand hand)
    {
        extractItem(container);
        return true;
    }
}