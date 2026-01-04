# Connect Notify

**Discord notifications for your Minekube Connect server status.**

Let your friends know when your local Minecraft server is up and ready to join! This plugin sends a Discord webhook message when your server starts or stops, including the Connect endpoint so players can easily connect.

## Features

- 🟢 **Online notifications** – Sends a message when your server starts
- 🔴 **Offline notifications** – Sends a message when your server stops
- 🔗 **Automatic endpoint** – Reads your endpoint from [Minekube Connect](https://connect.minekube.com) config
- 🎨 **Rich embeds** – Beautiful Discord embed messages with customizable colors
- 📢 **Multiple webhooks** – Notify multiple Discord channels or servers at once
- ⚡ **Zero config required** – Just paste your Discord webhook URL(s) and go!

## Requirements

- Minecraft server running Spigot, Paper, or compatible forks (1.8+)
- [Minekube Connect plugin](https://connect.minekube.com/guide/connectors/plugin) installed and configured

## Installation

1. Download the latest `ConnectNotify.jar` from [Releases](https://github.com/minekube/connect-notify/releases)
2. Drop it into your server's `plugins/` folder
3. Start your server once to generate the config
4. Edit `plugins/ConnectNotify/config.yml` and paste your Discord webhook URL
5. Restart your server – done!

## Configuration

```yaml
# plugins/ConnectNotify/config.yml

# Discord webhook notifications
discord:
  # List of webhook URLs to send notifications to
  # Create one: Right-click channel > Edit Channel > Integrations > Webhooks
  webhooks:
    - 'https://discord.com/api/webhooks/...'
    # - 'https://discord.com/api/webhooks/...'  # Add more webhooks here

  # Bot appearance (optional)
  username: 'Connect Notify'
  avatar-url: 'https://connect.minekube.com/img/logo.png'

# Message settings
messages:
  # Online message - sent when server starts
  online:
    enabled: true
    title: 'Server Online! 🟢'
    description: 'The server is now up and running.'
    color: '#00ff00'
    show-endpoint: true
    endpoint-text: 'Connect with: `{endpoint}`'

  # Offline message - sent when server stops
  offline:
    enabled: true
    title: 'Server Offline 🔴'
    description: 'The server has been shut down.'
    color: '#ff0000'
```

### Placeholders

| Placeholder     | Description                                                           |
| --------------- | --------------------------------------------------------------------- |
| `{endpoint}`    | Your Minekube Connect endpoint (e.g., `yourserver.play.minekube.net`) |
| `{server-name}` | Your server name from Connect config                                  |
| `{players}`     | Current online player count                                           |
| `{max-players}` | Maximum player slots                                                  |

## Creating a Discord Webhook

**Option A: Via Channel Settings (easiest)**

1. Right-click the channel where you want notifications
2. Click **Edit Channel** → **Integrations** → **Webhooks**
3. Click **New Webhook**
4. Copy the **Webhook URL**

**Option B: Via Server Settings**

1. Go to **Server Settings** → **Integrations** → **Webhooks**
2. Click **New Webhook**
3. Select the channel for notifications
4. Copy the **Webhook URL**

Then add the URL to `discord.webhooks` in `config.yml`.

> **Tip:** You can add multiple webhooks to notify different channels or servers!

## How It Works

```
┌─────────────────────────────────────────────────────────────┐
│  Your Minecraft Server                                      │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Minekube Connect│───▶│ Connect Notify (reads endpoint) │ │
│  │ config.yml      │    └──────────────┬──────────────────┘ │
│  │ endpoint: xyz   │                   │                    │
│  └─────────────────┘                   │ HTTP POST          │
└────────────────────────────────────────┼────────────────────┘
                                         ▼
                              ┌──────────────────┐
                              │ Discord Webhooks │
                              │ #server-status   │
                              │ #announcements   │
                              │ ...              │
                              └──────────────────┘
```

## Example Discord Messages

**Online:**

> ### Server Online! 🟢
>
> The server is now up and running.
>
> Connect with: `myserver.play.minekube.net`

**Offline:**

> ### Server Offline 🔴
>
> The server has been shut down.

## Troubleshooting

### No messages in Discord?

1. Check that your webhook URLs are correct under `discord.webhooks` in `config.yml`
2. Make sure the webhook(s) haven't been deleted in Discord
3. Check server console for error messages
4. Verify Minekube Connect is loaded and has a valid endpoint

### Wrong endpoint showing?

The plugin reads from `plugins/connect/config.yml`. Make sure Connect is properly configured and your endpoint is set.

## Building from Source

```bash
git clone https://github.com/minekube/connect-notify.git
cd connect-notify
./gradlew build
```

The built jar will be in `build/libs/`.

## License

MIT License – See [LICENSE](LICENSE) for details.

## Links

- [Minekube Connect](https://connect.minekube.com) – Free public addresses for your Minecraft server
- [Discord Webhooks Guide](https://support.discord.com/hc/en-us/articles/228383668-Intro-to-Webhooks)
- [Report Issues](https://github.com/minekube/connect-notify/issues)

---

<p align="center">
  Made with ❤️ by <a href="https://minekube.com">Minekube</a>
</p>
