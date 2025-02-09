package dev.sebastianb.stardriven.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sebastianb.stardriven.command.ship.BaseShipCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.terradevelopment.terrautil.TerraUtil;

import java.util.ArrayList;

public class StardrivenCommands {

    private static final ArrayList<ICommand> commands = new ArrayList<>();

    private static final String[] commandLiterals = new String[]{"stardriven", "s", "sd"};

    public static void register() {

        commands.add(new BaseShipCommand());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            for (ICommand command : commands) {
                for (String literal : commandLiterals) {
                    LiteralArgumentBuilder<ServerCommandSource> builder =
                            CommandManager.literal(literal)
                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                    .then(command.registerNode());
                    dispatcher.register(builder);
                }
            }
        });

    }

    protected static ArrayList<ICommand> getCommands() {
        return commands;
    }

}
