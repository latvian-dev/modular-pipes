package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.ModularPipesCaps;
import com.latmod.modularpipes.api.Module;
import com.latmod.modularpipes.api.ModuleContainer;
import com.latmod.modularpipes.api.ModuleData;
import com.latmod.modularpipes.api.NoData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

/**
 * @author LatvianModder
 */
public final class ModuleContainerImpl implements ModuleContainer, ITickable
{
    private final TileEntity tile;
    private final EnumFacing facing;
    private Module module;
    private ItemStack stack;
    private ModuleData data;
    private int filters;
    private int tick;

    public ModuleContainerImpl(TileEntity t, EnumFacing f, ItemStack stack)
    {
        tile = t;
        facing = f;
        setStack(stack);
    }

    public ModuleContainerImpl(TileEntity t, NBTTagCompound nbt)
    {
        this(t, EnumFacing.VALUES[nbt.getByte("Facing")], ItemStack.EMPTY);

        setFilters(nbt.getInteger("Filters"));

        if(nbt.hasKey("Item"))
        {
            setStack(new ItemStack(nbt.getCompoundTag("Item")));

            if(data.shouldSave() && stack.hasTagCompound() && stack.getTagCompound().hasKey("ModuleData"))
            {
                data.deserializeNBT(stack.getTagCompound().getCompoundTag("ModuleData"));
            }
        }

        tick = nbt.getInteger("Tick");
    }

    public void setStack(ItemStack s)
    {
        module = Module.EMPTY;
        data = NoData.INSTANCE;
        stack = ItemStack.EMPTY;
        tick = 0;

        if(s.getCount() > 0 && s.hasCapability(ModularPipesCaps.MODULE, null))
        {
            stack = s;
            Module m = stack.getCapability(ModularPipesCaps.MODULE, null);

            if(m != null)
            {
                module = m;
                data = module.createData(this);
            }
        }
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

    @Override
    public int getFilters()
    {
        return filters;
    }

    @Override
    public int getTick()
    {
        return tick;
    }

    public void setFilters(int f)
    {
        filters = f;
    }

    @Override
    public void update()
    {
        if(hasModule())
        {
            module.update(this);
            tick++;
        }
    }

    public static NBTTagCompound writeToNBT(ModuleContainer c)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("Facing", (byte) c.getFacing().getIndex());

        if(c.getTick() > 0)
        {
            nbt.setInteger("Tick", c.getTick());
        }
        if(c.getFilters() != 0)
        {
            nbt.setInteger("Filters", c.getFilters());
        }
        if(c.getItemStack().getCount() > 0)
        {
            nbt.setTag("Item", c.getItemStack().serializeNBT());

            if(c.getData().shouldSave())
            {
                c.getItemStack().setTagInfo("ModuleData", c.getData().serializeNBT());
            }
        }

        return nbt;
    }
}