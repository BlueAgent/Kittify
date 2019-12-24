package kittify.common;

import kittify.Kittify;
import kittify.common.module.EntityProtection;
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
public class CommonEventHandler {

    public static final int SWING_TICKS = Integer.MAX_VALUE / 2;
    public static long debug_timing = 0;

//    @SubscribeEvent
//    public static void onServerTick(TickEvent.ServerTickEvent e) {
//        if(e.phase != TickEvent.Phase.END) return;
//        int tick = FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();
//        if (tick % (20*10) == 0) {
//            double yay = debug_timing / (20. * 10.) / 1000.;
//            System.out.println(yay + " ms/t");
//            debug_timing = 0;
//        }
//    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        // Remove attack cooldown
        if (e.player.ticksSinceLastSwing < SWING_TICKS) {
            e.player.ticksSinceLastSwing = SWING_TICKS;
        }
    }

    public static boolean shouldSourceHurtTamed(EntityTameable entityTameable, DamageSource damageSource, float amount) {
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
        if (entityLivingBase instanceof EntityTameable && ((EntityTameable) entityLivingBase).isTamed()) {
            EntityTameable entityTameable = (EntityTameable) entityLivingBase;
            if (!shouldSourceHurtTamed(entityTameable, damageSource, amount)) {
                e.setCanceled(true);
            }
        }
    }

    public static EntityLivingBase onSetAttackTarget(EntityLiving attacker, EntityLivingBase target) {
        if (target == null) return null;
        // EntityProtection
        if (EntityProtection.shouldPreventTargeting(attacker, target)) {
            return null;
        }
        // Tamed Protection
        if (target instanceof EntityTameable && ((EntityTameable) target).isTamed()) {
            //return ((EntityTameable) target).getOwner();
            return null;
        }
        return target;
    }

    @SubscribeEvent
    public static void onSetAttackTarget(LivingSetAttackTargetEvent e) {
        EntityLivingBase baseAttacker = e.getEntityLiving();

        if (baseAttacker instanceof EntityLiving) {
            EntityLiving attacker = (EntityLiving) baseAttacker;

            EntityLivingBase baseTarget = e.getTarget();
            EntityLivingBase newTarget = onSetAttackTarget(attacker, baseTarget);

            if (baseTarget != newTarget) {
                attacker.setAttackTarget(newTarget);
            }
        }
    }
}