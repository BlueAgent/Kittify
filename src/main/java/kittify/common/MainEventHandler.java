package kittify.common;

import kittify.Kittify;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Kittify.MOD_ID)
public class MainEventHandler {

    public static final int SWING_TICKS = Integer.MAX_VALUE / 2;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        // Remove attack cooldown
        if (e.player.ticksSinceLastSwing < SWING_TICKS) {
            e.player.ticksSinceLastSwing = SWING_TICKS;
        }
    }

    public static boolean shouldSourceHurtTamable(EntityTameable entityTameable, DamageSource damageSource, float amount) {
        if (!entityTameable.isTamed()) return true;

        // Creative damage
        // TODO: Test if pets need to be protected from creative damage too (e.g. void damage)
        //  I think certain mods add forms of creative damage or use void damage
        // Float.MAX_VALUE (Kill Command) damage should be allowed through always
        if (damageSource.canHarmInCreative()) return true;

        // Sneaking Players
        Entity trueSource = damageSource.getTrueSource();
        return trueSource instanceof EntityPlayer && trueSource.isSneaking();
    }

    @SubscribeEvent
    public static void onAttack(LivingAttackEvent e) {
        float amount = e.getAmount();
        DamageSource damageSource = e.getSource();
        // Stop ocelots, cats, wolves or dogs from being hurt by non-players or creative damage
        EntityLivingBase entityLivingBase = e.getEntityLiving();
        if (entityLivingBase instanceof EntityTameable) {
            EntityTameable entityTameable = (EntityTameable) entityLivingBase;
            if (!shouldSourceHurtTamable(entityTameable, damageSource, amount)) {
                e.setCanceled(true);
            }
        }
    }

    public static EntityLivingBase onSetAttackTarget(EntityLiving attacker, EntityLivingBase target) {
        if (target instanceof EntityTameable) {
            //return ((EntityTameable) target).getOwner();
            return null;
        }
        return target;
    }

    @SubscribeEvent
    public static void onSetAttackTarget(LivingSetAttackTargetEvent e) {
        EntityLivingBase baseAttacker = e.getEntityLiving();

        if(baseAttacker instanceof EntityLiving) {
            EntityLiving attacker = (EntityLiving) baseAttacker;

            EntityLivingBase baseTarget = e.getTarget();
            EntityLivingBase newTarget = onSetAttackTarget(attacker, baseTarget);

            if (baseTarget != newTarget) {
                attacker.setAttackTarget(newTarget);
            }
        }
    }
}
