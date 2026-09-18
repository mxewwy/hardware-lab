package com.maxime.hardwarelab.item;

import com.maxime.hardwarelab.HardwareLabLanguage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class HardwareBlockItem extends BlockItem {
    private final String languageKey;
    public HardwareBlockItem(Block block, Properties properties, String languageKey) {
        super(block, properties);
        this.languageKey = languageKey;
    }
    @Override
    public Component getName(ItemStack stack) {
        return HardwareLabLanguage.component(languageKey);
    }
}
