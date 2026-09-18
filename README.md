# Hardware Lab

A digital electronics sandbox for Minecraft.

Hardware Lab is a physical playground for digital logic: gates, clocks, buses, memory, displays, an 8-bit CPU and a programmable 4-input LUT.

## Current status

**Version:** 0.2.0 — Complete Hardware Stack

The original roadmap is implemented as a playable baseline. The blocks use Minecraft redstone at the I/O boundary; internal values travel as packed digital buses.

### Implemented

- binary LOW / HIGH signal model
- AND, OR, XOR, NAND, NOR, XNOR, NOT and BUFFER
- Universal Logic Gate
- Digital Wire
- Redstone Input / Output adapters
- Logic Probe
- Oscilloscope
- Clock Generator and Clock Divider
- D Flip-Flop
- 4 / 8 / 16 / 32-bit Digital Bus
- Bus MUX
- Bus Splitter and Bus Merger
- Tri-State Bus Driver
- 4-bit ADC and DAC bridges
- 8-bit Register
- 256-byte RAM
- 256-byte ROM with selectable test patterns
- 7-Segment Display endpoint
- 8x8 LED Matrix endpoint
- 8-bit CPU with a small instruction set and demo firmware
- FPGA-style 4-input LUT with optional registered output

## Controls and in-game help

Press **H** at any time in a world to open the Hardware Lab Guide.

The guide is split into three pages and uses plain-language descriptions of every component. Use **Left / Right** to change pages and **Esc** to close it.

Hold the **Logic Probe** and right-click a Hardware Lab component to open a component information panel. The server-side probe readout also reports the current value/state.

Hold the **Oscilloscope** and right-click a signal source to open the live waveform viewer. Press **R** to clear the captured samples and **Esc** to close it.

Every Hardware Lab block and tool has an expanded inventory tooltip describing its purpose and controls.

The blocks now use distinct industrial-looking materials and marked front faces instead of reusing the same redstone-block texture everywhere. Directional blocks rotate their front face with their FACING state, making the input/output side easier to spot.

## Roadmap

- [x] Core digital signal model
- [x] Universal Logic Gate
- [x] Digital Wire
- [x] Logic Probe
- [x] Redstone I/O
- [x] Oscilloscope
- [x] Clock Generator
- [x] Clock Divider
- [x] D Flip-Flop
- [x] Registers
- [x] Bus / Splitter / Merger / MUX
- [x] 8-bit Register block
- [x] RAM / ROM
- [x] Displays
- [x] CPU
- [x] FPGA

## Port map

### 8-bit Register

Relative to facing:

- back: 8-bit data bus input
- front: 8-bit Q bus output
- right: clock input

Captures the input bus on the rising clock edge.

### RAM-256

Relative to facing:

- back: 8-bit address bus
- left: 8-bit data input
- right: redstone write enable
- front: 8-bit data output

A write occurs while write enable is HIGH.

### ROM-256

Relative to facing:

- back: 8-bit address bus
- front: 8-bit data output

Right-click cycles deterministic ROM test images.

### 7-Segment Display

Relative to facing:

- back: 4-bit input

The endpoint stores and reports the displayed hexadecimal digit. Right-click shows the digit and binary value.

### LED Matrix

Relative to facing:

- back: 8-bit row pattern input

Each click advances the selected row (0–7). The current row and eight LED states are available through the overlay and Logic Probe.

### 8-bit CPU

The CPU has A, B and PC registers, Z/C flags, a 256-byte RAM and a 256-byte program ROM. Its front-facing bus is the OUT register.

Right-click cycles the built-in demo firmware. Sneak-right-click cycles instruction speed. The CPU executes continuously when not halted.

Instruction set:

- `00 NOP`
- `10 imm` — LDI A
- `11 imm` — LDI B
- `20` — ADD A,B
- `21` — SUB A,B
- `30` — XOR A,B
- `31` — AND A,B
- `32` — OR A,B
- `40` — OUT A
- `50 addr` — LD A,[addr]
- `51 addr` — ST A,[addr]
- `60 addr` — JMP
- `61 addr` — JZ
- `70` — INC A
- `71` — DEC A
- `72` — SHL A
- `73` — SHR A
- `F0` — HALT

### FPGA 4-LUT

The FPGA block is a compact programmable-logic primitive:

- back: input 0
- left: input 1
- right: input 2
- top: input 3
- bottom: clock for registered mode
- front: logic output

Right-click cycles four LUT configurations: AND4, OR4, parity XOR4 and MUX. Sneak-right-click toggles registered output mode. In registered mode the LUT result is captured on a rising clock edge.

### Bus Driver

The driver reads an 8-bit bus from its back and drives it to the front only while its right-side enable input is HIGH. Disabled output is electrically disconnected from the bus network.

### Bus Merger

The merger packs four redstone inputs into one 4-bit bank of its configured 4 / 8 / 16 / 32-bit output bus. It uses the same bank concept as Bus Splitter.

### ADC / DAC

ADC samples vanilla/redstone power (0–15) into a 4-bit digital bus.

DAC converts the low four bits of an incoming bus back into redstone power (0–15).

## Memory model

Hardware Lab uses an 8-bit data path and 8-bit addresses for its standalone memory blocks, giving 256 addressable bytes.

The CPU's conceptual map reserves:

- `0x00–0x7F` — RAM region
- `0x80–0xEF` — ROM region
- `0xF0` — DISPLAY
- `0xF1` — GPIO

The standalone RAM/ROM blocks expose the same 8-bit address width physically through the bus system.

## Design principle

Minecraft redstone is an I/O interface, not the foundation of the digital system.

Inside Hardware Lab, packed buses, stateful registers, memory and programmable logic are handled by the mod's own digital model. Block Entities hold state that should not explode into block-state variants.

## Development

- Minecraft: 26.2
- Fabric Loader: 0.19.5
- Fabric API: 0.160.0+26.2
- Java: 25
- License: MIT
