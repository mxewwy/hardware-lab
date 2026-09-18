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

    public static final BlockEntityType<EightBitRegisterBlockEntity> EIGHT_BIT_REGISTER =
            register("eight_bit_register", EightBitRegisterBlockEntity::new, ModBlocks.EIGHT_BIT_REGISTER);

    public static final BlockEntityType<Ram256BlockEntity> RAM_256 =
            register("ram_256", Ram256BlockEntity::new, ModBlocks.RAM_256);

    public static final BlockEntityType<Rom256BlockEntity> ROM_256 =
            register("rom_256", Rom256BlockEntity::new, ModBlocks.ROM_256);

    public static final BlockEntityType<SevenSegmentDisplayBlockEntity> SEVEN_SEGMENT_DISPLAY =
            register("seven_segment_display", SevenSegmentDisplayBlockEntity::new, ModBlocks.SEVEN_SEGMENT_DISPLAY);

    public static final BlockEntityType<LedMatrixBlockEntity> LED_MATRIX =
            register("led_matrix", LedMatrixBlockEntity::new, ModBlocks.LED_MATRIX);

    public static final BlockEntityType<CpuBlockEntity> CPU =
            register("cpu", CpuBlockEntity::new, ModBlocks.CPU);

    public static final BlockEntityType<FpgaBlockEntity> FPGA =
            register("fpga", FpgaBlockEntity::new, ModBlocks.FPGA);

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
