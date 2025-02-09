package dev.sebastianb.stardriven.command.ship.admin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.sebastianb.stardriven.command.ICommand;
import net.minecraft.server.command.ServerCommandSource;

public class MoveShipCommand implements ICommand {
    @Override
    public String commandName() {
        return "move";
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> registerNode() {
        return LiteralArgumentBuilder.<ServerCommandSource>literal(commandName())
                .requires(source -> source.hasPermissionLevel(2))
                .executes(this::execute);
    }

    private int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();


        return 1;
    }

}
