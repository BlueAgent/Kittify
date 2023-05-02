package kittify.vanilla;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public class MixinHooks {
    @SuppressWarnings("RedundantIfStatement")
    public static boolean shouldPreventDeath(LivingEntity livingEntity, DamageSource damageSource) {
        if (livingEntity instanceof TamableAnimal tamableAnimal) {
            if (preventTamedDeaths(tamableAnimal)) {
                return true;
            }
        }

        return false;
    }

    private static boolean preventTamedDeaths(TamableAnimal tamableAnimal) {
        return tamableAnimal.isTame();
    }
}
