package com.backrooms.registry;

import com.backrooms.command.LocateMegaCommand;
import com.backrooms.command.MapCommand;
import com.backrooms.command.PlaceMegaCommand;
import com.backrooms.command.SliceCommand;
import com.backrooms.command.TpDimCommand;

public class ModCommands {
    public static void register() {
        SliceCommand.register();
        TpDimCommand.register();
        PlaceMegaCommand.register();
        MapCommand.register();
        LocateMegaCommand.register();
    }
}