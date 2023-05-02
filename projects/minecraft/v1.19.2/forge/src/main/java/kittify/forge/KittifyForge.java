package kittify.forge;

import kittify.Kittify;
import net.minecraftforge.fml.common.Mod;

@Mod(Kittify.MOD_ID)
public class KittifyForge {
    public KittifyForge() {
        Kittify.LOGGER.info(Kittify.NAME + " is on Forge.");
    }
}
