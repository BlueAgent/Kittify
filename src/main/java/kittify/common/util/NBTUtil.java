package kittify.common.util;

import kittify.Kittify;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class NBTUtil {
    public static final String TAG_KITTIFY = Kittify.MOD_ID;

    /**
     * Gets, and if it doesn't exist, sets the specified Compound Tag
     *
     * @param root Root Tag
     * @param name Sub Tag name to get and maybe set
     * @return Tag with the given name (saved under the root tag)
     */
    public static NBTTagCompound getSetTag(NBTTagCompound root, String name) {
        final NBTTagCompound subTag = root.getCompoundTag(name);
        if (!root.hasKey(name))
            root.setTag(name, subTag);
        return subTag;
    }

    /**
     * Get tag for this mod that is LOST on player cloning (unless manually transferred over in the event)
     *
     * @param player Target player
     * @return Tag saved under player data tag
     */
    public static NBTTagCompound getModEphemeralTag(EntityPlayer player) {
        return getSetTag(player.getEntityData(), TAG_KITTIFY);
    }

    /**
     * Get tag that is persisted on player cloning
     *
     * @param player Target player
     * @return Tag saved under the persisted player data tag
     */
    public static NBTTagCompound getPersistedTag(EntityPlayer player) {
        return getSetTag(player.getEntityData(), EntityPlayer.PERSISTED_NBT_TAG);
    }

    /**
     * Get tag for this mod that that is KEPT on player cloning.
     *
     * @param player Target player
     * @return Tag for the mod saved under the persisted player data tag
     */
    public static NBTTagCompound getModPersistedTag(EntityPlayer player) {
        return getSetTag(getPersistedTag(player), TAG_KITTIFY);
    }
}
