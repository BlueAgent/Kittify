package kittify.common.module;

import kittify.Kittify;
import kittify.common.util.NBTUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Kittify.MOD_ID)
public class FoodAndHunger {
    public static final String REGEN_COOLDOWN = "regenCooldown";

    @SubscribeEvent
    public static void recordLastHurt(LivingHurtEvent event) {
        final EntityLivingBase living = event.getEntityLiving();
        if (living instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) living;
            final int COOLDOWN_TICKS = 20 * 60;
            NBTUtil.getModEphemeralTag(player).setInteger(REGEN_COOLDOWN, COOLDOWN_TICKS);
        }
    }

    public static boolean doSpecialRegen(EntityPlayer player, float maxExhaustion) {
        final NBTTagCompound ephemeralTag = NBTUtil.getModEphemeralTag(player);
        final int cooldown = Math.max(0, ephemeralTag.getInteger(REGEN_COOLDOWN) - 1);
        ephemeralTag.setInteger(REGEN_COOLDOWN, cooldown);
        if (cooldown > 0 || !player.shouldHeal()) return false;
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
