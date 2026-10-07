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
   Set **Recently used** to 5. Click two different standard Bank Tags tabs, then open the picker and confirm the last clicked tag is first with a clock. Switch back to the first tag and confirm it moves to the top without duplicates. Leave a tag open and confirm the order stays stable. Set Recently used to 0 and confirm further clicks do not change history. Switch profiles and confirm an already active tag is not added just by switching.
8. Inspect icon.png and docs/icons/icon-16.png for legibility. The Plugin Hub icon cannot be verified through a Hub listing until the plugin is submitted. This local package adds no sidebar button or custom configuration-list icon.

## Changes in this package

Version is 0.2.2. This release adds recent-history tracking for tags opened through the standard Bank Tags tabs, reusing the existing active-tag check. It keeps the v0.2 features, selected icon, and v0.2.1 optimizations. Author: Ian-Fund. The README credits akump for the inspiration and base code and discloses AI assistance.

Local verification uses JDK 21.0.12 / Gradle 8.10 and cached dependencies. Tests cover native tag transitions, repeated ticks without repeated history reads/writes, disabled tracking, empty active tags, profile resets, and picker selection deduplication, plus the existing matching, snapshot, geometry, and live color checks. See PERFORMANCE.md for the v0.2.1 optimization results. In-game interactions still require the manual pass above.

After this update, especially recheck fuzzy search, live color changes, window resizing, popup row count, bank-side width/position, collapse/expand, profile switching, and recent tag ordering.
