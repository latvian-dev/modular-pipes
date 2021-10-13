package dev.latvian.mods.modularpipes.client;

import dev.latvian.mods.modularpipes.ModularPipesUtils;
import dev.latvian.mods.modularpipes.block.BasePipeBlock;
import dev.latvian.mods.modularpipes.block.entity.BasePipeBlockEntity;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class BakedPipeModel implements IDynamicBakedModel {
	public final PipeModelGeometry geometry;
	public final TextureAtlasSprite particle;
	public final PipeModelGeometry.ModelCallback modelCallback;
	public final List<List<BakedQuad>> base, connection, glassBase, glassConnection, module;
	public final List<BakedQuad> overlay;
	private final Int2ObjectOpenHashMap<List<BakedQuad>> solidCache;
	private final Int2ObjectOpenHashMap<List<BakedQuad>> cutoutCache;
	private final BakedModel bakedItem;
	private final ItemOverrides itemOverrideList;

	public BakedPipeModel(PipeModelGeometry m, TextureAtlasSprite p, PipeModelGeometry.ModelCallback c) {
		geometry = m;
		particle = p;
		modelCallback = c;
		BakedModel itemModel = c.getModel(m.modelItem, BlockModelRotation.X0_Y0, true);

		base = new ArrayList<>(4);
		base.add(modelCallback.get(geometry.modelBase, BlockModelRotation.X0_Y0, true));
		base.add(modelCallback.get(geometry.modelVertical, BlockModelRotation.X90_Y90, true));
		base.add(modelCallback.get(geometry.modelVertical, BlockModelRotation.X0_Y0, true));
		base.add(modelCallback.get(geometry.modelVertical, BlockModelRotation.X90_Y0, true));

		connection = new ArrayList<>(6);

		for (int i = 0; i < 6; i++) {
			connection.add(modelCallback.get(geometry.modelConnection, PipeModelGeometry.FACE_ROTATIONS[i], true));
		}

		glassBase = new ArrayList<>(4);
		glassBase.add(c.get(m.modelGlassBase, BlockModelRotation.X0_Y0, true));
		glassBase.add(c.get(m.modelGlassVertical, BlockModelRotation.X90_Y90, true));
		glassBase.add(c.get(m.modelGlassVertical, BlockModelRotation.X0_Y0, true));
		glassBase.add(c.get(m.modelGlassVertical, BlockModelRotation.X90_Y0, true));

		glassConnection = new ArrayList<>(6);

		for (int i = 0; i < 6; i++) {
			glassConnection.add(c.get(m.modelGlassConnection, PipeModelGeometry.FACE_ROTATIONS[i], true));
		}

		module = new ArrayList<>(6);

		for (int i = 0; i < 6; i++) {
			module.add(c.get(m.modelModule, PipeModelGeometry.FACE_ROTATIONS[i], false));
		}

		overlay = m.modelOverlay == null ? Collections.emptyList() : c.get(m.modelOverlay, BlockModelRotation.X0_Y0, true);

		solidCache = new Int2ObjectOpenHashMap<>();
		cutoutCache = new Int2ObjectOpenHashMap<>();

		bakedItem = new BakedPipeItemModel(this, itemModel.getTransforms(), ModularPipesUtils.combineAndOptimize(itemModel.getQuads(null, null, new Random()), overlay));

		itemOverrideList = new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) {
				return bakedItem;
			}
		};
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		RenderType layer = MinecraftForgeClient.getRenderLayer();

		if (layer == RenderType.cutout()) {
			return overlay;
		}

		int cutout = layer == RenderType.cutoutMipped() ? 1 : layer == RenderType.solid() ? 0 : -1;

		if (state == null || side != null || cutout == -1) {
			return Collections.emptyList();
		}

		boolean fancy = geometry.fancyModel;

		if (cutout == 1 && ModularPipesClientConfig.removePipeGlass && fancy) {
			return Collections.emptyList();
		}

		BasePipeBlockEntity pipe = null;

		if (extraData.hasProperty(BasePipeBlock.PIPE)) {
			pipe = extraData.getData(BasePipeBlock.PIPE);
		}

		if (pipe == null || pipe.invisible) {
			return Collections.emptyList();
		}

		if (!fancy && cutout == 0) {
			return Collections.emptyList();
		}

		// check for cover block here and return it in future

		int connections = pipe.getConnections();
		List<BakedQuad> quads = (cutout == 1 ? cutoutCache : solidCache).get(connections);

		if (quads != null) {
			return quads;
		}

		quads = new ArrayList<>();

		int baseIndex = 0;

		List<BakedQuad> extraQuads = null;
		int nconnections = 0;

		for (int i = 0; i < 6; i++) {
			int c = pipe.getConnection(i);

			if (c > 0) {
				nconnections |= 1 << i;

				if (c == 2 && cutout == 1) {
					if (extraQuads == null) {
						extraQuads = new ArrayList<>();
					}

					extraQuads.addAll(module.get(i));
				}
			}
		}

		if (pipe.getTier().maxModules == 0) {
			switch (nconnections) {
				case 0b110000:
					baseIndex = 1;
					break;
				case 0b000011:
					baseIndex = 2;
					break;
				case 0b001100:
					baseIndex = 3;
					break;
			}
		}

		if (cutout == 0 || !fancy) {
			quads.addAll(base.get(baseIndex));

			if (baseIndex == 0) {
				for (int i = 0; i < 6; i++) {
					if ((nconnections & (1 << i)) != 0) {
						quads.addAll(connection.get(i));
					}
				}
			}
		}

		if (cutout == 1) {
			quads.addAll(glassBase.get(baseIndex));

			if (baseIndex == 0) {
				for (int i = 0; i < 6; i++) {
					if ((nconnections & (1 << i)) != 0) {
						quads.addAll(glassConnection.get(i));
					}
				}
			}
		}

		quads = ModularPipesUtils.combineAndOptimize(quads, extraQuads);
		(cutout == 1 ? cutoutCache : solidCache).put(connections, quads);
		return quads;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean usesBlockLight() {
		return true;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return particle;
	}

	@Override
	public ItemOverrides getOverrides() {
		return itemOverrideList;
	}
}