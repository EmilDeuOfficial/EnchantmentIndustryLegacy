<!-- MODRINTH SUMMARY (paste into Project Settings → Summary, max 256 chars) -->
<!-- Automatic enchanting for Create: disenchant gear into Liquid Experience, enchant items on a belt with the Blaze Enchanter, mass-copy books with the Printer. The 1.20.1 feature set, ported to 1.21.1 NeoForge. -->

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

### How it works

Every machine is a Create `SmartBlockEntity`, so it plugs into the existing belt, fluid pipe and kinetic networks without any new infrastructure. The Disenchanter and Printer hook into belts through `BeltProcessingBehaviour`, and the Blaze Enchanter replaces a Blaze Burner in place while keeping its heat states.

XP storage is a virtual fluid (`VirtualFluid`) at a ratio of 1 mB per XP point, which is why it can be pumped, bottled by a Spout, drained by an Item Drain and fed straight into Mending gear. Enchantment data is read and written through `ItemEnchantments` and `Holder<Enchantment>`, so data-driven enchantments from other mods work without a compatibility layer.

This fork uses its own mod id (`create_enchantment_industry_legacy`) and its own block, item and fluid ids, so worlds built against the official mod will not find these machines under the old ids. It is also declared `incompatible` with `create_enchantment_industry` in its mod metadata. Both carry the same machines under different ids, and their enhanced-experience systems (Hyper Experience here, Super Experience there) do not interoperate, so running both would only duplicate everything. Pick one.
