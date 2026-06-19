package com.backrooms.worldgen;

import com.backrooms.Backrooms;
import com.backrooms.worldgen.structure.BackroomsStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.Optional;

public class StructurePlacer {

    public static boolean tryPlaceMega(MegaManager.MegaHit hit, ChunkPos pos, WorldGenRegion region, net.minecraft.util.RandomSource chunkRand) {
        if (hit == null) return false;

        // C'est LUI le vrai manager pour tes NBT
        StructureTemplateManager templateManager = region.getLevel().getServer().getStructureManager();

        for (int iy = 0; iy < hit.mega().tailleY; iy++) {
            BackroomsStructures.ModuleInfo piece = hit.mega().getMorceau(hit.ix(), iy, hit.iz());
            if (piece == null || piece.id == null) continue;

            Optional<StructureTemplate> templateOpt = templateManager.get(piece.id);
            if (templateOpt.isPresent()) {
                int targetY = 64 - ((hit.mega().tailleY - 1 - iy) * 8);
                BlockPos targetPos = new BlockPos(pos.getMinBlockX(), targetY, pos.getMinBlockZ());

                StructurePlaceSettings settings = new StructurePlaceSettings();
                settings.setRotation(piece.rotation);
                targetPos = switch (piece.rotation) {
                    case NONE -> targetPos;
                    case CLOCKWISE_90 -> targetPos.offset(15, 0, 0);
                    case CLOCKWISE_180 -> targetPos.offset(15, 0, 15);
                    case COUNTERCLOCKWISE_90 -> targetPos.offset(0, 0, 15);
                };

                templateOpt.get().placeInWorld(region, targetPos, targetPos, settings, chunkRand, 3);
            }
        }
        return true;
    }

    public static void placeRoom(String reqN, String reqS, String reqE, String reqO, ChunkPos pos, WorldGenRegion region, net.minecraft.util.RandomSource chunkRand) {
        List<BackroomsStructures.ModuleInfo> compatibles = BackroomsStructures.trouverPiecesCompatibles(reqN, reqS, reqE, reqO);

        if (compatibles.isEmpty()) {
            Backrooms.LOGGER.error("[Backrooms-Gen] 💥 Pièce manquante pour N:{} S:{} E:{} O:{}", reqN, reqS, reqE, reqO);
            return;
        }

        StructureTemplateManager templateManager = region.getLevel().getServer().getStructureManager();
        BackroomsStructures.ModuleInfo pieceChoisie = compatibles.get(chunkRand.nextInt(compatibles.size()));
        Optional<StructureTemplate> templateOpt = templateManager.get(pieceChoisie.id);

        if (templateOpt.isPresent()) {
            BlockPos targetPos = new BlockPos(pos.getMinBlockX(), 64, pos.getMinBlockZ());
            StructurePlaceSettings settings = new StructurePlaceSettings();

            settings.setRotation(pieceChoisie.rotation);
            targetPos = switch (pieceChoisie.rotation) {
                case NONE -> targetPos;
                case CLOCKWISE_90 -> targetPos.offset(15, 0, 0);
                case CLOCKWISE_180 -> targetPos.offset(15, 0, 15);
                case COUNTERCLOCKWISE_90 -> targetPos.offset(0, 0, 15);
            };

            templateOpt.get().placeInWorld(region, targetPos, targetPos, settings, chunkRand, 3);
        }
    }
}