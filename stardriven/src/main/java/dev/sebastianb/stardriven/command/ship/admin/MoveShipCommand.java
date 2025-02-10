package dev.sebastianb.stardriven.command.ship.admin;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import dev.sebastianb.stardriven.command.ICommand;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.UUID;

public class MoveShipCommand implements ICommand {
    @Override
    public String commandName() {
        return "move";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {

        return LiteralArgumentBuilder.<ServerCommandSource>literal(commandName())
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("ship_uuid", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            return CommandSource.suggestMatching(
                                    Stardriven.API.getDimensionalShipManager()
                                            .getAllDimensionalShips().descendingMap().entrySet().stream().map(ship -> ship.getValue().getShipUUID().toString()), builder);
                        })
                        .then(CommandManager.argument("x", DoubleArgumentType.doubleArg())
                                .then(CommandManager.argument("y", DoubleArgumentType.doubleArg())
                                        .then(CommandManager.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(this::execute)))));
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        DimensionalShipManager dimensionalShipManager = Stardriven.API.getDimensionalShipManager();

        UUID shipUUID = UUID.fromString(StringArgumentType.getString(context, "ship_uuid"));
        double x = DoubleArgumentType.getDouble(context, "x");
        double y = DoubleArgumentType.getDouble(context, "y");
        double z = DoubleArgumentType.getDouble(context, "z");

        DimensionalShip ship = dimensionalShipManager.getDimensionalShip(shipUUID);

        if (ship != null) {
            ship.setLogicalWorld(source.getWorld());
            ship.setDimensionShipPosition(new DimensionalStarPosition(x, y, z));
            // TODO: Update ship position
            source.sendMessage(Text.literal("Ship of UUID " + shipUUID + " moved to " + x + ", " + y + ", " + z));
        } else {
            source.sendMessage(Text.literal("Ship not found"));
            return 0;
        }

        return 1;
    }

}
