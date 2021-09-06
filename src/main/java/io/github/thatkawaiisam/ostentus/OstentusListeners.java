package io.github.thatkawaiisam.ostentus;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
public class OstentusListeners implements Listener {

    private Ostentus ostentus;

    /**
     * Nametag Listeners.
     *
     * @param ostentus instance.
     */
    public OstentusListeners(Ostentus ostentus) {
        this.ostentus = ostentus;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.ostentus.getBoards().putIfAbsent(event.getPlayer().getUniqueId(), new OstentusBoard(event.getPlayer(), this.ostentus));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        OstentusBoard board = this.ostentus.getBoards().get(event.getPlayer().getUniqueId());

        if (board == null) {
            return;
        }

        board.cleanup();
        this.ostentus.getBoards().remove(event.getPlayer().getUniqueId());
        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

}
