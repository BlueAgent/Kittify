package kittify.common.compat;

import kittify.Kittify;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.common.core.abilities.Ability;

public class AngelOfVengeance extends CompatBase {
    public static final int MINUTES_TILL_FULL_CHARGES = 10;
    public static final int TICKS_TILL_FULL_CHARGES = MINUTES_TILL_FULL_CHARGES * 60 * 20;
    @CapabilityInject(IAoVCapability.class)
    public static Capability<IAoVCapability> AOV_CAP = null;

    public AngelOfVengeance() {
        super(Kittify.ANGEL_OF_VENGEANCE_MODID, "Angel of Vengeance");
    }

    @Override
    public void init() {
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        IAoVCapability aov = e.player.getCapability(AOV_CAP, null);
        if (aov == null) return;
        long totalWorldTime = e.player.world.getTotalWorldTime();
        // Recharge abilities
        for (Ability a : aov.getAbilities()) {
            int chargeLimit = a.getAbility().getMaxCharges() + aov.getExtraCharges(e.player, a);
            // Should probably do this with capabilities... It'd be more consistent but also more laggy.
            // Not worth using capabilities for this. Having  slightly more charges isn't that game breaking.
            if (a.getCharges() >= chargeLimit) continue;
            int ticksPerCharge = TICKS_TILL_FULL_CHARGES / chargeLimit;
            if (totalWorldTime % ticksPerCharge == 0) {
                a.restoreCharge(e.player, aov, 1);
            }
        }
    }
}
