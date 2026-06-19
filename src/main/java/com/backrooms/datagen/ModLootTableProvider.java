package com.backrooms.datagen;

import com.backrooms.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // Une seule ligne par bloc pour garantir qu'il se drop lui-même
        dropSelf(ModBlocks.MOQUETTE_HUMIDE);
        dropSelf(ModBlocks.PAPIER_PEINT);
        dropSelf(ModBlocks.PLAFOND_BUREAU);
    }
}