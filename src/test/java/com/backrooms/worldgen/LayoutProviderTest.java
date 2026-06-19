package com.backrooms.worldgen;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LayoutProviderTest {

    @Test
    void testDeterminismeDesZones() {
        long seed = 42L;

        // Pour une seed et des coordonnées fixes, le bruit de Worley DOIT cracher exactement le même résultat
        LayoutProvider.Zone zoneTest1 = LayoutProvider.getZone(150, -300, seed);
        LayoutProvider.Zone zoneTest2 = LayoutProvider.getZone(150, -300, seed);

        assertEquals(zoneTest1, zoneTest2, "Alerte : Le générateur n'est pas déterministe. Les zones vont changer à chaque redémarrage du serveur.");
    }
}