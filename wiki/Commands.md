# Commands

**This mod registers no commands.**

There is nothing to enable, disable or reload from chat — every machine is a block you place and operate in
world, and every setting lives in the server config.

---

## Overview

| Command | Permission Level | Description |
|---|---|---|
| — | — | none registered |

---

## What to use instead

| You want to… | Do this |
|---|---|
| Change a setting | Edit `config/create_enchantment_industry_legacy-server.toml`, or open Create's config screen in game (Mods → Create: Enchantment Industry Legacy) |
| Apply a config change | Most values apply on the next machine tick. The three tank capacities are marked `RequiresRestart.SERVER` and need a server restart |
| Turn Hyper-Enchanting off | Set `enableHyperEnchant = false` in the server config |
| Inspect a machine's state | Hold Engineer's Goggles and look at it |
| Read a machine's state as text | Point a Display Link at it and pick **Copy Content** (Printer) or **Target Enchantment** (Blaze Enchanter) |
| Give yourself a machine | `/give @s create_enchantment_industry_legacy:disenchanter` (vanilla command) |

---

## Item and block ids

For use with `/give`, `/setblock` and datapacks:

| Id | Type |
|---|---|
| `create_enchantment_industry_legacy:disenchanter` | block + item |
| `create_enchantment_industry_legacy:printer` | block + item |
| `create_enchantment_industry_legacy:blaze_enchanter` | block only — made from a Blaze Burner, not craftable |
| `create_enchantment_industry_legacy:enchanting_guide` | item |
| `create_enchantment_industry_legacy:hyper_experience_bottle` | item |
| `create_enchantment_industry_legacy:experience_rotor` | item |
| `create_enchantment_industry_legacy:ink_bucket` | item |
| `create_enchantment_industry_legacy:experience` | fluid |
| `create_enchantment_industry_legacy:hyper_experience` | fluid |
| `create_enchantment_industry_legacy:ink` | fluid + block |
