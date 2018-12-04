package io.github.thatkawaiisam.nametags;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

@Getter @Setter
public class NametagHandler {

    private NametagAdapter adapter;
    private JavaPlugin plugin;

    public NametagHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        NametagThread thread = new NametagThread(this, 100);
        thread.start();
    }

    public NametagHandler(JavaPlugin plugin, NametagAdapter adapter) {
        this(plugin);
        this.adapter = adapter;
    }

    public void update(Player player) {
        if (adapter == null) {
            return;
        }
        List<BufferedNametag> nametags = adapter.getPlate(player);
        Scoreboard sb = player.getScoreboard();
        for (BufferedNametag bufferedNametag : nametags) {
            Team team = sb.getTeam(bufferedNametag.getGroupName());
            if (team == null) {
                team = sb.registerNewTeam(bufferedNametag.getGroupName());
            }
            team = sb.getTeam(bufferedNametag.getGroupName());
            if (bufferedNametag.getPrefix() != null) {
                team.setPrefix(bufferedNametag.getPrefix());
            } else {
                team.setPrefix(ChatColor.WHITE.toString());
            }
            if (bufferedNametag.getSuffix() != null) {
                team.setSuffix(bufferedNametag.getSuffix());
            } else {
                team.setSuffix(ChatColor.WHITE.toString());
            }
            if (team != null && bufferedNametag.getPlayer() != null && bufferedNametag.getPlayer().isOnline()) {
                team.addPlayer(bufferedNametag.getPlayer());
            }
            if (team != null && player != null && player.isOnline()) {
                team.addPlayer(player);
            }
        }
    }
}
