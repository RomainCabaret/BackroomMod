package com.backrooms.worldgen.structure;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Backrooms structural generation logic.
 * Validates room compatibility filtering and NBT filename parsing.
 */
public class BackroomsStructuresTest {

    /**
     * Clears the module catalog and injects a mock room before each test
     * to prevent state contamination between test runs.
     */
    @BeforeEach
    void setUp() {
        BackroomsStructures.CATALOGUE_NIVEAU_0.clear();

        // Inject a mock office room into the catalog
        BackroomsStructures.CATALOGUE_NIVEAU_0.add(new BackroomsStructures.ModuleInfo(
                ResourceLocation.fromNamespaceAndPath("backrooms", "test_room"),
                "office_room", 0, 0, 0,
                "open", "0", "7x1-8x1-7x2-8x2", "open", Rotation.NONE
        ));
    }

    /**
     * Verifies that the compatibility filter correctly matches or rejects rooms
     * based on their requested wall signatures.
     */
    @Test
    void testCompatibilitePieces() {
        // Test 1: The generator requests the exact configuration of our mock room
        List<BackroomsStructures.ModuleInfo> perfectMatch = BackroomsStructures.trouverPiecesCompatibles("open", "0", "7x1-8x1-7x2-8x2", "open");
        assertEquals(1, perfectMatch.size(), "The filter should have found exactly one matching room.");

        // Test 2: The generator requests solid walls everywhere, which our mock room doesn't have
        List<BackroomsStructures.ModuleInfo> impossibleMatch = BackroomsStructures.trouverPiecesCompatibles("0", "0", "0", "0");
        assertTrue(impossibleMatch.isEmpty(), "The filter should have returned an empty list.");
    }

    /**
     * Ensures the regex used to parse structure filenames is robust
     * and prevents malformed strings from causing crashes during generation.
     */
    @Test
    void testRegexNommage() {
        String regex = "(.+)_(\\d+)_(\\d+)_(\\d+)_n_([a-z0-9-]+)_s_([a-z0-9-]+)_e_([a-z0-9-]+)_o_([a-z0-9-]+)(?:_v\\d+)?";

        String validName = "open_void_bigpillar_0_0_0_n_open_s_open_e_open_o_0";
        assertTrue(validName.matches(regex), "The regex must accept a perfectly formatted NBT filename.");

        String invalidName = "open_void_typo_n_open_s";
        assertFalse(invalidName.matches(regex), "The regex must reject malformed filenames to prevent runtime crashes.");
    }
}