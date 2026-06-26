package com.backrooms.registry;

import com.backrooms.Backrooms;
import com.backrooms.block.AbyssalPitBlock;
import com.backrooms.block.PlafondBureauBlock;
import com.backrooms.block.PlafondLumineuxBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static final Block MOQUETTE_HUMIDE = new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL));
    public static final Block PAPIER_PEINT = new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));

    public static final Block PLAFOND_BUREAU = new PlafondBureauBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE));

    public static final Block ABYSSAL_PIT = new AbyssalPitBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK).noCollission().noOcclusion());

    public static final Block PLAFOND_LUMINEUX = new PlafondLumineuxBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).lightLevel(state -> 15)
    );
    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, "moquette_humide"), MOQUETTE_HUMIDE);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, "papier_peint"), PAPIER_PEINT);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, "plafond_bureau"), PLAFOND_BUREAU);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, "plafond_lumineux"), PLAFOND_LUMINEUX);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, "abyssal_pit"), ABYSSAL_PIT);
    }
}