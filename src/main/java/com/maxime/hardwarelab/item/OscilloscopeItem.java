package com.maxime.hardwarelab.item;

import com.maxime.hardwarelab.HardwareLabLanguage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class OscilloscopeItem extends Item {
    public OscilloscopeItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return HardwareLabLanguage.component("item.oscilloscope");
    }
}
