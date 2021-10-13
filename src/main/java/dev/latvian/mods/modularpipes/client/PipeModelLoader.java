package dev.latvian.mods.modularpipes.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import dev.latvian.mods.modularpipes.ModularPipes;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
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

	private BlockModel createUnbakedModel(JsonDeserializationContext context, String parent, String material, String overlay) {
		JsonObject json = new JsonObject();
		json.addProperty("parent", parent);

		JsonObject tex = new JsonObject();
		tex.addProperty("material", material);

		if (!overlay.isEmpty()) {
			tex.addProperty("overlay", overlay);
		}

		json.add("textures", tex);

		return context.deserialize(json, BlockModel.class);
	}

	private BlockModel createUnbakedModel(JsonDeserializationContext context, String parent, String material) {
		return createUnbakedModel(context, parent, material, "");
	}

	private String path(String path) {
		return ModularPipes.MOD_ID + ":block/pipe/" + path;
	}

	private String pathFromType(PipeModelGeometry m, String path) {
		return path(path + "_" + m.modelType.name);
	}

	@Override
	public PipeModelGeometry read(JsonDeserializationContext context, JsonObject json) {
		String material = json.get("material").getAsString();
		PipeModelGeometry m = new PipeModelGeometry();
		m.modelType = ModularPipesClientConfig.modelType;
		m.pipeGlass = ModularPipesClientConfig.pipeGlass;
		m.material = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(material));

		m.models.add(m.modelBase = createUnbakedModel(context, pathFromType(m, "base"), material));
		m.models.add(m.modelConnection = createUnbakedModel(context, pathFromType(m, "connection"), material));
		m.models.add(m.modelVertical = createUnbakedModel(context, pathFromType(m, "vertical"), material));
		m.models.add(m.modelModule = createUnbakedModel(context, path("module"), material));

		if (m.pipeGlass) {
			m.models.add(m.modelGlassBase = createUnbakedModel(context, path("glass_base"), material));
			m.models.add(m.modelGlassConnection = createUnbakedModel(context, path("glass_connection"), material));
			m.models.add(m.modelGlassVertical = createUnbakedModel(context, path("glass_vertical"), material));
		}

		if (json.has("overlay")) {
			m.models.add(m.modelOverlay = createUnbakedModel(context, path("overlay"), material, json.get("overlay").getAsString()));
		}

		return m;
	}
}