# Permissions

This mod registers **no commands and no permission nodes**. There is nothing to grant in LuckPerms.

---

## Default permission levels

| Action | Level required | Note |
|---|---|---|
| Placing and using every machine | 0 | anyone who can build |
| Crafting every item | 0 | anyone |
| Editing the server config in game | 2 (OP) | Create's config screen only exposes server values to operators |
| Editing the config file on disk | — | server file access |

Permission levels: 0 = everyone, 1 = moderator, 2 = OP, 3 = OP+, 4 = server owner.

---

## Controlling access on a server

Since the machines are ordinary blocks, use whatever already governs block placement and interaction:

- **Claim and region mods** (FTB Chunks, Open Parties and Claims, …) — a player who cannot interact with blocks
  in a claim cannot use the machines there either.
- **Removing content entirely** — a datapack that removes the crafting recipes stops players from obtaining the
  machines. The Blaze Enchanter has no recipe at all; it is made by right-clicking a Blaze Burner with an
  Enchanting Guide while sneaking, which cannot be disabled by recipe removal.
- **Config caps** — `enchantmentLevelCaps` and `maxHyperEnchantingLevelExtension` bound what players can reach
  regardless of permissions. See [Configuration](Configuration).

---

## What is worth restricting

| Concern | Why | Lever |
|---|---|---|
| Infinite enchanted books | The Printer copies any book it is given | `copyEnchantedBookCostCoefficient`, `copierTankCapacity` |
| Levels above vanilla maximum | Hyper-Enchanting adds levels | `enableHyperEnchant = false`, or `enchantmentLevelCaps` |
| XP farms via Deployer or Crushing Wheels | Both drop Experience Nuggets on kills | `deployerXpDropChance`, `crushingWheelDropExpRate` |
