# Windows local test

Use JDK 21 with Gradle 8.10. Ensure JAVA_HOME points to your JDK installation. Close your regular RuneLite client before starting the development client.

From PowerShell in this extracted project folder:

```powershell
.\gradlew.bat test jar
.\gradlew.bat runClient
```

The first command checks the existing tests and builds the JAR. The second starts RuneLite in developer mode with Bank Tag Picker loaded. Keep PowerShell open; close the development client when finished. Gradle may download dependencies on the first run.

## Final manual checklist

1. Enable Bank Tags and Bank Tag Picker. Check that the open hotkey is G and Require Ctrl/Cmd is enabled; saved settings can override defaults.
2. Open a bank and confirm normal bank interactions.
3. Press Ctrl+G. Type part of a tag name and confirm the results filter without duplicated characters.
4. Use Up/Down to move; press Enter to open the selected tag. Check a tag with a saved layout and one without.
5. Open the already selected tag again and confirm it stays open.
6. Check Escape and Ctrl+G close the picker and restore normal typing.
7. If enabled, check the searchable list beside the bank, mouse selection, and current-tag highlight.
8. Inspect icon.png and docs/icons/icon-16.png for legibility. The Plugin Hub icon cannot be verified through a Hub listing until the plugin is submitted. This local package adds no sidebar button or custom configuration-list icon.

## Changes in this package

Version is 0.2.1. This release keeps the v0.2 features and selected icon, caches normalized search names, reuses fuzzy-matching buffers and unchanged drawing state, and avoids tag reloads for appearance settings. Author: Ian-Fund. The README credits akump for the inspiration and base code and discloses AI assistance.

Local verification uses JDK 21.0.12 / Gradle 8.10 and cached dependencies. Existing tests and additional matching, snapshot, geometry, and live color tests cover the optimization changes. See PERFORMANCE.md for results. In-game interactions still require the manual pass above.

After this update, especially recheck fuzzy search, live color changes, window resizing, popup row count, bank-side width/position, collapse/expand, profile switching, and recent tag ordering.
