package com.latmod.mods.modularpipes.client;

import com.latmod.mods.modularpipes.ModularPipesCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ModularPipesClient extends ModularPipesCommon
{
	private ParticleExplosion.Factory explosionFactory = new ParticleExplosion.Factory();
	private ParticleRedstone.Factory redstoneFactory = new ParticleRedstone.Factory();

	@Override
	public void spawnParticle(BlockPos pos, @Nullable EnumFacing facing, int type)
	{
		Minecraft mc = Minecraft.getMinecraft();
		double x = pos.getX() + 0.5D + (facing == null ? 0D : facing.getXOffset() * 0.3D);
		double y = pos.getY() + 0.5D + (facing == null ? 0D : facing.getYOffset() * 0.3D);
		double z = pos.getZ() + 0.5D + (facing == null ? 0D : facing.getZOffset() * 0.3D);

		if (type == EXPLOSION)
		{
			mc.effectRenderer.addEffect(explosionFactory.createParticle(0, mc.world, x, y, z, 0D, 0D, 0D));
		}
		else if (type == SPARK)
		{
			mc.effectRenderer.addEffect(redstoneFactory.createParticle(0, mc.world, x, y, z, 1D, 0.8D, 0D));
		}
	}

	@Override
	public int getPipeLightValue(IBlockAccess world)
	{
		if (world instanceof World)
		{
			if (!((World) world).isRemote)
			{
				return 0;
			}
		}

		return MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT ? 15 : 0;
	}
}