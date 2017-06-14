package com.latmod.modularpipes.item;

import com.feed_the_beast.ftbl.lib.util.StringUtils;
import com.latmod.modularpipes.ModularPipesCaps;
import com.latmod.modularpipes.data.Module;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemModule extends ItemMPBase
{
	private static class ModuleCapabilityProvider implements ICapabilityProvider
	{
		private final Module module;

		public ModuleCapabilityProvider(Module m)
		{
			module = m;
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
		{
			return capability == ModularPipesCaps.MODULE;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
		{
			return capability == ModularPipesCaps.MODULE ? (T) module : null;
		}
	}

	private final ModuleCapabilityProvider capabilityProvider;
	private String moduleName;

	public ItemModule(String id, Module m)
	{
		super("module_" + id);
		capabilityProvider = new ModuleCapabilityProvider(m);
		setUnlocalizedName("modularpipes.module");
		moduleName = "item." + getRegistryName().getResourceDomain() + ".module." + id + ".name";
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
	{
		return capabilityProvider;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);

		if (playerIn.isSneaking() && (stack.hasTagCompound() && stack.getTagCompound().hasKey("Module")))
		{
			stack.getTagCompound().removeTag("Module");

			if (stack.getTagCompound().hasNoTags())
			{
				stack.setTagCompound(null);
			}

			playerIn.sendMessage(StringUtils.text("Cleared Module Data")); //TODO: Lang
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		return new ActionResult<>(EnumActionResult.PASS, stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flag)
	{
		tooltip.add(I18n.format(moduleName));
	}
}