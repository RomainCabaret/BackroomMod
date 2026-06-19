package com.backrooms.command;

import com.backrooms.worldgen.structure.BackroomsStructures;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import java.util.Optional;

public class PlaceMegaCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("place_mega")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("nom", StringArgumentType.word())
                            .executes(PlaceMegaCommand::placerMegaStructureCible)));
        });
    }

    private static int placerMegaStructureCible(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            ServerLevel level = player.serverLevel();
            BlockPos startPos = player.blockPosition();
            String nomCherche = StringArgumentType.getString(context, "nom");

            // On cherche la mégastructure qui contient le nom tapé
            BackroomsStructures.Megastructure megaCible = null;
            for (BackroomsStructures.Megastructure m : BackroomsStructures.CATALOGUE_MEGASTRUCTURES) {
                if (m.getMorceau(0, 0, 0).id.getPath().contains(nomCherche)) {
                    megaCible = m;
                    break;
                }
            }

            if (megaCible == null) {
                player.sendSystemMessage(Component.literal("§c[Erreur] Structure '" + nomCherche + "' introuvable."));
                return 0;
            }

            var templateManager = level.getServer().getStructureManager();

            for (int ix = 0; ix < megaCible.tailleX; ix++) {
                for (int iy = 0; iy < megaCible.tailleY; iy++) {
                    for (int iz = 0; iz < megaCible.tailleZ; iz++) {
                        BackroomsStructures.ModuleInfo piece = megaCible.getMorceau(ix, iy, iz);
                        if (piece == null || piece.id == null) continue;

                        Optional<StructureTemplate> templateOpt = templateManager.get(piece.id);
                        if (templateOpt.isPresent()) {
                            int targetX = startPos.getX() + (ix * 16);
                            int targetY = startPos.getY() - ((megaCible.tailleY - 1 - iy) * 8);
                            int targetZ = startPos.getZ() + (iz * 16);

                            BlockPos targetPos = new BlockPos(targetX, targetY, targetZ);
                            templateOpt.get().placeInWorld(level, targetPos, targetPos, new StructurePlaceSettings(), level.getRandom(), 3);
                        }
                    }
                }
            }
            player.sendSystemMessage(Component.literal("§a[Debug] Structure '" + nomCherche + "' assemblée !"));
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErreur : " + e.getMessage()));
            return 0;
        }
    }
    // Calcule la coordonnée 3D exacte d'une tranche de mégastructure
    public static net.minecraft.core.BlockPos calculatePiecePosition(net.minecraft.core.BlockPos origin, int ix, int iy, int iz, int tailleY) {
        int targetX = origin.getX() + (ix * 16);
        int targetY = origin.getY() - ((tailleY - 1 - iy) * 8);
        int targetZ = origin.getZ() + (iz * 16);
        return new net.minecraft.core.BlockPos(targetX, targetY, targetZ);
    }
}