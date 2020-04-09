package io.github.thatkawaiisam.nametags;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class NametagBoard {

    @Getter private final UUID uuid;
    @Getter private Set<String> bufferedTeams = new HashSet<>();
    @Getter private Map<String, List<String>> bufferedPlayers = new HashMap<>();
    @Getter private NametagHandler handler;

    public NametagBoard(Player player, NametagHandler handler) {
        this.uuid = player.getUniqueId();
        this.handler = handler;
        this.setup(player);
    }

    private void setup(Player player) {
        Scoreboard scoreboard = getScoreboard();

        // Update scoreboard
        player.setScoreboard(scoreboard);
    }

    public Scoreboard getScoreboard() {
        Player player = Bukkit.getPlayer(getUuid());
        if (getHandler().isHook() || player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
            return player.getScoreboard();
        } else {
            return Bukkit.getScoreboardManager().getNewScoreboard();
        }
    }

    private void cleanup() {
        bufferedPlayers.clear();
        bufferedTeams.clear();
    }

}

