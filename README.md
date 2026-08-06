# Create: Enchantment Industry Legacy

A continuation of the **1.20.1 feature set** of
[Create: Enchantment Industry](https://github.com/DragonsPlusMinecraft/CreateEnchantmentIndustry),
ported to **Minecraft 1.21.1** and **NeoForge**.

Upstream rewrote the mod for 1.21.1 with a different set of machines and mechanics. This fork keeps the
older, familiar content instead — the Disenchanter, the Blaze Enchanter with its Enchanting Guide, and the
Printer — and brings it forward to the current platform.

## Content

| Feature | Description |
| --- | --- |
| **Disenchanter** | Strips enchantments off items and outputs Liquid Experience. Also absorbs player XP and Experience Orbs from above. |
| **Blaze Enchanter** | A Blaze Burner fed an Enchanting Guide. Applies the configured enchantment to items on a belt, consuming Liquid Experience. |
| **Hyper-Enchanting** | Feeding Liquid Hyper Experience pushes enchantments one level beyond their normal maximum. |
| **Printer** | Copies Enchanted Books, Written Books, Name Tags, Train Schedules and Clipboards using Ink or Liquid Experience. |
| **Liquid Experience / Hyper Experience** | Fluids obtainable from disenchanting, furnaces, Crushing Wheels and Deployer kills. |
| **Ink** | Mixed from black dye, wither roses or ink sacs with water. Blinds anything swimming in it and reacts with lava. |
| **Enchanting Guide** | Configurable target enchantment, editable by hand or on a placed Blaze Enchanter. |
| **Experience Rotor** | Converts other mods' experience fluids into Liquid Experience via Mixing. |
| **Bottle O' Hyper Enchanting** | A throwable bottle that scatters Hyper Experience. |
| **Mending support** | Spouts and Deployers repair Mending gear with Liquid Experience. |

Ponder scenes, advancements and JEI integration are included.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.180 or newer
- Create 6.0.10 or newer

Optional integrations: JEI, Quark (Ancient Tome printing), Apotheosis (Potion of Knowledge mixing, tomes are
barred from the Blaze Enchanter).

> **Note:** this mod uses the same mod id (`create_enchantment_industry`) as upstream, so it is a drop-in
> replacement and **cannot be installed alongside** the official 1.21.1 version.

## Building

```bash
./gradlew build
```

The jar lands in `build/libs/`.

Regenerating the datapack and asset files:

```bash
./gradlew runData
```

## Configuration

Gameplay values live in the server config (`create_enchantment_industry-server.toml`), reachable in game
through Create's config screen. Tank capacities, experience costs, the Crushing Wheel drop rate and the
per-enchantment level caps (`enchantmentLevelCaps`) are all adjustable there.

## Credits

Original mod by **MarbleGateKeeper** and **LimonBlaze** (DragonsPlus). Licensed under the MIT license, see
[LICENSE](LICENSE).
