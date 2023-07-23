package kittify.quiltish;

import kittify.vanilla.common.registry.KittifyItems;
import net.minecraft.core.Registry;

public class KittifyQuiltish {
    public static void onInitialize() {
        KittifyItems.init((rl, item) -> Registry.register(Registry.ITEM, rl, item));
    }
}
