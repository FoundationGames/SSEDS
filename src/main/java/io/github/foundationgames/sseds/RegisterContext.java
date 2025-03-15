package io.github.foundationgames.sseds;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public interface RegisterContext {
    ResourceLocation createNextId();

    class Vanilla implements RegisterContext {
        private int ordinal = 0;

        @Override
        public ResourceLocation createNextId() {
            return ResourceLocation.withDefaultNamespace(Integer.toString(ordinal++));
        }
    }

    class Modded implements RegisterContext {
        private final Object2IntMap<String> ordinals = new Object2IntOpenHashMap<>();

        @Override
        public ResourceLocation createNextId() {
            var stackTrace = Thread.currentThread().getStackTrace();

            if (stackTrace.length < 5) {
                throw new RuntimeException("SSEDS: Invalid or missing stacktrace, JVM could be incompatible!");
            }

            // [0=getStackTrace, 1=createNextId, 2=mixin handler method, 3=registerSerializer, 4=<modded classes> ..., fabric loader invokeEntrypoints]
            List<StackTraceElement> relevantStackTrace = new ArrayList<>();
            for (int i = 4; i < stackTrace.length; i++) {
                var element = stackTrace[i];
                if (element.getClassName().startsWith("net.fabricmc.loader")) {
                    break;
                }

                relevantStackTrace.add(stackTrace[i]);
            }

            String modId = null;
            relevantStackTrace = relevantStackTrace.reversed();
            for (var mod : FabricLoader.getInstance().getAllMods()) {
                for (var el : relevantStackTrace) {
                    var file = el.getClassName().replace('.', '/') + ".class";
                    if (mod.findPath(file).isPresent()) {
                        modId = mod.getMetadata().getId();
                        break;
                    }
                }

                if (modId != null) {
                    break;
                }
            }

            if (modId == null) {
                throw new RuntimeException("SSEDS: Unknown mod is trying to register!");
            }

            int ordinal = ordinals.computeIfAbsent(modId, m -> 0);
            ordinals.put(modId, ordinal + 1);
            return ResourceLocation.fromNamespaceAndPath(modId, Integer.toString(ordinal));
        }
    }
}
