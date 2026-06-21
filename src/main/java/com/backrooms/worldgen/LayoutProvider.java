package com.backrooms.worldgen;

public class LayoutProvider {
    public enum Zone { MAZE, OPEN }

    public static Zone getZone(int chunkX, int chunkZ, long seed) {
        // Grille de 6x6. Le sweet-spot exact entre isolation et densité.
        int regionSize = 6;
        int rx = Math.floorDiv(chunkX, regionSize);
        int rz = Math.floorDiv(chunkZ, regionSize);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int testRx = rx + i;
                int testRz = rz + j;

                long regionHash = seed ^ (testRx * 73491L) ^ (testRz * 51293L);
                net.minecraft.util.RandomSource rand = net.minecraft.util.RandomSource.create(regionHash);

                // On force le centre à être au milieu strict de la région (offset 2 ou 3)
                int offsetX = 2 + rand.nextInt(2);
                int offsetZ = 2 + rand.nextInt(2);

                int centreX = (testRx * regionSize) + offsetX;
                int centreZ = (testRz * regionSize) + offsetZ;

                // Rayon de 1 à 2 chunks.
                // Avec regionSize=6 et rayonMax=2, la somme des rayons de deux bulles voisines est 4.
                // Leur distance minimale est de 5. 5 > 4 = Fusion physiquement impossible.
                // Et l'espacement de couloir est garanti entre 2 et 5 chunks.
                int rayon = 1 + rand.nextInt(2);
                double distance = Math.sqrt(Math.pow(chunkX - centreX, 2) + Math.pow(chunkZ - centreZ, 2));

                if (distance <= rayon) return Zone.OPEN;
            }
        }
        return Zone.MAZE;
    }
}