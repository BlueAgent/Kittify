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

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void setHealth(float health);

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "checkTotemDeathProtection(Lnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("RETURN"), cancellable = true)
    private void kittify$checkTotemDeathProtection$beforeDeath(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (MixinHooks.shouldPreventDeath((LivingEntity) (Entity) this, damageSource)) {
            this.setHealth(1.0F);
            cir.setReturnValue(true);
        }
    }
}
