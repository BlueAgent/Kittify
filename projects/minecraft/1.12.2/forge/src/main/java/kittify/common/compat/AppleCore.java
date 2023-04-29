package kittify.common.compat;

import kittify.common.module.FoodAndHunger;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import squeek.applecore.api.AppleCoreAPI;
import squeek.applecore.api.hunger.HealthRegenEvent;

import static net.minecraftforge.fml.common.eventhandler.Event.Result.DENY;

public class AppleCore extends CompatBase {
    @Override
    public void init() {
    }

    @SubscribeEvent
    public void disableSaturatedRegen(HealthRegenEvent.AllowSaturatedRegen event) {
        event.setResult(DENY);
        FoodAndHunger.doSpecialRegen(event.player, AppleCoreAPI.accessor.getMaxExhaustion(event.player));
    }

    @SubscribeEvent
    public void disableRegen(HealthRegenEvent.AllowRegen event) {
        event.setResult(DENY);
    }
}
