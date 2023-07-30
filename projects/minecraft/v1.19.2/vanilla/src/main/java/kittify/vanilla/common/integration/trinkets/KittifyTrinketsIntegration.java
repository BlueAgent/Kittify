package kittify.vanilla.common.integration.trinkets;

import dev.emi.trinkets.api.TrinketsApi;
import kittify.vanilla.common.registry.KittifyItems;

public class KittifyTrinketsIntegration {
    private KittifyTrinketsIntegration() {
    }

    public static void afterRegisterItems() {
        TrinketsApi.registerTrinket(KittifyItems.CUTE_COLLAR, new CuteCollarTrinket());
    }
}
