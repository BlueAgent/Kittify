package kittify.common.module;

import kittify.Kittify;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Kittify.MOD_ID)
public class FoodAndHunger {
    public static boolean doSpecialRegen(EntityPlayer player, float maxExhaustion) {
        if (!player.shouldHeal()) return false;
        final FoodStats foodStats = player.getFoodStats();
        // Regen using exhaustion
        if (foodStats.foodExhaustionLevel > maxExhaustion || foodStats.foodLevel <= 14) return false;
        final float EXHAUSTION_PER_PHEALTH = 6f;
        final float MINUTES_TILL_FULL = 5f;
        final float PHEALTH_PER_MINUTE = 1.0f / MINUTES_TILL_FULL;
        final float PHEALTH_PER_SECOND = PHEALTH_PER_MINUTE / 60f;
        final float PHEALTH_PER_TICK = PHEALTH_PER_SECOND / 20f;
        final float maxHealth = player.getMaxHealth();
        float healthDeficit = maxHealth - player.getHealth();
        float healthToHeal = Math.min(maxHealth * PHEALTH_PER_TICK, healthDeficit);
        player.heal(healthToHeal);
        foodStats.addExhaustion(EXHAUSTION_PER_PHEALTH * healthToHeal);
        return true;
    }
}
