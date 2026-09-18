package com.maxime.hardwarelab.block;

import com.maxime.hardwarelab.HardwareLab;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

    public static final Block DIGITAL_WIRE = register(
            "digital_wire",
            DigitalWireBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(0.2F)
                    .sound(SoundType.COPPER)
                    .noOcclusion()
    );

    public static final Block REDSTONE_INPUT = register(
            "redstone_input",
            RedstoneInputBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(1.5F)
                    .sound(SoundType.METAL)
    );

    public static final Block REDSTONE_OUTPUT = register(
            "redstone_output",
            RedstoneOutputBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(1.5F)
                    .sound(SoundType.METAL)
    );

    private ModBlocks() {
    }

    private static Block register(
            String name,
            Function<BlockBehaviour.Properties, Block> factory,
            BlockBehaviour.Properties properties
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(HardwareLab.MOD_ID, name);

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        Block block = factory.apply(properties.setId(blockKey));
        Registry.register(BuiltInRegistries.BLOCK, blockKey, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        BlockItem item = new BlockItem(
                block,
                new Item.Properties()
                        .setId(itemKey)
                        .useBlockDescriptionPrefix()
        );
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return block;
    }

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(entries -> {
            entries.accept(UNIVERSAL_LOGIC_GATE.asItem());
            entries.accept(DIGITAL_WIRE.asItem());
            entries.accept(REDSTONE_INPUT.asItem());
            entries.accept(REDSTONE_OUTPUT.asItem());
        });
    }
}
