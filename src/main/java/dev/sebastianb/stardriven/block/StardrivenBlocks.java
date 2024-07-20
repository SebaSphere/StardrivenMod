package dev.sebastianb.stardriven.block;

import dev.sebastianb.sebautil.BlockRegistry;
import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.display.DisplayBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

public class StardrivenBlocks {

    public enum DisplayBlocks implements BlockRegistry {
        DISPLAY;
        private final String name;
        private final Block block;

        DisplayBlocks() {
            name = this.toString().toLowerCase(Locale.ROOT);
            block = Stardriven.REGISTRY.block(
                    new DisplayBlock(FabricBlockSettings.create()),
                    BlockRegistry.getItemGroup(),
                    name().toLowerCase(Locale.ROOT)
            );

        }

        @Override
        public Block asBlock() {
            return block;
        }

        @Override
        public Item asItem() {
            return block.asItem();
        }
    }

    public static void register() {
        // good enough for now
        Arrays.stream(DisplayBlocks.values()).forEach(v -> Stardriven.LOGGER.log(Level.INFO, v.name)); // init all blocks

        // TODO: move to the block register class when it gets worked on
        Registry.register(Registries.ITEM_GROUP, new Identifier(Stardriven.MOD_ID, "block_group"), BlockRegistry.getItemGroup());
    }

}
