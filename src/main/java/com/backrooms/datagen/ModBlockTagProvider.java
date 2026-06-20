package com.backrooms.datagen;

import com.backrooms.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        // On assigne le bon outil de prédilection à chaque matériau
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PLAFOND_BUREAU);
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.PAPIER_PEINT);
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_HOE).add(ModBlocks.MOQUETTE_HUMIDE);

        // On exige un palier minimum : pioche en pierre obligatoire pour récolter le plafond
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.PLAFOND_BUREAU)
                .add(ModBlocks.PLAFOND_LUMINEUX);
    }
}