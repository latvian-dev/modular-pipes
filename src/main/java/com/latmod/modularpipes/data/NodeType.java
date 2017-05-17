package com.latmod.modularpipes.data;

/**
 * @author LatvianModder
 */
public enum NodeType
{
    NONE,
    SIMPLE,
    TILES;

    public static final NodeType[] VALUES = values();

    public boolean isNode()
    {
        return this != NONE;
    }

    public boolean hasTiles()
    {
        return this == TILES;
    }
}