package io.github.foundationgames.sseds.mixin;

import io.github.foundationgames.sseds.RegisterContext;
import io.github.foundationgames.sseds.SSEDS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityDataSerializers.class, priority = 900)
public class EntityDataSerializersMixin {
    static {
        SSEDS.registerContext = new RegisterContext.Modded();
    }

    @Inject(method = "registerSerializer", at = @At("HEAD"), cancellable = true)
    private static void sseds$redirectRegistration(EntityDataSerializer<?> serializer, CallbackInfo ci) {
        Registry.register(SSEDS.EDS_REGISTRY, SSEDS.registerContext.createNextId(), serializer);
        ci.cancel();
    }

    @Inject(method = "getSerializer", at = @At("HEAD"), cancellable = true)
    private static void sseds$redirectGetById(int id, CallbackInfoReturnable<EntityDataSerializer<?>> cir) {
        cir.setReturnValue(SSEDS.EDS_REGISTRY.getHolder(id).map(Holder.Reference::value).orElse(null));
        cir.cancel();
    }

    @Inject(method = "getSerializedId", at = @At("HEAD"), cancellable = true)
    private static void sseds$redirectGetRawId(EntityDataSerializer<?> serializer, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(SSEDS.EDS_REGISTRY.getId(serializer));
        cir.cancel();
    }
}
