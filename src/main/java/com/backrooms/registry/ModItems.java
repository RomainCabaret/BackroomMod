package com.backrooms.registry;

import com.backrooms.Backrooms;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {

    public static void register() {
        registerBlockItem("moquette_humide", ModBlocks.MOQUETTE_HUMIDE);
        registerBlockItem("papier_peint", ModBlocks.PAPIER_PEINT);
        registerBlockItem("plafond_bureau", ModBlocks.PLAFOND_BUREAU);
        registerBlockItem("plafond_lumineux", ModBlocks.PLAFOND_LUMINEUX);
        registerBlockItem("abyssal_pit", ModBlocks.ABYSSAL_PIT);
        registerBlockItem("plafond_lumineux_rouge", ModBlocks.PLAFOND_LUMINEUX_ROUGE);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(content -> {
            content.accept(ModBlocks.MOQUETTE_HUMIDE);
            content.accept(ModBlocks.PAPIER_PEINT);
            content.accept(ModBlocks.PLAFOND_BUREAU);
            content.accept(ModBlocks.PLAFOND_LUMINEUX);
            content.accept(ModBlocks.ABYSSAL_PIT);
            content.accept(ModBlocks.PLAFOND_LUMINEUX_ROUGE);
        });
    }

    private static void registerBlockItem(String name, net.minecraft.world.level.block.Block block) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Backrooms.MOD_ID, name), new BlockItem(block, new Item.Properties()));
    }
}