# Configuration

The config file lives at `config/create_enchantment_industry_legacy-server.toml`. It is created automatically
on first startup with default values.

It is a **server** config: on a dedicated server it is authoritative and synced to clients; in singleplayer it
lives in the world's config. It can also be edited in game through Create's config screen
(Mods → Create: Enchantment Industry Legacy), which requires operator level.

Most values take effect on the next machine tick. The three tank capacities are marked
`RequiresRestart.SERVER` and need a restart.

---

## Full default config

```toml
disenchanterTankCapacity = 1000
copierTankCapacity = 4000
blazeEnchanterTankCapacity = 2000
maxHyperEnchantingLevelExtension = 2
deployerXpDropChance = 1.0
enableHyperEnchant = true
enchantByBlazeEnchanterCostCoefficient = 1.0
hyperEnchantByBlazeEnchanterCostCoefficient = 1.0
copyEnchantedBookCostCoefficient = 1.0
copyEnchantedBookWithHyperExperienceCostCoefficient = 1.0
copyWrittenBookCostPerPage = 5
copyNameTagCost = 7
copyTrainScheduleCost = 10
copyClipboardCost = 10
crushingWheelDropExpRate = 0.3
crushingWheelDropExpScale = 0.34
copyingWrittenBookAlwaysGetOriginalVersion = true
enchantmentLevelCaps = []
```

---

## Option reference

### `disenchanterTankCapacity`

| | |
|---|---|
| Type | `int`, min 0 |
| Default | `1000` |
| Restart | server |

Internal tank of the Disenchanter, in mB. 1 mB is 1 experience point, so the default holds 1000 XP before the
machine stalls and waits for a pump to drain it.

---

### `copierTankCapacity`

| | |
|---|---|
| Type | `int`, min 0 |
| Default | `4000` |
| Restart | server |

Internal tank of the Printer, in mB. This is also the hard budget for a single copy job: a book whose cost
exceeds this value is rejected as **Too Expensive!** and will never print, no matter how much fluid you supply.
Raise this before raising the cost coefficients.

---

### `blazeEnchanterTankCapacity`

| | |
|---|---|
| Type | `int`, min 0 |
| Default | `2000` |
| Restart | server |

Internal tank of the Blaze Enchanter, in mB.

---

### `maxHyperEnchantingLevelExtension`

| | |
|---|---|
| Type | `int`, min 0 |
| Default | `2` |

How many levels beyond an enchantment's normal maximum Hyper-Enchanting may reach. With the default, Sharpness
can be pushed from V to VII. Set to `0` to allow no extension at all while keeping Hyper Experience useful for
its cheaper book copying.

Enchantments whose maximum level is 1 (Mending, Silk Touch, …) are never extended.

---

### `deployerXpDropChance`

| | |
|---|---|
| Type | `float`, 0.0 – 1.0 |
| Default | `1.0` |

Chance that a mob killed by a Deployer drops Experience Nuggets. Set to `0.0` to stop Deployer-based XP farms
entirely.

---

### `enableHyperEnchant`

| | |
|---|---|
| Type | `boolean` |
| Default | `true` |

Master switch for Hyper-Enchanting. When `false`, the Blaze Enchanter no longer enters its seething state and
will not produce levels above the normal maximum. Liquid Hyper Experience still exists and still works as a
cheaper input for the Printer.

---

### `enchantByBlazeEnchanterCostCoefficient` / `hyperEnchantByBlazeEnchanterCostCoefficient`

| | |
|---|---|
| Type | `float`, 0.01 – 100 |
| Default | `1.0` |

Multipliers on the experience cost of enchanting an item, for normal and hyper enchanting respectively. `2.0`
doubles the cost, `0.5` halves it.

---

### `copyEnchantedBookCostCoefficient` / `copyEnchantedBookWithHyperExperienceCostCoefficient`

| | |
|---|---|
| Type | `float`, 0.01 – 100 |
| Default | `1.0` |

Multipliers on the cost of copying an enchanted book, for Liquid Experience and Liquid Hyper Experience
respectively. The base cost is derived from the enchantments on the book, so a Mending book is cheap and a
fully-enchanted one is not.

Remember `copierTankCapacity` is the ceiling — a coefficient that pushes a common book past it makes that book
uncopyable rather than expensive.

---

### `copyWrittenBookCostPerPage`

| | |
|---|---|
| Type | `int`, 1 – 100 |
| Default | `5` |

Ink in mB consumed per page when copying a Written Book. A 20-page book costs 100 mB at the default.

---

### `copyNameTagCost`

| | |
|---|---|
| Type | `int`, 1 – 1000 |
| Default | `7` |

Liquid Experience in mB consumed per Name Tag copy. Note this is the one Printer recipe that also renames a
passing item rather than only duplicating the target.

---

### `copyTrainScheduleCost` / `copyClipboardCost`

| | |
|---|---|
| Type | `int`, 1 – 1000 |
| Default | `10` |

Ink in mB consumed per Train Schedule or Clipboard copy.

---

### `crushingWheelDropExpRate`

| | |
|---|---|
| Type | `float`, 0.0 – 1.0 |
| Default | `0.3` |

Probability that a mob killed by Crushing Wheels drops Experience Nuggets. Set to `0.0` to disable.

---

### `crushingWheelDropExpScale`

| | |
|---|---|
| Type | `float`, 0.1 – 100 |
| Default | `0.34` |

How much experience those drops are worth, relative to what the mob would normally give. The default returns
roughly a third.

---

### `copyingWrittenBookAlwaysGetOriginalVersion`

| | |
|---|---|
| Type | `boolean` |
| Default | `true` |

When `true`, a copied Written Book comes out as an *Original*. When `false`, the vanilla generation chain
applies: Original → Copy of Original → Copy of Copy, and a Copy of Copy cannot be copied further.

---

### `enchantmentLevelCaps`

| | |
|---|---|
| Type | list of `string` |
| Default | `[]` |
| Format | `"modid:enchantment=level"` |

Per-enchantment hard caps. Each entry overrides `maxHyperEnchantingLevelExtension` for that one enchantment and
additionally stops the Printer from copying any book that exceeds the cap.

```toml
enchantmentLevelCaps = [
    "minecraft:sharpness=5",     # never above vanilla maximum
    "minecraft:efficiency=7",    # allow two extra levels
    "minecraft:fortune=3"
]
```

Entries without a `=` are rejected by the config validator and the file will fail to load.
