# Proxied Warp

Proxied Warp allows players to teleport **across servers** within a Velocity network by triggering an existing warp plugin (e.g., Essentials, or similar) on the target server. The player doesn't need to manually switch servers – Proxied Warp automatically connects them to the target server and then executes the warp command there.

The project consists of three parts:

| Module     | Task                                                                                                                 |
|------------|------------------------------------------------------------------------------------------------------------------------|
| `velocity` | Forwards warp requests between backend servers and connects players to the target server when needed                 |
| `paper`    | Receives the `/proxiedwarp` command from the player and executes the actual warp command after the server connection. For **Paper** backend servers. Supports [MiniMessage](https://docs.papermc.io/adventure/minimessage/) formatting for messages |
| `spigot`   | Same functionality as the `paper` module, but for **Spigot** backend servers. Messages use legacy Minecraft color codes (`&`) instead of MiniMessage |

## How It Works

Communication between the proxy and backend servers happens via a shared **Plugin Messaging Channel**: `proxied_warp:proxied_warp`.

The process works as follows:

1. **Player executes command** – On a backend server, the player calls the registered command with a target server and warp name (e.g., `/proxiedwarp lobby spawn`).
2. **Backend plugin sends message** – The plugin (`paper` or `spigot` module, depending on the server) first checks the input and permission, then builds the message `<player> <server> <warp>` and sends it as a plugin message over the channel to the proxy.
3. **Velocity plugin processes the request**:
   - Checks whether the specified player and target server exist.
   - If the player is already on the target server, the message is forwarded directly.
   - Otherwise, a connection to the target server is established first, and the message is only forwarded after the server switch succeeds.
4. **Target server executes the warp** – The plugin on the target server receives the forwarded message, checks whether the player is online there, and executes the configured warp command with the given warp name.

## Installation

1. Place the **Velocity module** as a `.jar` file in the `plugins` folder of the Velocity proxy.
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
  usage: '<red>Usage:</red> /proxiedwarp <server> <warp>'
  permission: '<red>You do not have permission to do that!</red>'
  server-not-whitelisted: '<red>This server is not whitelisted!</red>'
  warp-not-whitelisted: '<red>This warp is not whitelisted!</red>'
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
  usage: '&cUsage:&r /proxiedwarp <server> <warp>'
  permission: '&cYou do not have permission to do that!'
  server-not-whitelisted: '&cThis server is not whitelisted!'
  warp-not-whitelisted: '&cThis warp is not whitelisted!'
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

In the **`paper` module**, messages support [MiniMessage](https://docs.papermc.io/adventure/minimessage/) formatting (e.g. `<red>`, `<bold>`). In the **`spigot` module**, messages use legacy Minecraft color codes with `&` as the formatting character (e.g. `&c` for red) instead, since MiniMessage is not available there.

## Command

The following command is available on the backend server (Paper or Spigot):

```
/proxiedwarp <server> <warp>
/proxiedwarp <alias>
```

- `<server>` – Name of the target server, as registered in the Velocity configuration (`velocity.toml`).
- `<warp>` – Name of the warp point on the target server.
- `<alias>` – An alias defined in the `config.yml` that maps to a specific server and warp.

The command can only be executed by players (not from the console).

## Permissions

If `need-permission` is set to `true` in `config.yml`, a player needs one of the following permissions to execute `/proxiedwarp <server> <warp>`:

| Permission                | Effect                                                                  |
|---------------------------|-------------------------------------------------------------------------|
| `proxiedwarp.warp.*`      | Allows warping to **all** servers.                                      |
| `proxiedwarp.warp.<warp>` | Allows warping to a **specific** warp (e.g., `proxiedwarp.warp.spawn`). |

If both permissions are missing, the player receives the error message configured in `message.permission`, and the request is not sent to the proxy.

If `need-permission` is set to `false` (default), no permission check is performed and every player can use the command for every registered server.

## Requirements

- A Velocity proxy.
- A **Paper** based backend server (with the `paper` module) or a **Spigot** based backend server (with the `spigot` module).
- A warp plugin on each involved backend server that can be triggered via command (not via API), and whose command can be entered in `command-prefix`.
- Java version 25 or higher.

## Known Limitations

- Proxied Warp itself does not check whether the specified warp actually exists – this is only checked by the invoked warp plugin on the target server.