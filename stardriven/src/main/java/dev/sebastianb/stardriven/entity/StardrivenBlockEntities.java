package dev.sebastianb.stardriven.entity;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.block.controller.ControllerBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class StardrivenBlockEntities {
    public static final BlockEntityType<ControllerBlockEntity> CONTROLLER = Stardriven.REGISTRY.blockEntity(ControllerBlockEntity::new, "controller", StardrivenBlocks.ControllerBlocks.CONTROLLER.asBlock());

    public static void register() {}
}
