package com.backrooms.worldgen;

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

        // On injecte une fausse mégastructure 2x2 chunks pour les tests
        BackroomsStructures.Megastructure mockMega = new BackroomsStructures.Megastructure(
                "poolrooms",
                List.of(
                        new BackroomsStructures.ModuleInfo(ResourceLocation.fromNamespaceAndPath("backrooms", "pool_0_0_0"), "pool", 0, 0, 0, "0", "0", "0", "0", Rotation.NONE),
                        new BackroomsStructures.ModuleInfo(ResourceLocation.fromNamespaceAndPath("backrooms", "pool_1_0_0"), "pool", 1, 0, 0, "0", "0", "0", "0", Rotation.NONE),
                        new BackroomsStructures.ModuleInfo(ResourceLocation.fromNamespaceAndPath("backrooms", "pool_0_0_1"), "pool", 0, 0, 1, "0", "0", "0", "0", Rotation.NONE),
                        new BackroomsStructures.ModuleInfo(ResourceLocation.fromNamespaceAndPath("backrooms", "pool_1_0_1"), "pool", 1, 0, 1, "0", "0", "0", "0", Rotation.NONE)
                )
        );
        BackroomsStructures.CATALOGUE_MEGASTRUCTURES.add(mockMega);
    }

    @Test
    void testTauxApparitionMegaStructures() {
        long seed = 999L;
        int secteursAvecMega = 0;
        int secteursTestes = 1000;

        // On teste 1000 secteurs
        for (int i = 0; i < secteursTestes; i++) {
            boolean found = false;

            // Le test doit chercher dans tout le secteur 32x32
            for (int x = 0; x < 32; x++) {
                for (int z = 0; z < 32; z++) {
                    if (MegaManager.check(i * 32 + x, z, seed) != null) {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            if (found) {
                secteursAvecMega++;
            }
        }

        assertTrue(secteursAvecMega > 0, "Le système de probabilité est cassé, aucune mégastructure n'a spawn.");
        assertTrue(secteursAvecMega > 100 && secteursAvecMega < 300,
                "Le ratio d'apparition est flingué. Obtenu : " + secteursAvecMega + "/1000");
    }
}