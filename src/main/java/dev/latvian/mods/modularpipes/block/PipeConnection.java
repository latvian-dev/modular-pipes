package dev.latvian.mods.modularpipes.block;

import net.minecraft.util.StringRepresentable;

/**
 * @author LatvianModder
 */
public enum PipeConnection implements StringRepresentable {
	NONE("none"),
	PIPE("pipe"),
	PIPE_MODULE("pipe_module");

	private final String name;

	PipeConnection(String n) {
		name = n;
	}

	public boolean hasModule() {
		return this == PIPE_MODULE;
	}

	public boolean hasPipe() {
		return this != NONE;
	}

	@Override
	public String getSerializedName() {
		return name;
	}
}