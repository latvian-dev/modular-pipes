package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.api.IFTBLibClientRegistry;
import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyByte;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.util.LMUtils;

import java.io.File;

/**
 * @author LatvianModder
 */
public class ModularPipesConfig
{
	public static final PropertyBool DEV_MODE = new PropertyBool(false);
	public static final PropertyDouble ITEM_BASE_SPEED = new PropertyDouble(0.01D).setMin(0D);
	public static final PropertyByte MAX_LINK_LENGTH = new PropertyByte(250, 1, 255).setUnsigned();
	public static final PropertyDouble SUPER_BOOST = new PropertyDouble(10D, 1D, 1000D);

	//Client
	public static final PropertyDouble ITEM_RENDER_DISTANCE = new PropertyDouble(90D).setMin(0D);
	public static final PropertyBool ITEM_PARTICLES = new PropertyBool(true);

	public static void init(IFTBLibRegistry reg)
	{
		reg.addConfigFileProvider(ModularPipes.MOD_ID, () -> new File(LMUtils.folderConfig, "ModularPipes.json"));

		String group = ModularPipes.MOD_ID;
		reg.addConfig(group, "dev_mode", DEV_MODE);
		reg.addConfig(group, "item_base_speed", ITEM_BASE_SPEED);
		reg.addConfig(group, "max_link_length", MAX_LINK_LENGTH);
		reg.addConfig(group, "super_boost", SUPER_BOOST);
	}

	public static void initClient(IFTBLibClientRegistry reg)
	{
		String group = ModularPipes.MOD_ID;
		reg.addClientConfig(group, "item_render_distance", ITEM_RENDER_DISTANCE);
		reg.addClientConfig(group, "item_particles", ITEM_PARTICLES);
	}
}