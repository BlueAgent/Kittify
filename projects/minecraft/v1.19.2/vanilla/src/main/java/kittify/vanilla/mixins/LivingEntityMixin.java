package kittify.vanilla.mixins;

import kittify.vanilla.MixinHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Priority 900 to make this death prevention work on Quilt in dev.
 * Reason why is `org.quiltmc.qsl.entity_events.mixin.LivingEntityMixin#invokeTryReviveAfterTotemEvent` is setting the return value to false.
 */
@Mixin(value = LivingEntity.class, priority = 900)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    protected abstract void playHurtSound(DamageSource source);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "checkTotemDeathProtection(Lnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("RETURN"), cancellable = true)
    private void kittify$checkTotemDeathProtection$after(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && MixinHooks.shouldPreventDeath((LivingEntity) (Entity) this, damageSource, false)) {
            this.setHealth(1.0F);
            playHurtSound(damageSource);
            cir.setReturnValue(true);
        }
    }
}
