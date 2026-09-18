# Hardware Lab

A digital electronics sandbox for Minecraft.

Hardware Lab brings digital electronics into the Minecraft world: logic gates, digital signals, buses, memory, instruments and eventually programmable hardware.

## Current status

**Version:** 0.1.0 — Digital Logic

The project is currently establishing its core digital-logic model.

### Implemented

- binary LOW / HIGH signal model
- reusable logic functions
- AND, OR, XOR, NAND, NOR, XNOR, NOT and BUFFER
- universal gate type model

### Roadmap

- [ ] Universal Logic Gate block
- [ ] Digital Wire
- [ ] Redstone Input
- [ ] Redstone Output
- [ ] Logic Probe
- [ ] Oscilloscope
- [ ] Clock and sequential logic
- [ ] Buses
- [ ] RAM / ROM
- [ ] Displays
- [ ] CPU
- [ ] FPGA

## Design principle

Minecraft redstone is an I/O interface, not the foundation of the digital system.

Inside Hardware Lab, signals and connections are represented by the mod's own digital-electronics model. Redstone connects that model to the Minecraft world.

## Development

- Minecraft: 26.2
- Fabric Loader: 0.19.5
- Fabric API: 0.160.0+26.2
- Java: 25
- License: MIT
