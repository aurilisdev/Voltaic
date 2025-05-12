package voltaic.common.item;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.utilities.VoltaicTextUtils;
import voltaic.prefab.utilities.WorldUtils;
import voltaic.prefab.utilities.NBTUtils;

public class ItemUpgrade extends ItemVoltaic {
	public final SubtypeItemUpgrade subtype;

	private static final DecimalFormat FORMATTER = new DecimalFormat("0.00");

	public ItemUpgrade(Properties properties, SubtypeItemUpgrade subtype, Supplier<ItemGroup> creativeTab) {
		super(properties.stacksTo(subtype.maxSize), creativeTab);
		this.subtype = subtype;
	}

	@Override
	public void appendHoverText(ItemStack stack, World context, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, context, tooltip, flagIn);
		if (subtype == SubtypeItemUpgrade.advancedcapacity || subtype == SubtypeItemUpgrade.basiccapacity) {
			double capacityMultiplier = subtype == SubtypeItemUpgrade.advancedcapacity ? 2.25 : 1.5;
			double voltageMultiplier = subtype == SubtypeItemUpgrade.advancedcapacity ? 4 : 2;
			tooltip.add(VoltaicTextUtils.tooltip("info.upgradecapacity", new StringTextComponent(capacityMultiplier + "x").withStyle(TextFormatting.GREEN)).withStyle(TextFormatting.GRAY));
			tooltip.add(VoltaicTextUtils.tooltip("info.upgradeenergytransfer", new StringTextComponent(capacityMultiplier + "x").withStyle(TextFormatting.GREEN)).withStyle(TextFormatting.GRAY));
			tooltip.add(VoltaicTextUtils.tooltip("info.upgradevoltage", new StringTextComponent(voltageMultiplier + "x").withStyle(TextFormatting.GREEN)).withStyle(TextFormatting.GRAY));
		}
		if (subtype == SubtypeItemUpgrade.advancedspeed || subtype == SubtypeItemUpgrade.basicspeed) {
			double speedMultiplier = subtype == SubtypeItemUpgrade.advancedspeed ? 2.25 : 1.5;
			tooltip.add(VoltaicTextUtils.tooltip("info.upgradespeed", new StringTextComponent(speedMultiplier + "x").withStyle(TextFormatting.GREEN)).withStyle(TextFormatting.GRAY));
			tooltip.add(VoltaicTextUtils.tooltip("info.upgradeenergyusage", new StringTextComponent(speedMultiplier + "x").withStyle(TextFormatting.RED)).withStyle(TextFormatting.GRAY));
		}
		if (subtype == SubtypeItemUpgrade.itemoutput || subtype == SubtypeItemUpgrade.iteminput) {
			if (subtype == SubtypeItemUpgrade.itemoutput) {
				tooltip.add(VoltaicTextUtils.tooltip("info.itemoutputupgrade").withStyle(TextFormatting.GRAY));
			} else {
				tooltip.add(VoltaicTextUtils.tooltip("info.iteminputupgrade").withStyle(TextFormatting.GRAY));
			}
			if (stack.getOrCreateTag().getBoolean(NBTUtils.SMART)) {
				tooltip.add(VoltaicTextUtils.tooltip("info.insmartmode").withStyle(TextFormatting.LIGHT_PURPLE));
			}
			List<Direction> dirs = NBTUtils.readDirectionList(stack);
			if (!dirs.isEmpty()) {
				tooltip.add(VoltaicTextUtils.tooltip("info.dirlist").withStyle(TextFormatting.BLUE));
				for (int i = 0; i < dirs.size(); i++) {
					Direction dir = dirs.get(i);
					tooltip.add(new StringTextComponent(i + 1 + ". " + StringUtils.capitalize(dir.getName())).withStyle(TextFormatting.BLUE));
				}
				tooltip.add(VoltaicTextUtils.tooltip("info.cleardirs").withStyle(TextFormatting.GRAY));
			} else {
				tooltip.add(VoltaicTextUtils.tooltip("info.nodirs").withStyle(TextFormatting.GRAY));
			}
			tooltip.add(VoltaicTextUtils.tooltip("info.togglesmart").withStyle(TextFormatting.GRAY));
		}
		if (subtype == SubtypeItemUpgrade.experience) {
			double storedXp = stack.getOrCreateTag().getDouble(NBTUtils.XP);
			tooltip.add(VoltaicTextUtils.tooltip("info.xpstored", new StringTextComponent(FORMATTER.format(storedXp)).withStyle(TextFormatting.LIGHT_PURPLE)).withStyle(TextFormatting.GRAY));
			tooltip.add(VoltaicTextUtils.tooltip("info.xpusage").withStyle(TextFormatting.GRAY));

		}
		if (subtype == SubtypeItemUpgrade.range) {
			tooltip.add(VoltaicTextUtils.tooltip("info.range").withStyle(TextFormatting.GRAY));
		}
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (!world.isClientSide) {
			ItemStack handStack = player.getItemInHand(hand);
			SubtypeItemUpgrade localSubtype = ((ItemUpgrade) handStack.getItem()).subtype;
			if (localSubtype == SubtypeItemUpgrade.iteminput || localSubtype == SubtypeItemUpgrade.itemoutput) {
				if (player.isShiftKeyDown()) {
					Vector3d look = player.getLookAngle();
					Direction lookingDir = Direction.getNearest(look.x, look.y, look.z);
					List<Direction> dirs = NBTUtils.readDirectionList(handStack);
					dirs.add(lookingDir);
					NBTUtils.clearDirectionList(handStack);
					NBTUtils.writeDirectionList(dirs, handStack);
				} else {
					CompoundNBT tag = handStack.getOrCreateTag();
					tag.putBoolean(NBTUtils.SMART, !tag.getBoolean(NBTUtils.SMART));
				}
				return ActionResult.pass(player.getItemInHand(hand));
			}
			if (localSubtype == SubtypeItemUpgrade.experience) {
				CompoundNBT tag = handStack.getOrCreateTag();
				double storedXp = tag.getDouble(NBTUtils.XP);
				int takenXp = (int) storedXp;
				// it uses a Vector3d for some reason don't ask me why
				Vector3d playerPos = new Vector3d(player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ());
				WorldUtils.award((ServerWorld) world, playerPos, takenXp);
				tag.putDouble(NBTUtils.XP, storedXp - takenXp);
				return ActionResult.pass(player.getItemInHand(hand));
			}
		}
		return super.use(world, player, hand);
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		if (!entity.level.isClientSide && entity.isShiftKeyDown()) {
			if (!entity.getItemInHand(Hand.MAIN_HAND).isEmpty() || !entity.getItemInHand(Hand.OFF_HAND).isEmpty()) {
				SubtypeItemUpgrade subtype = ((ItemUpgrade) stack.getItem()).subtype;
				if (subtype == SubtypeItemUpgrade.iteminput || subtype == SubtypeItemUpgrade.itemoutput) {
					NBTUtils.clearDirectionList(stack);
				}
			}
		}
		return super.onEntitySwing(stack, entity);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		SubtypeItemUpgrade subtype = ((ItemUpgrade) stack.getItem()).subtype;
		if (stack.hasTag()) {
			return stack.getTag().getBoolean(NBTUtils.SMART);
		}
		return subtype == SubtypeItemUpgrade.fortune || subtype == SubtypeItemUpgrade.unbreaking || subtype == SubtypeItemUpgrade.silktouch;
	}

}
