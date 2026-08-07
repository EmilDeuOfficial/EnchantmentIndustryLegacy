# FAQ

---

### Can I run this alongside the official Create: Enchantment Industry?

**No.** The mod declares `create_enchantment_industry` as `incompatible` in its metadata, so NeoForge refuses
to start with both installed and shows a message explaining why.

They are the same mod with different feature sets, carrying the same machines under different ids. Their
enhanced-experience systems do not interoperate either — Hyper Experience here, Super Experience there — so
running both would only give you two of everything. Pick one.

---

### Will my old world from the official mod work?

Not directly. This fork uses its own mod id and its own block, item and fluid ids, so a world built against
`create_enchantment_industry` will not find these machines under the old ids. Those blocks become air and
Minecraft warns about the missing ids.

Break your machines before switching, or use a datapack / id-remapping tool if you need to migrate an existing
base.

---

### The Printer doesn't seem to take my book — nothing happens?

It probably did take it. The Printer has **no visual** for its copy target; the block looks identical whether
it holds a book or not. Confirm with Engineer's Goggles, or point a Display Link at it and choose **Copy
Content**.

A sound plays when the target goes in or comes out. In creative mode the book also stays in your hand, since
creative never consumes it, which makes it look even more like nothing happened.

---

### The goggles say "Too Expensive!" — how do I fix it?

The cost of copying that book is higher than `copierTankCapacity`. Supplying more fluid does not help; the
whole job must fit in the tank at once.

Raise `copierTankCapacity` in the server config (default 4000, requires a server restart), or lower
`copyEnchantedBookCostCoefficient`.

---

### Why won't the Blaze Enchanter enchant my book?

By design — the Blaze Enchanter refuses books. Use the **Printer** to duplicate an enchanted book you already
have.

It also refuses to add an enchantment that conflicts with one already on the item, and Apotheosis tomes are
barred from it.

---

### How do I get a Blaze Enchanter? It has no recipe.

Correct, it has none. Sneak and right-click a **Blaze Burner** while holding an **Enchanting Guide**. To turn
it back into a Blaze Burner, sneak + right-click with a Wrench.

---

### How much is Liquid Experience worth?

1 mB = 1 experience point. Liquid Hyper Experience is 1 mB = 10 points.

---

### The Experience Rotor has no recipes in JEI.

The Rotor is a compatibility item: it converts *other mods'* experience fluids into Liquid Experience through
Mixing. Each of its eight recipes only loads when the corresponding mod (Thermal, EnderIO, Cyclic, Industrial
Foregoing, Mob Grinding Utils, PneumaticCraft, Reliquary, Sophisticated Core) is installed.

With none of them present the Rotor is craftable but does nothing. That is expected.

---

### Does this work with Forge, Fabric or Quilt?

No. This is NeoForge only. It uses NeoForge block capabilities, the NeoForge payload network and Create for
NeoForge, none of which exist on the other loaders.

---

### Does it work in singleplayer?

Yes. It works in singleplayer and on dedicated servers alike. The config is a server config, which in
singleplayer means it lives in the world folder.

---

### Can I disable Hyper-Enchanting?

Set `enableHyperEnchant = false` in the server config. The Blaze Enchanter then never enters its seething state
and produces no levels above the normal maximum. Liquid Hyper Experience still works as a cheaper input for
copying books.

For finer control, cap individual enchantments with `enchantmentLevelCaps` instead. See
[Configuration](Configuration).

---

### How do I stop players from farming XP with Deployers and Crushing Wheels?

Set `deployerXpDropChance = 0.0` and `crushingWheelDropExpRate = 0.0`. Both default to dropping Experience
Nuggets on kills.

---

### I edited the config and nothing changed.

Most values apply on the next machine tick. The three tank capacities — `disenchanterTankCapacity`,
`copierTankCapacity` and `blazeEnchanterTankCapacity` — are marked `RequiresRestart.SERVER` and need a restart.

If the file fails to load entirely, check `enchantmentLevelCaps`: every entry must contain a `=`, otherwise the
validator rejects it.
