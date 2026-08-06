# Changelog

## 1.5.0 — Port to Minecraft 1.21.1 / NeoForge

The 1.20.1 feature set of Create: Enchantment Industry, brought to 1.21.1 on NeoForge.

### Identity
- Mod id is `create_enchantment_industry_legacy` and the root package is
  `plus.dragons.createenchantmentindustrylegacy`, so this fork can be installed alongside the official
  `create_enchantment_industry`. All blocks, items, fluids, tags, recipes and advancements live under the
  new namespace, so existing worlds and packs built against the official mod will not find them under the
  old ids.

### Platform
- Migrated from Forge to NeoForge (21.1.180+) and from Create 6.0.8 to Create 6.0.10+.
- Build system moved from ForgeGradle to ModDevGradle; `mods.toml` is now a templated
  `neoforge.mods.toml`.

### API migrations
- Item NBT replaced by data components. The Enchanting Guide's target is now the
  `create_enchantment_industry_legacy:enchanting_target` component instead of loose `target`/`index` tags.
- Enchantments are data driven: everything works on `Holder<Enchantment>`, curses are detected via the
  `#minecraft:curse` tag, and the removed `Enchantment.Rarity` is mapped back from enchantment weight so
  experience costs stay identical.
- Forge capabilities replaced by NeoForge block capabilities. The furnace experience output no longer
  needs a `getCapability` mixin, it is registered against the vanilla furnace block entity types.
- Networking rewritten from `SimpleChannel` to NeoForge payloads.
- Advancement criteria rewritten for the codec based trigger system; triggers now live in the
  `minecraft:trigger_type` registry. Advancement icons and external criteria are resolved lazily so the
  triggers can be registered before the registry events fire.
- Written book copying uses `WrittenBookContent#tryCraftCopy` instead of editing the `generation` tag.
- Block interaction split into `useWithoutItem` / `useItemOn`.

### Data
- Datapack layout updated for 1.21 (`recipe`, `advancement`, `loot_table`, singular tag folders).
- Recipe JSON updated to the new ingredient/result format, fluid ingredients now use
  `neoforge:single` / `neoforge:tag`.
- Recipe conditions moved to the `neoforge:conditions` field, so integrations for absent mods are skipped
  instead of failing to parse.
- The `forge` common tag namespace became `c`.
- Generated lang now includes the interface, tooltip and advancement strings again.

### Changed behaviour
- Apotheosis' Potion of Knowledge mixing recipe is injected into Create's per-level potion mixing list
  rather than a static registry, and is only added when the potion actually exists.
- `ancient_tome` printing and Apotheosis tome blacklisting also recognise the `apothic_enchanting`
  namespace used on 1.21.1.
