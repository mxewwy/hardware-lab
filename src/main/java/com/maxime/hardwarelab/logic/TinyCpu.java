package com.maxime.hardwarelab.logic;

import java.util.Arrays;

public final class TinyCpu {
    public static final int ROM_SIZE = 256;
    public static final int RAM_SIZE = 256;

    private final byte[] rom = new byte[ROM_SIZE];
    private final byte[] ram = new byte[RAM_SIZE];

    private int a;
    private int b;
    private int pc;
    private boolean zero;
    private boolean carry;
    private boolean halted;
    private int output;
    private int programId;

    public TinyCpu() {
        loadProgram(0);
    }

    public void step() {
        if (halted) {
            return;
        }

        int opcode = readRom(pc);
        switch (opcode) {
            case 0x00 -> pc = nextPc(1);
            case 0x10 -> {
                a = readRom(pc + 1);
                updateZero();
                pc = nextPc(2);
            }
            case 0x11 -> {
                b = readRom(pc + 1);
                updateZero();
                pc = nextPc(2);
            }
            case 0x20 -> {
                int sum = a + b;
                carry = sum > 0xFF;
                a = sum & 0xFF;
                updateZero();
                pc = nextPc(1);
            }
            case 0x21 -> {
                int diff = a - b;
                carry = diff < 0;
                a = diff & 0xFF;
                updateZero();
                pc = nextPc(1);
            }
            case 0x30 -> {
                a ^= b;
                updateZero();
                pc = nextPc(1);
            }
            case 0x31 -> {
                a &= b;
                updateZero();
                pc = nextPc(1);
            }
            case 0x32 -> {
                a |= b;
                updateZero();
                pc = nextPc(1);
            }
            case 0x40 -> {
                output = a;
                pc = nextPc(1);
            }
            case 0x50 -> {
                a = ram[readRom(pc + 1)] & 0xFF;
                updateZero();
                pc = nextPc(2);
            }
            case 0x51 -> {
                ram[readRom(pc + 1)] = (byte) a;
                pc = nextPc(2);
            }
            case 0x60 -> pc = readRom(pc + 1);
            case 0x61 -> pc = zero ? readRom(pc + 1) : nextPc(2);
            case 0x70 -> {
                a = (a + 1) & 0xFF;
                updateZero();
                pc = nextPc(1);
            }
            case 0x71 -> {
                a = (a - 1) & 0xFF;
                updateZero();
                pc = nextPc(1);
            }
            case 0x72 -> {
                a = (a << 1) & 0xFF;
                updateZero();
                pc = nextPc(1);
            }
            case 0x73 -> {
                a = a >>> 1;
                updateZero();
                pc = nextPc(1);
            }
            case 0xF0 -> halted = true;
            default -> {
                halted = true;
                zero = false;
            }
        }
    }

    public void reset() {
        Arrays.fill(ram, (byte) 0);
        a = 0;
        b = 0;
        pc = 0;
        zero = false;
        carry = false;
        halted = false;
        output = 0;
    }

    public void loadProgram(int id) {
        programId = Math.floorMod(id, 4);
        Arrays.fill(rom, (byte) 0);

        switch (programId) {
            case 0 -> {
                put(0x00, 0x10, 0x00);
                put(0x02, 0x40);
                put(0x03, 0x70);
                put(0x04, 0x60, 0x02);
            }
            case 1 -> {
                put(0x00, 0x10, 0x01);
                put(0x02, 0x40);
                put(0x03, 0x72);
                put(0x04, 0x60, 0x02);
            }
            case 2 -> {
                put(0x00, 0x10, 0x01);
                put(0x02, 0x11, 0x01);
                put(0x04, 0x40);
                put(0x05, 0x20);
                put(0x06, 0x60, 0x04);
            }
            case 3 -> {
                put(0x00, 0x10, 0xAA);
                put(0x02, 0x40);
                put(0x03, 0x30);
                put(0x04, 0x40);
                put(0x05, 0x60, 0x02);
            }
            default -> throw new AssertionError();
        }

        reset();
    }

    public void restore(
            int a,
            int b,
            int pc,
            boolean zero,
            boolean carry,
            boolean halted,
            int output
    ) {
        this.a = a & 0xFF;
        this.b = b & 0xFF;
        this.pc = pc & 0xFF;
        this.zero = zero;
        this.carry = carry;
        this.halted = halted;
        this.output = output & 0xFF;
    }

    public int a() {
        return a;
    }

    public int b() {
        return b;
    }

    public int pc() {
        return pc;
    }

    public boolean zero() {
        return zero;
    }

    public boolean carry() {
        return carry;
    }

    public boolean halted() {
        return halted;
    }

    public int output() {
        return output;
    }

    public int programId() {
        return programId;
    }

    private int readRom(int address) {
        return rom[address & 0xFF] & 0xFF;
    }

    private int nextPc(int amount) {
        return (pc + amount) & 0xFF;
    }

    private void updateZero() {
        zero = a == 0;
    }

    private void put(int offset, int... bytes) {
        for (int i = 0; i < bytes.length; i++) {
            rom[(offset + i) & 0xFF] = (byte) bytes[i];
        }
    }
}
