package kittify.vanilla.mixins;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import kittify.Kittify;
import kittify.vanilla.MixinHooks;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.server.network.ServerConnectionListener$1")
public abstract class ServerConnectionListenerMixin<C extends Channel> extends ChannelInitializer<C> {
    /**
     * @param channel
     * @param ci
     */
    @Inject(method = "initChannel(Lio/netty/channel/Channel;)V", at = @At(value = "INVOKE", target = "Lio/netty/channel/Channel;pipeline()Lio/netty/channel/ChannelPipeline;", ordinal = 0), require = 1)
    protected void initChannel$addLatencySimulator(@Coerce C channel, CallbackInfo ci) {
        int simulatedLatency = MixinHooks.SIMULATED_LATENCY;
        if (simulatedLatency > 0) {
            Kittify.LOGGER.warn("Kittify is simulating a latency of " + simulatedLatency + " ms. " + MixinHooks.SIMULATED_LATENCY_PROPERTY_NAME + " = " + simulatedLatency);
            channel.pipeline().addLast(new ServerConnectionListener.LatencySimulator(simulatedLatency, simulatedLatency / 5));
        }
    }
}
