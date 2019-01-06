package com.latmod.modularpipes.client;

/**
 * @author LatvianModder
 */
public class ModelPipe// extends DefaultStateMapper implements IModel, ICustomModelLoader
{
	public static final ModelPipe INSTANCE = new ModelPipe();

	/*
	public final ModelResourceLocation ID;
	public final ResourceLocation textureParticle;
	public final Collection<ResourceLocation> textures;

	public final ResourceLocation modelBase, modelConnection, modelVertical, modelModule;
	public final ResourceLocation modelOverlay, modelOverlayVertical, modelOverlayError, modelOverlayErrorVertical;
	public final ResourceLocation modelGlassBase, modelGlassConnection, modelGlassVertical;

	public final Collection<ResourceLocation> models;

	private ModelPipe()
	{
		ID = new ModelResourceLocation("modularpipes:pipe#normal");
		Collection<ResourceLocation> models0 = new ArrayList<>();

		models0.add(modelBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/base"));
		models0.add(modelConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/connection"));
		models0.add(modelVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/vertical"));
		models0.add(modelOverlay = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/overlay/basic"));
		models0.add(modelOverlayVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/overlay/basic_vertical"));
		models0.add(modelOverlayError = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/overlay/error"));
		models0.add(modelOverlayErrorVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/overlay/error_vertical"));
		models0.add(modelModule = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/module"));
		models0.add(modelGlassBase = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass/base"));
		models0.add(modelGlassConnection = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass/connection"));
		models0.add(modelGlassVertical = new ResourceLocation(ModularPipes.MOD_ID, "block/pipe/glass/vertical"));

		models = Collections.unmodifiableCollection(models0);

		textureParticle = new ResourceLocation(ModularPipes.MOD_ID, "blocks/pipe/particle");
		textures = ImmutableList.of(textureParticle);
	}

	@Override
	public boolean accepts(ResourceLocation id)
	{
		return ID.getNamespace().equals(id.getNamespace()) && ID.getPath().equals(id.getPath());
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

	private static class Baked implements IBakedModel
	{
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

		public static Pair<? extends IBakedModel, Matrix4f> handlePerspective(IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType)
		{
			return PerspectiveMapWrapper.handlePerspective(model, TRANSFORM_MAP, cameraTransformType);
		}

		private final TextureAtlasSprite particle;
		private final List<List<BakedQuad>> base, glassBase, error;
		private final List<List<BakedQuad>> connection, glassConnection, module;
		private final List<List<BakedQuad>> overlay;
		private final Int2ObjectOpenHashMap<List<BakedQuad>> cache;
		private final IBakedModel bakedItem;
		private final ItemOverrideList itemOverrideList;

		private final class BakedItem implements IBakedModel
		{
			private final List<BakedQuad> quads;

			public BakedItem()
			{
				quads = new ArrayList<>();
				quads.addAll(base.get(0));
				quads.addAll(glassBase.get(0));
				quads.addAll(overlay.get(0));
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
		}

		public Baked(ModelPipe m, VertexFormat format, TextureAtlasSprite p, ModelCallback c)
		{
			particle = p;
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

			error = new ArrayList<>(4);
			error.add(c.get(m.modelOverlayError, ModelRotation.X0_Y0, false));
			error.add(c.get(m.modelOverlayErrorVertical, ModelRotation.X90_Y90, false));
			error.add(c.get(m.modelOverlayErrorVertical, ModelRotation.X0_Y0, false));
			error.add(c.get(m.modelOverlayErrorVertical, ModelRotation.X90_Y0, false));

			connection = new ArrayList<>(6);
			glassConnection = new ArrayList<>(6);
			module = new ArrayList<>(6);

			for (int i = 0; i < 6; i++)
			{
				connection.add(c.get(m.modelConnection, ClientUtils.FACE_ROTATIONS[i], false));
				glassConnection.add(c.get(m.modelGlassConnection, ClientUtils.FACE_ROTATIONS[i], true));
				module.add(c.get(m.modelModule, ClientUtils.FACE_ROTATIONS[i], false));
			}

			overlay = new ArrayList<>(4);
			overlay.add(c.get(m.modelOverlay, ModelRotation.X0_Y0, false));
			overlay.add(c.get(m.modelOverlayVertical, ModelRotation.X90_Y90, false));
			overlay.add(c.get(m.modelOverlayVertical, ModelRotation.X0_Y0, false));
			overlay.add(c.get(m.modelOverlayVertical, ModelRotation.X90_Y0, false));

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
						model = new BakedItem(pipe instanceof BlockPipeModular ? ((BlockPipeModular) pipe).tier : null, pipe.opaque);
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

			if (pipe instanceof TilePipeModular)
			{
				TilePipeModular modularPipe = (TilePipeModular) pipe;

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
				if (pipe instanceof TilePipeModular)
				{
					TilePipeModular modularPipe = (TilePipeModular) pipe;

					if (modularPipe.hasError())
					{
						return error.get(baseIndex);
					}
					else
					{
						return overlay.get(modularPipe.tier.ordinal() + PipeTier.NAME_MAP.size() * baseIndex);
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
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
		{
			return handlePerspective(this, cameraTransformType);
		}
	}
	*/
}