package dev.latvian.mods.modularpipes.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author LatvianModder
 */
public class PipeItem {
	public PipeNetwork network;
	public final long id;
	public ItemStack stack;
	public int pos;
	public int ttl;
	public PathSegment path;

	public boolean update = false;
	private double scale = -1D;

	public PipeItem(PipeNetwork n, long i, ItemStack is) {
		network = n;
		id = i;
		stack = is;
		pos = 0;
		ttl = is.getEntityLifespan(n.level);
		update = true;
	}

	public PipeItem(FriendlyByteBuf buf) {
		id = buf.readVarLong();
		stack = buf.readItem();
		pos = buf.readVarInt();
		ttl = buf.readVarInt();
		path = new PathSegment(buf.readVarIntArray());
	}

	public PipeItem(CompoundTag tag) {
		id = tag.getLong("ID");
		stack = ItemStack.of(tag.getCompound("Item"));
		pos = tag.getInt("Pos");
		ttl = tag.getInt("TTL");
		path = new PathSegment(tag.getIntArray("Path"));
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarLong(id);
		buf.writeItem(stack);
		buf.writeVarInt(pos);
		buf.writeVarInt(ttl);
		buf.writeVarIntArray(path.toIntArray());
	}

	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putLong("ID", id);
		tag.put("Item", stack.serializeNBT());
		tag.putInt("Pos", pos);
		tag.putInt("TTL", ttl);
		tag.putIntArray("Path", path.toIntArray());
		return tag;
	}

	public void tick() {
		// TODO: check if chunk at current pos is loaded
		ttl--;
		pos++;

		if (pos >= path.steps * 4) {
			path = path.next;
			pos = 0;
		}

		if (ttl <= 0 || path == null) {
			ttl = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public double getScale(Minecraft mc, ItemRenderer itemRenderer) {
		if (scale <= 0D) {
			scale = 0.6D;

			// if (renderItem.shouldRenderItemIn3D(stack)) {
			// 	scale = 0.72F;
			// }

			scale += mc.level.random.nextFloat() * 0.01D;
		}

		return scale;
	}
}