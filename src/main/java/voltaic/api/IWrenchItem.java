package voltaic.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IWrenchItem {
	boolean shouldRotate(ItemStack stack, BlockPos pos, PlayerEntity player);

	boolean shouldPickup(ItemStack stack, BlockPos pos, PlayerEntity player);
}
