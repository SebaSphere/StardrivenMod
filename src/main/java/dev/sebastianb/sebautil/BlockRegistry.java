/*
 * Copyright (c) 2022 mc.neko.rs contributors <https://mc.neko.rs>
 *
 * Licensed with GNU Lesser General Public License v3.0
 */

package dev.sebastianb.sebautil;

import dev.sebastianb.stardriven.block.StardrivenBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public interface BlockRegistry extends ItemRegistry {
  default Item asItem() {
    return asBlock().asItem();
  }
  public Block asBlock();

  static ItemGroup getItemGroup() {
    return FabricItemGroup.builder()
            .icon(() -> new ItemStack(StardrivenBlocks.DisplayBlocks.DISPLAY.asBlock()))
            .displayName(Text.translatable("item_group.stardriven.blocks")) // TODO: properly datagen this
            .entries((context, entries) -> {
              // TODO: make a better system to automatically add blocks to the block group
              for (var item : StardrivenBlocks.DisplayBlocks.values()) {
                entries.add(item.asStack());
              }
            })
            .build();
  }

}