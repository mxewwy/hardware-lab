package com.maxime.hardwarelab.logic;

public record BusSignal(BusWidth width, int value) {
    public BusSignal {
        value &= width.mask();
    }

    public static BusSignal of(BusWidth width, int value) {
        return new BusSignal(width, value);
    }

    public boolean bit(int index) {
        if (index < 0 || index >= width.bits()) {
            return false;
        }
        return ((value >>> index) & 1) != 0;
    }

    public BusSignal resized(BusWidth targetWidth) {
        return new BusSignal(targetWidth, value);
    }

    public long unsignedValue() {
        return Integer.toUnsignedLong(value);
    }

    public String hex() {
        int digits = width.bits() / 4;
        return String.format("%0" + digits + "X", unsignedValue());
    }

    public String binary() {
        return String.format("%" + width.bits() + "s",
                        Integer.toBinaryString(value))
                .replace(' ', '0');
    }

    public static BusSignal zero(BusWidth width) {
        return new BusSignal(width, 0);
    }
}
