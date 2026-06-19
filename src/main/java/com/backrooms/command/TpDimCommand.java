package com.backrooms.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class TpDimCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("tpdim")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.argument("dimension", DimensionArgument.dimension())
                            .executes(TpDimCommand::teleporter)));
        });
    }

    private static int teleporter(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            ServerLevel targetLevel = DimensionArgument.getDimension(context, "dimension");

            if (player.level() == targetLevel) {
                player.sendSystemMessage(Component.literal("§cT'es déjà dans cette dimension."));
                return 0;
            }

            // On te tp à la même position X et Z, mais on fixe Y à 64 pour que tu ne tombes pas dans le vide
// Au lieu de garder le Y actuel du joueur, on l'écrase :
            double targetY = 72.0; // Niveau du sol standard dans ta dimension
            player.teleportTo(targetLevel, 6, targetY, 40, player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.literal("§a[Backrooms] Fwooosh. Bienvenue dans " + targetLevel.dimension().location()));

            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("§cErreur: " + e.getMessage()));
            return 0;
        }
    }
}