package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.HardwareLab;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class ModBlocks {
    public static final Block UNIVERSAL_LOGIC_GATE = register(
            "universal_logic_gate",
            UniversalLogicGateBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(2.0F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
    );

    private ModBlocks() {
    }

    private static Block register(
            String name,
            Function<BlockBehaviour.Properties, Block> factory,
            BlockBehaviour.Properties properties
    ) {
        ResourceKey<Block> blockKey = ResourceKey.create(
                BuiltInRegistries.BLOCK.key(),
                ResourceLocation.fromNamespaceAndPath(HardwareLab.MOD_ID, name)
        );

        Block block = factory.apply(properties.setId(blockKey));

        ResourceKey<Item> itemKey = ResourceKey.create(
                BuiltInRegistries.ITEM.key(),
                ResourceLocation.fromNamespaceAndPath(HardwareLab.MOD_ID, name)
        );

        BlockItem item = new BlockItem(
                block,
                new Item.Properties()
                        .setId(itemKey)
                        .useBlockDescriptionPrefix()
        );

        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return block;
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE).register(entries ->
                entries.accept(UNIVERSAL_LOGIC_GATE.asItem())
        );
    }
}
