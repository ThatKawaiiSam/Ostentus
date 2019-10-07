# Nametags
```java
new NametagHandler(HubPlugin.getProvidingPlugin(HubPlugin.class), player -> {
    List<BufferedNametag> tags = new ArrayList<>();
    for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
        String ting = PermissionsModule.getHook().getNameOfGroup(loopPlayer);
        String colour = groups.get(ting);
        if (colour != null) {
            tags.add(
                //Buffered Nametag Object stores the target players name, the prefix, suffix, if you want health and the target players player object.
                new BufferedNametag(
                    loopPlayer.getName(),
                    MessageUtility.formatMessage(colour),
                    "",
                    false,
                    loopPlayer
                )
            );
        } else {
            tags.add(
                new BufferedNametag(
                    loopPlayer.getName(),
                    MessageUtility.formatMessage(unknownString),
                    "",
                    false,
                    loopPlayer
                )
            );
        }
    }
    return tags;
});
```

### Contact
- MC-Market: https://www.mc-market.org/members/53967/
- Discord: ThatKawaiiSam#1337
- Twitter: ThatKawaiiSam
- Telegram: ThatKawaiiSam
