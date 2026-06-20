package com.backrooms.datagen;

import com.backrooms.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModEnglishLangProvider extends FabricLanguageProvider {
    public ModEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        // "en_us" est le code officiel de Minecraft pour l'anglais par défaut
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        builder.add(ModBlocks.MOQUETTE_HUMIDE, "Damp Carpet");
        builder.add(ModBlocks.PAPIER_PEINT, "Yellowed Wallpaper");
        builder.add(ModBlocks.PLAFOND_BUREAU, "Office Ceiling Tile");

        builder.add(ModBlocks.PLAFOND_LUMINEUX, "Office Ceiling Light");

        builder.add("itemGroup.backrooms", "The Backrooms");
    }
}