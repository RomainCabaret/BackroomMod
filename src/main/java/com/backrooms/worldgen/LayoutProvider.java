package com.backrooms.worldgen;

public class LayoutProvider {
    public enum Zone { MAZE, OPEN }

    public static Zone getZone(int chunkX, int chunkZ, long seed) {
        int regionSize = 10;
        int rx = Math.floorDiv(chunkX, regionSize);
        int rz = Math.floorDiv(chunkZ, regionSize);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int testRx = rx + i;
                int testRz = rz + j;

                long regionHash = seed ^ (testRx * 73491L) ^ (testRz * 51293L);
                net.minecraft.util.RandomSource rand = net.minecraft.util.RandomSource.create(regionHash);

                if (rand.nextFloat() < 0.05f) continue;

                int offsetX = (int) ((rand.nextFloat() * 1.5f - 0.25f) * regionSize);
                int offsetZ = (int) ((rand.nextFloat() * 1.5f - 0.25f) * regionSize);

                int centreX = (testRx * regionSize) + offsetX;
                int centreZ = (testRz * regionSize) + offsetZ;

                int rayon = 2 + rand.nextInt(2);
                double distance = Math.sqrt(Math.pow(chunkX - centreX, 2) + Math.pow(chunkZ - centreZ, 2));

                if (distance <= rayon) return Zone.OPEN;
            }
        }
        return Zone.MAZE;
    }
}