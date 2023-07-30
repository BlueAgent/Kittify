package kittify;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Kittify {
    public static final String MOD_ID = "kittify";
    public static final String NAME = "Kittify";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String TRINKETS_MOD_ID = "trinkets";
    public static final String CURIOS_MOD_ID = "curios";
    public static Kittify INSTANCE;

    protected Kittify() {
        if (INSTANCE != null) {
            throw new RuntimeException("Expected only one Kittify to be instantiated.");
        }

        INSTANCE = this;
    }

    public abstract boolean isModLoaded(String modId);
}
