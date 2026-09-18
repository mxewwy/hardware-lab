package com.maxime.hardwarelab.logic;

public final class MemoryMap {
    public static final int RAM_START = 0x00;
    public static final int RAM_END = 0x7F;
    public static final int ROM_START = 0x80;
    public static final int ROM_END = 0xEF;
    public static final int DISPLAY = 0xF0;
    public static final int GPIO = 0xF1;

    private MemoryMap() {
    }
}
