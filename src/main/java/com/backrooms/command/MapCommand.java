package com.backrooms.command;

import com.backrooms.worldgen.LayoutProvider;
import com.backrooms.worldgen.MegaManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class MapCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("map_backrooms")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("rayon", IntegerArgumentType.integer(10, 1000))
                            .executes(MapCommand::genererCarte)));
        });
    }

    private static int genererCarte(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            long seed = player.serverLevel().getSeed();
            ChunkPos center = player.chunkPosition();
            int radius = IntegerArgumentType.getInteger(context, "rayon");

            int size = radius * 2 + 1;
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int chunkX = center.x + x;
                    int chunkZ = center.z + z;

                    Color color = Color.BLACK;

                    // Les appels utilisent maintenant tes nouvelles classes
                    if (MegaManager.check(chunkX, chunkZ, seed) != null) {
                        color = Color.RED;
                    } else if (LayoutProvider.getZone(chunkX, chunkZ, seed) == LayoutProvider.Zone.OPEN) {
                        color = Color.WHITE;
                    }

                    if (x == 0 && z == 0) {
                        color = Color.CYAN;
                    }

                    image.setRGB(x + radius, z + radius, color.getRGB());
                }
            }

            File outputfile = new File("backrooms_map.png");
            ImageIO.write(image, "png", outputfile);

            player.sendSystemMessage(Component.literal("§a[Cartographe] Carte générée dans : " + outputfile.getAbsolutePath()));
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErreur : " + e.getMessage()));
            return 0;
        }
    }
    public static int getPixelColor(int chunkX, int chunkZ, long seed) {
        if (com.backrooms.worldgen.MegaManager.check(chunkX, chunkZ, seed) != null) return 0xFF0000; // RED
        if (com.backrooms.worldgen.LayoutProvider.getZone(chunkX, chunkZ, seed) == com.backrooms.worldgen.LayoutProvider.Zone.OPEN) return 0xFFFFFF; // WHITE
        return 0x000000; // BLACK
    }
}