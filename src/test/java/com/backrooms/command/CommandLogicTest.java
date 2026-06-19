package com.backrooms.command;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CommandLogicTest {

    @Test
    void testSliceSignatureLogic() {
        // Cas 1 : La face contient du verre, la salle doit s'ouvrir totalement
        assertEquals("open", SliceCommand.calculateSignature(true, List.of("1x1", "2x2")), "Le verre doit forcer l'ouverture.");

        // Cas 2 : Un mur plein sans bedrock
        assertEquals("0", SliceCommand.calculateSignature(false, List.of()), "Une liste vide doit renvoyer '0'.");

        // Cas 3 : Une porte custom en bedrock
        assertEquals("7x1-8x1", SliceCommand.calculateSignature(false, List.of("7x1", "8x1")), "La signature de la porte est mal formatée.");
    }

    @Test
    void testPlaceMegaMath() {
        BlockPos origin = new BlockPos(100, 64, 200);
        int tailleY = 3; // Mégastructure de 3 étages

        // Test du rez-de-chaussée (iy = 2, car tailleY - 1 = 2)
        // Le rez-de-chaussée doit se poser sur le Y d'origine
        BlockPos posRdc = PlaceMegaCommand.calculatePiecePosition(origin, 1, 2, 1, tailleY);
        assertEquals(116, posRdc.getX(), "Décalage X foireux.");
        assertEquals(64, posRdc.getY(), "Le RDC ne s'aligne pas avec l'origine.");
        assertEquals(216, posRdc.getZ(), "Décalage Z foireux.");

        // Test du sous-sol 1 (iy = 1)
        // Doit être 8 blocs plus bas (Y = 56)
        BlockPos posSousSol = PlaceMegaCommand.calculatePiecePosition(origin, 0, 1, 0, tailleY);
        assertEquals(56, posSousSol.getY(), "Le calcul d'enfoncement des étages souterrains est cassé.");
    }

    @Test
    void testMapColorFallback() {
        // On teste juste que la méthode ne crache pas de NullPointer avec une seed aléatoire
        // (La logique pure est déjà testée dans MegaManagerTest et LayoutProviderTest)
        int color = MapCommand.getPixelColor(10, 10, 847291L);
        assertTrue(color == 0xFF0000 || color == 0xFFFFFF || color == 0x000000, "La couleur générée par le cartographe est invalide.");
    }
}