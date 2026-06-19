package com.backrooms.command;

import com.backrooms.worldgen.MegaManager;
import com.backrooms.worldgen.structure.BackroomsStructures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MegaManagerTest {

    @BeforeEach
    void setUp() {
        BackroomsStructures.CATALOGUE_MEGASTRUCTURES.clear();
        BackroomsStructures.Megastructure mockMega = new BackroomsStructures.Megastructure(
                "poolrooms",
                List.of(
                        new BackroomsStructures.ModuleInfo(ResourceLocation.fromNamespaceAndPath("backrooms", "pool_0_0_0"), "pool", 0, 0, 0, "0", "0", "0", "0", Rotation.NONE)
                )
        );
        BackroomsStructures.CATALOGUE_MEGASTRUCTURES.add(mockMega);
    }

    @Test
    void testRadarMegastructure() {
        long seed = 12345L;

        net.minecraft.core.BlockPos pos = MegaManager.findNearestMega(seed, 0, 0, "pool", 10);
        assertNotNull(pos, "Le radar aurait dû trouver la poolrooms dans un rayon de 10 secteurs.");

        net.minecraft.core.BlockPos posFantome = MegaManager.findNearestMega(seed, 0, 0, "structure_imaginaire", 10);
        assertNull(posFantome, "Le radar a trouvé une structure qui n'existe pas dans le catalogue.");
    }
}