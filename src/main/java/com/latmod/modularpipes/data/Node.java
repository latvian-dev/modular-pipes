package com.latmod.modularpipes.data;

import com.latmod.modularpipes.tile.TileModularPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author LatvianModder
 */
public class Node extends BlockPos
{
	public final PipeNetwork network;
	public final Collection<Link> linkedWith;
	public final NodeType type;
	private final TileEntity[] tiles;

	public Node(PipeNetwork n, int x, int y, int z, NodeType t)
	{
		super(x, y, z);
		network = n;
		linkedWith = new HashSet<>();
		type = t;
		tiles = new TileEntity[7];
	}

	private static boolean isValidTile(TileEntity tile, EnumFacing facing)
	{
		return !(tile instanceof TileModularPipe) && (tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing) || tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing));
	}

	@Nullable
	public TileEntity getTile(int facing)
	{
		int i = facing < 0 || facing >= 6 ? 6 : facing;

		if (i != 6 && !type.hasTiles())
		{
			return null;
		}

		TileEntity prevTile = tiles[i];
		if (tiles[i] == null || tiles[i].isInvalid())
		{
			if (i == 6)
			{
				tiles[6] = network.world.getTileEntity(this);
			}
			else
			{
				tiles[i] = network.world.getTileEntity(offset(EnumFacing.VALUES[i]));

				if (tiles[i] != null && !isValidTile(tiles[i], EnumFacing.VALUES[i].getOpposite()))
				{
					tiles[i] = null;
				}
			}
		}
		if (tiles[i] != null && tiles[i].isInvalid())
		{
			tiles[i] = null;
		}
		if (prevTile != tiles[i])
		{
			network.networkUpdated = true;
		}

		return tiles[i];
	}

	public void clearCache()
	{
		Arrays.fill(tiles, null);
	}

	@Nullable
	public Link getBestPath(Node to)
	{
		if (linkedWith.isEmpty())
		{
			return null;
		}

		List<Link> list = new ArrayList<>();
		for (Link link : linkedWith)
		{
			if (link.contains(to, true))
			{
				list.add(link);
			}
		}
		if (list.isEmpty())
		{
			return null;
		}
		else if (list.size() > 1)
		{
			list.sort(Link.COMPARATOR);
		}

		return list.get(0);
	}

	public String toString()
	{
		return "[" + getX() + ',' + getY() + ',' + getZ() + ']';
	}
}