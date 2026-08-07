# Installation

## Requirements

| Requirement | Version |
|---|---|
| Minecraft | **1.21.1** |
| NeoForge | **21.1.180 or higher** |
| Create | **6.0.10 or higher** |
| Java | **21 or higher** |

Fabric, Forge and Quilt are **not** supported.

---

## Step-by-step

### 1. Install NeoForge

Download and run the [NeoForge installer](https://neoforged.net/) for Minecraft 1.21.1. For a dedicated server,
use the server installer option.

### 2. Download Create

Download [Create 6.0.10+](https://modrinth.com/mod/create) for 1.21.1 and place it in your `mods/` folder.
Create's own dependencies (Ponder, Flywheel) ship with it.

### 3. Download Create: Enchantment Industry Legacy

Grab the latest `CreateEnchantmentIndustryLegacy_x.x.x_1.21.jar` from the
[Releases page](https://github.com/EmilDeuOfficial/EnchantmentIndustryLegacy/releases).

### 4. Place in mods folder

Copy both JARs into your `mods/` directory:

```
.minecraft/
└── mods/
    ├── create-1.21.1-6.0.10.jar
    └── CreateEnchantmentIndustryLegacy_1.5.0_1.21.jar   ← here
```

### 5. Remove the official Create: Enchantment Industry

If `create_enchantment_industry` is present, **delete it**. The two mods are declared incompatible and the game
will refuse to start with both. They carry the same machines under different ids.

### 6. Start the game

On first launch the mod writes `config/create_enchantment_industry_legacy-server.toml` with default values.

### 7. Verify

Open the creative menu — there is a tab called **Create: Enchantment Industry Legacy** with the Disenchanter,
Printer, Enchanting Guide, Bottle O' Hyper Enchanting, Experience Rotor and Ink Bucket.

---

## First use

Craft a Disenchanter, place it one block above a Create belt, and send an enchanted item along that belt. The
enchantments are stripped off and stored inside the machine as Liquid Experience — attach a pipe and pump to
move it into a tank.

Hold Engineer's Goggles to see the fill level and the machine's current state.

---

## Updating

1. Stop the game or server
2. Replace the old JAR in `mods/` with the new one
3. Start again

Your config and your existing builds are preserved across updates.

---

## Uninstalling

1. Break every machine first — a broken machine drops its stored experience as orbs, and its copy target or
   Enchanting Guide as items. Removing the mod without doing this loses them.
2. Stop the game or server
3. Delete the JAR from `mods/`

Blocks left in the world become air on the next load, and Minecraft will warn about missing block ids.
