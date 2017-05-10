package com.latmod.modularpipes.data;

import com.feed_the_beast.ftbl.lib.util.InvUtils;
import com.latmod.modularpipes.client.ClientTransportedItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class TransportedItem implements ITickable
{
    public enum Action
    {
        NONE,
        REMOVE,
        UPDATE,
        HIDE;

        public static final Action[] VALUES = values();

        public boolean remove()
        {
            return this == REMOVE;
        }

        public boolean update()
        {
            return this == UPDATE || this == REMOVE;
        }

        public boolean invisible()
        {
            return this == HIDE || this == REMOVE;
        }
    }

    public static final Predicate<TransportedItem> REMOVE_PREDICATE = TransportedItem::remove;
    public static final Consumer<TransportedItem> FOREACH_UPDATE = TransportedItem::update;
    public static final Consumer<TransportedItem> FOREACH_POST_UPDATE = TransportedItem::postUpdate;

    public final PipeNetwork network;
    public int id;
    public final List<BlockPos> path = new ArrayList<>();
    public ItemStack stack = ItemStack.EMPTY;
    public int filters = 0;
    public float speed = 0F, progress = 0F;
    public Action action = Action.NONE;
    public double prevX, prevY, prevZ;
    public double posX, posY, posZ;

    public TransportedItem(PipeNetwork n)
    {
        network = n;
    }

    public void addToNetwork()
    {
        network.addItem(this);
    }

    @Override
    public void update()
    {
        updatePrevData();
        if(!updatePosition())
        {
            return;
        }

        /*
        if(prevX != posX || prevY != posY || prevZ != posZ)
        {
            BlockPos oldPos = new BlockPos(prevX, prevY, prevZ);
            //check for block change
        }
        */

        BlockPos pos = new BlockPos(posX, posY, posZ);
        float s = speed;
        IBlockState state = network.world.getBlockState(pos);

        if(state.getBlock().isAir(state, network.world, pos))
        {
            action = Action.REMOVE;

            if(!network.world.isRemote)
            {
                InvUtils.dropItem(network.world, pos, stack, 12);
            }
        }
        else if(state.getBlock() instanceof IPipeBlock)
        {
            s *= ((IPipeBlock) state.getBlock()).getSpeedModifier(network.world, pos, state);
        }

        progress += s;
    }

    public void blockCrossed()
    {
    }

    public void updatePrevData()
    {
        prevX = posX;
        prevY = posY;
        prevZ = posZ;
    }

    public boolean updatePosition()
    {
        if(path.size() < 2)
        {
            return false;
        }

        //ModularPipes.LOGGER.info(id + ": " + path);

        BlockPos pos0 = path.get(0);
        BlockPos pos1 = path.get(1);
        double d = Math.sqrt(pos0.distanceSq(pos1));
        double p = progress % d;

        posX = pos0.getX() + (pos1.getX() - pos0.getX()) * p + 0.5D;
        posY = pos0.getY() + (pos1.getY() - pos0.getY()) * p + 0.5D;
        posZ = pos0.getZ() + (pos1.getZ() - pos0.getZ()) * p + 0.5D;

        if(progress >= d)
        {
            path.remove(0);
        }

        return true;
    }

    public void copyFrom(TransportedItem item)
    {
        action = item.action;

        if(action.remove())
        {
            return;
        }

        path.clear();
        path.addAll(item.path);
        stack = item.stack.copy();
        filters = item.filters;
        speed = item.speed;
        progress = item.progress;

        updatePosition();
        updatePrevData();
    }

    public void postUpdate()
    {
        action = Action.NONE;
    }

    public boolean remove()
    {
        return action.remove() || path.size() < 2 || stack.isEmpty();
    }

    public ClientTransportedItem client()
    {
        throw new IllegalStateException();
    }
}