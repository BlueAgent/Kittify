package kittify.quiltish;

import kittify.Kittify;
import kittify.vanilla.common.registry.KittifyItems;
import net.minecraft.core.Registry;

public abstract class KittifyQuiltish extends Kittify {
    public static KittifyQuiltish INSTANCE;

    protected KittifyQuiltish() {
        if (INSTANCE != null) {
            throw new RuntimeException("Expected only one KittifyQuiltish to be instantiated.");
        }

        INSTANCE = this;
    }

    protected void onInitialize() {
        KittifyItems.init((rl, item) -> Registry.register(Registry.ITEM, rl, item));
    }
}
