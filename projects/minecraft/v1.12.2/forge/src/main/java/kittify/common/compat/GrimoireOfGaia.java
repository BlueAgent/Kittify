package kittify.common.compat;

import kittify.common.module.EntityProtection;

public class GrimoireOfGaia extends CompatBase {
    public void init() {
        EntityProtection.addVillagerAttackerClass("gaia.entity.EntityMobHostileBase");
    }
}
