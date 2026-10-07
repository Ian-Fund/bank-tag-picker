# v0.2.1 efficiency improvements

- Cache each tag's lowercase search name when tag data is loaded.
- Replace a full fuzzy-distance matrix per tag with three reusable integer rows owned by the synchronized model. Matching work still scales with name length times query length; the change reduces allocation and memory use.
- Reuse immutable model snapshots while the visible state is unchanged.
- Reuse popup and bank-side layout geometry until dimensions, settings, row count, or mode change.
- Reuse the painter and derived theme colors until a color setting, profile, or plugin lifecycle change requires a rebuild.
- Refresh tag data only for relevant search/order/history settings and Bank Tags changes. Appearance and geometry settings no longer reload the tag repository.
- Remove unused Inventory Setups message constants, imports, and EventBus injection. Tested legacy parsing and section helpers remain; they are not enabled as runtime features.

## Local search measurement

Windows, JDK 21.0.12. Compare the v0.2 full-matrix algorithm plus per-tag lowercase conversion with the new cached lowercase names and reused workspace. Synthetic tag names cover Vorkath, Slayer, Chambers, Zulrah, ToA, and Barrows with numeric suffixes. Six queries exercise typo-distance calculation, including misses. Each size has 100 warm-up scans per variant and six alternating rounds of 100 measured scans per variant. Time is wall-clock time; allocation is the executing thread's allocated bytes.

| Tags | Original ms/scan | v0.2.1 ms/scan | Original allocated bytes/scan | v0.2.1 allocated bytes/scan |
| --- | ---: | ---: | ---: | ---: |
| 100 | 0.1065 | 0.0487 | 61,859 | 0 |
| 500 | 0.5146 | 0.2513 | 317,783 | 0 |
| 1,000 | 0.9715 | 0.5057 | 638,002 | 0 |

This isolates the fuzzy-distance scan, not the entire filtering, rendering, or client workload. Zero allocation applies only to this warmed-up scan; the plugin still allocates result lists and other objects when its state changes. These are single-machine measurements, not an FPS guarantee or a rigorous cross-machine benchmark.

## Verification

The original functional tests are retained. Additional tests compare 10,000 deterministic random name/query pairs against the original full-matrix distance, including workspace reuse with changing lengths. Other checks cover snapshot immutability, geometry and hit-testing across resizing/settings/mode changes, and live color invalidation without a tag reload.

Run `./gradlew test jar` to verify and build; run `./gradlew runClient` for the final in-game pass. No publishing action is part of this package.
