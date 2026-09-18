package com.maxime.hardwarelab.logic;

public enum BusWidth {
    BITS_4(4),
    BITS_8(8),
    BITS_16(16),
    BITS_32(32);

    private final int bits;

    BusWidth(int bits) {
        this.bits = bits;
    }

    public int bits() {
        return bits;
    }

    public int mask() {
        return bits == 32 ? -1 : (1 << bits) - 1;
    }

    public int nibbleCount() {
        return bits / 4;
    }

    public BusWidth next() {
        BusWidth[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public static BusWidth fromIndex(int index) {
        BusWidth[] values = values();
        if (index < 0 || index >= values.length) {
            return BITS_8;
        }
        return values[index];
    }
}
