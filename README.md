# Bank Tag Picker

![Bank Tag Picker icon](icon.png)

A searchable launcher for RuneLite **Bank Tags** and the layouts attached to them.

**Author:** Ian-Fund

Bank Tag Picker was developed with assistance from OpenAI's ChatGPT and Codex for coding, testing, and documentation.

Bank Tag Picker was inspired by [Inventory Setups Picker](https://github.com/akump/inventory-setups-picker) by **akump**, whose project provided the base code for this plugin. It builds on that project's fast picker UI with a Bank Tags integration using RuneLite's built-in Bank Tags service.

## What it does

Press **Ctrl+G** on Windows/Linux or **Cmd+G** on macOS to open the picker. Start typing to filter your Bank Tag tabs, use the keyboard to choose one, and press `Enter` to open it. Selecting the tag that is already open simply keeps that tag selected, matching the normal tab behavior.

Opening a result uses RuneLite's `BankTagsService.openBankTag(...)`. If that Bank Tag has a saved custom layout, RuneLite loads the layout automatically. If it does not, the normal Bank Tag opens.

| Key | Action |
| --- | --- |
| Type | Filter Bank Tags by name |
| `Up` / `Down`, `Tab` / `Shift+Tab` | Move the selection |
| `Page Up` / `Page Down` | Move a page at a time |
| `Enter` | Open the selected Bank Tag |
| `Left` / `Right`, `Home` / `End` | Move through the search text |
| `Backspace` / `Delete` | Edit the search text |
| `Ctrl+A` / `Cmd+A` | Select the search text |
| `Esc`, the hotkey again, or clicking elsewhere | Cancel |

The picker can also show the same searchable list beside the bank. That can be disabled in the plugin settings. The currently open tag is highlighted, and while searching the title shows the number of matching tags.

## Data source

The picker reads the Bank Tag tabs RuneLite stores under the built-in Bank Tags configuration and reuses their configured item icons. It does not maintain a separate list of tags or layouts.

Layouts do not need special entries in the picker. A tag appears once; when it is opened, RuneLite applies its saved layout when one exists.

## Settings

The default hotkey is **G** with **Require Ctrl/Cmd** enabled, giving Ctrl+G on Windows/Linux and Cmd+G on macOS. The hotkey, popup size, sorting, fuzzy search, recent tags, bank-side list, and colors can all be customized.

## Development

`./gradlew runClient` starts RuneLite in developer mode with the plugin loaded.

`./gradlew test` runs the picker tests, including matching equivalence and rendering cache checks. Keyboard tests use the Ctrl+G / Cmd+G shortcut. Some tested Inventory Setups helpers remain in the code but are not used by the Bank Tag runtime path.

Version **0.2.1** reduces repeated search and drawing allocations while preserving the v0.2 controls and Bank Tags behavior. See [PERFORMANCE.md](PERFORMANCE.md) for the changes, measurements, and limits.

For Windows commands and the final local checklist, see [TESTING.md](TESTING.md).

## Icon assets

The selected gold tag and magnifying glass design is used for both the repository icon and the packaged RuneLite resources. The transparent padding was cropped before resizing; the artwork was retained.

- `icon.png`: 48x48 transparent PNG for Plugin Hub. RuneLite's Hub tooling discovers this root file; no extra icon property is needed in `runelite-plugin.properties`.
- `src/main/resources/com/setuppicker/icon.png`: the same 48x48 PNG, included in the plugin JAR.
- `src/main/resources/com/setuppicker/icon-16.png`: 16x16 resource for small displays.
- `docs/icons/`: 16, 32, 48, and 128 pixel previews of the selected artwork.

This plugin uses the in-game picker and has no sidebar navigation button. RuneLite's `PluginDescriptor` has no custom icon field, so packaging an icon resource does not add an icon to the local configuration list. The Hub listing uses the root icon after submission and review.

References: [Plugin Hub icon requirements](https://github.com/runelite/plugin-hub/blob/master/README.md), [RuneLite PluginDescriptor](https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/PluginDescriptor.java).

## Attribution

Special thanks to **akump**, the author of [Inventory Setups Picker](https://github.com/akump/inventory-setups-picker), for the inspiration and base code used to develop Bank Tag Picker. The picker UI and much of its interaction code originate from that project; the Bank Tags integration and adaptations were developed for Bank Tag Picker.

This project retains the original BSD 2-Clause license and copyright notice.
