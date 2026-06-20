package com.backrooms.datagen;

import com.backrooms.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class ModFrenchLangProvider extends FabricLanguageProvider {
    public ModFrenchLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "fr_fr", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {
        builder.add(ModBlocks.MOQUETTE_HUMIDE, "Moquette Humide");
        builder.add(ModBlocks.PAPIER_PEINT, "Papier Peint Jauni");
        builder.add(ModBlocks.PLAFOND_BUREAU, "Dalle de Faux Plafond");

        builder.add(ModBlocks.PLAFOND_LUMINEUX, "Néon de Faux Plafond");

        // Tu peux aussi traduire tes commandes ou messages d'erreur ici
        builder.add("itemGroup.backrooms", "Les Backrooms");
    }
}