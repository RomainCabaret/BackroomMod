package com.backrooms;

import com.backrooms.datagen.ModBlockTagProvider;
import com.backrooms.datagen.ModEnglishLangProvider;
import com.backrooms.datagen.ModFrenchLangProvider;
import com.backrooms.datagen.ModLootTableProvider;
import com.backrooms.datagen.ModModelProvider;
import com.backrooms.datagen.ModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BackroomsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModLootTableProvider::new);
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModRecipeProvider::new);

        // On génère les deux langues
        pack.addProvider(ModFrenchLangProvider::new);
        pack.addProvider(ModEnglishLangProvider::new);
    }
}