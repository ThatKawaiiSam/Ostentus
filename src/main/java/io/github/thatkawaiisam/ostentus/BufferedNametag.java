package io.github.thatkawaiisam.ostentus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter @Setter @AllArgsConstructor
public class BufferedNametag {

    private String groupName, prefix, suffix;
    private boolean friendlyInvis;
    private Player player;

}
