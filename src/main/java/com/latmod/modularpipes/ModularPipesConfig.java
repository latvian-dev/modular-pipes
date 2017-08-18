package com.latmod.modularpipes;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.events.registry.RegisterClientConfigEvent;
import com.feed_the_beast.ftbl.api.events.registry.RegisterConfigEvent;
import com.feed_the_beast.ftbl.lib.config.PropertyBool;
import com.feed_the_beast.ftbl.lib.config.PropertyByte;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

/**
 * @author LatvianModder
 */
@EventHandler
public class ModularPipesConfig
{
	public static final PropertyBool DEV_MODE = new PropertyBool(false);
	public static final PropertyDouble ITEM_BASE_SPEED = new PropertyDouble(0.01D).setMin(0D);
	public static final PropertyByte MAX_LINK_LENGTH = new PropertyByte(250, 1, 255).setUnsigned();
	public static final PropertyDouble SUPER_BOOST = new PropertyDouble(10D, 1D, 1000D);

	//Client
	public static final PropertyDouble ITEM_RENDER_DISTANCE = new PropertyDouble(90D).setMin(0D);
	public static final PropertyBool ITEM_PARTICLES = new PropertyBool(true);

	@SubscribeEvent
	public static void init(RegisterConfigEvent event)
	{
		event.registerFile(ModularPipes.MOD_ID, () -> new File(CommonUtils.folderConfig, "ModularPipes.json"));

		String group = ModularPipes.MOD_ID;
		event.register(group, "dev_mode", DEV_MODE);
		event.register(group, "item_base_speed", ITEM_BASE_SPEED);
		event.register(group, "max_link_length", MAX_LINK_LENGTH);
		event.register(group, "super_boost", SUPER_BOOST);
	}

	@SubscribeEvent
	public static void initClient(RegisterClientConfigEvent event)
	{
		String group = ModularPipes.MOD_ID;
		event.register(group, "item_render_distance", ITEM_RENDER_DISTANCE);
		event.register(group, "item_particles", ITEM_PARTICLES);
	}
}