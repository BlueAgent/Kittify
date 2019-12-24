package kittify.common.compat;

import kittify.Kittify;
import kittify.common.module.EntityProtection;

public class GrimoireOfGaia extends CompatBase {
    public GrimoireOfGaia() {
        super(Kittify.GRIMOIRE_OF_GAIA_MODID, "Grimoire of Gaia");
    }

    public void init() {
        EntityProtection.addVillagerAttackerClass("gaia.entity.EntityMobHostileBase");
    }
}
