package kittify.vanilla;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public class MixinHooks {
    public static final String SIMULATED_LATENCY_PROPERTY_NAME = "kittify.simulated_latency";
    public static final int SIMULATED_LATENCY = Integer.getInteger(SIMULATED_LATENCY_PROPERTY_NAME, 0);

    @SuppressWarnings("RedundantIfStatement")
    public static boolean shouldPreventDeath(LivingEntity livingEntity, DamageSource damageSource, boolean simulate) {
        if (livingEntity instanceof TamableAnimal tamableAnimal) {
            if (preventTamedDeaths(tamableAnimal, damageSource, simulate)) {
                return true;
            }
        }

        return false;
    }

    private static boolean preventTamedDeaths(TamableAnimal tamableAnimal, DamageSource damageSource, boolean simulate) {
        if (!tamableAnimal.isTame()) {
            return false;
        }

        if (!simulate) {
            // TODO: Prevent tamable mob from attacking / dealing damage and being targeted / dealing damage.
        }

        return true;
    }
}
