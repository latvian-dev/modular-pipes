package com.latmod.modularpipes.api;

import com.latmod.modularpipes.util.MathUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class TransportedItem implements ITickable, INBTSerializable<NBTTagCompound>
{
    public enum Action
    {
        NONE,
        REMOVE,
        UPDATE;

        public static final Action[] VALUES = values();
    }

    public int id;
    public int startingPosX, startingPosY, startingPosZ;
    public ItemStack stack = ItemStack.EMPTY;
    public int filters = 0, movingDistance = 0;
    public EnumFacing movingDirection = null, dstTurn = null;
    public float speed = 0F, progress = 0F, prevProgress = 0F;
    public Action action = Action.NONE;

    public void update()
    {
        action = Action.NONE;
        prevProgress = progress;
        progress += speed;
    }

    @SideOnly(Side.CLIENT)
    public void render(float partialTick)
    {
        GlStateManager.pushMatrix();
        GlStateManager.popMatrix();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Item", stack.serializeNBT());
        nbt.setIntArray("Pos", new int[] {startingPosX, startingPosY, startingPosZ});
        nbt.setInteger("Filters", filters);
        nbt.setShort("MovingDistance", (short) movingDistance);
        nbt.setByte("MovingDirection", (byte) MathUtils.getFacingIndex(movingDirection));
        nbt.setByte("DstTurn", (byte) MathUtils.getFacingIndex(dstTurn));
        nbt.setFloat("Speed", speed);
        nbt.setFloat("Progress", progress);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        action = Action.NONE;
        stack = new ItemStack(nbt.getCompoundTag("Item"));
        int pos[] = nbt.getIntArray("Pos");
        startingPosX = pos[0];
        startingPosY = pos[1];
        startingPosZ = pos[2];
        filters = nbt.getInteger("Filters");
        movingDistance = nbt.getShort("MovingDistance") & 0xFFFF;
        movingDirection = MathUtils.getFacing(nbt.getByte("MovingDirection"));
        dstTurn = MathUtils.getFacing(nbt.getByte("DstTurn"));
        speed = nbt.getFloat("Speed");
        progress = nbt.getFloat("Progress");

        if(stack.getCount() == 0)
        {
            action = Action.REMOVE;
        }
    }

    public void writeToByteBuf(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeByte(action.ordinal());

        if(action == Action.REMOVE)
        {
            return;
        }

        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(startingPosX);
        buf.writeInt(startingPosY);
        buf.writeInt(startingPosZ);
        buf.writeShort(filters);
        buf.writeShort(movingDistance);
        buf.writeByte(MathUtils.getFacingIndex(movingDirection));
        buf.writeByte(MathUtils.getFacingIndex(dstTurn));
        buf.writeFloat(speed);
        buf.writeFloat(progress);
        buf.writeFloat(prevProgress);
    }

    public void readFromByteBuf(ByteBuf buf)
    {
        id = buf.readInt();
        action = Action.VALUES[buf.readUnsignedByte()];

        if(action == Action.REMOVE)
        {
            return;
        }

        stack = ByteBufUtils.readItemStack(buf);
        startingPosX = buf.readInt();
        startingPosY = buf.readInt();
        startingPosZ = buf.readInt();
        filters = buf.readUnsignedShort();
        movingDistance = buf.readUnsignedShort();
        speed = buf.readFloat();
        progress = buf.readFloat();
        prevProgress = buf.readFloat();
    }
}