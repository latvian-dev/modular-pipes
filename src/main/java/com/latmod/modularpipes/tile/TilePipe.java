package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.block.EnumPipeTier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;

/**
 * @author LatvianModder
 */
public class TilePipe extends TileEntity
{
    private EnumPipeTier tier = EnumPipeTier.MK1;

    public TilePipe()
    {
    }

    public TilePipe(EnumPipeTier t)
    {
        tier = t;
    }

    public EnumPipeTier getTier()
    {
        return tier;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("Tier", (byte) tier.ordinal());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        tier = EnumPipeTier.getFromMeta(nbt.getByte("Tier"));
    }

    public void onRightClick(EntityPlayer playerIn, EnumHand hand)
    {
        playerIn.sendMessage(new TextComponentString("Test!"));
    }
}