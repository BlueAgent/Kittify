package kittify.common.compat;

import kittify.Kittify;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import tamaized.aov.common.capabilities.CapabilityList;
import tamaized.aov.common.capabilities.aov.IAoVCapability;
import tamaized.aov.common.core.abilities.Ability;
import tamaized.tammodized.common.helper.CapabilityHelper;

public class AngelOfVengeance extends CompatBase {

    public static final int MINUTES_TILL_FULL_CHARGES = 10;
    public static final int TICKS_TILL_FULL_CHARGES = MINUTES_TILL_FULL_CHARGES * 60 * 20;

    public AngelOfVengeance() {
        super(Kittify.ANGEL_OF_VENGEANCE_MODID, "Angel of Vengeance");
    }

    @Override
    public void init() {

    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        IAoVCapability cap = CapabilityHelper.getCap(e.player, CapabilityList.AOV, null);
        if (cap == null) return;
        long totalWorldTime = e.player.world.getTotalWorldTime();
        // Recharge abilities
        for (Ability a : cap.getAbilities()) {
            int chargeLimit = a.getAbility().getMaxCharges() + cap.getExtraCharges(e.player, a);
            // Should probably do this with capabilities... It'd be more consistent but also more laggy.
            if (a.getCharges() >= chargeLimit) continue;
            int ticksPerCharge = TICKS_TILL_FULL_CHARGES / chargeLimit;
            if (totalWorldTime % ticksPerCharge == 0) {
                a.restoreCharge(e.player, cap, 1);
            }
        }
    }
}
