package dev.sebastianb.stardriven.entity;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.StardrivenBlocks;
import dev.sebastianb.stardriven.block.display.DisplayBlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class StardrivenBlockEntities {
    public static final BlockEntityType<DisplayBlockEntity> DISPLAY = Stardriven.REGISTRY.blockEntity(DisplayBlockEntity::new, "display", StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock());

    public static void register() {}
}
