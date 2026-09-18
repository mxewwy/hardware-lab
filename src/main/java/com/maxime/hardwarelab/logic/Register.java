package com.maxime.hardwarelab.logic;

public final class Register {
    private final int width;
    private final int mask;
    private int value;

    public Register(int width) {
        if (width < 1 || width > 31) {
            throw new IllegalArgumentException("Register width must be between 1 and 31 bits");
        }

        this.width = width;
        this.mask = (1 << width) - 1;
    }

    public void capture(int value) {
        this.value = value & mask;
    }

    public int value() {
        return value;
    }

    public boolean bit(int index) {
        if (index < 0 || index >= width) {
            throw new IndexOutOfBoundsException("Register bit: " + index);
        }

        return ((value >>> index) & 1) != 0;
    }

    public int width() {
        return width;
    }

    public void reset() {
        value = 0;
    }
}
