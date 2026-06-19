package com.backrooms.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BackroomsChunkGenerator extends ChunkGenerator {

    public static final MapCodec<BackroomsChunkGenerator> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(BackroomsChunkGenerator::getBiomeSource)
                    ).apply(instance, BackroomsChunkGenerator::new)
            );

    public BackroomsChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource, biome -> BiomeGenerationSettings.EMPTY);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager manager, RandomState randomState, ChunkAccess chunk) {
        ChunkPos pos = chunk.getPos();
        long seed = region.getSeed();

        long chunkHash = seed ^ (pos.x * 341873128712L) ^ (pos.z * 132897987541L);
        net.minecraft.util.RandomSource chunkRand = net.minecraft.util.RandomSource.create(chunkHash);

        // 1. Priorité Mégastructure (Court-circuit)
        MegaManager.MegaHit megaHit = MegaManager.check(pos.x, pos.z, seed);
        if (StructurePlacer.tryPlaceMega(megaHit, pos, region, chunkRand)) return;

        // 2. Calcul des bordures via le Router
        String reqN = WallRouter.getEdge(pos.x, pos.z, pos.x, pos.z - 1, seed);
        String reqS = WallRouter.getEdge(pos.x, pos.z, pos.x, pos.z + 1, seed);
        String reqE = WallRouter.getEdge(pos.x, pos.z, pos.x + 1, pos.z, seed);
        String reqO = WallRouter.getEdge(pos.x, pos.z, pos.x - 1, pos.z, seed);

        // 3. Pose de la pièce
        StructurePlacer.placeRoom(reqN, reqS, reqE, reqO, pos, region, chunkRand);
    }

    // --- Méthodes obligatoires de l'API ChunkGenerator ---
    @Override public void applyCarvers(WorldGenRegion r, long s, RandomState rs, BiomeManager bm, StructureManager sm, ChunkAccess c, GenerationStep.Carving cv) {}
    @Override public void spawnOriginalMobs(WorldGenRegion region) {}
    @Override public CompletableFuture<ChunkAccess> fillFromNoise(Blender b, RandomState rs, StructureManager sm, ChunkAccess c) { return CompletableFuture.completedFuture(c); }
    @Override public int getGenDepth() { return 256; }
    @Override public int getSeaLevel() { return 63; }
    @Override public int getMinY() { return 0; }
    @Override public int getBaseHeight(int x, int z, Heightmap.Types t, LevelHeightAccessor w, RandomState rs) { return 64; }
    @Override public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor w, RandomState rs) { return new NoiseColumn(0, new net.minecraft.world.level.block.state.BlockState[0]); }
    @Override public void addDebugScreenInfo(List<String> list, RandomState rs, BlockPos pos) { list.add("[Backrooms] Architecture Modulaire Active"); }
}