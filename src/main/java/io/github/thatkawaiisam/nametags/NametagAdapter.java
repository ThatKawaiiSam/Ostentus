package io.github.thatkawaiisam.nametags;

import org.bukkit.entity.Player;

import java.util.List;

public interface NametagAdapter {

    /**
     * Get all of the nametags of other players.
     *
     * @param player to show plates to.
     * @return list of nametags.
     */
    List<BufferedNametag> getPlate(Player player);

    /**
     * Whether or not to display the health below name to a player.
     *
     * @param player to display health values to.
     * @return whether to see health or not.
     */
    boolean showHealthBelowName(Player player);

}
