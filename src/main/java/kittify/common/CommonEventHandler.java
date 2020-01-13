package kittify.common;

import kittify.Kittify;
import kittify.common.module.EntityProtection;
import kittify.common.util.NBTUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Kittify.MOD_ID)
public class CommonEventHandler {

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

    private static final int SWING_TICKS = Integer.MAX_VALUE / 2;

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

    @SubscribeEvent
    public static void nicerCreeperExplosions(ExplosionEvent.Detonate e) {
        final Explosion explosion = e.getExplosion();
        if (explosion.getExplosivePlacedBy() instanceof EntityCreeper) {
            final World world = e.getWorld();
            // Reference: net.minecraft.world.Explosion.doExplosionB
            // Explode non-TileEntities without loss
            explosion.getAffectedBlockPositions().forEach(blockPos -> {
                final IBlockState state = world.getBlockState(blockPos);
                final Block block = state.getBlock();
                if (state.getMaterial() == Material.AIR) return;
                if (!block.canDropFromExplosion(explosion)) return;
                if (block.hasTileEntity(state) || world.getTileEntity(blockPos) != null) return;
                block.dropBlockAsItemWithChance(world, blockPos, state, 1.0f, 0);
                block.onBlockExploded(world, blockPos, explosion);
            });
            explosion.clearAffectedBlockPositions();
            // No damage to non-living entities
            e.getAffectedEntities().removeIf(entity -> !(entity instanceof EntityLiving));
        }
    }

    private static final String TAG_DEATH_EXPERIENCE = "DeathXP";

    /**
     * Save experience on death (so it can be returned on respawn).
     * Lowest to allow other mods to save experience if they want to (this is a last resort).
     * This doesn't get run if keepInventory is on.
     *
     * @param e Event data
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void experienceSaveOnDeath(LivingExperienceDropEvent e) {
        final EntityLivingBase entity = e.getEntityLiving();
        if (entity instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) entity;
            final int currentExperience = Math.max(e.getDroppedExperience(), player.experienceTotal);
            final NBTTagCompound modPersistedTag = NBTUtil.getModPersistedTag(player);

            // For when the mod was removed then added back in (handle overflow too)
            final int previousExperience = Math.min(
                    Integer.MAX_VALUE - currentExperience,
                    modPersistedTag.getInteger(TAG_DEATH_EXPERIENCE)
            );
            final int experienceToSave = currentExperience + previousExperience;

            if (experienceToSave > 0) {
                // Save the experience
                modPersistedTag.setInteger(TAG_DEATH_EXPERIENCE, experienceToSave);

                // Try to prevent duplication of experience from other sources
                player.experience = 0f;
                player.experienceLevel = 0;
                player.experienceTotal = 0;
                e.setDroppedExperience(0);
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void experienceLoadOnRespawn(PlayerEvent.Clone e) {
        final EntityPlayer clone = e.getEntityPlayer();
        final NBTTagCompound modPersistedTag = NBTUtil.getModPersistedTag(clone);
        if (modPersistedTag.hasKey(TAG_DEATH_EXPERIENCE)) {
            final int experienceTotal = modPersistedTag.getInteger(TAG_DEATH_EXPERIENCE);
            clone.addExperience(experienceTotal);
            modPersistedTag.removeTag(TAG_DEATH_EXPERIENCE);
        }
    }
}
