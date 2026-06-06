## Version 1.11.0 (06-Jun-2026)

### Changes

- **Installer overhaul**: Refactored the monolithic `AutoExtract` installer (844 lines) into 5 focused classes
  (`Installer`, `InstallerDialog`, `Extractor`, `ShortcutManager`, `OsUtil`).
- **Reduced confirmation dialogs** from 5 user prompts down to 1 — a single "Install to" dialog with Proceed/Cancel.
- **Removed** license agreement dialog, start-menu shortcut prompt, and Windows file associations prompt.
- **Removed** Windows `.battle`/`.br` file association registration (rarely used, required admin privileges).
- **Removed** legacy code: Win 9x/ME `command.com` path, obsolete `.robotcache` migration, fragile self-cleanup hacks.
- Windows shortcuts are now always created without prompting.
- Improved code quality: try-with-resources, `StringBuilder`, proper error handling, fixed spinner animation bug.

### Bugfix

- #89: Prevent a single fire command from spawning duplicate bullets with the same ID
    - A single `setFire()` call could spawn multiple bullets sharing the same bullet ID, which could cause issues with
      bullet tracking and battle accuracy.
    - Thanks go to [Pavel Savara](https://github.com/pavelsavara) for fixing this ❤️. Sharp-eyed fix!
