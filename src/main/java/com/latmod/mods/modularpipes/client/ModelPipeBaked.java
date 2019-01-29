package com.latmod.mods.modularpipes.client;

import com.latmod.mods.modularpipes.block.BlockPipeBase;
import com.latmod.mods.modularpipes.block.BlockPipeModular;
import com.latmod.mods.modularpipes.block.EnumMK;
import com.latmod.mods.modularpipes.block.PipeSkin;
import com.latmod.mods.modularpipes.item.module.PipeModule;
import com.latmod.mods.modularpipes.tile.TilePipeBase;
import com.latmod.mods.modularpipes.tile.TilePipeModularMK1;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ModelPipeBaked implements IBakedModel
{
	public final TextureAtlasSprite particle;
	public final Map<PipeSkin, List<List<BakedQuad>>> base, connection;
	public final List<List<BakedQuad>> glassBase, glassConnection, overlay, module;
	private final Map<PipeSkin, Int2ObjectOpenHashMap<List<BakedQuad>>> cache;
	private final IBakedModel bakedItem;
	private final IBakedModel[] bakedItemWithOverlay;
	private final ItemOverrideList itemOverrideList;

	public ModelPipeBaked(ModelPipe m, TextureAtlasSprite p, ModelPipe.ModelCallback c)
	{
		particle = p;
		base = new HashMap<>(PipeSkin.MAP.size());

		for (PipeSkin skin : PipeSkin.MAP.values())
		{
			AbstractMap.SimpleEntry<String, ResourceLocation> entry = new AbstractMap.SimpleEntry<>("material", skin.texture);
			List<List<BakedQuad>> base1 = new ArrayList<>(4);
			base1.add(c.get(m.modelBase, ModelRotation.X0_Y0, entry));
			base1.add(c.get(m.modelVertical, ModelRotation.X90_Y90, entry));
			base1.add(c.get(m.modelVertical, ModelRotation.X0_Y0, entry));
			base1.add(c.get(m.modelVertical, ModelRotation.X90_Y0, entry));
			base.put(skin, base1);
		}

		glassBase = new ArrayList<>(4);
		glassBase.add(c.get(m.modelGlassBase, ModelRotation.X0_Y0));
		glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X90_Y90));
		glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X0_Y0));
		glassBase.add(c.get(m.modelGlassVertical, ModelRotation.X90_Y0));

		connection = new HashMap<>(PipeSkin.MAP.size());
		glassConnection = new ArrayList<>(6);

		for (PipeSkin skin : PipeSkin.MAP.values())
		{
			AbstractMap.SimpleEntry<String, ResourceLocation> entry = new AbstractMap.SimpleEntry<>("material", skin.texture);
			List<List<BakedQuad>> connection1 = new ArrayList<>(6);

			for (int i = 0; i < 6; i++)
			{
				connection1.add(c.get(m.modelConnection, ModelPipe.FACE_ROTATIONS[i], entry));
			}

			connection.put(skin, connection1);
		}

		for (int i = 0; i < 6; i++)
		{
			glassConnection.add(c.get(m.modelGlassConnection, ModelPipe.FACE_ROTATIONS[i]));
		}

		overlay = new ArrayList<>(EnumMK.VALUES.length);

		for (int i = 0; i < EnumMK.VALUES.length; i++)
		{
			overlay.add(c.get(m.modelOverlay, ModelRotation.X0_Y0, new AbstractMap.SimpleEntry<>("overlay", m.overlayTextures[i])));
		}

		module = new ArrayList<>(6);

		for (int i = 0; i < 6; i++)
		{
			module.add(c.get(m.modelModule, ModelPipe.FACE_ROTATIONS[i], false));
		}

		cache = new HashMap<>();
		bakedItem = new ModelPipeBakedItem(this, null);
		bakedItemWithOverlay = new ModelPipeBakedItem[EnumMK.VALUES.length];

		for (EnumMK mk : EnumMK.VALUES)
		{
			bakedItemWithOverlay[mk.ordinal()] = new ModelPipeBakedItem(this, mk);
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

		if (pipe == null || pipe.invisible)
		{
			return Collections.emptyList();
		}

		BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();

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

		List<BakedQuad> extraQuads = null;

		if (pipe instanceof TilePipeModularMK1)
		{
			int[] modules = new int[6];

			for (PipeModule module : ((TilePipeModularMK1) pipe).modules)
			{
				for (int i = 0; i < 6; i++)
				{
					if (module.isConnected(EnumFacing.VALUES[i]))
					{
						modules[i]++;
					}
				}
			}

			for (int i = 0; i < 6; i++)
			{
				if (modules[i] > 0)
				{
					if (extraQuads == null)
					{
						extraQuads = new ArrayList<>();
					}

					extraQuads.addAll(module.get(i));
				}
			}
		}

		Int2ObjectOpenHashMap<List<BakedQuad>> cacheMap = cache.get(pipe.skin);

		if (cacheMap == null)
		{
			cacheMap = new Int2ObjectOpenHashMap<>();
			cache.put(pipe.skin, cacheMap);
		}

		int cacheIndex = connections | ((layer == BlockRenderLayer.CUTOUT ? 1 : 0) << 6);

		List<BakedQuad> quads = cacheMap.get(cacheIndex);

		if (quads != null)
		{
			if (extraQuads != null)
			{
				ArrayList<BakedQuad> combined = new ArrayList<>(extraQuads.size() + quads.size());
				combined.addAll(quads);
				combined.addAll(extraQuads);
				return combined;
			}

			return quads;
		}

		quads = new ArrayList<>();

		if (layer == BlockRenderLayer.SOLID)
		{
			quads.addAll(base.get(pipe.skin).get(baseIndex));

			if (baseIndex == 0)
			{
				for (int i = 0; i < 6; i++)
				{
					if ((connections & (1 << i)) != 0)
					{
						quads.addAll(connection.get(pipe.skin).get(i));
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
		cacheMap.put(cacheIndex, quads);

		if (extraQuads != null)
		{
			ArrayList<BakedQuad> combined = new ArrayList<>(extraQuads.size() + quads.size());
			combined.addAll(quads);
			combined.addAll(extraQuads);
			return combined;
		}

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
		return ModelPipe.handleModelPerspective(this, cameraTransformType);
	}
}