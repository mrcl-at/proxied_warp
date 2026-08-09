# Changelog

## [1.3.0] - 2026-08-09

### Added
- **BungeeCord support**: Added a BungeeCord proxy module for forwarding warp requests and switching players between servers.
- **Warp other command**: Added `/proxiedwarpother` to trigger a warp for another online player.
- **Tab completion**: Added context-aware suggestions for players, servers, aliases, and warps.
- **Connection feedback**: Added configurable messages for missing players or servers and failed connections.

### Changed
- Refactored the Bukkit/Paper/Spigot command and plugin-message handling into shared components.
- Added a dedicated plugin-message channel for status and error messages.
- Improved proxy-side error handling and player connection handling.

## [1.2.0] - 2026-08-01

### Added
- **Whitelist System**: Optional whitelists for servers (`whitelisted-servers`) and warps (`whitelisted-warps`).
- **Warp Aliases**: Allows defining aliases in the configuration (e.g., `/proxiedwarp spawn` instead of `/proxiedwarp lobby spawn`).

### Changed
- Refactored the plugin architecture for better maintainability and extensibility.
