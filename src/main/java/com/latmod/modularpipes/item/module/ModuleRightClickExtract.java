package com.latmod.modularpipes.item.module;

import com.latmod.modularpipes.api.Module;
import com.latmod.modularpipes.api.ModuleContainer;
import com.latmod.modularpipes.api.TransportedItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * @author LatvianModder
 */
public class ModuleRightClickExtract extends Module
{
    @Override
    public boolean onRightClick(ModuleContainer container, EntityPlayer player, EnumHand hand)
    {
        TileEntity tile = container.getFacingTile();

        if(tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, container.getFacing().getOpposite()))
        {
            IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, container.getFacing().getOpposite());

            if(handler != null)
            {
                int slot = -1;
                ItemStack stack = null;

                for(int i = 0; i < handler.getSlots(); i++)
                {
                    stack = handler.extractItem(i, 1, true);

                    if(stack.getCount() > 0)
                    {
                        slot = i;
                        break;
                    }
                }

                if(slot != -1)
                {
                    TransportedItem item = new TransportedItem();
                    item.stack = stack;
                    item.filters = container.getFilterConfig().get();

                    if(container.getNetwork().generatePath(container, item))
                    {
                        handler.extractItem(slot, 1, false);
                        container.getNetwork().addItem(item);
                    }
                }
            }
        }

        return true;
    }
}