package voltaic.common.item.gear;

import voltaic.api.IWrenchItem;
import voltaic.common.item.ItemVoltaic;
import voltaic.prefab.tile.IWrenchable;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWrench extends ItemVoltaic implements IWrenchItem {

	public ItemWrench(Properties properties, Supplier<ItemGroup> creativeTab) {
		super(properties, creativeTab);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {

		PlayerEntity player = context.getPlayer();

		if (player == null) {
			return ActionResultType.FAIL;
		}

		BlockPos pos = context.getClickedPos();
		BlockState state = context.getLevel().getBlockState(pos);
		Block block = state.getBlock();

		ItemStack stack = player.getItemInHand(context.getHand());

		if (block instanceof IWrenchable) {
			
			IWrenchable wrenchable = (IWrenchable) block;

			if (player.isShiftKeyDown()) {

				if (shouldPickup(stack, pos, player)) {

					wrenchable.onPickup(stack, pos, player);

					return ActionResultType.CONSUME;

				}

			} else if (shouldRotate(stack, pos, player)) {

				wrenchable.onRotate(stack, pos, player);

				return ActionResultType.CONSUME;

			}

		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> use(World pLevel, PlayerEntity pPlayer, Hand pUsedHand) {
		return ActionResult.fail(pPlayer.getItemInHand(pUsedHand));
	}

	@Override
	public boolean shouldRotate(ItemStack stack, BlockPos pos, PlayerEntity player) {
		return true;
	}

	@Override
	public boolean shouldPickup(ItemStack stack, BlockPos pos, PlayerEntity player) {
		return true;
	}
}
