package kittify.common.compat;

import kittify.Kittify;
import kittify.common.module.EntityProtection;
import net.tslat.aoa3.entity.base.AoAFlyingMeleeMob;
import net.tslat.aoa3.entity.base.AoAFlyingRangedMob;
import net.tslat.aoa3.entity.base.AoAMeleeMob;
import net.tslat.aoa3.entity.base.AoARangedMob;

public class Nevermine {
    public static void init() {
        Kittify.log.info("Nevermine is installed");
        EntityProtection.VILLAGER_ATTACKER_BASE_CLASSES.add(AoAMeleeMob.class);
        EntityProtection.VILLAGER_ATTACKER_BASE_CLASSES.add(AoARangedMob.class);
        EntityProtection.VILLAGER_ATTACKER_BASE_CLASSES.add(AoAFlyingMeleeMob.class);
        EntityProtection.VILLAGER_ATTACKER_BASE_CLASSES.add(AoAFlyingRangedMob.class);
    }
}
