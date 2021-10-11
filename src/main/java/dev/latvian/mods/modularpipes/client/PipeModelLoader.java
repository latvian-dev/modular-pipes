package dev.latvian.mods.modularpipes.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

/**
 * @author LatvianModder
 */
public class PipeModelLoader implements IModelLoader<PipeModelGeometry> {
	public static final PipeModelLoader INSTANCE = new PipeModelLoader();

	@Override
	public void onResourceManagerReload(ResourceManager arg) {
	}

	@Override
	public PipeModelGeometry read(JsonDeserializationContext jsonDeserializationContext, JsonObject json) {
		return new PipeModelGeometry();
	}
}