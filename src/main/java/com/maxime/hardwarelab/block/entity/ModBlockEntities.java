package com.maxime.hardwarelab.block.entity;

import com.maxime.hardwarelab.HardwareLab;
import com.maxime.hardwarelab.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
    public static final BlockEntityType<ClockDividerBlockEntity> CLOCK_DIVIDER =
            register("clock_divider", ClockDividerBlockEntity::new, ModBlocks.CLOCK_DIVIDER);

    public static final BlockEntityType<DFlipFlopBlockEntity> D_FLIP_FLOP =
            register("d_flip_flop", DFlipFlopBlockEntity::new, ModBlocks.D_FLIP_FLOP);

    public static final BlockEntityType<DigitalBusBlockEntity> DIGITAL_BUS =
            register("digital_bus", DigitalBusBlockEntity::new, ModBlocks.DIGITAL_BUS);

    public static final BlockEntityType<BusMuxBlockEntity> BUS_MUX =
            register("bus_mux", BusMuxBlockEntity::new, ModBlocks.BUS_MUX);

    public static final BlockEntityType<BusSplitterBlockEntity> BUS_SPLITTER =
            register("bus_splitter", BusSplitterBlockEntity::new, ModBlocks.BUS_SPLITTER);

    private ModBlockEntities() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> factory,
            Block... blocks
    ) {
        Identifier id = Identifier.fromNamespaceAndPath(HardwareLab.MOD_ID, name);
        return Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                id,
                FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build()
        );
    }

    public static void initialize() {
    }
}
