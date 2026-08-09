# Proxied Warp

[GitHub](https://github.com/mrcl-at/proxied_warp) - [Hangar](https://hangar.papermc.io/mrcl/proxied_warp) - [Modrinth](https://modrinth.com/plugin/proxied-warp) - [SpigotMC](https://www.spigotmc.org/resources/proxied-warp.137397/) - [Discord](https://discord.gg/8pSzR4XYPw)

Proxied Warp allows players to teleport **across servers** within a Velocity or BungeeCord network by triggering an existing warp plugin (e.g., Essentials, or similar) on the target server. The player doesn't need to manually switch servers – Proxied Warp automatically connects them to the target server and then executes the warp command there.

The project consists of four modules:

| Module       | Task                                                                                                                                                                                                                                                |
|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `velocity`   | Forwards warp requests between backend servers and connects players to the target server when needed                                                                                                                                                |
| `paper`      | Receives the `/proxiedwarp` command from the player and executes the actual warp command after the server connection. For **Paper** backend servers. Supports [MiniMessage](https://docs.papermc.io/adventure/minimessage/) formatting for messages |
| `spigot`     | Same functionality as the `paper` module, but for **Spigot** backend servers. Messages use legacy Minecraft color codes (`&`) instead of MiniMessage                                                                                                |
| `bungeecord` | BungeeCord proxy module that forwards warp requests and connects players to the target server                                                                                                                                                       |

## How It Works

Communication between the proxy and backend servers happens via the shared **Plugin Messaging Channels** `proxied_warp:warp` and `proxied_warp:message`.

The process works as follows:

1. **Player executes command** – On a backend server, the player calls the registered command with a target server and warp name (e.g., `/proxiedwarp lobby spawn`).
2. **Backend plugin sends message** – The plugin (`paper` or `spigot` module, depending on the server) first checks the input and permission, then builds the message `<player> <server> <warp>` and sends it as a plugin message over the channel to the proxy.
3. **Proxy plugin processes the request** (Velocity or BungeeCord):
   - Checks whether the specified player and target server exist.
   - If the player is already on the target server, the message is forwarded directly.
   - Otherwise, a connection to the target server is established first, and the message is only forwarded after the server switch succeeds.
4. **Target server executes the warp** – The plugin on the target server receives the forwarded message, checks whether the player is online there, and executes the configured warp command with the given warp name.

## Installation

1. Place the matching proxy module in the proxy's `plugins` folder: **`velocity`** for Velocity or **`bungeecord`** for BungeeCord.
2. Install the matching backend module on **every** backend server where players should be able to trigger or receive warps: the **`paper` module** on Paper servers, the **`spigot` module** on Spigot servers.
3. Restart the proxy and the affected backend servers.

## Configuration (Paper/Spigot Modules)

On first startup, the respective plugin generates a `config.yml` with the following default values:

**`paper` module:**

```yaml
command-prefix: warp
need-permission: false
whitelisted-servers: []
whitelisted-warps: []
aliases:
  spawn: lobby
message:
  usage: '<red>Usage:</red> /proxiedwarp <server/alias> [<warp>]'
  usage-other: '<red>Usage:</red> /proxiedwarpother <player> <server/alias> [<warp>]'
  permission: '<red>You do not have permission to do that!</red>'
  server-not-whitelisted: '<red>This server is not whitelisted!</red>'
  warp-not-whitelisted: '<red>This warp is not whitelisted!</red>'
  player-not-online: '<red>Player is not online!</red>'
  key-not-found: "<red>Key '<key>' not found!"
  server-not-found: '<red>Server not found!'
  connection-failed: '<red>Connection failed!'
```

**`spigot` module:**

```yaml
command-prefix: warp
need-permission: false
whitelisted-servers: []
whitelisted-warps: []
aliases:
  spawn: lobby
message:
  usage: '&cUsage:&r /proxiedwarp <server/alias> [<warp>]'
  usage-other: '&cUsage:&r /proxiedwarpother <player> <server/alias> [<warp>]'
  permission: '&cYou do not have permission to do that!'
  server-not-whitelisted: '&cThis server is not whitelisted!'
  warp-not-whitelisted: '&cThis warp is not whitelisted!'
  player-not-online: '&cPlayer is not online!'
  key-not-found: "&cKey &f'%s' &cnot found!"
  server-not-found: '&cServer not found!'
  connection-failed: '&cConnection failed!'
```

| Key                              | Description                                                                                                                                       |
|----------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `command-prefix`                 | Command that is executed on the target server to actually teleport the player to the warp point (e.g., the command of the installed warp plugin). |
| `need-permission`                | Determines whether players need an explicit permission to use `/proxiedwarp` for a specific server (see [Permissions](#permissions)).             |
| `whitelisted-servers`            | List of servers that are allowed to be used as a target. If empty, all servers are allowed.                                                       |
| `whitelisted-warps`              | List of warps that are allowed to be triggered. If empty, all warps are allowed.                                                                  |
| `aliases`                        | Section to define aliases for warps, mapping a simple name to a target server (e.g., `spawn: lobby`).                                             |
| `message.usage`                  | Message shown when the command is used incorrectly.                                                                                               |
| `message.permission`             | Message shown when permission is missing.                                                                                                         |
| `message.server-not-whitelisted` | Message shown when a server is not whitelisted.                                                                                                   |
| `message.warp-not-whitelisted`   | Message shown when a warp is not whitelisted.                                                                                                     |
| `message.usage-other`            | Message shown when `/proxiedwarpother` is used incorrectly.                                                                                       |
| `message.player-not-online`      | Message shown when the selected player is not online.                                                                                             |
| `message.key-not-found`          | Fallback message used when a configured message key is missing.                                                                                   |
| `message.server-not-found`       | Message shown when the target server cannot be found by the proxy.                                                                                 |
| `message.connection-failed`      | Message shown when the proxy cannot connect the player to the target server.                                                                       |

In the **`paper` module**, messages support [MiniMessage](https://docs.papermc.io/adventure/minimessage/) formatting (e.g. `<red>`, `<bold>`). In the **`spigot` module**, messages use legacy Minecraft color codes with `&` as the formatting character (e.g. `&c` for red) instead, since MiniMessage is not available there.

## Command

The following commands are available on the backend server (Paper or Spigot):

```
/proxiedwarp <server> <warp>
/proxiedwarp <alias>
/proxiedwarpother <player> <server> <warp>
/proxiedwarpother <player> <alias>
```

- `<server>` – Name of the target server, as registered in the proxy configuration (for example `velocity.toml`).
- `<warp>` – Name of the warp point on the target server.
- `<alias>` – An alias defined in the `config.yml` that maps to a specific server and warp.
- `<player>` – An online player who should be connected and sent to the warp.

`/proxiedwarp` can only be executed by players. `/proxiedwarpother` can also be executed from the console.

## Permissions

If `need-permission` is set to `true` in `config.yml`, a player needs one of the following permissions to execute `/proxiedwarp <server> <warp>`:

| Permission                | Effect                                                                  |
|---------------------------|-------------------------------------------------------------------------|
| `proxiedwarp.warp.*`      | Allows warping to **all** servers.                                      |
| `proxiedwarp.warp.<warp>` | Allows warping to a **specific** warp (e.g., `proxiedwarp.warp.spawn`). |

For `/proxiedwarpother`, the corresponding permissions are `proxiedwarp.warp.other.*` and `proxiedwarp.warp.other.<warp>`.

If both permissions are missing, the player receives the error message configured in `message.permission`, and the request is not sent to the proxy.

If `need-permission` is set to `false` (default), no permission check is performed and every player can use the command for every registered server.

## Requirements

- A Velocity or BungeeCord proxy with the matching proxy module (`velocity` or `bungeecord`).
- A **Paper** based backend server (with the `paper` module) or a **Spigot** based backend server (with the `spigot` module).
- A warp plugin on each involved backend server that can be triggered via command (not via API), and whose command can be entered in `command-prefix`.
- Java version 25 or higher.

## Known Limitations

- Proxied Warp itself does not check whether the specified warp actually exists – this is only checked by the invoked warp plugin on the target server.
