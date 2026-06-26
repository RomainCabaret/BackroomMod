package com.backrooms.registry;

import com.backrooms.Backrooms;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final SoundEvent NEON_HUM =
            SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, "neon_hum")
            );

    public static void register() {
        Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, "neon_hum"),
                NEON_HUM
        );
    }
}