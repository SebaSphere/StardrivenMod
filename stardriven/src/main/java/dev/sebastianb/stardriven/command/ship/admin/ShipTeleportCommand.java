package dev.sebastianb.stardriven.command.ship.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.sebastianb.stardriven.command.ICommand;
import net.minecraft.server.command.ServerCommandSource;

public class ShipTeleportCommand implements ICommand {
    @Override
    public String commandName() {
        return "teleport";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
        return null;
    }
}
