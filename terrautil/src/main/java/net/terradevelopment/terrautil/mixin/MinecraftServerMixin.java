package net.terradevelopment.terrautil.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "startServer", at = @At("HEAD"))
    private static <S> void start(Function<Thread, S> function, CallbackInfoReturnable<S> cir) {
        System.out.println("Terra Util is loaded in the world!");
    }


}
