package kittify.common.compat;

import kittify.Kittify;
import kittify.common.module.EntityProtection;

public class Nevermine extends CompatBase {
    public Nevermine() {
        super(Kittify.NEVERMINE_MODID, "Advent of Ascension");
    }

    public void init() {
        EntityProtection.addVillagerAttackerClass("net.tslat.aoa3.entity.base.AoAMeleeMob");
        EntityProtection.addVillagerAttackerClass("net.tslat.aoa3.entity.base.AoARangedMob");
        EntityProtection.addVillagerAttackerClass("net.tslat.aoa3.entity.base.AoAFlyingMeleeMob");
        EntityProtection.addVillagerAttackerClass("net.tslat.aoa3.entity.base.AoAFlyingRangedMob");
    }
}
