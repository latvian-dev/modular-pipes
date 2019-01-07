package com.latmod.modularpipes.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.block.BlockPipeBase;
import com.latmod.modularpipes.block.BlockPipeModular;
import com.latmod.modularpipes.block.EnumMK;
import com.latmod.modularpipes.block.EnumPipeSkin;
import com.latmod.modularpipes.tile.TilePipeBase;
import com.latmod.modularpipes.tile.TilePipeModularMK1;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class ModelPipe extends DefaultStateMapper implements IModel
{
	public static final ModelRotation[] FACE_ROTATIONS = {
			ModelRotation.X0_Y0,
			ModelRotation.X180_Y0,
			ModelRotation.X90_Y180,
			ModelRotation.X90_Y0,
			ModelRotation.X90_Y90,
			ModelRotation.X90_Y270
	};

	private static final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> TRANSFORM_MAP;

	private static TRSRTransformation get(float ty, float ax, float ay, float s)
	{
		return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new javax.vecmath.Vector3f(0F, ty / 16F, 0F), TRSRTransformation.quatFromXYZDegrees(new javax.vecmath.Vector3f(ax, ay, 0F)), new javax.vecmath.Vector3f(s, s, s), null));
	}

	static
	{
		TRSRTransformation thirdperson = get(2.5F, 75, 45, 0.375F);
		TRSRTransformation flipX = new TRSRTransformation(null, null, new javax.vecmath.Vector3f(-1, 1, 1), null);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		builder.put(ItemCameraTransforms.TransformType.GUI, get(0, 30, 225, 0.625F));
		builder.put(ItemCameraTransforms.TransformType.GROUND, get(3, 0, 0, 0.25F));
		builder.put(ItemCameraTransforms.TransformType.FIXED, get(0, 0, 0, 0.5F));
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
		builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(thirdperson)).compose(flipX)));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, get(0, 0, 45, 0.4F));
		builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, get(0, 0, 225, 0.4F));
		TRANSFORM_MAP = Maps.immutableEnumMap(builder.build());
	}

	public static org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handleModelPerspective(IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType)
	{
		return PerspectiveMapWrapper.handlePerspective(model, TRANSFORM_MAP, cameraTransformType);
	}

	public final ResourceLocation textureParticle;
	public final Collection<ResourceLocation> textures;

	public final ResourceLocation modelBase, modelConnection, modelVertical;
	public final ResourceLocation modelOverlay;
	public final ResourceLocation modelGlassBase, modelGlassConnection, modelGlassVertical;
	public final ResourceLocation modelColor;

	public final ResourceLocation[] skinTextures, colorTextures;
	public final ResourceLocation[] overlayTextures;

	public final Collection<ResourceLocation> models;

	public ModelPipe()
	{
		Collection<ResourceLocation> models0 = new HashSet<>();
		models0.add(modelBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/base"));
		models0.add(modelConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/connection"));
		models0.add(modelVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/vertical"));
		models0.add(modelOverlay = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/overlay"));
		models0.add(modelGlassBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_base"));
		models0.add(modelGlassConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_connection"));
		models0.add(modelGlassVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass_vertical"));
		models0.add(modelColor = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/color"));

		models = Collections.unmodifiableCollection(models0);
		textures = new HashSet<>();

		skinTextures = new ResourceLocation[EnumPipeSkin.VALUES.length];

		for (EnumPipeSkin skin : EnumPipeSkin.VALUES)
		{
			textures.add(skinTextures[skin.ordinal()] = skin.texture);
		}

		colorTextures = new ResourceLocation[6];
		textures.add(colorTextures[0] = new ResourceLocation("minecraft:blocks/concrete_gray"));
		textures.add(colorTextures[1] = new ResourceLocation("minecraft:blocks/concrete_silver"));
		textures.add(colorTextures[2] = new ResourceLocation("minecraft:blocks/concrete_red"));
		textures.add(colorTextures[3] = new ResourceLocation("minecraft:blocks/concrete_light_blue"));
		textures.add(colorTextures[4] = new ResourceLocation("minecraft:blocks/concrete_lime"));
		textures.add(colorTextures[5] = new ResourceLocation("minecraft:blocks/concrete_yellow"));

		overlayTextures = new ResourceLocation[EnumMK.VALUES.length];

		for (int i = 0; i < EnumMK.VALUES.length; i++)
		{
			textures.add(overlayTextures[i] = new ResourceLocation("modularpipes:blocks/pipe/overlay/" + EnumMK.VALUES[i].getName()));
		}

		textures.add(textureParticle = new ResourceLocation("minecraft:blocks/cobblestone"));
	}

	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return models;
	}

	@Override
	public Collection<ResourceLocation> getTextures()
	{
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> tex)
	{
		return new Baked(this, tex.apply(textureParticle), (id, rotation, retextures) ->
		{
			ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

			for (Map.Entry entry : retextures)
			{
				builder.put(new AbstractMap.SimpleEntry(entry.getKey().toString(), new ResourceLocation(entry.getValue().toString()).toString()));
			}

			IModel model = ModelLoaderRegistry.getModelOrMissing(id).uvlock(true).retexture(builder.build());//.smoothLighting(false);
			IBakedModel bakedModel = model.bake(rotation, format, tex);
			return Arrays.asList(bakedModel.getQuads(null, null, 0L).toArray(new BakedQuad[0]));
		});
	}

	private interface ModelCallback
	{
		List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, Map.Entry... retextures);
	}

	private static class Baked implements IBakedModel
	{
		private final TextureAtlasSprite particle;
		private final List<List<List<BakedQuad>>> base, connection;
		private final List<List<BakedQuad>> glassBase, glassConnection, colors, overlay;
		private final Int2ObjectOpenHashMap<List<BakedQuad>> cache;
		private final IBakedModel bakedItem;
		private final IBakedModel[] bakedItemWithOverlay;
		private final ItemOverrideList itemOverrideList;

		private final class BakedItem implements IBakedModel
		{
			private List<BakedQuad> quads;

			public BakedItem(@Nullable EnumMK mk)
			{
				quads = new ArrayList<>();
				quads.addAll(base.get(0).get(0));
				quads.addAll(glassBase.get(0));

				if (mk != null)
				{
					quads.addAll(overlay.get(mk.ordinal()));
				}

				quads = Collections.unmodifiableList(Arrays.asList(quads.toArray(new BakedQuad[0])));
			}

			@Override
			public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
			{
				return quads;
			}

			@Override
			public boolean isAmbientOcclusion()
			{
				return true;
			}

			@Override
			public boolean isGui3d()
			{
				return true;
			}

			@Override
			public boolean isBuiltInRenderer()
			{
				return false;
			}

			@Override
			public TextureAtlasSprite getParticleTexture()
			{
				return particle;
			}

			@Override
			public ItemOverrideList getOverrides()
			{
				return ItemOverrideList.NONE;
			}

			@Override
			public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
			{
				return handleModelPerspective(this, cameraTransformType);
			}
		}

		public Baked(ModelPipe m, TextureAtlasSprite p, ModelCallback c)
		{
			particle = p;
			base = new ArrayList<>(m.skinTextures.length);

			for (int i = 0; i < m.skinTextures.length; i++)
			{
				AbstractMap.SimpleEntry entry = new AbstractMap.SimpleEntry("material", m.skinTextures[i]);
				List<List<BakedQuad>> base1 = new ArrayList<>(4);
				base1.add(c.get(m.modelBase, ModelRotation.X0_Y0, entry));
				base1.add(c.get(m.modelVertical, ModelRotation.X90_Y90, entry));
				base1.add(c.get(m.modelVertical, ModelRotation.X0_Y0, entry));
				base1.add(c.get(m.modelVertical, ModelRotation.X90_Y0, entry));
				base.add(base1);
			}

			glassBase = new ArrayList<>(4);
			glassBase.add(c.get(m.modelGlassBase, ModelRotation.X0_Y0));
			glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X90_Y90));
			glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X0_Y0));
			glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X90_Y0));

			connection = new ArrayList<>(m.skinTextures.length);
			glassConnection = new ArrayList<>(6);
			colors = new ArrayList<>(6);

			for (int col = 0; col < m.skinTextures.length; col++)
			{
				AbstractMap.SimpleEntry entry = new AbstractMap.SimpleEntry("material", m.skinTextures[col]);
				List<List<BakedQuad>> connection1 = new ArrayList<>(6);

				for (int i = 0; i < 6; i++)
				{
					connection1.add(c.get(m.modelConnection, FACE_ROTATIONS[i], entry));
				}

				connection.add(connection1);
			}

			for (int i = 0; i < 6; i++)
			{
				glassConnection.add(c.get(m.modelGlassConnection, FACE_ROTATIONS[i]));
				colors.add(c.get(m.modelColor, FACE_ROTATIONS[i], new AbstractMap.SimpleEntry("color", m.colorTextures[i])));
			}

			overlay = new ArrayList<>(EnumMK.VALUES.length);

			for (int i = 0; i < EnumMK.VALUES.length; i++)
			{
				overlay.add(c.get(m.modelOverlay, ModelRotation.X0_Y0, new AbstractMap.SimpleEntry("overlay", m.overlayTextures[i])));
			}

			cache = new Int2ObjectOpenHashMap<>();
			bakedItem = new BakedItem(null);
			bakedItemWithOverlay = new BakedItem[EnumMK.VALUES.length];

			for (EnumMK mk : EnumMK.VALUES)
			{
				bakedItemWithOverlay[mk.ordinal()] = new BakedItem(mk);
			}

			itemOverrideList = new ItemOverrideList(Collections.emptyList())
			{
				@Override
				public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
				{
					Block block = Block.getBlockFromItem(stack.getItem());
					return block instanceof BlockPipeBase ? block instanceof BlockPipeModular ? bakedItemWithOverlay[((BlockPipeModular) block).tier.ordinal()] : bakedItem : originalModel;
				}
			};
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
		{
			if (state == null || side != null)
			{
				return Collections.emptyList();
			}

			TilePipeBase pipe = null;

			if (state instanceof IExtendedBlockState)
			{
				pipe = ((IExtendedBlockState) state).getValue(BlockPipeBase.PIPE);
			}

			if (pipe == null)
			{
				return Collections.emptyList();
			}

			BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();

			int connections = 0;

			for (EnumFacing facing : EnumFacing.VALUES)
			{
				if (pipe.isConnected(facing))
				{
					connections |= 1 << facing.getIndex();
				}
			}

			int baseIndex = 0;

			switch (connections)
			{
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

			if (layer == BlockRenderLayer.TRANSLUCENT)
			{
				if (pipe instanceof TilePipeModularMK1)
				{
					return overlay.get(((TilePipeModularMK1) pipe).getMK().ordinal());
				}
				else
				{
					return Collections.emptyList();
				}
			}

			int cacheIndex = connections | ((layer == BlockRenderLayer.CUTOUT ? 1 : 0) << 7) | ((pipe instanceof TilePipeModularMK1 ? 1 : 0) << 8) | (pipe.skin.ordinal() << 16);

			List<BakedQuad> quads = cache.get(cacheIndex);

			if (quads != null)
			{
				return quads;
			}

			quads = new ArrayList<>();

			if (layer == BlockRenderLayer.SOLID)
			{
				quads.addAll(base.get(pipe.skin.ordinal()).get(baseIndex));

				if (baseIndex == 0)
				{
					for (int i = 0; i < 6; i++)
					{
						if ((connections & (1 << i)) != 0)
						{
							quads.addAll(connection.get(pipe.skin.ordinal()).get(i));
						}
					}
				}

				if (pipe instanceof TilePipeModularMK1)
				{
					for (int i = 0; i < 6; i++)
					{
						if ((connections & (1 << i)) != 0)
						{
							quads.addAll(colors.get(i));
						}
					}
				}
			}

			if (layer == BlockRenderLayer.CUTOUT)
			{
				quads.addAll(glassBase.get(baseIndex));

				if (baseIndex == 0)
				{
					for (int i = 0; i < 6; i++)
					{
						if ((connections & (1 << i)) != 0)
						{
							quads.addAll(glassConnection.get(i));
						}
					}
				}
			}

			quads = Collections.unmodifiableList(Arrays.asList(quads.toArray(new BakedQuad[0])));
			cache.put(cacheIndex, quads);
			return quads;
		}

		@Override
		public boolean isAmbientOcclusion()
		{
			return true;
		}

		@Override
		public boolean isGui3d()
		{
			return true;
		}

		@Override
		public boolean isBuiltInRenderer()
		{
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture()
		{
			return particle;
		}

		@Override
		public ItemOverrideList getOverrides()
		{
			return itemOverrideList;
		}

		@Override
		public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
		{
			return handleModelPerspective(this, cameraTransformType);
		}
	}
}