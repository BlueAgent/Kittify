package kittify.common.compat;

import kittify.Kittify;
import kittify.common.module.EntityProtection;

public class MinecraftComesAlive extends CompatBase {
    public MinecraftComesAlive() {
        super(Kittify.MINECRAFT_COMES_ALIVE_MODID, "Minecraft Comes Alive");
    }

    public void init() {
        EntityProtection.addVillagerClass("mca.entity.EntityVillagerMCA");
    }
}
