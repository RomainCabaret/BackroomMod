package com.backrooms;

import com.backrooms.registry.*;
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
		ModSounds.register();

		net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTING.register(BackroomsStructures::chargerStructuresDynamiques);
	}
}