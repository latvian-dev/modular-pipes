package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.block.BlockPipe;
import com.latmod.modularpipes.block.EnumPipeTier;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class PipeModel implements IModel
{
    private static String PIPE_TEX_PATH = ModularPipes.MOD_ID + ":blocks/pipes/";

    private static ResourceLocation pipeTex(String s)
    {
        return new ResourceLocation(PIPE_TEX_PATH + s);
    }

    public static final ResourceLocation PIPE_BASE = pipeTex("base");
    public static final ResourceLocation PIPE_VERTICAL = pipeTex("vertical");
    public static final ResourceLocation[] PIPE_MK = {pipeTex("mk1"), pipeTex("mk2"), pipeTex("mk3"), pipeTex("mk4"), pipeTex("mk5"), pipeTex("mk6"), pipeTex("mk7")};
    public static final Collection<ResourceLocation> TEXTURES;

    static
    {
        Collection<ResourceLocation> c = new ArrayList<>();
        c.add(PIPE_BASE);
        c.add(PIPE_VERTICAL);

        for(ResourceLocation tex : PIPE_MK)
        {
            c.add(tex);
        }

        TEXTURES = Collections.unmodifiableCollection(c);
    }

    public final EnumPipeTier tier;
    public final ResourceLocation markTex;

    public PipeModel(String variant)
    {
        tier = BlockPipe.TIER.parseValue(variant.split("=")[1]).get();
        markTex = tier.ordinal() > 0 ? PIPE_MK[tier.ordinal() - 1] : null;
    }

    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return TEXTURES;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, com.google.common.base.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
    {
        TextureAtlasSprite base = bakedTextureGetter.apply(PIPE_BASE);
        TextureAtlasSprite vertical = bakedTextureGetter.apply(PIPE_VERTICAL);
        TextureAtlasSprite mark = markTex == null ? null : bakedTextureGetter.apply(markTex);

        List<List<BakedQuad>> quads = new ArrayList<>(64);
        ModelBuilder builder = new ModelBuilder(ModelRotation.X0_Y0);

        for(int i = 0; i < 64; i++)
        {
            switch(i)
            {
                case 0:
                    builder.addCube(4F, 4F, 4F, 12F, 12F, 12F, facing -> base);

                    if(mark != null)
                    {
                        builder.addCube(3.97F, 3.97F, 3.97F, 12.03F, 12.03F, 12.03F, facing -> mark);
                    }
                    break;
                case BlockPipe.AXIS_X:
                case BlockPipe.AXIS_Y:
                case BlockPipe.AXIS_Z:
                    if(markTex == null)
                    {
                        if(i == BlockPipe.AXIS_X)
                        {
                            builder.setRotation(ModelRotation.X90_Y90);
                        }
                        else if(i == BlockPipe.AXIS_Z)
                        {
                            builder.setRotation(ModelRotation.X90_Y0);
                        }

                        builder.addCube(4F, 0F, 4F, 12F, 16F, 12F, face -> face.getAxis().isVertical() ? null : vertical);
                        builder.setRotation(ModelRotation.X0_Y0);
                        break;
                    }
                default:
                    for(EnumFacing facing : EnumFacing.VALUES)
                    {
                        if(((i >> facing.ordinal()) & 1) != 0)
                        {
                            builder.setRotation(facing);
                            builder.addCube(4F, 0F, 4F, 12F, 4F, 12F, face -> face.getAxis().isVertical() ? null : base);
                            builder.setRotation(ModelRotation.X0_Y0);
                        }
                        else
                        {
                            builder.addQuad(4F, 4F, 4F, 12F, 12F, 12F, facing, base);

                            if(mark != null)
                            {
                                builder.addQuad(3.97F, 3.97F, 3.97F, 12.03F, 12.03F, 12.03F, facing, mark);
                            }
                        }
                    }
            }

            quads.add(builder.getQuads());
            builder.clear();
        }

        builder.addCube(4F, 0F, 4F, 12F, 16F, 12F, face -> base);

        if(mark != null)
        {
            builder.addCube(3.97F, 3.97F, 3.97F, 12.03F, 12.03F, 12.03F, face -> face.getAxis().isVertical() ? null : mark);
        }

        return new PipeBakedModel(base, quads, builder.getQuads());
    }

    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }
}