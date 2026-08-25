<!-- MODRINTH SUMMARY (paste into Project Settings → Summary, max 256 chars) -->
<!-- Unofficial 1.21.1 NeoForge port of the MIT-licensed 1.20.1 Create: Enchantment Industry by MarbleGateKeeper and LimonBlaze (DragonsPlus). Disenchanter, Blaze Enchanter and Printer - enchanting as a Create factory process. Original fully credited. -->

## Create: Enchantment Industry Legacy

Automatic enchanting for Create: the 1.20.1 feature set, ported to Minecraft 1.21.1 and NeoForge. Strip enchantments into Liquid Experience, pour them back onto gear with a Blaze Enchanter, and mass-produce enchanted books with a Printer.

Upstream rewrote the mod for 1.21.1 around a different set of machines. This fork keeps the machines people actually built their factories around. There are no new resources and no parallel progression: everything runs on Create's existing kinetics, fluids and belts.

### Features

- **Disenchanter**: strips enchantments off items on a belt and outputs Liquid Experience. Also drinks player XP and Experience Orbs that fall on top of it.
- **Blaze Enchanter**: a Blaze Burner fed an Enchanting Guide. Applies the configured enchantment to items passing on a belt, paid for in Liquid Experience.
- **Hyper-Enchanting**: feed it Liquid Hyper Experience and enchantments go one level past their normal maximum, up to a configurable cap.
- **Printer**: copies Enchanted Books, Written Books, Name Tags, Train Schedules and Clipboards using Ink or Liquid Experience.
- **Liquid Experience and Hyper Experience**: obtainable from disenchanting, furnaces, Crushing Wheel kills and Deployer kills. 1 mB equals 1 XP point, or 10 for Hyper.
- **Ink**: mixed from black dye, wither roses or ink sacs with water. Blinds anything swimming in it and reacts with lava.
- **Experience Rotor**: a Mixing catalyst that converts eight other mods' experience fluids into Liquid Experience.
- **Mending support**: Spouts and Deployers repair Mending gear directly with Liquid Experience.
- Ponder scenes, advancements and JEI integration are all included.

### Requirements

| | |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.180+ |
| Create | 6.0.10+ |
| Java | 21+ |

Fabric, Forge and Quilt are not supported.

This mod cannot be installed next to the official Create: Enchantment Industry. The game refuses to start with both, see [How it works](#how-it-works).

Optional integrations: JEI, Quark (Ancient Tome printing) and Apotheosis (Potion of Knowledge mixing, tomes barred from the Blaze Enchanter).

### Installation

1. Download the latest JAR from [Releases](https://github.com/EmilDeuOfficial/EnchantmentIndustryLegacy/releases)
2. Make sure Create 6.0.10+ and NeoForge 21.1.180+ are installed
3. Remove the official Create: Enchantment Industry if you have it
4. Drop the JAR into your `mods/` folder

### Configuration

Server config at `config/create_enchantment_industry_legacy-server.toml`, also editable in game through Create's config screen.

```toml
# Tank capacities, in mB
disenchanterTankCapacity = 1000
copierTankCapacity = 4000
blazeEnchanterTankCapacity = 2000

# Hyper-Enchanting
enableHyperEnchant = true
maxHyperEnchantingLevelExtension = 2

# Experience cost multipliers
enchantByBlazeEnchanterCostCoefficient = 1.0
hyperEnchantByBlazeEnchanterCostCoefficient = 1.0
copyEnchantedBookCostCoefficient = 1.0
copyEnchantedBookWithHyperExperienceCostCoefficient = 1.0

# Printer costs, in mB
copyWrittenBookCostPerPage = 5
copyNameTagCost = 7
copyTrainScheduleCost = 10
copyClipboardCost = 10
copyingWrittenBookAlwaysGetOriginalVersion = true

# Experience Nugget drops
deployerXpDropChance = 1.0
crushingWheelDropExpRate = 0.3
crushingWheelDropExpScale = 0.34

# Per-enchantment hard caps, format "modid:enchantment=level"
enchantmentLevelCaps = []
```

### Commands and permissions

The mod registers no commands and no permission nodes. Everything is configured through the server config file or Create's config screen, and operated in world through the machines themselves.

Editing the server config in game requires permission level 2 (OP). Placing and using the machines is open to everyone. The machines are plain blocks, so normal world protection such as claims or region plugins governs who may use them.

### How it works

Every machine is a Create `SmartBlockEntity`, so it plugs into the existing belt, fluid pipe and kinetic networks without any new infrastructure. The Disenchanter and Printer hook into belts through `BeltProcessingBehaviour`, and the Blaze Enchanter replaces a Blaze Burner in place while keeping its heat states.

XP storage is a virtual fluid (`VirtualFluid`) at a ratio of 1 mB per XP point, which is why it can be pumped, bottled by a Spout, drained by an Item Drain and fed straight into Mending gear. Enchantment data is read and written through `ItemEnchantments` and `Holder<Enchantment>`, so data-driven enchantments from other mods work without a compatibility layer.

This fork uses its own mod id (`create_enchantment_industry_legacy`) and its own block, item and fluid ids, so worlds built against the official mod will not find these machines under the old ids. It is also declared `incompatible` with `create_enchantment_industry` in its mod metadata. Both carry the same machines under different ids, and their enhanced-experience systems (Hyper Experience here, Super Experience there) do not interoperate, so running both would only duplicate everything. Pick one.

### Credits and license

**The original mod and all of its content were made by MarbleGateKeeper and LimonBlaze
([DragonsPlus](https://github.com/DragonsPlusMinecraft)), Copyright (c) 2022.**

| | |
|---|---|
| Original project | [modrinth.com/mod/create-enchantment-industry](https://modrinth.com/mod/create-enchantment-industry) |
| Original source | [DragonsPlusMinecraft/CreateEnchantmentIndustry](https://github.com/DragonsPlusMinecraft/CreateEnchantmentIndustry) |
| License | MIT, see [LICENSE](https://github.com/EmilDeuOfficial/EnchantmentIndustryLegacy/blob/main/LICENSE). The original copyright notice is kept intact and ships inside the JAR |

DragonsPlus released the **1.20.1** version, which this fork is derived from, under the **MIT license**, which
permits redistribution and modification as long as the copyright notice travels with it. Their newer **1.21.1
rewrite is licensed LGPL-3.0-or-later** and none of that code is used here, so do not go by the license shown
on their current Modrinth page when judging this fork.

What this fork changed: Forge to NeoForge (21.1.x), Minecraft 1.20.1 to 1.21.1, item NBT to data components,
hardcoded to data-driven enchantments, ForgeGradle to ModDevGradle, plus bug fixes found during the port. No
content, textures or mechanics were added.

If the original authors want this fork taken down, that request is respected. Open an issue on the repository.
