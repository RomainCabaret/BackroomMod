package com.backrooms.datagen;

import com.backrooms.registry.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        // Cette seule putain de ligne crée :
        // 1. Le fichier blockstates/moquette_humide.json
        // 2. Le fichier models/block/moquette_humide.json
        // 3. Le fichier models/item/moquette_humide.json
        blockStateModelGenerator.createTrivialCube(ModBlocks.MOQUETTE_HUMIDE);
        blockStateModelGenerator.createTrivialCube(ModBlocks.PAPIER_PEINT);
        blockStateModelGenerator.createTrivialCube(ModBlocks.ABYSSAL_PIT);

        // Même chose pour ton plafond. Le modèle restera un cube basique visuellement,
        // (les collisions custom gérées en Java fonctionneront toujours).
        blockStateModelGenerator.createTrivialCube(ModBlocks.PLAFOND_BUREAU);
        blockStateModelGenerator.createTrivialCube(ModBlocks.PLAFOND_LUMINEUX);
        blockStateModelGenerator.createTrivialCube(ModBlocks.PLAFOND_LUMINEUX_ROUGE);

    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        // Laisse vide pour l'instant, les items de tes blocs sont déjà gérés au-dessus.
        // On l'utilisera quand tu créeras des objets simples (comme une bouteille d'eau d'amande).
    }
}