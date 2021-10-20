package dev.latvian.mods.modularpipes.item;

import dev.latvian.mods.modularpipes.ModularPipes;
import dev.latvian.mods.modularpipes.util.PipeNetwork;
import dev.latvian.mods.modularpipes.util.ServerPipeNetwork;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

/**
 * @author LatvianModder
 */
public class VisualizerItem extends Item {
	public VisualizerItem() {
		super(new Properties().tab(ModularPipes.TAB).stacksTo(1));
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		PipeNetwork network = PipeNetwork.get(ctx.getLevel());

		if (!(network instanceof ServerPipeNetwork)) {
			return InteractionResult.SUCCESS;
		}

		/*
		System.out.println("===");

		BlockPos p = ctx.getClickedPos().relative(ctx.getClickedFace());
		PathSegment segment = new PathSegment(p.getX(), p.getY(), p.getZ(), 0, 1, 8);

		PipeItem item = ((ServerPipeNetwork) network).insert(ctx.getPlayer().getOffhandItem().isEmpty() ? new ItemStack(Items.APPLE) : ItemHandlerHelper.copyStackWithSize(ctx.getPlayer().getOffhandItem(), 1));
		item.nextPath = new ArrayList<>();
		item.nextPath.add(segment);
		System.out.println(segment);

		for (int i = 0; i < 10; i++) {
			segment = segment.chain(ctx.getLevel().random.nextInt(6));
			item.nextPath.add(segment);
			System.out.println(segment);
		}
		 */

		return InteractionResult.SUCCESS;
	}
}