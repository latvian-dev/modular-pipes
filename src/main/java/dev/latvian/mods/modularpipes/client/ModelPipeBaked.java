package dev.latvian.mods.modularpipes.client;

import dev.latvian.mods.modularpipes.ModularPipesUtils;
import dev.latvian.mods.modularpipes.block.BlockPipeBase;
import dev.latvian.mods.modularpipes.block.BlockPipeModular;
import dev.latvian.mods.modularpipes.block.EnumMK;
import dev.latvian.mods.modularpipes.block.entity.TilePipeBase;
import dev.latvian.mods.modularpipes.block.entity.TilePipeModularMK1;
import dev.latvian.mods.modularpipes.item.module.PipeModule;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author LatvianModder
 */
public class ModelPipeBaked implements IBakedModel {
	public final ModelPipe modelPipe;
	public final TextureAtlasSprite particle;
	public final ModelPipe.ModelCallback modelCallback;
	public final Int2ObjectOpenHashMap<List<List<BakedQuad>>> base, connection;
	public final List<List<BakedQuad>> glassBase, glassConnection, overlay, module;
	private final Int2ObjectOpenHashMap<Int2ObjectOpenHashMap<List<BakedQuad>>> cache;
	private final IBakedModel bakedItem;
	private final IBakedModel[] bakedItemWithOverlay;
	private final ItemOverrideList itemOverrideList;

	public ModelPipeBaked(ModelPipe m, TextureAtlasSprite p, ModelPipe.ModelCallback c) {
		modelPipe = m;
		particle = p;
		modelCallback = c;
		base = new Int2ObjectOpenHashMap<>();
		glassBase = new ArrayList<>(4);
		glassBase.add(c.get(m.modelGlassBase, ModelRotation.X0_Y0));
		glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X90_Y90));
		glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X0_Y0));
		glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X90_Y0));

		connection = new Int2ObjectOpenHashMap<>();
		glassConnection = new ArrayList<>(6);

		for (int i = 0; i < 6; i++) {
			glassConnection.add(c.get(m.modelGlassConnection, ModelPipe.FACE_ROTATIONS[i]));
		}

		overlay = new ArrayList<>(EnumMK.VALUES.length);

		for (int i = 0; i < EnumMK.VALUES.length; i++) {
			overlay.add(c.get(m.modelOverlay, ModelRotation.X0_Y0, new AbstractMap.SimpleEntry<>("overlay", m.overlayTextures[i])));
		}

		module = new ArrayList<>(6);

		for (int i = 0; i < 6; i++) {
			module.add(c.get(m.modelModule, ModelPipe.FACE_ROTATIONS[i], false));
		}

		cache = new Int2ObjectOpenHashMap<>();
		bakedItem = new ModelPipeBakedItem(this, null);
		bakedItemWithOverlay = new ModelPipeBakedItem[EnumMK.VALUES.length];

		for (EnumMK mk : EnumMK.VALUES) {
			bakedItemWithOverlay[mk.ordinal()] = new ModelPipeBakedItem(this, mk);
		}

		itemOverrideList = new ItemOverrideList() {
			@Override
			public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
				Block block = Block.getBlockFromItem(stack.getItem());
				return block instanceof BlockPipeBase ? block instanceof BlockPipeModular ? bakedItemWithOverlay[((BlockPipeModular) block).tier.ordinal()] : bakedItem : originalModel;
			}
		};
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		return Collections.emptyList();
	}

	@Nonnull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
		if (state == null || side != null) {
			return Collections.emptyList();
		}

		TilePipeBase pipe = null;

		if (extraData.hasProperty(BlockPipeBase.PIPE)) {
			pipe = extraData.getData(BlockPipeBase.PIPE);
		}

		if (pipe == null || pipe.invisible) {
			return Collections.emptyList();
		}

		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();

		if (layer == BlockRenderLayer.TRANSLUCENT) {
			if (pipe instanceof TilePipeModularMK1) {
				return overlay.get(((TilePipeModularMK1) pipe).getMK().ordinal());
			} else {
				return Collections.emptyList();
			}
		}

		int connections = 0;

		for (Direction facing : Direction.values()) {
			if (pipe.isConnected(facing)) {
				connections |= 1 << facing.getIndex();
			}
		}

		int baseIndex = 0;

		switch (connections) {
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

		List<BakedQuad> extraQuads = null;

		if (pipe instanceof TilePipeModularMK1) {
			int[] modules = new int[6];

			for (PipeModule module : ((TilePipeModularMK1) pipe).modules) {
				for (int i = 0; i < 6; i++) {
					if (module.isConnected(Direction.values()[i])) {
						modules[i]++;
					}
				}
			}

			for (int i = 0; i < 6; i++) {
				if (modules[i] > 0) {
					if (extraQuads == null) {
						extraQuads = new ArrayList<>();
					}

					extraQuads.addAll(module.get(i));
				}
			}
		}

		Int2ObjectOpenHashMap<List<BakedQuad>> cacheMap = cache.get(pipe.paint);

		if (cacheMap == null) {
			cacheMap = new Int2ObjectOpenHashMap<>();
			cache.put(pipe.paint, cacheMap);

			TextureAtlasSprite sprite = null;

			if (pipe.paint != 0) {
				try {
					sprite = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Block.getStateById(pipe.paint));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (sprite == null) {
				sprite = particle;
			}

			AbstractMap.SimpleEntry<String, ResourceLocation> entry = new AbstractMap.SimpleEntry<>("material", sprite.getName());
			List<List<BakedQuad>> base1 = new ArrayList<>(4);
			base1.add(modelCallback.get(modelPipe.modelBase, ModelRotation.X0_Y0, entry));
			base1.add(modelCallback.get(modelPipe.modelVertical, ModelRotation.X90_Y90, entry));
			base1.add(modelCallback.get(modelPipe.modelVertical, ModelRotation.X0_Y0, entry));
			base1.add(modelCallback.get(modelPipe.modelVertical, ModelRotation.X90_Y0, entry));
			base.put(pipe.paint, base1);

			List<List<BakedQuad>> connection1 = new ArrayList<>(6);

			for (int i = 0; i < 6; i++) {
				connection1.add(modelCallback.get(modelPipe.modelConnection, ModelPipe.FACE_ROTATIONS[i], entry));
			}

			connection.put(pipe.paint, connection1);
		}

		int cacheIndex = connections | ((layer == BlockRenderLayer.CUTOUT ? 1 : 0) << 6);

		List<BakedQuad> quads = cacheMap.get(cacheIndex);

		if (quads != null) {
			if (extraQuads != null) {
				return ModularPipesUtils.combineAndOptimize(quads, extraQuads);
			}

			return quads;
		}

		quads = new ArrayList<>();

		if (layer == BlockRenderLayer.SOLID) {
			quads.addAll(base.get(pipe.paint).get(baseIndex));

			if (baseIndex == 0) {
				for (int i = 0; i < 6; i++) {
					if ((connections & (1 << i)) != 0) {
						quads.addAll(connection.get(pipe.paint).get(i));
					}
				}
			}
		}

		if (layer == BlockRenderLayer.CUTOUT) {
			quads.addAll(glassBase.get(baseIndex));

			if (baseIndex == 0) {
				for (int i = 0; i < 6; i++) {
					if ((connections & (1 << i)) != 0) {
						quads.addAll(glassConnection.get(i));
					}
				}
			}
		}

		quads = ModularPipesUtils.optimize(quads);
		cacheMap.put(cacheIndex, quads);

		if (extraQuads != null) {
			return ModularPipesUtils.combineAndOptimize(quads, extraQuads);
		}

		return quads;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return particle;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return itemOverrideList;
	}

	@Override
	public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		return ModelPipe.handleModelPerspective(this, cameraTransformType);
	}
}