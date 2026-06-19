package com.backrooms.command;

import com.backrooms.Backrooms;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SliceCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("slice_room")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("nom_salle", StringArgumentType.string())
                            // Cas 1 : Juste le nom (1x1x1 par défaut)
                            .executes(context -> executerDecoupage(context, 1, 1, 1))
                            // Cas 2 : Nom + Dimensions X Y Z
                            .then(Commands.argument("tailleX", IntegerArgumentType.integer(1, 16))
                                    .then(Commands.argument("tailleY", IntegerArgumentType.integer(1, 16))
                                            .then(Commands.argument("tailleZ", IntegerArgumentType.integer(1, 16))
                                                    .executes(context -> executerDecoupage(
                                                            context,
                                                            IntegerArgumentType.getInteger(context, "tailleX"),
                                                            IntegerArgumentType.getInteger(context, "tailleY"),
                                                            IntegerArgumentType.getInteger(context, "tailleZ")
                                                    ))
                                            )
                                    )
                            )
                    ));
        });
    }

    private static int executerDecoupage(CommandContext<CommandSourceStack> context, int tailleX, int tailleY, int tailleZ) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            String nom = StringArgumentType.getString(context, "nom_salle");
            ServerLevel level = (ServerLevel) player.level();

            ChunkPos chunkOrigine = player.chunkPosition();
            // L'origine Y arrondie au multiple de 8 inférieur
            int origineY = ((int) Math.floor(player.getY() / 8.0)) * 8;

            int morceauxSauves = 0;

            // On boucle sur toutes les tranches demandées
            for (int ix = 0; ix < tailleX; ix++) {
                for (int iy = 0; iy < tailleY; iy++) {
                    for (int iz = 0; iz < tailleZ; iz++) {
                        int chunkX = chunkOrigine.x + ix;
                        int chunkZ = chunkOrigine.z + iz;
                        int startY = origineY + (iy * 8);

                        decouperUnMorceau(level, nom, chunkX, startY, chunkZ, ix, iy, iz);
                        morceauxSauves++;
                    }
                }
            }

            player.sendSystemMessage(Component.literal("§a[Backrooms] Mégastructure capturée : " + nom + " (" + morceauxSauves + " morceaux)"));
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErreur lors du découpage : " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }

    private static void decouperUnMorceau(ServerLevel level, String nom, int chunkX, int startY, int chunkZ, int ix, int iy, int iz) {
        int minX = chunkX * 16;
        int minZ = chunkZ * 16;
        BlockPos pointDeDepart = new BlockPos(minX, startY, minZ);

        List<String> listN = new ArrayList<>();
        List<String> listS = new ArrayList<>();
        List<String> listO = new ArrayList<>();
        List<String> listE = new ArrayList<>();

        boolean openN = false, openS = false, openO = false, openE = false;

        // Scan des bordures
        for (int h = 0; h < 8; h++) {
            for (int i = 0; i < 16; i++) {
                // Nord (Z min)
                BlockPos posN = pointDeDepart.offset(i, h, 0);
                if (level.getBlockState(posN).is(Blocks.GLASS)) openN = true;
                else if (level.getBlockState(posN).is(Blocks.BEDROCK)) {
                    listN.add(i + "x" + h);
                    level.setBlock(posN, Blocks.AIR.defaultBlockState(), 3);
                }

                // Sud (Z max)
                BlockPos posS = pointDeDepart.offset(i, h, 15);
                if (level.getBlockState(posS).is(Blocks.GLASS)) openS = true;
                else if (level.getBlockState(posS).is(Blocks.BEDROCK)) {
                    listS.add(i + "x" + h);
                    level.setBlock(posS, Blocks.AIR.defaultBlockState(), 3);
                }

                // Ouest (X min)
                BlockPos posO = pointDeDepart.offset(0, h, i);
                if (level.getBlockState(posO).is(Blocks.GLASS)) openO = true;
                else if (level.getBlockState(posO).is(Blocks.BEDROCK)) {
                    listO.add(i + "x" + h);
                    level.setBlock(posO, Blocks.AIR.defaultBlockState(), 3);
                }

                // Est (X max)
                BlockPos posE = pointDeDepart.offset(15, h, i);
                if (level.getBlockState(posE).is(Blocks.GLASS)) openE = true;
                else if (level.getBlockState(posE).is(Blocks.BEDROCK)) {
                    listE.add(i + "x" + h);
                    level.setBlock(posE, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        // Détermination de la signature
        String sigN = openN ? "open" : (listN.isEmpty() ? "0" : String.join("-", listN));
        String sigS = openS ? "open" : (listS.isEmpty() ? "0" : String.join("-", listS));
        String sigO = openO ? "open" : (listO.isEmpty() ? "0" : String.join("-", listO));
        String sigE = openE ? "open" : (listE.isEmpty() ? "0" : String.join("-", listE));

        // Nettoyage du verre (optionnel, pour ne pas l'avoir dans la structure finale)
        for (int h = 0; h < 8; h++) {
            for (int i = 0; i < 16; i++) {
                BlockPos posN = pointDeDepart.offset(i, h, 0);
                if (level.getBlockState(posN).is(Blocks.GLASS)) level.setBlock(posN, Blocks.AIR.defaultBlockState(), 3);
                BlockPos posS = pointDeDepart.offset(i, h, 15);
                if (level.getBlockState(posS).is(Blocks.GLASS)) level.setBlock(posS, Blocks.AIR.defaultBlockState(), 3);
                BlockPos posO = pointDeDepart.offset(0, h, i);
                if (level.getBlockState(posO).is(Blocks.GLASS)) level.setBlock(posO, Blocks.AIR.defaultBlockState(), 3);
                BlockPos posE = pointDeDepart.offset(15, h, i);
                if (level.getBlockState(posE).is(Blocks.GLASS)) level.setBlock(posE, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        StructureTemplate template = new StructureTemplate();
        BlockPos tailleDuMorceau = new BlockPos(16, 8, 16);
        template.fillFromWorld(level, pointDeDepart, tailleDuMorceau, true, Blocks.STRUCTURE_VOID);

        String nomFichier = nom.toLowerCase() + "_" + ix + "_" + iy + "_" + iz
                + "_n_" + sigN.toLowerCase()
                + "_s_" + sigS.toLowerCase()
                + "_e_" + sigE.toLowerCase()
                + "_o_" + sigO.toLowerCase();

        try {
            Path dossierStructures = level.getServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("generated/backrooms/structures");
            java.io.File structureFile = dossierStructures.resolve(nomFichier + ".nbt").toFile();
            structureFile.getParentFile().mkdirs();

            net.minecraft.nbt.CompoundTag nbtData = template.save(new net.minecraft.nbt.CompoundTag());
            net.minecraft.nbt.NbtIo.writeCompressed(nbtData, structureFile.toPath());
        } catch (Exception e) {
            Backrooms.LOGGER.error("Erreur de sauvegarde pour " + nomFichier, e);
        }
    }
    // Transforme la détection des blocs en signature NBT propre
    public static String calculateSignature(boolean hasGlass, java.util.List<String> bedrockHits) {
        if (hasGlass) return "open";
        if (bedrockHits.isEmpty()) return "0";
        return String.join("-", bedrockHits);
    }
}