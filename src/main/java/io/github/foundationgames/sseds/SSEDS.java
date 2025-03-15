package io.github.foundationgames.sseds;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class SSEDS implements ModInitializer {
    public static final ResourceKey<Registry<EntityDataSerializer<?>>> EDS_REGISTRY_KEY =
            ResourceKey.createRegistryKey(rl("entity_data_serializer"));

    public static final Registry<EntityDataSerializer<?>> EDS_REGISTRY =
            FabricRegistryBuilder.createSimple(EDS_REGISTRY_KEY)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static RegisterContext registerContext = new RegisterContext.Vanilla();

    @Override
    public void onInitialize() {
    }

    public static final ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath("sseds", path);
    }
}
