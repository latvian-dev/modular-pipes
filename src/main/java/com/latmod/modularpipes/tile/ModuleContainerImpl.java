package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.api.Module;
import com.latmod.modularpipes.api.ModuleContainer;
import com.latmod.modularpipes.api.ModuleData;
import com.latmod.modularpipes.api.NoData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * @author LatvianModder
 */
public class ModuleContainerImpl implements ModuleContainer
{
    private final Module module;
    private final TileEntity tile;
    private final EnumFacing facing;
    private final ItemStack stack;
    private final ModuleData data;

    public ModuleContainerImpl(TileEntity t, EnumFacing f, ItemStack s)
    {
        tile = t;
        facing = f;
        stack = s;
        module = stack.getCapability(ModularPipesCommon.CAP_MODULE, null);
        data = module.createData(this);
    }

    @Override
    public Module getModule()
    {
        return module;
    }

    @Override
    public TileEntity getTile()
    {
        return tile;
    }

    @Override
    public EnumFacing getFacing()
    {
        return facing;
    }

    @Override
    public ItemStack getItemStack()
    {
        return stack;
    }

    @Override
    public ModuleData getData()
    {
        return data;
    }

    public static NBTTagCompound writeToNBT(ModuleContainer c)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("Facing", (byte) c.getFacing().getIndex());
        nbt.setTag("Item", c.getItemStack().serializeNBT());
        if(c.getData() != NoData.INSTANCE)
        {
            nbt.setTag("Data", c.getData().serializeNBT());
        }
        return nbt;
    }

    public static ModuleContainer readFromNBT(TileEntity tile, NBTTagCompound nbt)
    {
        ModuleContainer c = new ModuleContainerImpl(tile, EnumFacing.VALUES[nbt.getByte("Facing")], new ItemStack(nbt.getCompoundTag("Item")));
        if(c.getData() != NoData.INSTANCE)
        {
            c.getData().deserializeNBT(nbt.getCompoundTag("Data"));
        }
        return c;
    }
}