package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.block.BlockPipe;
import com.latmod.modularpipes.util.ModelBuilder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
    public static final ResourceLocation[] PIPE_MK = {null, pipeTex("mk1"), pipeTex("mk2"), pipeTex("mk3"), pipeTex("mk4"), pipeTex("mk5"), pipeTex("mk6"), pipeTex("mk7")};
    public static final Collection<ResourceLocation> TEXTURES;

    static
    {
        Collection<ResourceLocation> c = new ArrayList<>();
        c.add(PIPE_BASE);
        c.add(PIPE_VERTICAL);

        for(ResourceLocation tex : PIPE_MK)
        {
            if(tex != null)
            {
                c.add(tex);
            }
        }

        TEXTURES = Collections.unmodifiableCollection(c);
    }

    public final int tier;

    public PipeModel(String variant)
    {
        Map<String, String> map = new HashMap<>();

        for(String s : variant.split(","))
        {
            String[] s1 = s.split("=");
            map.put(s1[0], s1[1]);
        }

        tier = Integer.parseInt(map.get("tier"));
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
        Function<EnumFacing, TextureAtlasSprite> verticalSprites = face -> face.getAxis().isVertical() ? null : vertical;
        Function<EnumFacing, TextureAtlasSprite> connectionSprites = face -> face.getAxis().isVertical() ? null : base;

        TextureAtlasSprite mark = PIPE_MK[tier] == null ? null : bakedTextureGetter.apply(PIPE_MK[tier]);

        List<List<BakedQuad>> quads = new ArrayList<>(64);
        ModelBuilder builder = new ModelBuilder(ModelRotation.X0_Y0);
        builder.setShading(false);

        float f0 = BlockPipe.SIZE;
        float f1 = 16F - f0;
        float of0 = f0 - 0.03F;
        float of1 = f1 + 0.03F;
        float t = 0.5F;
        float tf0 = f0 + t;
        float tf1 = f1 - t;

        for(int i = 0; i < 64; i++)
        {
            switch(i)
            {
                case BlockPipe.AXIS_X:
                case BlockPipe.AXIS_Y:
                case BlockPipe.AXIS_Z:
                    if(mark == null)
                    {
                        if(i == BlockPipe.AXIS_X)
                        {
                            builder.setRotation(ModelRotation.X90_Y90);
                        }
                        else if(i == BlockPipe.AXIS_Z)
                        {
                            builder.setRotation(ModelRotation.X90_Y0);
                        }

                        builder.addCube(f0, 0F, f0, tf0, 16F, tf0, verticalSprites);
                        builder.addCube(tf1, 0F, f0, f1, 16F, tf0, verticalSprites);
                        builder.addCube(f0, 0F, tf1, tf0, 16F, f1, verticalSprites);
                        builder.addCube(tf1, 0F, tf1, f1, 16F, f1, verticalSprites);
                        builder.setRotation(ModelRotation.X0_Y0);
                        break;
                    }
                default:
                    for(EnumFacing facing : EnumFacing.VALUES)
                    {
                        if(((i >> facing.ordinal()) & 1) != 0)
                        {
                            builder.setRotation(facing);
                            builder.addCube(f0, 0F, f0, tf0, f0, tf0, connectionSprites);
                            builder.addCube(tf1, 0F, f0, f1, f0, tf0, connectionSprites);
                            builder.addCube(f0, 0F, tf1, tf0, f0, f1, connectionSprites);
                            builder.addCube(tf1, 0F, tf1, f1, f0, f1, connectionSprites);
                            builder.setRotation(ModelRotation.X0_Y0);
                        }
                        else if(mark != null)
                        {
                            builder.addQuad(of0, of0, of0, of1, of1, of1, facing, mark);
                        }
                    }

                    builder.addCube(f0, f0, f0, tf0, f1, tf0, base);
                    builder.addCube(tf1, f0, f0, f1, f1, tf0, base);
                    builder.addCube(f0, f0, tf1, tf0, f1, f1, base);
                    builder.addCube(tf1, f0, tf1, f1, f1, f1, base);

                    builder.addCube(f0, f0, tf0, tf0, tf0, tf1, base);
                    builder.addCube(tf1, f0, tf0, f1, tf0, tf1, base);
                    builder.addCube(tf0, f0, f0, tf1, tf0, tf0, base);
                    builder.addCube(tf0, f0, tf1, tf1, tf0, f1, base);

                    builder.addCube(f0, tf1, tf0, tf0, f1, tf1, base);
                    builder.addCube(tf1, tf1, tf0, f1, f1, tf1, base);
                    builder.addCube(tf0, tf1, f0, tf1, f1, tf0, base);
                    builder.addCube(tf0, tf1, tf1, tf1, f1, f1, base);

                    //builder.addQuad(tf0, tf0, tf0, tf1, tf1, tf1, EnumFacing.NORTH, base);
                    //builder.addQuad(tf0, tf0, tf0, tf1, tf1, tf1, EnumFacing.SOUTH, base);
                    //builder.addQuad(tf0, tf0, tf0, tf1, tf1, tf1, EnumFacing.WEST, base);
                    //builder.addQuad(tf0, tf0, tf0, tf1, tf1, tf1, EnumFacing.EAST, base);
            }

            builder.clear();
            builder.addCube(0F, 0F, 0F, 16, 16, 16, base);
            quads.add(builder.getQuads());
            builder.clear();
        }

        builder.addCube(f0, 0F, f0, f1, 16F, f1, base);

        if(mark != null)
        {
            builder.addCube(of0, of0, of0, of1, of1, of1, face -> face.getAxis().isVertical() ? null : mark);
        }

        return new PipeBakedModel(base, quads, builder.getQuads());
    }

    @Override
    public IModelState getDefaultState()
    {
        return TRSRTransformation.identity();
    }
}