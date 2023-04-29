package kittify.common.compat;

import kittify.common.module.EntityProtection;

public class Nevermine extends CompatBase {
    public void init() {
        EntityProtection.addVillagerAttackerClass("net.tslat.aoa3.entity.base.AoAMeleeMob");
        EntityProtection.addVillagerAttackerClass("net.tslat.aoa3.entity.base.AoARangedMob");
        EntityProtection.addVillagerAttackerClass("net.tslat.aoa3.entity.base.AoAFlyingMeleeMob");
        EntityProtection.addVillagerAttackerClass("net.tslat.aoa3.entity.base.AoAFlyingRangedMob");
    }
}
