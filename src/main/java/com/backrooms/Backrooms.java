package com.backrooms;

import com.backrooms.registry.ModBlocks;
import com.backrooms.registry.ModCommands;
import com.backrooms.registry.ModItems;
import com.backrooms.registry.ModWorldGen;
import com.backrooms.worldgen.structure.BackroomsStructures;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Backrooms implements ModInitializer {
	public static final String MOD_ID = "backrooms";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initialisation du cauchemar géométrique...");

		ModBlocks.register();
		ModItems.register();
		ModCommands.register();
		ModWorldGen.register();

		net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTING.register(BackroomsStructures::chargerStructuresDynamiques);
	}
}