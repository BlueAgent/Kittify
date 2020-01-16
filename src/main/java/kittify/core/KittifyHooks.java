package kittify.core;

import kittify.common.module.FoodAndHunger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import org.objectweb.asm.tree.ClassNode;

public final class KittifyHooks {

    /**
     * Hook to control if hunger should be ignored.
     * This is currently used to make it so players can always eat.
     *
     * @param player       Player Entity
     * @param ignoreHunger current value of ignoreHunger
     * @return iff hunger should be ignored
     * @see EntityPlayer#canEat(boolean)
     * @see KittifyTransformer#hookPlayerCanEat(ClassNode)
     */
    public static boolean canEat(EntityPlayer player, boolean ignoreHunger) {
        // Makes it so that players can always eat.
        return true;
    }

    public static boolean shouldNaturalRegen(boolean naturalRegen, EntityPlayer player, FoodStats foodStats) {
        return false;
    }

    public static boolean doSpecialRegen(boolean naturalRegen, EntityPlayer player, FoodStats foodStats) {
        // TODO: Find better way of doing this?
        return FoodAndHunger.doSpecialRegen(player, 4.0f);
    }
}
