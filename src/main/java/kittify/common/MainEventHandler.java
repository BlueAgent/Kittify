package kittify.common;

import kittify.Kittify;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Kittify.MOD_ID)
public class MainEventHandler {

    public static final int SWING_TICKS = Integer.MAX_VALUE / 2;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.player.ticksSinceLastSwing < SWING_TICKS) {
            e.player.ticksSinceLastSwing = SWING_TICKS;
        }
    }
}
