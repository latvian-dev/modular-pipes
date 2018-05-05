package com.latmod.modularpipes.client;

import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.client.ModelBase;
import com.feed_the_beast.ftblib.lib.io.Bits;
import com.google.common.collect.ImmutableList;
import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.ModularPipesConfig;
import com.latmod.modularpipes.block.BlockModularPipe;
import com.latmod.modularpipes.block.BlockPipeBase;
import com.latmod.modularpipes.data.IPipe;
import com.latmod.modularpipes.tile.TileModularPipe;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class ModelPipe extends DefaultStateMapper implements IModel, ICustomModelLoader
{
	public static final ModelPipe INSTANCE = new ModelPipe();

	public final ModelResourceLocation ID;
	public final ResourceLocation textureParticle;
	public final Collection<ResourceLocation> textures;

	public final ResourceLocation modelBase, modelConnection, modelVertical, modelOverlayError, modelOverlayErrorVertical, modelModule;
	public final ResourceLocation[] modelOverlay, modelOverlayVertical;
	public final ResourceLocation modelGlassBase, modelGlassConnection, modelGlassVertical;
	public final ResourceLocation modelGlassBaseOpaque, modelGlassConnectionOpaque, modelGlassVerticalOpaque;

	public final Collection<ResourceLocation> models;

	private ModelPipe()
	{
		ID = new ModelResourceLocation("modularpipes:pipe#normal");
		Collection<ResourceLocation> models0 = new ArrayList<>();

		models0.add(modelBase = new ResourceLocation("modularpipes:block/pipe/base"));
		models0.add(modelConnection = new ResourceLocation("modularpipes:block/pipe/connection"));
		models0.add(modelVertical = new ResourceLocation("modularpipes:block/pipe/vertical"));
		models0.add(modelOverlayError = new ResourceLocation("modularpipes:block/pipe/overlay/error"));
		models0.add(modelOverlayErrorVertical = new ResourceLocation("modularpipes:block/pipe/overlay/error_vertical"));
		models0.add(modelModule = new ResourceLocation("modularpipes:block/pipe/module"));

		modelOverlay = new ResourceLocation[ModularPipesConfig.tiers.getNameMap().values.size()];
		modelOverlayVertical = new ResourceLocation[ModularPipesConfig.tiers.getNameMap().values.size()];

		for (ModularPipesConfig.Tier tier : ModularPipesConfig.tiers.getNameMap())
		{
			models0.add(modelOverlay[tier.getIndex()] = new ResourceLocation(ModularPipes.MOD_ID + ":block/pipe/overlay/" + tier.toString()));
			models0.add(modelOverlayVertical[tier.getIndex()] = new ResourceLocation(ModularPipes.MOD_ID + ":block/pipe/overlay/" + tier.toString() + "_vertical"));
		}

		models0.add(modelGlassBase = new ResourceLocation("modularpipes:block/pipe/glass/base"));
		models0.add(modelGlassConnection = new ResourceLocation("modularpipes:block/pipe/glass/connection"));
		models0.add(modelGlassVertical = new ResourceLocation("modularpipes:block/pipe/glass/vertical"));

		models0.add(modelGlassBaseOpaque = new ResourceLocation("modularpipes:block/pipe/glass/base_opaque"));
		models0.add(modelGlassConnectionOpaque = new ResourceLocation("modularpipes:block/pipe/glass/connection_opaque"));
		models0.add(modelGlassVerticalOpaque = new ResourceLocation("modularpipes:block/pipe/glass/vertical_opaque"));

		models = Collections.unmodifiableCollection(models0);

		textureParticle = new ResourceLocation("modularpipes:blocks/pipe/particle");
		textures = ImmutableList.of(textureParticle);
	}

	@Override
	public boolean accepts(ResourceLocation id)
	{
		return ID.getResourceDomain().equals(id.getResourceDomain()) && ID.getResourcePath().equals(id.getResourcePath());
	}

	@Override
	public IModel loadModel(ResourceLocation id)
	{
		return this;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
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
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textures)
	{
		return new Baked(this, format, textures.apply(textureParticle), (id, rotation, uvlock) ->
		{
			IModel model = ModelLoaderRegistry.getModelOrMissing(id).smoothLighting(false).uvlock(uvlock);
			IBakedModel bakedModel = model.bake(rotation, format, textures);
			return Arrays.asList(bakedModel.getQuads(null, null, 0L).toArray(new BakedQuad[0]));
		});
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state)
	{
		return ID;
	}

	private interface ModelCallback
	{
		List<BakedQuad> get(ResourceLocation id, ModelRotation rotation, boolean uvlock);
	}

	private static class Baked extends ModelBase
	{
		private final List<List<BakedQuad>> base, glassBase, glassBaseOpaque, error;
		private final List<List<BakedQuad>> connection, glassConnection, glassConnectionOpaque, module;
		private final List<List<BakedQuad>> overlay;
		private final Int2ObjectOpenHashMap<List<BakedQuad>> cache;
		private final Map<BlockPipeBase, IBakedModel> bakedItem;
		private final ItemOverrideList itemOverrideList;

		private final class BakedItem extends ModelBase
		{
			private final List<BakedQuad> quads;

			public BakedItem(@Nullable ModularPipesConfig.Tier tier, boolean opaque)
			{
				super(null);
				quads = new ArrayList<>();
				quads.addAll(base.get(0));
				quads.addAll((opaque ? glassBaseOpaque : glassBase).get(0));

				if (tier != null)
				{
					quads.addAll(overlay.get(tier.getIndex()));
				}
			}

			@Override
			public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
			{
				return quads;
			}
		}

		public Baked(ModelPipe m, VertexFormat format, TextureAtlasSprite p, ModelCallback c)
		{
			super(p);
			base = new ArrayList<>(4);
			base.add(c.get(m.modelBase, ModelRotation.X0_Y0, false));
			base.add(c.get(m.modelVertical, ModelRotation.X90_Y90, false));
			base.add(c.get(m.modelVertical, ModelRotation.X0_Y0, false));
			base.add(c.get(m.modelVertical, ModelRotation.X90_Y0, false));

			glassBase = new ArrayList<>(4);
			glassBase.add(c.get(m.modelGlassBase, ModelRotation.X0_Y0, true));
			glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X90_Y90, true));
			glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X0_Y0, true));
			glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X90_Y0, true));

			glassBaseOpaque = new ArrayList<>(4);
			glassBaseOpaque.add(c.get(m.modelGlassBaseOpaque, ModelRotation.X0_Y0, true));
			glassBaseOpaque.add(c.get(m.modelGlassVerticalOpaque, ModelRotation.X90_Y90, true));
			glassBaseOpaque.add(c.get(m.modelGlassVerticalOpaque, ModelRotation.X0_Y0, true));
			glassBaseOpaque.add(c.get(m.modelGlassVerticalOpaque, ModelRotation.X90_Y0, true));

			error = new ArrayList<>(4);
			error.add(c.get(m.modelOverlayError, ModelRotation.X0_Y0, false));
			error.add(c.get(m.modelOverlayErrorVertical, ModelRotation.X90_Y90, false));
			error.add(c.get(m.modelOverlayErrorVertical, ModelRotation.X0_Y0, false));
			error.add(c.get(m.modelOverlayErrorVertical, ModelRotation.X90_Y0, false));

			connection = new ArrayList<>(6);
			glassConnection = new ArrayList<>(6);
			glassConnectionOpaque = new ArrayList<>(6);
			module = new ArrayList<>(6);

			for (int i = 0; i < 6; i++)
			{
				connection.add(c.get(m.modelConnection, ClientUtils.FACE_ROTATIONS[i], false));
				glassConnection.add(c.get(m.modelGlassConnection, ClientUtils.FACE_ROTATIONS[i], true));
				glassConnectionOpaque.add(c.get(m.modelGlassConnectionOpaque, ClientUtils.FACE_ROTATIONS[i], true));
				module.add(c.get(m.modelModule, ClientUtils.FACE_ROTATIONS[i], false));
			}

			overlay = new ArrayList<>(m.modelOverlay.length * 4);

			for (int i = 0; i < m.modelOverlay.length; i++)
			{
				overlay.add(c.get(m.modelOverlay[i], ModelRotation.X0_Y0, false));
			}

			for (int i = 0; i < m.modelOverlay.length; i++)
			{
				overlay.add(c.get(m.modelOverlayVertical[i], ModelRotation.X90_Y90, false));
			}

			for (int i = 0; i < m.modelOverlay.length; i++)
			{
				overlay.add(c.get(m.modelOverlayVertical[i], ModelRotation.X0_Y0, false));
			}

			for (int i = 0; i < m.modelOverlay.length; i++)
			{
				overlay.add(c.get(m.modelOverlayVertical[i], ModelRotation.X90_Y0, false));
			}

			cache = new Int2ObjectOpenHashMap<>();
			bakedItem = new HashMap<>();
			itemOverrideList = new ItemOverrideList(Collections.emptyList())
			{
				@Override
				public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
				{
					Block block = Block.getBlockFromItem(stack.getItem());

					if (!(block instanceof BlockPipeBase))
					{
						return originalModel;
					}

					BlockPipeBase pipe = (BlockPipeBase) block;

					IBakedModel model = bakedItem.get(pipe);

					if (model == null)
					{
						model = new BakedItem(pipe instanceof BlockModularPipe ? ((BlockModularPipe) pipe).tier : null, pipe.opaque);
						bakedItem.put(pipe, model);
					}

					return model;
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

			IPipe pipe = null;

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
			int modules = 0;
			int tierIndex = -1;

			if (pipe instanceof TileModularPipe)
			{
				TileModularPipe modularPipe = (TileModularPipe) pipe;

				for (int i = 0; i < 6; i++)
				{
					if (modularPipe.modules[i].hasModule())
					{
						modules |= 1 << i;
					}
				}

				connections = modules;
			}

			connections |= pipe.getConnections();
			boolean opaque = pipe.isPipeOpaque();
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
				if (pipe instanceof TileModularPipe)
				{
					TileModularPipe modularPipe = (TileModularPipe) pipe;

					if (modularPipe.hasError())
					{
						return error.get(baseIndex);
					}
					else
					{
						return overlay.get(modularPipe.tier.getIndex() + ModularPipesConfig.tiers.getNameMap().values.size() * baseIndex);
					}
				}
				else
				{
					return Collections.emptyList();
				}
			}

			int cacheIndex = connections | ((opaque ? 1 : 0) << 6) | ((layer == BlockRenderLayer.CUTOUT ? 1 : 0) << 7) | (modules << 8);

			List<BakedQuad> quads = cache.get(cacheIndex);

			if (quads != null)
			{
				return quads;
			}

			quads = new ArrayList<>();

			if (layer == BlockRenderLayer.SOLID)
			{
				quads.addAll(base.get(baseIndex));

				if (baseIndex == 0)
				{
					for (int i = 0; i < 6; i++)
					{
						if (Bits.getFlag(connections, 1 << i))
						{
							quads.addAll(connection.get(i));
						}
					}
				}

				for (int i = 0; i < 6; i++)
				{
					if (Bits.getFlag(modules, 1 << i))
					{
						quads.addAll(module.get(i));
					}
				}

				if (opaque)
				{
					quads.addAll(glassBaseOpaque.get(baseIndex));

					if (baseIndex == 0)
					{
						for (int i = 0; i < 6; i++)
						{
							if (Bits.getFlag(connections, 1 << i))
							{
								quads.addAll(glassConnectionOpaque.get(i));
							}
						}
					}
				}
			}

			if (!opaque && layer == BlockRenderLayer.CUTOUT)
			{
				quads.addAll(glassBase.get(baseIndex));

				if (baseIndex == 0)
				{
					for (int i = 0; i < 6; i++)
					{
						if (Bits.getFlag(connections, 1 << i))
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
		public ItemOverrideList getOverrides()
		{
			return itemOverrideList;
		}
	}
}