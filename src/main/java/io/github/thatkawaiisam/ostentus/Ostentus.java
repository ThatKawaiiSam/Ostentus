package io.github.thatkawaiisam.ostentus;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class Ostentus {

    private JavaPlugin plugin;

    private OstentusAdapter adapter;
    private OstentusThread thread;
    private OstentusListeners listeners;

    private Map<UUID, OstentusBoard> boards;
    private long ticks = 2;
    private boolean hook = false;

    /**
     * Ostentus Handler.
     *
     * @param plugin instance.
     * @param adapter to display nametags.
     */
    public Ostentus(JavaPlugin plugin, OstentusAdapter adapter) {
        if (plugin == null) {
            throw new RuntimeException("Ostentus can not be instantiated without a plugin instance!");
        }

        this.plugin = plugin;
        this.adapter = adapter;
        this.boards = new ConcurrentHashMap<>();

        this.setup();
    }

    /**
     * Setup Ostentus.
     */
    public void setup() {
        // Register Events.
        this.listeners = new OstentusListeners(this);
        this.plugin.getServer().getPluginManager().registerEvents(this.listeners, this.plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.boards.putIfAbsent(player.getUniqueId(), new OstentusBoard(player, this));
        }

        this.thread = new OstentusThread(this);
    }

    /**
     * Cleanup Ostentus.
     */
    public void cleanup() {
        // Unregister Thread.
        if (this.thread != null) {
            this.thread.stop();
            this.thread = null;
        }

        // Unregister Listeners.
        if (this.listeners != null) {
            HandlerList.unregisterAll(this.listeners);
            this.listeners = null;
        }

        // Destroy boards.
        for (UUID uuid : getBoards().keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                continue;
            }

            this.boards.remove(uuid);
            if (!this.hook) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }
    }

}
