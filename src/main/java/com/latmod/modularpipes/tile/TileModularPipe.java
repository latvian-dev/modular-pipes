package com.latmod.modularpipes.tile;

import com.feed_the_beast.ftblib.lib.math.MathUtils;
import com.feed_the_beast.ftblib.lib.tile.EnumSaveType;
import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.block.PipeConnection;
import com.latmod.modularpipes.data.IModule;
import com.latmod.modularpipes.data.IPipe;
import com.latmod.modularpipes.data.ModuleContainer;
import com.latmod.modularpipes.data.Node;
import com.latmod.modularpipes.data.NodeType;
import com.latmod.modularpipes.data.PipeNetwork;
import com.latmod.modularpipes.data.TransportedItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class TileModularPipe extends TilePipeBase implements IModularPipeNetworkTile
{
	public ModularPipesConfig.Tier tier;
	public final ModuleContainer[] modules;
	private PipeNetwork network;
	private BlockPos controllerPos;
	private TileController cachedController;

	public TileModularPipe()
	{
		this(ModularPipesConfig.tiers.basic);
	}

	public TileModularPipe(ModularPipesConfig.Tier t)
	{
		tier = t;
		modules = new ModuleContainer[6];

		for (int i = 0; i < 6; i++)
		{
			modules[i] = new ModuleContainer(this, EnumFacing.VALUES[i], ItemStack.EMPTY);
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) ? (T) modules[facing.getIndex()] : super.getCapability(capability, facing);
	}

	public void clearModules()
	{
		for (int i = 0; i < 6; i++)
		{
			modules[i].setStack(ItemStack.EMPTY);
		}
	}

	@Override
	protected void writeData(NBTTagCompound nbt, EnumSaveType type)
	{
		super.writeData(nbt, type);

		ModularPipesConfig.tiers.getNameMap().writeToNBT(nbt, "Tier", type, tier);

		NBTTagList moduleList = new NBTTagList();

		for (ModuleContainer c : modules)
		{
			NBTTagCompound nbt1 = c.writeToNBT(type);

			if (!nbt1.hasNoTags())
			{
				moduleList.appendTag(nbt1);
			}
		}

		if (type.save || !moduleList.hasNoTags())
		{
			nbt.setTag("Modules", moduleList);
		}

		if (controllerPos != null)
		{
			nbt.setIntArray("Controller", new int[] {controllerPos.getX(), controllerPos.getY(), controllerPos.getZ()});
		}
	}

	@Override
	protected void readData(NBTTagCompound nbt, EnumSaveType type)
	{
		super.readData(nbt, type);
		tier = ModularPipesConfig.tiers.getNameMap().readFromNBT(nbt, "Tier", type);
		clearModules();

		NBTTagList moduleList = nbt.getTagList("Modules", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < moduleList.tagCount(); i++)
		{
			ModuleContainer c = new ModuleContainer(this, moduleList.getCompoundTagAt(i), type);
			modules[c.facing.getIndex()] = c;
		}

		controllerPos = null;
		cachedController = null;

		if (nbt.hasKey("Controller"))
		{
			int[] ai = nbt.getIntArray("Controller");

			if (ai.length == 3)
			{
				controllerPos = new BlockPos(ai[0], ai[1], ai[2]);
			}
		}
	}

	@Override
	public void updateContainingBlockInfo()
	{
		super.updateContainingBlockInfo();
		cachedController = null;
	}

	@Override
	public void updateNetworkTile()
	{
		for (ModuleContainer c : modules)
		{
			c.update();
		}

		if (!world.isRemote)
		{
			getConnections();
		}

		checkIfDirty();
	}

	public void onRightClick(EntityPlayer player, EnumHand hand)
	{
		if (world.isRemote)
		{
			return;
		}

		RayTraceResult ray = MathUtils.rayTrace(player, false);

		if (ray == null)
		{
			return;
		}

		int facing = ray.subHit;

		if (facing <= 0)
		{
			facing = ray.sideHit.getIndex();
		}
		else
		{
			facing = (ray.subHit - 1) % 6;
		}

		ItemStack stack = player.getHeldItem(hand);
		ModuleContainer c = modules[facing];

		if (stack.isEmpty() && player.isSneaking())
		{
			if (!c.getItemStack().isEmpty())
			{
				c.getModule().removeFromPipe(c, player);

				if (!c.getData().isEmpty())
				{
					NBTTagCompound nbt1 = new NBTTagCompound();
					c.getData().serializeNBT(nbt1, EnumSaveType.SAVE);
					c.getItemStack().setTagInfo("ModuleData", nbt1);
				}

				if (!player.inventory.addItemStackToInventory(c.getItemStack()) && !c.getItemStack().isEmpty())
				{
					world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, c.getItemStack()));
				}

				c.setStack(ItemStack.EMPTY);
				updateContainingBlockInfo();
				markDirty();
				return;
			}
		}

		if (c.getItemStack().isEmpty() && stack.getItem() instanceof IModule)
		{
			int modulesSize = 0;

			for (ModuleContainer module : modules)
			{
				if (module.hasModule())
				{
					modulesSize++;
				}
			}

			if (tier.modules <= modulesSize)
			{
				player.sendMessage(new TextComponentTranslation("item.modularpipes.module.cant_insert"));
				return;
			}

			c.setStack(ItemHandlerHelper.copyStackWithSize(stack, 1));

			if (c.hasModule() && c.getModule().insertInPipe(c, player))
			{
				stack.shrink(1);
				updateContainingBlockInfo();
				markDirty();
				return;
			}
			else
			{
				c.setStack(ItemStack.EMPTY);
			}
		}

		if (!c.getModule().onModuleRightClick(c, player, hand))
		{
			player.sendMessage(new TextComponentString("GUI Not Implemented!")); //TODO: Open GUI
		}

		/*
		List<TileModularPipe> list = PipeNetwork.findPipes(this, false);
		List<String> list1 = new ArrayList<>();

		for (TileModularPipe t : list)
		{
			list1.add("[" + t.getPos().getX() + ", " + t.getPos().getY() + ", " + t.getPos().getZ() + "]");
		}

		playerIn.sendMessage(new TextComponentString("Found " + list.size() + " pipes on network: " + list1));
		*/
	}

	public void onBroken()
	{
		for (ModuleContainer c : modules)
		{
			c.getModule().pipeBroken(c);

			if (!c.getData().isEmpty())
			{
				NBTTagCompound nbt1 = new NBTTagCompound();
				c.getData().serializeNBT(nbt1, EnumSaveType.SAVE);
				c.getItemStack().setTagInfo("ModuleData", nbt1);
			}

			if (!c.getItemStack().isEmpty())
			{
				EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, c.getItemStack());
				entityItem.setPickupDelay(10);
				world.spawnEntity(entityItem);
			}
		}

		clearModules();
	}

	public PipeNetwork getNetwork()
	{
		if (network == null)
		{
			network = PipeNetwork.get(world);
		}

		return network;
	}

	@Override
	public void onNeighborChange()
	{
		super.onNeighborChange();

		if (!world.isRemote)
		{
			Node node = getNetwork().getNode(pos);

			if (node != null)
			{
				node.clearCache();
				node.network.markDirty();
			}
		}
	}

	@Nullable
	@Override
	public TileController getController()
	{
		if (cachedController == null && controllerPos != null)
		{
			TileEntity tileEntity = world.getTileEntity(controllerPos);

			if (tileEntity instanceof TileController)
			{
				cachedController = (TileController) tileEntity;
			}
		}

		return cachedController;
	}

	@Override
	public void setControllerPosition(BlockPos pos)
	{
		controllerPos = pos;
		cachedController = null;
	}

	@Override
	public PipeConnection getPipeConnectionType(EnumFacing facing)
	{
		return modules[facing.getIndex()].hasModule() ? PipeConnection.PIPE_MODULE : super.getPipeConnectionType(facing);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.MODULAR;
	}

	@Override
	public double getItemSpeedModifier(TransportedItem item)
	{
		return tier.speed;
	}

	@Override
	public EnumFacing getItemDirection(TransportedItem item, EnumFacing source)
	{
		TileEntity tileEntity = world.getTileEntity(pos);

		if (tileEntity instanceof IPipe)
		{
			return ((IPipe) tileEntity).getItemDirection(item, source);
		}

		return source;
	}
}