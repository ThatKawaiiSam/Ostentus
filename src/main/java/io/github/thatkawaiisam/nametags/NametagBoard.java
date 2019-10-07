package io.github.thatkawaiisam.nametags;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

@Getter
public class NametagBoard {

    private final UUID uuid;
    private Scoreboard scoreboard;
    private Set<String> bufferedTeams = new HashSet<>();
    private Map<String, List<String>> bufferedPlayers = new HashMap<>();
    private NametagHandler handler;

    public NametagBoard(Player player, NametagHandler handler) {
        this.uuid = player.getUniqueId();
        this.handler = handler;
        this.setup(player);
    }

    private void setup(Player player) {
        // Register new scoreboard if needed
        if (getHandler().isHook() || !(player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard())) {
            this.scoreboard = player.getScoreboard();
        } else {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        // Update scoreboard
        player.setScoreboard(this.scoreboard);
    }

    private void cleanup() {
        bufferedPlayers.clear();
        bufferedTeams.clear();
    }

}

