# Machines

Every machine sits **one block above a Create belt** and processes items passing underneath, the same way a
Spout or a Mechanical Press does. Hold Engineer's Goggles to see what a machine is currently doing.

---

## Disenchanter

**Craft:** see JEI. **Input:** enchanted items on a belt. **Output:** Liquid Experience.

Strips every enchantment off items passing below and converts them into Liquid Experience stored in its
internal tank (default 1000 mB). The item continues on the belt, unenchanted.

It also absorbs experience from two other sources:

- **Experience Orbs** that fall onto it
- **Players standing on top of it** — their level drains away into the tank

Curses are removed like any other enchantment but are worth nothing.

Attach a pipe to any side except the bottom to pump the fluid out. The machine stalls once the tank is full.

---

## Blaze Enchanter

**Craft:** none. Sneak + right-click a **Blaze Burner** with an **Enchanting Guide** in hand.

Applies one configured enchantment to items passing below, paying in Liquid Experience.

### Enchanting Guide

The Guide holds the target enchantment. Configure it by right-clicking it in hand, or by right-clicking a
placed Blaze Enchanter — both open the same screen. To take the Guide back out, sneak + right-click the machine
with a Wrench. Right-clicking with a different Guide swaps it.

### What it can and cannot do

| | |
|---|---|
| Enchants ordinary items | yes |
| Enchants books | no — use the Printer to duplicate books instead |
| Adds an enchantment incompatible with one already present | no |
| Upgrades an existing enchantment to a higher level | yes, if the Guide's level is higher |

### Hyper-Enchanting

Feed it **Liquid Hyper Experience** instead and it enters a seething state: the enchantment it produces is one
level above what the Guide is set to, up to `maxHyperEnchantingLevelExtension`. Enchantments whose maximum
level is 1 (Mending, Silk Touch, …) are never extended.

---

## Printer

**Input:** a copy target set by hand, plus blank material on a belt. **Fluid:** Liquid Experience, Liquid Hyper
Experience or Ink depending on what is being copied.

Right-click the Printer with the item you want to duplicate to set it as the **copy target**. Right-click with
an empty hand to take it back out. There is no visual for the target on the block itself — check with
Engineer's Goggles, or read it out with a Display Link.

| Copy target | Material on the belt | Fluid |
|---|---|---|
| Enchanted Book | Book | Liquid Experience, or Hyper if the book is above maximum level |
| Written Book | Book | Ink |
| Name Tag | Name Tag, or any item to rename it | Liquid Experience |
| Train Schedule | Train Schedule | Ink |
| Clipboard | Clipboard | Ink |
| Ancient Tome (Quark) | Book | Liquid Experience |

The cost of copying an enchanted book scales with the enchantments on it. If that cost exceeds
`copierTankCapacity`, the goggles overlay reads **Too Expensive!** and the job will never run — raise the tank
capacity rather than the fluid supply.

---

## Fluids

| Fluid | Worth | Obtained from |
|---|---|---|
| **Liquid Experience** | 1 mB = 1 XP | Disenchanter, furnace output, Crushing Wheel and Deployer kills, Item Drain with a Bottle o' Enchanting |
| **Liquid Hyper Experience** | 1 mB = 10 XP | Mixing recipe, Bottle O' Hyper Enchanting |
| **Ink** | — | Mixing black dye, wither roses or ink sacs with water |

Both experience fluids can be bottled by a Spout, drained by an Item Drain, and used by a Spout or a Deployer
to repair **Mending** gear directly.

Ink blinds anything swimming in it and turns to obsidian or blackstone on contact with lava.

Leaking experience from an open-ended pipe turns back into Experience Orbs; a player standing at the opening
absorbs it directly.

---

## Experience Rotor

Not a machine — a **Mixing catalyst**. Put it in a Basin under a Mechanical Mixer together with another mod's
experience fluid and it converts that fluid into Liquid Experience. It is never consumed.

Supported: Cyclic, EnderIO, Industrial Foregoing, Mob Grinding Utils, PneumaticCraft, Reliquary, Sophisticated
Core and Thermal. Each recipe only exists when the corresponding mod is installed — without any of them the
Rotor is craftable but has no recipes.
