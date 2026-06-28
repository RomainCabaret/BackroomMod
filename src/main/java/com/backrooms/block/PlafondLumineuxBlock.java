package com.backrooms.block;

import com.backrooms.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlafondLumineuxBlock extends Block {

    /**
     * Probabilité de déclenchement du son (1 chance sur X).
     * animateTick étant appelé aléatoirement par le client, un HUM_CHANCE de 1000
     * garantit que le son reste rare à l'échelle d'un seul bloc, mais crée une
     * ambiance de grésillement sporadique quand il y a beaucoup de néons autour.
     */
    private static final int HUM_CHANCE = 1000;

    // Volume forcé pour compenser un fichier .ogg mixé très bas
    private static final float HUM_VOLUME = 4.0F;

    // Le pitch de base du son (0.8 = un peu ralenti/grave)
    private static final float MIN_PITCH = 0.8F;

    // La variation maximale à ajouter au pitch de base (crée l'instabilité)
    private static final float PITCH_VARIANCE = 0.2F;

    // On met en cache la forme pour ne pas cramer le CPU
    private static final VoxelShape SHAPE;

    static {
        VoxelShape basePleine = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        VoxelShape zoneAEnlever = Shapes.box(1.0 / 16.0, 0.0, 1.0 / 16.0, 15.0 / 16.0, 1.0 / 16.0, 15.0 / 16.0);
        SHAPE = Shapes.join(basePleine, zoneAEnlever, BooleanOp.ONLY_FIRST);
    }

    public PlafondLumineuxBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE; // Renvoie la forme statique instantanément
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (random.nextInt(HUM_CHANCE) == 0) {
            world.playLocalSound(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    ModSounds.NEON_HUM,
                    SoundSource.BLOCKS,
                    HUM_VOLUME,
                    MIN_PITCH + random.nextFloat() * PITCH_VARIANCE,
                    false
            );
        }
    }
}