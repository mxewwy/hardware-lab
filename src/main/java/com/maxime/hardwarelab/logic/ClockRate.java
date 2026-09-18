package com.maxime.hardwarelab.logic;

import net.minecraft.util.StringRepresentable;

public enum ClockRate implements StringRepresentable {
    EVERY_TICK("1T", 1),
    EVERY_2_TICKS("2T", 2),
    EVERY_4_TICKS("4T", 4),
    EVERY_8_TICKS("8T", 8);

    private final String displayName;
    private final int interval;

    ClockRate(String displayName, int interval) {
        this.displayName = displayName;
        this.interval = interval;
    }

    public String displayName() {
        return displayName;
    }

    public int interval() {
        return interval;
    }

    public ClockRate next() {
        ClockRate[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}
