# Changelog

## 1.5.0 — Port to Minecraft 1.21.1 / NeoForge

The 1.20.1 feature set of Create: Enchantment Industry, brought to 1.21.1 on NeoForge.

### Identity
- Mod id is `create_enchantment_industry_legacy` and the root package is
  `plus.dragons.createenchantmentindustrylegacy`, so this fork can be installed alongside the official
  `create_enchantment_industry`. All blocks, items, fluids, tags, recipes and advancements live under the
  new namespace, so existing worlds and packs built against the official mod will not find them under the
  old ids.

### Fixed
- The Printer, Enchanting Guide and Experience Rotor recipes asked for `neoforge:plates/iron`,
  `neoforge:plates/obsidian` and `neoforge:ingots/zinc`, which do not exist - common tags moved to the
  `c` namespace on 1.21, not to `neoforge`. All three showed up as "Empty Tag" and the items were
  uncraftable. They now resolve to Iron Sheet, Sturdy Sheet and Zinc Ingot.
- The JEI category showed the raw key `...recipe.disenchanting`. Create 6 builds the title as
  `<namespace>.recipe.<path>` where 1.20.1 used `recipe.<namespace>.<path>`.
- The Display Link source names were registered under ids the mod no longer uses
  (`printer_source_copy_content`, `blaze_enchanter_source_target_enchantment`) and showed as raw keys
  too; they are now `copy_content` and `target_enchantment`. All twelve translations were moved along
  with the English entries.
- The Disenchanter, Printer and Blaze Enchanter were invisible in their own Ponder scenes. The scene
  structures are `.nbt` files that store block ids as plain strings, and those still named the machines
  under the old `create_enchantment_industry` namespace, so they loaded as air. Their stored block
  entity data - 1.18.2 era tank and item layouts that the 1.21.1 codecs cannot read - was dropped at
  the same time; every scene sets the tanks and target items it needs from the storyboard anyway.
- Ponder scenes showed raw lang keys such as `...ponder.transform.header` instead of their text. Scene
  titles and texts only exist inside the storyboards, and the lang generator never asked Ponder to
  replay them; the generated `en_us.json` now carries all 39 ponder entries. The translations already
  had them, so only English was affected.
- Handing the Printer a new copy target in creative mode duplicated items. Creative never consumes the
  item from the player's hand, but the previous target was still handed back, so every click added one
  more copy to the inventory. The old target is only returned now when the new one was actually taken.
- Inserting or taking the Printer's copy target plays a sound. The Printer has no visual for its target,
  so the interaction was silent and looked like nothing had happened - the target is still shown by
  Engineer's Goggles and by a Display Link.
- Throwing a Bottle O' Hyper Enchanting crashed the client with a null entity renderer, and the
  Disenchanter, Printer and Blaze Enchanter had no block entity renderer either. Registrate registers
  those client side through `OneTimeEventReceiver`, which drops the listener when the registrate does
  not know the mod event bus yet, so `registerEventListeners` now runs before anything is built.
- Opening the inventory crashed with "already exists in the tab's list". Registrate 1.21 defaults every
  entry to `CreativeModeTabs.SEARCH`, so listing the items in the tab's own `displayItems` added each of
  them a second time. The registrate's default tab is now pointed at this mod's tab and the manual list
  is gone.

### Compatibility
- Declared incompatible with the official `create_enchantment_industry`; the game refuses to start with an
  explanatory message if both are installed. The two carry the same machines under different ids and their
  enhanced-experience systems (Hyper Experience here, Super Experience there) do not interoperate.

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
