package com.backrooms.worldgen;

import com.backrooms.worldgen.structure.BackroomsStructures;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BackroomsDebugGenerator extends ChunkGenerator {

    public static final MapCodec<BackroomsDebugGenerator> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source")
                                    .forGetter(BackroomsDebugGenerator::getBiomeSource)
                    ).apply(instance, BackroomsDebugGenerator::new)
            );

    public BackroomsDebugGenerator(BiomeSource biomeSource) {
        super(biomeSource, biome -> BiomeGenerationSettings.EMPTY);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess chunkAccess) {
        ChunkPos pos = chunkAccess.getPos();
        var templateManager = region.getLevel().getServer().getStructureManager();

        // 1. On ne garde que les originaux (pas de clones tournés)
        List<BackroomsStructures.ModuleInfo> piecesOriginales = BackroomsStructures.CATALOGUE_NIVEAU_0.stream()
                .filter(m -> m.rotation == net.minecraft.world.level.block.Rotation.NONE)
                .toList();

        // 2. On trie : Couloirs vs Salles
        List<BackroomsStructures.ModuleInfo> couloirs = piecesOriginales.stream()
                .filter(m -> m.nomDeBase.contains("couloir"))
                .toList();
        List<BackroomsStructures.ModuleInfo> salles = piecesOriginales.stream()
                .filter(m -> !m.nomDeBase.contains("couloir"))
                .toList();

        // 3. Espacement sur l'axe X : Un chunk plein, un chunk vide (X pair uniquement et positif)
        if (pos.x < 0 || pos.x % 2 != 0) return;
        int index = pos.x / 2;

        BackroomsStructures.ModuleInfo pieceChoisie = null;

        // 4. Placement sur l'axe Z : Couloirs en Z=0, Salles en Z=2 (chunk vide au milieu)
        if (pos.z == 0 && index < couloirs.size()) {
            pieceChoisie = couloirs.get(index);
        } else if (pos.z == 2 && index < salles.size()) {
            pieceChoisie = salles.get(index);
        } else {
            return; // Chunk vide
        }

        // 5. On génère
        Optional<StructureTemplate> templateOpt = templateManager.get(pieceChoisie.id);
        if (templateOpt.isPresent()) {
            BlockPos targetPos = new BlockPos(pos.getMinBlockX(), 64, pos.getMinBlockZ());
            StructurePlaceSettings settings = new StructurePlaceSettings();

            settings.setRotation(pieceChoisie.rotation);
            // Inutile ici vu qu'on a filtré sur NONE, mais on le laisse si tu changes d'avis un jour
            targetPos = switch (pieceChoisie.rotation) {
                case NONE -> targetPos;
                case CLOCKWISE_90 -> targetPos.offset(15, 0, 0);
                case CLOCKWISE_180 -> targetPos.offset(15, 0, 15);
                case COUNTERCLOCKWISE_90 -> targetPos.offset(0, 0, 15);
            };

            templateOpt.get().placeInWorld(region, targetPos, targetPos, settings, net.minecraft.util.RandomSource.create(), 3);

            BlockPos signPos = targetPos.offset(8, 2, 8);

            // On passe le flag à 2 (UPDATE_CLIENTS en moins) pour calmer le jeu
            region.setBlock(signPos, net.minecraft.world.level.block.Blocks.OAK_SIGN.defaultBlockState(), 2);

            if (region.getBlockEntity(signPos) instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign) {
                net.minecraft.world.level.block.entity.SignText frontText = sign.getFrontText()
                        .setMessage(1, net.minecraft.network.chat.Component.literal("§lPièce :"))
                        .setMessage(2, net.minecraft.network.chat.Component.literal("§l" + pieceChoisie.nomDeBase));

                try {
                    sign.setText(frontText, true);
                } catch (NullPointerException e) {
                    // Tais-toi. Le texte est bien enregistré dans le chunk.
                    // On ignore juste la tentative pathétique du jeu d'envoyer un paquet réseau dans le vide.
                }
            }
        }
    }

    // --- Les méthodes obligatoires pour que ça compile ---
    @Override public void applyCarvers(WorldGenRegion r, long s, RandomState rs, BiomeManager bm, StructureManager sm, ChunkAccess c, GenerationStep.Carving cv) {}
    @Override public void spawnOriginalMobs(WorldGenRegion region) {}
    @Override public CompletableFuture<ChunkAccess> fillFromNoise(Blender b, RandomState rs, StructureManager sm, ChunkAccess c) { return CompletableFuture.completedFuture(c); }
    @Override public int getGenDepth() { return 256; }
    @Override public int getSeaLevel() { return 63; }
    @Override public int getMinY() { return 0; }
    @Override public int getBaseHeight(int x, int z, Heightmap.Types t, LevelHeightAccessor w, RandomState rs) { return 64; }
    @Override public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor w, RandomState rs) { return new NoiseColumn(0, new net.minecraft.world.level.block.state.BlockState[0]); }
    @Override public void addDebugScreenInfo(List<String> list, RandomState rs, BlockPos pos) { list.add("[Backrooms] Mode Debug Actif"); }
}