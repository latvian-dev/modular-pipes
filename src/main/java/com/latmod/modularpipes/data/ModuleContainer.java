package com.latmod.modularpipes.data;

import com.latmod.modularpipes.ModularPipesCaps;
import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public final class ModuleContainer implements ITickable
{
    private final TileModularPipe tile;
    private final EnumFacing facing;
    private Module module;
    private ItemStack stack;
    private ModuleData data;
    private FilterConfig filterConfig;
    private int tick;

    public ModuleContainer(TileModularPipe t, EnumFacing f, ItemStack stack)
    {
        tile = t;
        facing = f;
        filterConfig = new FilterConfig();
        setStack(stack);
    }

    public ModuleContainer(TileModularPipe t, NBTTagCompound nbt)
    {
        this(t, EnumFacing.VALUES[nbt.getByte("Facing")], ItemStack.EMPTY);

        if(nbt.hasKey("Item"))
        {
            setStack(new ItemStack(nbt.getCompoundTag("Item")));

            if(data.shouldSave() && stack.hasTagCompound() && stack.getTagCompound().hasKey("ModuleData"))
            {
                data.deserializeNBT(stack.getTagCompound().getCompoundTag("ModuleData"));
            }
        }

        if(nbt.hasKey("FilterConfig"))
        {
            filterConfig.deserializeNBT(nbt.getTag("FilterConfig"));
        }

        tick = nbt.getInteger("Tick");
    }

    public PipeNetwork getNetwork()
    {
        return tile.getNetwork();
    }

    public boolean hasModule()
    {
        return !getModule().isEmpty();
    }

    public void setStack(ItemStack is)
    {
        module = Module.EMPTY;
        data = NoData.INSTANCE;
        stack = ItemStack.EMPTY;
        tick = 0;

        if(!is.isEmpty() && is.hasCapability(ModularPipesCaps.MODULE, null))
        {
            stack = is;
            Module m = stack.getCapability(ModularPipesCaps.MODULE, null);

            if(m != null)
            {
                module = m;
                data = module.createData(this);
                filterConfig = module.createFilterConfig(this);
            }
        }
    }

    public Module getModule()
    {
        return module;
    }

    public TileEntity getTile()
    {
        return tile;
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public ItemStack getItemStack()
    {
        return stack;
    }

    public ModuleData getData()
    {
        return data;
    }

    public FilterConfig getFilterConfig()
    {
        return filterConfig;
    }

    public int getTick()
    {
        return tick;
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

    @Nullable
    public TileEntity getFacingTile()
    {
        return getTile().getWorld().getTileEntity(getTile().getPos().offset(getFacing()));
    }

    public boolean isRemote()
    {
        return getTile().getWorld().isRemote;
    }

    public static NBTTagCompound writeToNBT(ModuleContainer c)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("Facing", (byte) c.getFacing().getIndex());

        if(c.getTick() > 0)
        {
            nbt.setInteger("Tick", c.getTick());
        }
        if(c.getFilterConfig().shouldSave())
        {
            nbt.setTag("FilterConfig", c.getFilterConfig().serializeNBT());
        }
        if(!c.getItemStack().isEmpty())
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