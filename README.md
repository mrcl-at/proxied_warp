# Proxied Warp

Proxied Warp allows players to teleport **across servers** within a Velocity network by triggering an existing warp plugin (e.g., Essentials, or similar) on the target server. The player doesn't need to manually switch servers – Proxied Warp automatically connects them to the target server and then executes the warp command there.

The project consists of two parts:

| Module     | Task                                                                                                                 |
|------------|----------------------------------------------------------------------------------------------------------------------|
| `velocity` | Forwards warp requests between backend servers and connects players to the target server when needed                 |
| `paper`    | Receives the `/proxiedwarp` command from the player and executes the actual warp command after the server connection |

## How It Works

Communication between the proxy and backend servers happens via a shared **Plugin Messaging Channel**: `proxied_warp:proxied_warp`.

The process works as follows:

1. **Player executes command** – On a backend server, the player calls the registered command with a target server and warp name (e.g., `/proxiedwarp lobby spawn`).
2. **Paper plugin sends message** – The Paper plugin first checks the input and permission, then builds the message `<player> <server> <warp>` and sends it as a plugin message over the channel to the proxy.
3. **Velocity plugin processes the request**:
    - Checks whether the specified player and target server exist.
    - If the player is already on the target server, the message is forwarded directly.
    - Otherwise, a connection to the target server is established first, and the message is only forwarded after the server switch succeeds.
4. **Target server executes the warp** – The Paper plugin on the target server receives the forwarded message, checks whether the player is online there, and executes the configured warp command with the given warp name.

## Installation

1. Place the **Velocity module** as a `.jar` file in the `plugins` folder of the Velocity proxy.
2. Install the **Paper module** on **every** backend server where players should be able to trigger or receive warps.
3. Restart the proxy and the affected backend servers.

## Configuration (Paper Module)

On first startup, the Paper plugin generates a `config.yml` with the following default values:

```yaml
command-prefix: warp
need-permission: false
message:
  usage: '<red>Usage:</red> /proxiedwarp <server> <warp>'
  permission: '<red>You do not have permission to do that!</red>'
```

| Key                  | Description                                                                                                                                       |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `command-prefix`     | Command that is executed on the target server to actually teleport the player to the warp point (e.g., the command of the installed warp plugin). |
| `need-permission`    | Determines whether players need an explicit permission to use `/proxiedwarp` for a specific server (see [Permissions](#permissions)).             |
| `message.usage`      | Message shown when the command is used incorrectly.                                                                                               |
| `message.permission` | Message shown when permission is missing.                                                                                                         |

Messages support [MiniMessage](https://docs.papermc.io/adventure/minimessage/) formatting.

## Command

The following command is available on the backend server (Paper):

```
/proxiedwarp <server> <warp>
```

- `<server>` – Name of the target server, as registered in the Velocity configuration (`velocity.toml`).
- `<warp>` – Name of the warp point on the target server.

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
- A Paper based backend server.
- A warp plugin on each involved backend server that can be triggered via command (not via API), and whose command can be entered in `command-prefix`.
- Java version 25 or higher.

## Known Limitations

- Proxied Warp itself does not check whether the specified warp actually exists – this is only checked by the invoked warp plugin on the target server.