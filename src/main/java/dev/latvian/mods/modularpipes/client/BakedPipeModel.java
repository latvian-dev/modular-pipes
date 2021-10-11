package dev.latvian.mods.modularpipes.client;

import dev.latvian.mods.modularpipes.ModularPipesUtils;
import dev.latvian.mods.modularpipes.block.BasePipeBlock;
import dev.latvian.mods.modularpipes.block.entity.BasePipeBlockEntity;
import dev.latvian.mods.modularpipes.block.entity.ModularPipeMK1BlockEntity;
import dev.latvian.mods.modularpipes.item.module.PipeModule;
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
public class BakedPipeModel implements BakedModel {
	public final PipeModelGeometry pipeModelGeometry;
	public final TextureAtlasSprite particle;
	public final PipeModelGeometry.ModelCallback modelCallback;
	public final Int2ObjectOpenHashMap<List<List<BakedQuad>>> base, connection;
	public final List<List<BakedQuad>> glassBase, glassConnection, module;
	private final Int2ObjectOpenHashMap<Int2ObjectOpenHashMap<List<BakedQuad>>> cache;
	private final BakedModel bakedItem;
	private final ItemOverrides itemOverrideList;

	public BakedPipeModel(PipeModelGeometry m, TextureAtlasSprite p, PipeModelGeometry.ModelCallback c) {
		pipeModelGeometry = m;
		particle = p;
		modelCallback = c;
		base = new Int2ObjectOpenHashMap<>();
		glassBase = new ArrayList<>(4);
		glassBase.add(c.get(m.modelGlassBase, BlockModelRotation.X0_Y0));
		glassBase.add(c.get(m.modelGlassVertical, BlockModelRotation.X90_Y90));
		glassBase.add(c.get(m.modelGlassVertical, BlockModelRotation.X0_Y0));
		glassBase.add(c.get(m.modelGlassVertical, BlockModelRotation.X90_Y0));

		connection = new Int2ObjectOpenHashMap<>();
		glassConnection = new ArrayList<>(6);

		for (int i = 0; i < 6; i++) {
			glassConnection.add(c.get(m.modelGlassConnection, PipeModelGeometry.FACE_ROTATIONS[i]));
		}

		module = new ArrayList<>(6);

		for (int i = 0; i < 6; i++) {
			module.add(c.get(m.modelModule, PipeModelGeometry.FACE_ROTATIONS[i], false));
		}

		cache = new Int2ObjectOpenHashMap<>();

		List<BakedQuad> itemQuads = new ArrayList<>();
		itemQuads.addAll(modelCallback.get(pipeModelGeometry.modelBase, BlockModelRotation.X0_Y0));
		itemQuads.addAll(glassBase.get(0));
		bakedItem = new BakedPipeItemModel(this, Collections.unmodifiableList(ModularPipesUtils.optimize(itemQuads)));

		itemOverrideList = new ItemOverrides() {
			@Override
			public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity) {
				return bakedItem;
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
		RenderType layer = MinecraftForgeClient.getRenderLayer();

		if (state == null || side != null) {
			return Collections.emptyList();
		}

		BasePipeBlockEntity pipe = null;

		if (extraData.hasProperty(BasePipeBlock.PIPE)) {
			pipe = extraData.getData(BasePipeBlock.PIPE);
		}

		if (pipe == null || pipe.invisible) {
			return Collections.emptyList();
		}

		if (layer == RenderType.translucent()) {
			return Collections.emptyList();
		}

		int connections = 0;

		for (Direction facing : Direction.values()) {
			if (pipe.isConnected(facing)) {
				connections |= 1 << facing.get3DDataValue();
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

		if (pipe instanceof ModularPipeMK1BlockEntity) {
			int[] modules = new int[6];

			for (PipeModule module : ((ModularPipeMK1BlockEntity) pipe).modules) {
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

		int paint = 0; // TODO: Remove

		Int2ObjectOpenHashMap<List<BakedQuad>> cacheMap = cache.get(paint);

		if (cacheMap == null) {
			cacheMap = new Int2ObjectOpenHashMap<>();
			cache.put(paint, cacheMap);
			List<List<BakedQuad>> base1 = new ArrayList<>(4);
			base1.add(modelCallback.get(pipeModelGeometry.modelBase, BlockModelRotation.X0_Y0));
			base1.add(modelCallback.get(pipeModelGeometry.modelVertical, BlockModelRotation.X90_Y90));
			base1.add(modelCallback.get(pipeModelGeometry.modelVertical, BlockModelRotation.X0_Y0));
			base1.add(modelCallback.get(pipeModelGeometry.modelVertical, BlockModelRotation.X90_Y0));
			base.put(paint, base1);

			List<List<BakedQuad>> connection1 = new ArrayList<>(6);

			for (int i = 0; i < 6; i++) {
				connection1.add(modelCallback.get(pipeModelGeometry.modelConnection, PipeModelGeometry.FACE_ROTATIONS[i]));
			}

			connection.put(paint, connection1);
		}

		int cacheIndex = connections | ((layer == RenderType.cutout() ? 1 : 0) << 6);

		List<BakedQuad> quads = cacheMap.get(cacheIndex);

		if (quads != null) {
			if (extraQuads != null) {
				return ModularPipesUtils.combineAndOptimize(quads, extraQuads);
			}

			return quads;
		}

		quads = new ArrayList<>();

		if (layer == RenderType.solid()) {
			quads.addAll(base.get(paint).get(baseIndex));

			if (baseIndex == 0) {
				for (int i = 0; i < 6; i++) {
					if ((connections & (1 << i)) != 0) {
						quads.addAll(connection.get(paint).get(i));
					}
				}
			}
		}

		if (layer == RenderType.cutout()) {
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