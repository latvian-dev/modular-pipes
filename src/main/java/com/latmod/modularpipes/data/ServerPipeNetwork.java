package com.latmod.modularpipes.data;

import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.net.MessageUpdateItems;
import com.latmod.modularpipes.net.MessageVisualizeNetwork;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ServerPipeNetwork extends PipeNetwork
{
	public static final Int2ObjectOpenHashMap<ServerPipeNetwork> NETWORK_MAP = new Int2ObjectOpenHashMap<>();

	public static void clearAll()
	{
		NETWORK_MAP.clear();
	}

	public boolean loaded;
	private final Map<BlockPos, Node> nodes = new HashMap<>();
	private final Collection<Link> links = new HashSet<>();
	private int nextItemId = 0;
	private final Map<Integer, TransportedItem> updateCache = new HashMap<>();
	private boolean prevDev = false;

	ServerPipeNetwork(World w)
	{
		super(w);
	}

	@Override
	public ServerPipeNetwork server()
	{
		return this;
	}

	@Override
	public void clear()
	{
		super.clear();
		nodes.clear();
		links.clear();
		nextItemId = 0;
	}

	public void save()
	{
		File dir = new File(world.getSaveHandler().getWorldDirectory(), "data/modularpipes");
		File file = new File(dir, "modularpipes.dat");

		if (!file.exists() && nodes.isEmpty() && links.isEmpty() && items.isEmpty())
		{
			return;
		}

		//ModularPipes.LOGGER.info("Saved pipe info to " + dir.getAbsolutePath());
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList list = new NBTTagList();

		for (Node node : getNodes())
		{
			list.appendTag(new NBTTagIntArray(new int[] {node.getX(), node.getY(), node.getZ(), node.type.ordinal()}));
		}

		nbt.setTag("Nodes", list);
		list = new NBTTagList();

		for (Link link : links)
		{
			if (link.invalid())
			{
				continue;
			}

			NBTTagCompound nbt1 = new NBTTagCompound();
			int[] ai = new int[link.path.size() * 3];

			for (int i = 0; i < link.path.size(); i++)
			{
				BlockPos pos = link.path.get(i);
				ai[i * 3] = pos.getX();
				ai[i * 3 + 1] = pos.getY();
				ai[i * 3 + 2] = pos.getZ();
			}

			nbt1.setIntArray("Path", ai);
			nbt1.setInteger("Length", link.length);
			list.appendTag(nbt1);
		}
		nbt.setTag("Links", list);
		list = new NBTTagList();

		for (TransportedItem item : items.values())
		{
			list.appendTag(item.serializeNBT());
		}

		nbt.setTag("Items", list);
		FileUtils.writeNBT(file, nbt);
	}

	public void load()
	{
		clear();
		loaded = true;
		File dir = new File(world.getSaveHandler().getWorldDirectory(), "data/modularpipes");
		// ModularPipes.LOGGER.info("Loading pipe info from " + dir.getAbsolutePath());

		File file = new File(dir, "modularpipes.dat");
		NBTTagCompound nbt = FileUtils.readNBT(file);

		if (nbt == null)
		{
			return;
		}

		NBTTagList list = nbt.getTagList("Nodes", Constants.NBT.TAG_INT_ARRAY);

		for (int i = 0; i < list.tagCount(); i++)
		{
			int[] pos = list.getIntArrayAt(i);

			if (pos.length >= 3)
			{
				Node node = new Node(this, pos[0], pos[1], pos[2], pos.length >= 4 ? NodeType.VALUES[pos[3]] : NodeType.MODULAR);
				nodes.put(node, node);
			}
		}

		list = nbt.getTagList("Links", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound nbt1 = list.getCompoundTagAt(i);

			List<BlockPos> path = new ArrayList<>();
			int[] ai = nbt1.getIntArray("Path");
			Node start, end;

			for (int j = 0; j < ai.length; j += 3)
			{
				path.add(new BlockPos(ai[j], ai[j + 1], ai[j + 2]));
			}

			if (path.size() >= 2)
			{
				start = getNode(path.get(0));
				end = getNode(path.get(path.size() - 1));

				if (start != null && end != null && !start.equals(end))
				{
					Link link = new Link(this, path, start, end, nbt1.hasKey("ActualLength") ? nbt1.getInteger("ActualLength") : nbt1.getInteger("Length"));
					links.add(link);
					start.linkedWith.add(link);
					end.linkedWith.add(link);
				}
			}
		}

		list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); i++)
		{
			TransportedItem item = new TransportedItem(this);
			item.deserializeNBT(list.getCompoundTagAt(i));

			if (!item.remove())
			{
				item.id = ++nextItemId;
				item.action = TransportedItem.Action.UPDATE;
				items.put(item.id, item);
			}
		}
	}

	public void unload()
	{
		loaded = false;

		for (Node node : getNodes())
		{
			node.clearCache();
		}

		NETWORK_MAP.remove(world.provider.getDimension());
	}

	@Override
	@Nullable
	public Node getNode(BlockPos pos)
	{
		return nodes.get(pos);
	}

	@Override
	public Collection<Node> getNodes()
	{
		return nodes.values();
	}

	@Override
	public Collection<Link> getLinks()
	{
		return links;
	}

	@Override
	public boolean removePipe(TileEntity tileEntity, boolean simulate)
	{
		if (!loaded)
		{
			return false;
		}

		Node node = getNode(tileEntity.getPos());

		if (simulate)
		{
			if (node != null)
			{
				return true;
			}

			for (Link link : links)
			{
				if (link.contains(tileEntity.getPos(), false))
				{
					return true;
				}
			}

			return false;
		}

		boolean removedNode = false, removedLink = false;

		if (node != null)
		{
			removedNode = true;

			if (!node.linkedWith.isEmpty())
			{
				for (Link link : node.linkedWith)
				{
					link.setInvalid();
				}

				removedLink = true;
			}

			nodes.remove(tileEntity.getPos());
			markDirty();
		}

		if (removedLink || node == null)
		{
			for (Link link : links)
			{
				if (link.contains(tileEntity.getPos(), node != null))
				{
					link.setInvalid();
					removedLink = true;
					markDirty();
				}
			}
		}

		return removedLink || removedNode;
	}

	@Override
	public void addPipe(TileEntity tileEntity)
	{
		if (!loaded || !(tileEntity instanceof IPipe))
		{
			return;
		}

		if (addPipe0(tileEntity, true))
		{
			for (EnumFacing facing : EnumFacing.VALUES)
			{
				TileEntity tileEntity1 = world.getTileEntity(tileEntity.getPos().offset(facing));

				if (tileEntity1 instanceof IPipe)
				{
					addPipe0(tileEntity1, true);
				}
			}
		}
	}

	private boolean addPipe0(TileEntity tileEntity, boolean init)
	{
		NodeType type = ((IPipe) tileEntity).getNodeType();

		if (init && removePipe(tileEntity, true))
		{
			return false;
		}

		boolean isNode = type.isNode();
		BlockPos pos = tileEntity.getPos();

		if (isNode)
		{
			Node node = getNode(pos);

			if (node == null)
			{
				node = new Node(this, pos.getX(), pos.getY(), pos.getZ(), type);
				nodes.put(node, node);
			}
		}

		for (EnumFacing facing : EnumFacing.VALUES)
		{
			TileEntity tileEntity1 = world.getTileEntity(pos.offset(facing));

			if (tileEntity1 instanceof IPipe)
			{
				CachedBlock data = findNode(pos, facing, isNode);

				if (data != null)
				{
					if (isNode)
					{
						Link link = data.getLink();
						if (link != null && !link.invalid())
						{
							link.start.linkedWith.add(link);
							link.end.linkedWith.add(link);
							links.add(link);
						}
					}
					else if (data.getNode() != null)
					{
						TileEntity tileEntity2 = world.getTileEntity(data.getNode());

						if (tileEntity2 instanceof IPipe)
						{
							addPipe0(tileEntity2, false);
						}
					}
				}
			}
		}

		markDirty();
		return true;
	}

	@Nullable
	private CachedBlock findNode(BlockPos start, EnumFacing facing, boolean isNode)
	{
		LinkedHashSet<BlockPos> path = new LinkedHashSet<>();
		path.add(start);
		BlockPos pos = start.offset(facing);
		EnumFacing source = facing.getOpposite();
		TileEntity tileEntity;

		for (int length = 1; length < ModularPipesConfig.pipes.max_link_length; length++)
		{
			tileEntity = world.getTileEntity(pos);

			if (!(tileEntity instanceof IPipe))
			{
				return null;
			}

			if (path.contains(pos))
			{
				//ModularPipes.LOGGER.warn("Loop @ " + pos);
				return null;
			}

			path.add(pos);

			IPipe pipe = (IPipe) tileEntity;

			if (pipe.getNodeType().isNode())
			{
				if (isNode)
				{
					List<BlockPos> list = Link.simplify(path);
					Node startNode = list.size() >= 2 ? getNode(list.get(0)) : null;
					Node endNode = startNode == null ? null : getNode(list.get(list.size() - 1));
					return endNode == null ? null : new CachedBlock.LinkData(new Link(this, list, startNode, endNode, length));
				}
				else
				{
					return new CachedBlock.NodeData(new Node(this, pos.getX(), pos.getY(), pos.getZ(), NodeType.SIMPLE));
				}
			}
			else
			{
				facing = pipe.getPipeFacing(source);

				if (facing != source)
				{
					source = facing.getOpposite();
					pos = pos.offset(facing);
				}
				else
				{
					//ModularPipes.LOGGER.warn("Dead end @ " + pos);
					return null;
				}
			}
		}

		//ModularPipes.LOGGER.warn("Path too long!");
		return null;
	}

	@Override
	public void addItem(TransportedItem item)
	{
		item.id = ++nextItemId;
		item.progress = 0;

		if (nextItemId == 2000000000)
		{
			nextItemId = 0;
		}

		super.addItem(item);
	}

	@Override
	public void update()
	{
		updateCache.clear();
		super.update();

		if (!updateCache.isEmpty())
		{
			new MessageUpdateItems(updateCache.values()).sendToDimension(world.provider.getDimension());
		}

		if (isDirty)
		{
			isDirty = false;

			Iterator<Link> iterator = links.iterator();

			while (iterator.hasNext())
			{
				Link link = iterator.next();

				if (link.invalid())
				{
					link.start.linkedWith.remove(link);
					link.end.linkedWith.remove(link);
					iterator.remove();
				}
			}

			if (prevDev || ModularPipesConfig.general.dev_mode)
			{
				prevDev = ModularPipesConfig.general.dev_mode;
				Collection<BlockPos> ns = null;
				Collection<BlockPos> nt = null;
				Collection<Collection<BlockPos>> l = null;
				Collection<BlockPos> t = null;

				for (ForgePlayer player : Universe.get().getOnlinePlayers())
				{
					ModularPipesPlayerData data = ModularPipesPlayerData.get(player);
					boolean dev = ModularPipesConfig.general.dev_mode && data.devMode.getBoolean();
					if (ns == null && dev)
					{
						ns = new HashSet<>();
						nt = new HashSet<>();

						for (Node node : nodes.values())
						{
							if (node.type.isModular())
							{
								nt.add(node);
							}
							else
							{
								ns.add(node);
							}
						}

						l = new ArrayList<>();
						for (Link link : links)
						{
							l.add(link.path);
						}

						t = new ArrayList<>();
						for (Node node : nodes.values())
						{
							for (int facing = 0; facing < 6; facing++)
							{
								if (node.getTile(facing) != null)
								{
									t.add(node.offset(EnumFacing.VALUES[facing]));
								}
							}
						}
					}

					if (dev)
					{
						new MessageVisualizeNetwork(ns, nt, l, t).sendTo(player.getPlayer());
					}
					else
					{
						new MessageVisualizeNetwork(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()).sendTo(player.getPlayer());
					}
				}
			}
		}
	}

	@Override
	public void itemUpdated(int id, TransportedItem item)
	{
		updateCache.put(item.id, item);
	}

	public void playerLoggedIn(EntityPlayerMP player)
	{
		new MessageUpdateItems(items.values()).sendTo(player);
		markDirty();
	}
}