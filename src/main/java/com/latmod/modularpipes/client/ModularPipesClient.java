package com.latmod.modularpipes.client;

import com.latmod.modularpipes.ModularPipes;
import com.latmod.modularpipes.ModularPipesCommon;
import com.latmod.modularpipes.block.EnumPipeTier;
import com.latmod.modularpipes.item.ModularPipesItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Arrays;

/**
 * @author LatvianModder
 */
public class ModularPipesClient extends ModularPipesCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        Item item = Item.getItemFromBlock(ModularPipesItems.PIPE);

        //TODO: Please, replace me with custom baked model later. PLEASE
        for(int meta = 0; meta < 16; meta++)
        {
            String[] props = {
                    "opaque=" + (meta > 7),
                    "tier=" + EnumPipeTier.getFromMeta(meta).getName(),
                    "axis=none",
                    "con_down=false",
                    "con_up=false",
                    "con_north=false",
                    "con_south=false",
                    "con_west=false",
                    "con_east=false"
            };

            Arrays.sort(props);
            String variant = String.join(",", props);
            ModularPipes.LOGGER.info("Variant: " + variant);
            ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(ModularPipesItems.PIPE.getRegistryName(), variant));
        }

        ModelLoader.setCustomModelResourceLocation(ModularPipesItems.MODULE, 0, new ModelResourceLocation(ModularPipesItems.MODULE.getRegistryName(), "inventory"));
    }
}