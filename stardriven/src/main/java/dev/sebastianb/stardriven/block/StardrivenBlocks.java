package dev.sebastianb.stardriven.block;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.block.display.DisplayBlock;
import dev.sebastianb.stardriven.block.display.DisplayBlockWithEntity;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.terradevelopment.terrautil.util.BlockRegistry;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

public class StardrivenBlocks {

    public static ItemGroup itemGroup = FabricItemGroup.builder()
            .icon(() -> new ItemStack(DisplayBlocks.DISPLAY))
            .displayName(Text.translatable("item_group.stardriven.blocks")) // TODO: properly datagen this
            .entries((context, entries) -> {
                // TODO: make a better system to automatically add blocks to the block group
                for (var item : DisplayBlocks.values()) {
                    if (item.inGroup) {
                        entries.add(item.asStack());
                    }
                }
            })
            .build();

    public enum DisplayBlocks implements BlockRegistry {

        DISPLAY(new DisplayBlock(FabricBlockSettings.create()), true),
        DISPLAY_WITH_ENTITY(new DisplayBlockWithEntity(FabricBlockSettings.create()), false);

        private final String name;
        private final Block block;
        private final boolean inGroup;

        DisplayBlocks(Block templateBlock, boolean inGroup) {
            name = this.toString().toLowerCase(Locale.ROOT);

            if (inGroup) {
                block = Stardriven.REGISTRY.block(
                        templateBlock,
                        itemGroup,
                        name().toLowerCase(Locale.ROOT)
                );
            } else {
                block = Stardriven.REGISTRY.block(
                        templateBlock,
                        name().toLowerCase(Locale.ROOT)
                );
            }

            this.inGroup = inGroup;
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

        // TODO: move to the block register class when it gets worked on
        Registry.register(Registries.ITEM_GROUP, new Identifier(Stardriven.MOD_ID, "block_group"), itemGroup);
    }

}
