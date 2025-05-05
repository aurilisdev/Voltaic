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
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemAntidote extends ItemVoltaic {

    public ItemAntidote(Properties properties, Supplier<ItemGroup> creativeTab) {
        super(properties, creativeTab);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (!worldIn.isClientSide) {
        	entityLiving.removeEffect(VoltaicEffects.RADIATION);
        }
        if (entityLiving instanceof ServerPlayerEntity) {
        	ServerPlayerEntity server = (ServerPlayerEntity) entityLiving;
            CriteriaTriggers.CONSUME_ITEM.trigger(server, stack);
            server.awardStat(Stats.ITEM_USED.get(this));
        }
        if (entityLiving instanceof PlayerEntity && !((PlayerEntity) entityLiving).abilities.instabuild) {
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
        return UseAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return ItemUtils.startUsingInstantly(worldIn, playerIn, handIn);
    }

}
