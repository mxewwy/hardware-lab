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
- Clock Divider with divide-by-2 / 4 / 8 / 16
- D Flip-Flop with rising-edge state storage
- Register logic primitive for upcoming bus integration
- 4 / 8 / 16 / 32-bit Digital Bus
- Bus MUX with redstone select
- Bus Splitter exposing four digital bit lanes at a time

### Roadmap

- [x] Core digital signal model
- [x] Universal Logic Gate
- [x] Digital Wire
- [x] Logic Probe
- [x] Redstone adapters
- [x] Oscilloscope
- [x] Clock Generator
- [x] Clock Divider
- [x] D Flip-Flop
- [x] Register primitive
- [x] Bus / Bus Splitter / MUX
- [ ] 8-bit Register block
- [ ] RAM / ROM
- [ ] Displays
- [ ] CPU
- [ ] FPGA

## Port map

### D Flip-Flop

Relative to the block's facing direction:

- front: Q
- front-left: /Q
- back: D
- front-right: CLK

The flip-flop captures D on the rising edge of CLK.

### Clock Divider

Relative to the block's facing direction:

- back: clock input
- front: divided clock output

Right-click cycles DIV2, DIV4, DIV8 and DIV16.

### Digital Bus

Relative to the block's facing direction:

- back: bus input
- front: bus output

Right-click cycles test patterns. Sneak-right-click cycles 4 / 8 / 16 / 32-bit width. With no bus input connected, the block acts as a test source; with an input connected, it forwards that bus value at its configured width.

### Bus MUX

Relative to the block's facing direction:

- back: input A
- left: input B
- right: redstone SELECT
- front: bus output

SELECT LOW routes A. SELECT HIGH routes B. Right-click cycles 4 / 8 / 16 / 32-bit width.

### Bus Splitter

Relative to the block's facing direction:

- back: bus input
- front: bit 0
- left: bit 1
- right: bit 2
- top: bit 3

Right-click changes the active 4-bit bank. Sneak-right-click cycles bus width. This lets a 32-bit bus be inspected in eight 4-bit banks.

## Design principle

Minecraft redstone is an I/O interface, not the foundation of the digital system.

Inside Hardware Lab, signals and connections are represented by the mod's own digital-electronics model. Redstone connects that model to the Minecraft world.

The current sequential blocks keep state in Block Entities instead of inflating block-state variants. Bus width/configuration state also lives in Block Entities so the physical blocks only need a facing property.

## Development

- Minecraft: 26.2
- Fabric Loader: 0.19.5
- Fabric API: 0.160.0+26.2
- Java: 25
- License: MIT
