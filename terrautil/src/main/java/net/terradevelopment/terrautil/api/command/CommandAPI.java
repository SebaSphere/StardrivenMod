package net.terradevelopment.terrautil.api.command;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class CommandAPI {

    private static HashMap<Identifier, ArrayList<String>> commandEntryPoints
            = new HashMap<>();

    private void registerCommandEntryPoint(Identifier entryPoint, String... commandStarter) {
        if (!commandEntryPoints.containsKey(entryPoint)) {
            commandEntryPoints.put(entryPoint, new ArrayList<>(Arrays.asList(commandStarter)));
        } else {
            throw new IllegalArgumentException("Command entry point already registered");
        }
    }

    public CommandAPI init(Identifier identifier, String... commandStarter) {

        registerCommandEntryPoint(identifier, commandStarter);

        return new CommandAPI();

    }



}
