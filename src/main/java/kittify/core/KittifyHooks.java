package kittify.core;

import net.minecraft.entity.player.EntityPlayer;

public final class KittifyHooks {

    public static boolean canEat(EntityPlayer player, boolean ignoreHunger) {
        // Makes it so that players can always eat.
        return true;
    }
}
