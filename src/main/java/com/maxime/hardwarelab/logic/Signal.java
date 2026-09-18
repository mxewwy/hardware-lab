package com.maxime.hardwarelab.logic;

public enum Signal {
    LOW(0),
    HIGH(1);

    private final int value;

    Signal(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean isHigh() {
        return this == HIGH;
    }

    public static Signal of(int value) {
        return value == 0 ? LOW : HIGH;
    }

    public static Signal of(boolean value) {
        return value ? HIGH : LOW;
    }
}
