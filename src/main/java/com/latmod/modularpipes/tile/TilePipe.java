package com.latmod.modularpipes.tile;

import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.api.ModuleContainer;
import com.latmod.modularpipes.util.MathUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Arrays;

/**
 * @author LatvianModder
 */
public class TilePipe extends TileEntity implements ITickable
{
    private int tier;
    private boolean isDirty;
    public final ModuleContainer[] modules;

    public TilePipe()
    {
        this(1);
    }

    public TilePipe(int t)
    {
        tier = t;
        isDirty = true;
        modules = new ModuleContainer[6];
    }

    public int getTier()
    {
        return tier;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setByte("Tier", (byte) tier);
        NBTTagList moduleList = new NBTTagList();

        for(ModuleContainer c : modules)
        {
            if(c != null)
            {
                moduleList.appendTag(ModuleContainerImpl.writeToNBT(c));
            }
        }

        nbt.setTag("Modules", moduleList);
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        tier = nbt.getByte("Tier");
        Arrays.fill(modules, null);

        NBTTagList moduleList = nbt.getTagList("Modules", Constants.NBT.TAG_COMPOUND);

        for(int i = 0; i < moduleList.tagCount(); i++)
        {
            ModuleContainer c = ModuleContainerImpl.readFromNBT(this, moduleList.getCompoundTagAt(i));
            modules[c.getFacing().getIndex()] = c;
        }
    }

    public void markDirty()
    {
        isDirty = true;
    }

    @Override
    public void update()
    {
        for(ModuleContainer c : modules)
        {
            if(c != null)
            {
                c.getModule().update(c);
            }
        }

        if(isDirty)
        {
            if(world != null && world.isRemote)
            {
                updateContainingBlockInfo();
                world.markChunkDirty(pos, this);
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 255);
            }

            isDirty = false;
        }
    }

    public void onRightClick(EntityPlayer playerIn, EnumHand hand)
    {
        if(world.isRemote)
        {
            return;
        }

        RayTraceResult ray = MathUtils.rayTrace(playerIn, false);

        if(ray == null)
        {
            return;
        }

        int facing = ray.subHit;

        if(facing >= 6 || facing < 0)
        {
            facing = ray.sideHit.getIndex();
        }

        ItemStack stack = playerIn.getHeldItem(hand);

        if(modules[facing] == null)
        {
            if(stack.getCount() > 0 && stack.hasCapability(ModularPipesCommon.CAP_MODULE, null))
            {
                ModuleContainer c = new ModuleContainerImpl(this, EnumFacing.VALUES[facing], ItemHandlerHelper.copyStackWithSize(stack, 1));

                if(c.getModule().insertInPipe(c, playerIn))
                {
                    stack.shrink(1);
                    modules[c.getFacing().getIndex()] = c;
                    markDirty();
                }
            }
        }
        else
        {
            if(stack.getCount() == 0 && playerIn.isSneaking())
            {
                //TODO: Extract

                modules[facing].getModule().removeFromPipe(modules[facing], playerIn);

                if(!playerIn.inventory.addItemStackToInventory(modules[facing].getItemStack()) && modules[facing].getItemStack().getCount() > 0)
                {
                    world.spawnEntity(new EntityItem(world, playerIn.posX, playerIn.posY, playerIn.posZ, modules[facing].getItemStack()));
                }

                modules[facing] = null;
                markDirty();
            }
            else if(!modules[facing].getModule().onRightClick(modules[facing], playerIn, hand))
            {
                //TODO: Open GUI
                playerIn.sendMessage(new TextComponentString("GUI Not Implemented!"));
            }
        }

        /*
        List<TilePipe> list = PipeNetwork.findPipes(this, false);
        List<String> list1 = new ArrayList<>();

        for(TilePipe t : list)
        {
            list1.add("[" + t.getPos().getX() + ", " + t.getPos().getY() + ", " + t.getPos().getZ() + "]");
        }

        playerIn.sendMessage(new TextComponentString("Found " + list.size() + " pipes on network: " + list1));
        */
    }

    public void onBroken()
    {
        for(ModuleContainer c : modules)
        {
            if(c != null && c.getItemStack().getCount() > 0)
            {
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, c.getItemStack()));
            }
        }

        Arrays.fill(modules, null);
    }
}