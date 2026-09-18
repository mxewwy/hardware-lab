package com.maxime.hardwarelab;

import com.maxime.hardwarelab.block.ModBlocks;
import com.maxime.hardwarelab.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HardwareLab implements ModInitializer {
    public static final String MOD_ID = "hardware_lab";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModItems.initialize();
        LOGGER.info("Hardware Lab initialized");
    }
}
