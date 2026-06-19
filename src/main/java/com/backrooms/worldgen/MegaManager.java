package com.backrooms.worldgen;

import com.backrooms.worldgen.structure.BackroomsStructures;
import net.minecraft.core.BlockPos;

public class MegaManager {

    public record MegaHit(BackroomsStructures.Megastructure mega, int ix, int iz) {}

    public static MegaHit check(int chunkX, int chunkZ, long seed) {
        if (BackroomsStructures.CATALOGUE_MEGASTRUCTURES.isEmpty()) return null;

        int spacing = 32;
        int rx = Math.floorDiv(chunkX, spacing);
        int rz = Math.floorDiv(chunkZ, spacing);

        long regionHash = seed ^ (rx * 193487L) ^ (rz * 982134L);
        net.minecraft.util.RandomSource rand = net.minecraft.util.RandomSource.create(regionHash);

        if (rand.nextFloat() > 0.20f) return null;

        int indexStructure = (int) (Math.abs(regionHash) % BackroomsStructures.CATALOGUE_MEGASTRUCTURES.size());
        BackroomsStructures.Megastructure mega = BackroomsStructures.CATALOGUE_MEGASTRUCTURES.get(indexStructure);

        int originX = rx * spacing + rand.nextInt(spacing - mega.tailleX);
        int originZ = rz * spacing + rand.nextInt(spacing - mega.tailleZ);

        if (chunkX >= originX && chunkX < originX + mega.tailleX &&
                chunkZ >= originZ && chunkZ < originZ + mega.tailleZ) {
            return new MegaHit(mega, chunkX - originX, chunkZ - originZ);
        }
        return null;
    }

    public static BlockPos findNearestMega(long seed, int startChunkX, int startChunkZ, String targetName, int radiusInChunks) {

        if (BackroomsStructures.CATALOGUE_MEGASTRUCTURES.isEmpty()) return null;

        int spacing = 32;
        int rxStart = Math.floorDiv(startChunkX, spacing);
        int rzStart = Math.floorDiv(startChunkZ, spacing);

        int bestX = 0, bestZ = 0;
        double minDistance = Double.MAX_VALUE;
        boolean trouve = false;

        for (int i = -radiusInChunks; i <= radiusInChunks; i++) {
            for (int j = -radiusInChunks; j <= radiusInChunks; j++) {
                int rx = rxStart + i;
                int rz = rzStart + j;

                long regionHash = seed ^ (rx * 193487L) ^ (rz * 982134L);
                net.minecraft.util.RandomSource rand = net.minecraft.util.RandomSource.create(regionHash);

                if (rand.nextFloat() <= 0.20f) {
                    int indexStructure = (int) (Math.abs(regionHash) % BackroomsStructures.CATALOGUE_MEGASTRUCTURES.size());
                    BackroomsStructures.Megastructure megaCible = BackroomsStructures.CATALOGUE_MEGASTRUCTURES.get(indexStructure);

                    if (megaCible.getMorceau(0, 0, 0).id.getPath().contains(targetName)) {
                        int originX = rx * spacing + rand.nextInt(spacing - megaCible.tailleX);
                        int originZ = rz * spacing + rand.nextInt(spacing - megaCible.tailleZ);

                        double distance = Math.sqrt(Math.pow(startChunkX - originX, 2) + Math.pow(startChunkZ - originZ, 2));

                        if (distance < minDistance) {
                            minDistance = distance;
                            bestX = originX * 16;
                            bestZ = originZ * 16;
                            trouve = true;
                        }
                    }
                }
            }
        }
        return trouve ? new net.minecraft.core.BlockPos(bestX, 65, bestZ) : null;
    }
}