# Show all advancements
Minecraft server side mod that makes all advancements show up for clients.<br>

"Hidden" advancements are, well, hidden. By default. You can override
this by setting config option `showHiddens`.

Advancement IDs in the list `alwaysHiddenAdvs` will be hidden
regardless of the first option.

A default config file is created at launch, in `.minecraft/config`
directory. Example:

```json
{
  "showHiddens": true,
  "alwaysHiddenAdvs": [
      "minecraft:nether/all_effects"
  ]
}
```

Screenshot with BACAP and AdvancementInfo Reloaded:
[![2024-11-21-18-29-54.png](https://i.postimg.cc/J4myKHfR/2024-11-21-18-29-54.png)](https://postimg.cc/BtYQnbZz)
