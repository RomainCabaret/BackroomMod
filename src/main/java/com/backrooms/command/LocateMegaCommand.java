package com.backrooms.command;

import com.backrooms.worldgen.structure.BackroomsStructures;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public class LocateMegaCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("locate_mega")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("nom", StringArgumentType.word())
                            .executes(LocateMegaCommand::trouverMegaStructureCible)));
        });
    }

    private static int trouverMegaStructureCible(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            long seed = player.serverLevel().getSeed();
            ChunkPos chunkOrigine = player.chunkPosition();
            String nomCherche = StringArgumentType.getString(context, "nom");

            if (BackroomsStructures.CATALOGUE_MEGASTRUCTURES.isEmpty()) {
                player.sendSystemMessage(Component.literal("§c[Radar] Le catalogue de mégastructures est vide !"));
                return 0;
            }

            int spacing = 32;
            int rxJoueur = Math.floorDiv(chunkOrigine.x, spacing);
            int rzJoueur = Math.floorDiv(chunkOrigine.z, spacing);

            int rayonRecherche = 5;
            int bestX = 0, bestZ = 0;
            double minDistance = Double.MAX_VALUE;
            boolean trouve = false;

            for (int i = -rayonRecherche; i <= rayonRecherche; i++) {
                for (int j = -rayonRecherche; j <= rayonRecherche; j++) {
                    int rx = rxJoueur + i;
                    int rz = rzJoueur + j;

                    long regionHash = seed ^ (rx * 193487L) ^ (rz * 982134L);
                    net.minecraft.util.RandomSource rand = net.minecraft.util.RandomSource.create(regionHash);

                    if (rand.nextFloat() <= 0.20f) {
                        int indexStructure = (int) (Math.abs(regionHash) % BackroomsStructures.CATALOGUE_MEGASTRUCTURES.size());
                        BackroomsStructures.Megastructure megaCible = BackroomsStructures.CATALOGUE_MEGASTRUCTURES.get(indexStructure);

                        if (megaCible.getMorceau(0, 0, 0).id.getPath().contains(nomCherche)) {
                            int originX = rx * spacing + rand.nextInt(spacing - megaCible.tailleX);
                            int originZ = rz * spacing + rand.nextInt(spacing - megaCible.tailleZ);

                            double distance = Math.sqrt(Math.pow(chunkOrigine.x - originX, 2) + Math.pow(chunkOrigine.z - originZ, 2));

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

            if (trouve) {
                int finalBestX = bestX;
                int finalBestZ = bestZ;
                net.minecraft.network.chat.Component msg = net.minecraft.network.chat.Component.literal("§a[Radar] Mégastructure '" + nomCherche + "' : X=" + bestX + " Z=" + bestZ + ". §e§l[CLIQUE POUR TP]")
                        .withStyle(style -> style
                                .withClickEvent(new net.minecraft.network.chat.ClickEvent(net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND, "/tp @s " + finalBestX + " 65 " + finalBestZ))
                                .withHoverEvent(new net.minecraft.network.chat.HoverEvent(net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT, net.minecraft.network.chat.Component.literal("Se téléporter au sol")))
                        );
                player.sendSystemMessage(msg);
            } else {
                player.sendSystemMessage(Component.literal("§c[Radar] Structure '" + nomCherche + "' introuvable dans le secteur. Marche un peu et relance."));
            }

            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErreur radar : " + e.getMessage()));
            return 0;
        }
    }
}