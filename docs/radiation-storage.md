# Radiation from stored items

Voltaic reads physical contents. Opening a GUI is not required. A grid, terminal,
controller or external storage connection does not reproduce its network's total
as another radiation source.

## Supported contents

| Holder | Behaviour |
| --- | --- |
| Vanilla `Container` block entity | Local slots; each double chest half is read once. Unopened loot is left unopened. |
| Item frame or glow frame | Displayed stack and its contents. Separate frame UUIDs can share a block. |
| AE2 19.2.18 drive or ME chest | Original live basic cell inventories, including unsaved changes and offline stock; the ME chest's local input buffer also counts. Creative and nonbasic proxy cells are excluded. |
| Refined Storage 2.0.9 disk drive or storage block | Finite physical repository storage. Network power and access filters do not hide the contents. Creative and unbounded stores are excluded. |
| Capability inventory in `voltaic:radiation/local_item_inventories` | One local `IItemHandler` view, with native slots and specialised readers taking precedence. |
| Shulker box, bundle, supported filled AE2 cell or RS disk | Contents are read in containers, frames, dropped items and carried items. |
| Player inventory | Main inventory, hotbar, armour and offhand once; full direct dose every server tick. Open menus, ender chests and remote GUI slots are not carried inventory. |
| Other living entities | Carried equipment slots receive direct exposure. |

AE2 and Refined Storage remain optional. Compatibility code loads only when its
mod is present. Exact compilation versions are pinned in `libs.versions.toml`;
the optional dependency ranges match their supported major versions.

Container scans happen once every 20 ticks, staggered by loaded chunk. The delay
also applies to hopper transfers and network insertion or extraction. No chunks
are loaded to inspect storage. Container sources expire if refreshes stop and
are cleared immediately on chunk unload. They are rebuilt after loading and are
not saved to disk.

Frames and dropped items refresh their identified world source every tick.
Contents calculations are cached for up to 20 ticks, including changes to a disk
repository that leave its item components unchanged. Moving, replacing or
changing a stack invalidates that holder's cache immediately. Exposure still
applies every tick. Carrying an item gives direct dose to its carrier; it does
not add a world source around that carrier.

## Quantity, range and bounds

Amounts use `long` item quantities and finite `double` totals. Work scales with
item types rather than the number of individual items. An item key's sample
stack keeps its components and its quantity is passed separately. The summed
amount and strongest material penetration determine the emission.

`radiation.maxItemRadiationRange` defaults to 256 blocks and accepts 0 to 1024.
The amount keeps increasing after the radius reaches this limit. Existing
machine emission helpers keep their caller supplied ranges. Effect durations
and received totals saturate instead of overflowing.

Contained items are traversed through at most three nesting levels and 1024
nested stacks or item keys per calculation. Contents beyond these limits are
not traversed. Repeated backing storage references are read once within a
holder calculation, preventing cycles and duplicate references from multiplying
the emission. Independent physical stores still emit independently.

## Other local capability inventories

The shipped block tag contains optional entries for Sophisticated Storage and
Iron Chests physical containers. Native `Container` implementations work without
this tag. A modpack can add another proven local storage block with a data pack:

`data/voltaic/tags/block/radiation/local_item_inventories.json`

```json
{
  "replace": false,
  "values": ["examplemod:physical_chest"]
}
```

Do not tag network interfaces, controllers, external storage connectors or ghost
inventories. Voltaic selects the unsided view first, or the first available face;
it never sums faces. A storage with disjoint sided inventories needs its own
`IRadiationContentsReader` registered with `RadiationEmitterHandler.registerReader`.

## Containment and extension APIs

Surrounding shielding participates in the existing ray tracing. The source's own
block is skipped by the tracer, so a container does not gain shielding from its
appearance.

A block entity or block can implement `IRadiationContainment`. A block entity's
policy takes precedence over its block. An item can implement
`IRadiationItemContainment` to attenuate contained items. Fractions are clamped
from zero to one, with NaN treated as unshielded. Each holder's policy is applied
once before world shielding or direct carried exposure. Nested shielded holders
can each attenuate their own contents. No existing AE2 or RS container is assumed
to be shielded.

Add another physical block reader using `IRadiationContentsReader`, or a contained
item reader using `IRadiationItemContentsReader` and
`RadiationUtils.registerItemReader`. The first matching reader owns that holder.
Use `ItemEmissionAccumulator.add(sampleStack, longQuantity)` to preserve components
and `readStorage(identity, reader)` for repeated backing references. Readers must
be read only and must not generate loot, extract stock or enumerate a network's
storage at every interface.

## Validation

Java 21 is required. CI runs the ordinary build and four GameTest combinations:

```sh
./gradlew build
./gradlew runGameTestServer -PradiationTestMods=none
./gradlew runGameTestServer -PradiationTestMods=ae2
./gradlew runGameTestServer -PradiationTestMods=rs
./gradlew runGameTestServer -PradiationTestMods=both
```

The separate `gameTest` source set supplies a test mod with radioactive paper and
an empty structure; these are excluded from the distributed mod. Each storage
combination uses its own world under `runs/radiation-*`. Tests cover
large quantities, nested items, source replacement and expiry, local chest
halves, extraction, independent frames, dropped contents, repeated carried dose,
capability opt in, offline drives, live AE2 contents and repeated RS references.

Before release, playtest hopper movement, moving cells between storage systems,
unloading and reloading chunks, server restart, configuration disabling and
containment with surrounding lead walls. Include a populated area with many
stores and players. Use `/debug start` and `/debug stop` to inspect
`voltaicRadiationContainers` and `voltaicRadiationContents`; compare empty stores,
one very large quantity, many item types and many independent sources. Record
tick cost and shielding cost before changing the radius default. Automated
fixtures do not establish a multiplayer performance budget.
