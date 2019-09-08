package io.github.thatkawaiisam.nametags;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class NametagBoard {

    private Set<BufferedNametag> currentEntries = new HashSet<>();
    private final UUID uuid;
    private Scoreboard scoreboard;
    private Set<String> bufferedTeams = new HashSet<>();

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

        //Send Event
    }

    private void cleanup() {

    }

}

