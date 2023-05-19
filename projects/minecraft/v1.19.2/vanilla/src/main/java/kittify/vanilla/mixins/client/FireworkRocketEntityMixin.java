package kittify.vanilla.mixins.client;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends Projectile implements ItemSupplier {
    @Shadow
    private int life;

    @Shadow
    private int lifetime;

    public FireworkRocketEntityMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    public void kittify$onTick$clientSideExplode(CallbackInfo ci) {
        if (this.level.isClientSide && this.life > this.lifetime) {
            this.explode();
        }
    }

    @Shadow
    protected abstract void explode();
}
