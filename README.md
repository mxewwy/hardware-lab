# Hardware Lab

A digital electronics sandbox for Minecraft.

Hardware Lab brings digital electronics into the Minecraft world: logic gates, digital signals, buses, memory, instruments and eventually programmable hardware.

## Current status

**Version:** 0.1.0 — Digital Logic

### Working

- binary LOW / HIGH signal model
- reusable logic functions
- AND, OR, XOR, NAND, NOR, XNOR, NOT and BUFFER
- Universal Logic Gate block with configurable gate type
- Digital Wire with horizontal and vertical connections
- Dedicated Redstone Input and Redstone Output adapters
- Logic Probe for instantaneous signal inspection
- Oscilloscope with a live 96-sample waveform
- Clock Generator with 1 / 2 / 4 / 8 tick intervals

### Roadmap

- [x] Core digital signal model
- [x] Universal Logic Gate
- [x] Digital Wire
- [x] Logic Probe
- [x] Redstone adapters
- [x] Oscilloscope
- [x] Clock Generator
- [ ] Clock Divider
- [ ] Sequential logic
- [ ] Buses
- [ ] RAM / ROM
- [ ] Displays
- [ ] CPU
- [ ] FPGA

## Design principle

Minecraft redstone is an I/O interface, not the foundation of the digital system.

Inside Hardware Lab, signals and connections are represented by the mod's own digital-electronics model. Redstone connects that model to the Minecraft world.

The first implementation intentionally keeps the wire network lightweight and block-based. The architecture can later evolve toward explicit ports, buses and network evaluation without changing the public concept of a digital wire.

## Development

- Minecraft: 26.2
- Fabric Loader: 0.19.5
- Fabric API: 0.160.0+26.2
- Java: 25
- License: MIT
