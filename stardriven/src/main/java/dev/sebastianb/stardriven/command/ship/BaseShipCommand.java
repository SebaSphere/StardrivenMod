package dev.sebastianb.stardriven.command.ship;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.sebastianb.stardriven.command.ICommand;
import dev.sebastianb.stardriven.command.ship.admin.CreateShipCommand;
import dev.sebastianb.stardriven.command.ship.admin.MoveShipCommand;
import dev.sebastianb.stardriven.command.ship.admin.ShipListCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class BaseShipCommand implements ICommand {

    private final CreateShipCommand createShipCommand;
    private final MoveShipCommand moveShipCommand;
    private final ShipListCommand shipListCommand;

    public BaseShipCommand() {
        this.createShipCommand = new CreateShipCommand();
        this.moveShipCommand = new MoveShipCommand();
        this.shipListCommand = new ShipListCommand();
    }

    @Override
    public String commandName() {
        return "ship";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {

        return CommandManager.literal(commandName())
                .then(createShipCommand.registerNode())
                .then(moveShipCommand.registerNode())
                .then(shipListCommand.registerNode())
                .executes(BaseShipCommand::execute);
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        // TODO: make a default help thingy
        return Command.SINGLE_SUCCESS;
    }

}
