package io.github.thatkawaiisam.nametags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter @Setter @AllArgsConstructor
public class BufferedNametag {

    private String groupName, prefix, suffix;
    private boolean showHealth = false, friendlyInvis = false;
    private Player player;

}
