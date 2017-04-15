package com.latmod.modularpipes.block;

import net.minecraft.util.IStringSerializable;

/**
 * @author LatvianModder
 */
public enum EnumPipeTier implements IStringSerializable
{
    MK0("mk0"),
    MK1("mk1"),
    MK2("mk2"),
    MK3("mk3"),
    MK4("mk4"),
    MK5("mk5"),
    MK6("mk6"),
    MK7("mk7");

    public static final EnumPipeTier[] VALUES = values();
    private final String name;

    EnumPipeTier(String n)
    {
        name = n;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public static EnumPipeTier getFromMeta(int meta)
    {
        return VALUES[meta & 7];
    }

    public boolean isBasic()
    {
        return this == MK0;
    }
}