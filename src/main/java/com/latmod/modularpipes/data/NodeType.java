package com.latmod.modularpipes.data;

/**
 * @author LatvianModder
 */
public enum NodeType
{
	NONE,
	SIMPLE,
	MODULAR;

	public static final NodeType[] VALUES = values();

	public boolean isNode()
	{
		return this != NONE;
	}

	public boolean isModular()
	{
		return this == MODULAR;
	}
}