package dev.latvian.mods.modularpipes.client;

import dev.latvian.mods.modularpipes.ModularPipesUtils;
import dev.latvian.mods.modularpipes.block.PipeBlock;
import dev.latvian.mods.modularpipes.block.entity.PipeBlockEntity;
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
	public final List<List<BakedQuad>> base, connection, glassBase, glassConnection, module, light;
	public final List<BakedQuad> overlay;
	private final Int2ObjectOpenHashMap<List<BakedQuad>> cache;
	private final BakedModel bakedItem;
	private final ItemOverrides itemOverrideList;

	public BakedPipeModel(PipeModelGeometry g, TextureAtlasSprite p, PipeModelGeometry.ModelCallback c) {
		geometry = g;
		particle = p;
		BakedModel baseModel = c.getModel(geometry.modelBase, BlockModelRotation.X0_Y0, false);

		base = new ArrayList<>(4);
		base.add(c.get(geometry.modelBase, BlockModelRotation.X0_Y0, false));
		base.add(c.get(geometry.modelVertical, BlockModelRotation.X90_Y90, false));
		base.add(c.get(geometry.modelVertical, BlockModelRotation.X0_Y0, false));
		base.add(c.get(geometry.modelVertical, BlockModelRotation.X90_Y0, false));

		connection = new ArrayList<>(6);

		for (int i = 0; i < 6; i++) {
			connection.add(c.get(geometry.modelConnection, PipeModelGeometry.FACE_ROTATIONS[i], false));
		}

		if (geometry.pipeGlass) {
			glassBase = new ArrayList<>(4);
			glassBase.add(c.get(geometry.modelGlassBase, BlockModelRotation.X0_Y0, true));
			glassBase.add(c.get(geometry.modelGlassVertical, BlockModelRotation.X90_Y90, true));
			glassBase.add(c.get(geometry.modelGlassVertical, BlockModelRotation.X0_Y0, true));
			glassBase.add(c.get(geometry.modelGlassVertical, BlockModelRotation.X90_Y0, true));

			glassConnection = new ArrayList<>(6);

			for (int i = 0; i < 6; i++) {
				glassConnection.add(c.get(geometry.modelGlassConnection, PipeModelGeometry.FACE_ROTATIONS[i], true));
			}
		} else {
			glassBase = Collections.emptyList();
			glassConnection = Collections.emptyList();
		}

		module = new ArrayList<>(6);
		light = new ArrayList<>(6);

		for (int i = 0; i < 6; i++) {
			module.add(c.get(geometry.modelModule, PipeModelGeometry.FACE_ROTATIONS[i], false));
			light.add(c.get(geometry.modelLight, PipeModelGeometry.FACE_ROTATIONS[i], false));
		}

		overlay = geometry.modelOverlay == null ? Collections.emptyList() : c.get(geometry.modelOverlay, BlockModelRotation.X0_Y0, false);

		cache = new Int2ObjectOpenHashMap<>();

		List<BakedQuad> bakedItemQuads = new ArrayList<>(base.get(0).size() + (geometry.pipeGlass ? glassBase.get(0).size() : 0) + overlay.size());
		bakedItemQuads.addAll(base.get(0));

		if (geometry.pipeGlass) {
			bakedItemQuads.addAll(glassBase.get(0));
		}

		bakedItemQuads.addAll(overlay);

		bakedItem = new BakedPipeItemModel(this, baseModel.getTransforms(), ModularPipesUtils.optimize(bakedItemQuads));

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

		if (layer == RenderType.cutoutMipped()) {
			return overlay;
		}

		if (state == null || side != null || layer != RenderType.cutout()) {
			return Collections.emptyList();
		}

		PipeBlockEntity pipe = null;

		if (extraData.hasProperty(PipeBlock.PIPE)) {
			pipe = extraData.getData(PipeBlock.PIPE);
		}

		if (pipe == null) {
			return Collections.emptyList();
		}

		// check for cover block here and return it in future

		int modelIndex = pipe.getModelIndex();
		List<BakedQuad> quads = cache.get(modelIndex);

		if (quads != null) {
			return quads;
		}

		quads = new ArrayList<>();

		int nconnections = 0;
		int baseIndex = 0;

		for (int i = 0; i < 6; i++) {
			int c = (modelIndex >> (i * 3)) & 7;

			if ((c & 1) != 0) {
				nconnections |= 1 << i;
			}

			if ((c & 2) != 0) {
				quads.addAll(module.get(i));
			}

			if ((c & 4) != 0) {
				quads.addAll(light.get(i));
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

		quads.addAll(base.get(baseIndex));

		if (baseIndex == 0) {
			for (int i = 0; i < 6; i++) {
				if ((nconnections & (1 << i)) != 0) {
					quads.addAll(connection.get(i));
				}
			}
		}

		if (geometry.pipeGlass) {
			quads.addAll(glassBase.get(baseIndex));

			if (baseIndex == 0) {
				for (int i = 0; i < 6; i++) {
					if ((nconnections & (1 << i)) != 0) {
						quads.addAll(glassConnection.get(i));
					}
				}
			}
		}

		quads = ModularPipesUtils.optimize(quads);
		cache.put(modelIndex, quads);
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