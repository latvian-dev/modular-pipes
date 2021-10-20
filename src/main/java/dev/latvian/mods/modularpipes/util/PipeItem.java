package dev.latvian.mods.modularpipes.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	public List<PathSegment> nextPath;

	public boolean update = false;
	private float scale = -1F;

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
		path = new PathSegment(buf);
		int s = buf.readVarInt();
		nextPath = s == 0 ? Collections.emptyList() : new ArrayList<>(s);

		for (int i = 0; i < s; i++) {
			nextPath.add(new PathSegment(buf));
		}
	}

	public PipeItem(CompoundTag tag) {
		id = tag.getLong("ID");
		stack = ItemStack.of(tag.getCompound("Item"));
		pos = tag.getInt("Pos");
		ttl = tag.getInt("TTL");
		path = new PathSegment(tag.getIntArray("Path"));
		ListTag stag = tag.getList("NextPath", Constants.NBT.TAG_COMPOUND);
		nextPath = stag.size() == 0 ? Collections.emptyList() : new ArrayList<>(stag.size());

		for (int i = 0; i < stag.size(); i++) {
			nextPath.add(new PathSegment(stag.getIntArray(i)));
		}
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeVarLong(id);
		buf.writeItem(stack);
		buf.writeVarInt(pos);
		buf.writeVarInt(ttl);
		path.write(buf);
		buf.writeVarInt(nextPath.size());

		for (PathSegment segment : nextPath) {
			segment.write(buf);
		}
	}

	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();
		tag.putLong("ID", id);
		tag.put("Item", stack.serializeNBT());
		tag.putInt("Pos", pos);
		tag.putInt("TTL", ttl);
		tag.putIntArray("Path", path.toIntArray());

		if (!nextPath.isEmpty()) {
			ListTag stag = new ListTag();

			for (PathSegment segment : nextPath) {
				stag.add(new IntArrayTag(segment.toIntArray()));
			}

			tag.put("NextPath", stag);
		}

		return tag;
	}

	public void tick() {
		// TODO: check if chunk at current pos is loaded

		ttl--;

		if (path == null) {
			shiftPath();
		}

		if (ttl <= 0 || path == null) {
			ttl = 0;
			return;
		}

		pos++;

		if (pos >= path.steps * 4) {
			shiftPath();
			pos = 0;
		}

		if (ttl <= 0 || path == null) {
			ttl = 0;
		}
	}

	private void shiftPath() {
		path = null;

		if (!nextPath.isEmpty()) {
			path = nextPath.get(0);

			if (nextPath.size() == 1) {
				nextPath = Collections.emptyList();
			} else {
				nextPath.remove(0);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void render(PoseStack poseStack, ItemRenderer renderItem, MultiBufferSource multiBufferSource, int light, int overlay) {
		renderItem.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlay, poseStack, multiBufferSource);
	}

	@OnlyIn(Dist.CLIENT)
	public float getScale(Minecraft mc, ItemRenderer itemRenderer) {
		if (scale <= 0F) {
			scale = 0.4F;

			// if (renderItem.shouldRenderItemIn3D(stack)) {
			// 	scale = 0.72F;
			// }

			scale += mc.level.random.nextFloat() * 0.01F;
		}

		return scale;
	}
}