package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.PipeNetwork;
import com.latmod.modularpipes.block.EnumPipeTier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;

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
        if(world.isRemote)
        {
            return;
        }

        List<TilePipe> list = PipeNetwork.findPipes(this, false);
        List<String> list1 = new ArrayList<>();

        for(TilePipe t : list)
        {
            list1.add("[" + t.getPos().getX() + ", " + t.getPos().getY() + ", " + t.getPos().getZ() + "]");
        }

        playerIn.sendMessage(new TextComponentString("Found " + list.size() + " pipes on network: " + list1));
    }
}