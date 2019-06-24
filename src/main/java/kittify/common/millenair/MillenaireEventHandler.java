package kittify.common.millenair;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.millenaire.common.entity.MillVillager;

public class MillenaireEventHandler {
    @SubscribeEvent
    public static void handleMillenaireVillagerProtection(LivingSetAttackTargetEvent e) {
        if (!(e.getTarget() instanceof MillVillager)) return;
        EntityLivingBase base = e.getEntityLiving();
        if (!(base instanceof EntityLiving)) return;
        EntityLiving attacker = (EntityMob) base;
        Class<?> attackerClass = attacker.getClass();
        if (attackerClass.getName().startsWith("net.tslat.aoa3.entity")) {
            attacker.setAttackTarget(null);
        }
    }
}
