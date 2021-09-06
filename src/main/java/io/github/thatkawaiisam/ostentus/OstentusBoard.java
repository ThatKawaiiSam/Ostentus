package io.github.thatkawaiisam.ostentus;

import lombok.Getter;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class OstentusBoard {

    private final UUID uuid;

    private Ostentus ostentus;

    private Set<String> bufferedTeams = new HashSet<>();
    private Map<String, List<String>> bufferedPlayers = new ConcurrentHashMap<>();

    /**
     * Nametag Board.
     *
     * @param player that board belongs to.
     * @param ostentus instance.
     */
    public OstentusBoard(Player player, Ostentus ostentus) {
        this.uuid = player.getUniqueId();
        this.ostentus = ostentus;
        this.setup(player);
    }

    /**
     * Setup Nametag Board.
     *
     * @param player that board belongs to.
     */
    private void setup(Player player) {
        Scoreboard scoreboard = this.getScoreboard();

        // Update Bukkit scoreboard.
        player.setScoreboard(scoreboard);
    }

    /**
     * Get Scoreboard Object.
     *
     * @return existing scoreboard if in hook, or create new one.
     */
    public Scoreboard getScoreboard() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (this.ostentus.isHook() || player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
            return player.getScoreboard();
        } else {
            return Bukkit.getScoreboardManager().getNewScoreboard();
        }
    }

    /**
     * Update Health Slot.
     *
     * @param player object.
     * @param scoreboard of player.
     */
    private void updateHealthBelow(Player player, Scoreboard scoreboard) {
        if (this.ostentus.getAdapter().showHealthBelowName(player)) {
            if (scoreboard.getObjective(DisplaySlot.BELOW_NAME) == null) {
                Objective objective = scoreboard.registerNewObjective("showhealth", "health");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objective.setDisplayName(ChatColor.RED + StringEscapeUtils.unescapeJava("\u2764"));
                // Ensures that 0 isn't displayed if they haven't lost health.
                for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
                    objective.getScore(loopPlayer).setScore((int) Math.floor(loopPlayer.getHealth()));
                }
            }
        } else {
            if (scoreboard.getObjective(DisplaySlot.BELOW_NAME) != null) {
                Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
                objective.unregister();
            }
        }
    }

    /**
     * Get's or creates a team for the scoreboard.
     *
     * @param scoreboard of team.
     * @param name of team.
     * @return new or existing team.
     */
    private Team getOrRegisterTeam(Scoreboard scoreboard, String name) {
        Team team = scoreboard.getTeam(name);

        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }

        return team;
    }

    /**
     * Update Nametags slot.
     *
     * @param player object.
     * @param scoreboard of player.
     */
    private void updateNametags(Player player, Scoreboard scoreboard) {
        List<BufferedNametag> nametags = this.ostentus.getAdapter().getPlate(player);

        if (nametags == null) {
            return;
        }

        Set<String> toReturn = new HashSet<>();
        Map<String, List<String>> strings = new HashMap<>();

        for (BufferedNametag bufferedNametag : nametags) {
            Team team = this.getOrRegisterTeam(scoreboard, bufferedNametag.getGroupName());

            toReturn.add(team.getName());
            this.getBufferedTeams().remove(team.getName());

            String prefix = bufferedNametag.getPrefix() != null ? bufferedNametag.getPrefix() : ChatColor.RESET.toString();
            String suffix = bufferedNametag.getSuffix() != null ? bufferedNametag.getSuffix() : ChatColor.RESET.toString();

            team.setPrefix(prefix);
            team.setSuffix(suffix);

            if (bufferedNametag.getPlayer() != null) {
                if (!team.hasEntry(bufferedNametag.getPlayer().getName())) {
                    team.addEntry(bufferedNametag.getPlayer().getName());
                }
                List<String> inner = new ArrayList<>();
                if (strings.containsKey(team.getName())) {
                    inner = strings.get(team.getName());
                }
                inner.add(bufferedNametag.getPlayer().getName());
                strings.put(team.getName(), inner);
            }

            // Friendly Invisibility.
            team.setCanSeeFriendlyInvisibles(bufferedNametag.isFriendlyInvis());
        }

        // Unregister teams that are no longer in use.
        for (String newGroupName : this.getBufferedTeams()) {
            Team team = scoreboard.getTeam(newGroupName);

            if (team == null) {
                continue;
            }

            for (String entry : team.getEntries()) {
                team.removeEntry(entry);
            }
            team.unregister();
        }

        this.getBufferedTeams().clear();
        this.getBufferedTeams().addAll(toReturn);

        // Clean out members who are no longer in the team.
        for (String teamName : this.getBufferedTeams()) {
            List<String> members = strings.get(teamName);
            Team team = scoreboard.getTeam(teamName);

            if (team == null) {
                continue;
            }

            for (String entry : team.getEntries()) {
                if (members.contains(entry)) {
                    continue;
                }
                team.removeEntry(entry);
            }
        }
    }

    /**
     * Update Health and Nametag slots.
     */
    public void update() {
        Scoreboard scoreboard = this.getScoreboard();
        Player player = Bukkit.getPlayer(getUuid());

        this.updateHealthBelow(player, scoreboard);
        this.updateNametags(player, scoreboard);
    }

    /**
     * Cleanup Board.
     */
    public void cleanup() {
        this.bufferedPlayers.clear();
        this.bufferedTeams.clear();
    }

}

