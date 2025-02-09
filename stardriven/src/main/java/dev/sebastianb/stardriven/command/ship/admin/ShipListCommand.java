package dev.sebastianb.stardriven.command.ship.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.command.ICommand;
import net.minecraft.command.ControlFlowAware;
import net.minecraft.server.command.ServerCommandSource;

public class ShipListCommand implements ICommand {
    @Override
    public String commandName() {
        return "list";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
        return LiteralArgumentBuilder.<ServerCommandSource>literal(commandName())
                .requires(source -> source.hasPermissionLevel(2))
                .executes(this::execute);
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        DimensionalShipManager dimensionalShipManager = Stardriven.API.getDimensionalShipManager();
        for (DimensionalShip dimensionalShip : dimensionalShipManager.getAllDimensionalShips()) {
            System.out.println(dimensionalShip.getShipUUID());
        }

        return ControlFlowAware.Command.SINGLE_SUCCESS;
    }
}
