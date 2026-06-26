package com.backrooms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class AbyssalPitBlock extends Block {

    public AbyssalPitBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // Le bloc n'a pas de hitbox, le joueur passe au travers
    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        return false;
    }

    // Quand le joueur tombe dedans...
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide()) return; // On ne gère la mort que sur le serveur

        // Si c'est un joueur, on vérifie s'il triche
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            if (player.isCreative() || player.isSpectator()) {
                return; // On ne touche pas aux dieux de la matrice.
            }
        }

        // Pour tout le reste (joueurs en survie, monstres, etc.)
        if (entity instanceof LivingEntity living) {
            // Obscurité pour l'effet dramatique (optionnel si la mort est immédiate, mais stylé)
            living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));

            // Dégâts infinis liés au vide.
            // C'est beaucoup plus propre que living.kill() car ça affiche le bon message de mort dans le chat ("A disparu du monde").
            living.hurt(level.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
        }
    }
}