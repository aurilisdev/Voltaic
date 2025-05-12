package voltaic.common.item;

import voltaic.prefab.utilities.ItemUtils;
import voltaic.registers.VoltaicEffects;

import java.util.function.Supplier;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemIodineTablet extends ItemVoltaic {

    public static final int TIME_MINUTES = 5;
    private static final int TIME = TIME_MINUTES * 60 * 20;
    public ItemIodineTablet(Properties properties, Supplier<ItemGroup> creativeTab) {
        super(properties, creativeTab);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World level, LivingEntity livingEntity) {
        if(!level.isClientSide()) {
            livingEntity.addEffect(new EffectInstance(VoltaicEffects.RADIATION_RESISTANCE, TIME, 0, false, false, true));
        }
        if (livingEntity instanceof ServerPlayerEntity) {
        	ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) livingEntity;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
            serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
        }
        if (livingEntity instanceof PlayerEntity && !((PlayerEntity) livingEntity).abilities.instabuild) {
            stack.shrink(1);
        }
        return stack;
    }
    
    @Override
    public int getUseDuration(ItemStack pStack) {
    	return 32;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ItemUtils.startUsingInstantly(worldIn, playerIn, handIn);
    }
}
