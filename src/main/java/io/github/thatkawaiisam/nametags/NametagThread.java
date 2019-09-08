package io.github.thatkawaiisam.nametags;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

            Set<String> toReturn = new HashSet<>();

            boolean isHealth = false;

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
                }

                //Friendly Invis
                team.setCanSeeFriendlyInvisibles(bufferedNametag.isFriendlyInvis());

                if (bufferedNametag.isShowHealth() && scoreboard.getObjective(DisplaySlot.BELOW_NAME) == null) {
                    Objective objective = scoreboard.registerNewObjective(bufferedNametag.getGroupName(), "health");
                    objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                    objective.setDisplayName(ChatColor.RED + StringEscapeUtils.unescapeJava("\u2764"));
                    objective.getScore(bufferedNametag.getPlayer()).setScore((int) Math.floor(bufferedNametag.getPlayer().getHealth()));
                    isHealth = true;
                }

                if (!bufferedNametag.isShowHealth() && scoreboard.getObjective(DisplaySlot.BELOW_NAME) != null) {
                    Objective objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
                    objective.unregister();
                }

                if (bufferedNametag.isShowHealth() && scoreboard.getObjective(DisplaySlot.BELOW_NAME) != null) {
                    isHealth = true;
                    Objective objective = scoreboard.getObjective(bufferedNametag.getGroupName());
                    if (objective.getScore(bufferedNametag.getPlayer()) == null || objective.getScore(bufferedNametag.getPlayer()).getScore() == 0) {
                        objective.getScore(bufferedNametag.getPlayer()).setScore((int) Math.floor(bufferedNametag.getPlayer().getHealth()));
                    }
                }
            }

            for (String newGroupName : board.getBufferedTeams()) {
                Team team = scoreboard.getTeam(newGroupName);

                if (team == null) {
                    continue;
                }

                team.unregister();
            }

            if (!isHealth) {
                Objective objective = scoreboard.getObjective("showhealth");
                if (objective != null) {
                    objective.unregister();
                }
            }

            board.getBufferedTeams().clear();
            board.getBufferedTeams().addAll(toReturn);
        }
    }
}
