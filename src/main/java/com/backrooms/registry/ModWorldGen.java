package com.backrooms.registry;

import com.backrooms.Backrooms;
import com.backrooms.worldgen.BackroomsChunkGenerator;
import com.backrooms.worldgen.BackroomsDebugGenerator;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModWorldGen {
    public static void register() {
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, "backrooms_chunk_generator"), BackroomsChunkGenerator.CODEC);
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, "backrooms_debug_chunk_generator"), BackroomsDebugGenerator.CODEC);
    }
}