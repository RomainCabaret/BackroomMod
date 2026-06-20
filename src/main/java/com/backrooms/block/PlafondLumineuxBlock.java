package com.backrooms.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlafondLumineuxBlock extends Block {

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
}