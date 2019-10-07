package io.github.thatkawaiisam.nametags;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class NametagThread extends Thread {

    private NametagHandler handler;

    public NametagThread(NametagHandler handler) {
        this.handler = handler;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                tick();
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
            try {
                sleep(50 * handler.getTicks());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        if (this.handler.getAdapter() == null) {
            return;
        }

        for (Player player : this.handler.getPlugin().getServer().getOnlinePlayers()) {
            NametagBoard board = this.handler.getBoards().get(player.getUniqueId());

            // This shouldn't happen, but just in case
            if (board == null) {
                continue;
            }

            Scoreboard scoreboard = board.getScoreboard();
            List<BufferedNametag> nametags = this.handler.getAdapter().getPlate(player);

            if (nametags == null) {
                continue;
            }

            if (this.handler.getAdapter().showHealthBelowName(player)) {
                if (scoreboard.getObjective(DisplaySlot.BELOW_NAME) == null) {
                    Objective objective = scoreboard.registerNewObjective("showhealth", "health");
                    objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                    objective.setDisplayName(ChatColor.RED + StringEscapeUtils.unescapeJava("\u2764"));
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

            Set<String> toReturn = new HashSet<>();
            Map<String, List<String>> strings = new HashMap<>();

            for (BufferedNametag bufferedNametag : nametags) {
                //Get Team
                Team team = scoreboard.getTeam(bufferedNametag.getGroupName());

                if (team == null) {
                    team = scoreboard.registerNewTeam(bufferedNametag.getGroupName());
                }

                toReturn.add(team.getName());
                board.getBufferedTeams().remove(team.getName());

                //Set Prefix
                if (bufferedNametag.getPrefix() != null) {
                    team.setPrefix(bufferedNametag.getPrefix());
                } else {
                    team.setPrefix(ChatColor.WHITE.toString());
                }
                //Set Suffix
                if (bufferedNametag.getSuffix() != null) {
                    team.setSuffix(bufferedNametag.getSuffix());
                } else {
                    team.setSuffix(ChatColor.WHITE.toString());
                }
                if (bufferedNametag.getPlayer() != null && bufferedNametag.getPlayer().isOnline()) {
                    team.addEntry(bufferedNametag.getPlayer().getName());
                    if (strings.containsKey(team.getName())) {
                        List<String> lol = strings.get(team.getName());
                        lol.add(bufferedNametag.getPlayer().getName());
                        strings.put(team.getName(), lol);
                    } else {
                        List<String> lol = new ArrayList<>();
                        lol.add(bufferedNametag.getPlayer().getName());
                        strings.put(team.getName(), lol);
                    }
                }

                //Friendly Invis
                team.setCanSeeFriendlyInvisibles(bufferedNametag.isFriendlyInvis());
            }

            for (String newGroupName : board.getBufferedTeams()) {
                Team team = scoreboard.getTeam(newGroupName);

                if (team == null) {
                    continue;
                }

                team.unregister();
            }

            board.getBufferedTeams().clear();
            board.getBufferedTeams().addAll(toReturn);

            for (String teamName : board.getBufferedTeams()) {
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
    }
}
