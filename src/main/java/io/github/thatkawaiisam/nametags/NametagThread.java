package io.github.thatkawaiisam.nametags;

import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

public class NametagThread extends Thread {

    private NametagHandler handler;
    private Long interval;

    public NametagThread(NametagHandler handler, int interval) {
        setName("Nametag-Library");
        this.handler = handler;
        this.interval = TimeUnit.SECONDS.toMillis(interval);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Bukkit.getOnlinePlayers().forEach(localPlayer -> {
                    if (localPlayer != null && localPlayer.isOnline()) {
                        handler.update(localPlayer);
                    }
                });
            }catch(NullPointerException e) {
                e.printStackTrace();
            }
            try {
                //TODO make this configurable
                //TODO do a hook mode
                //TODO fix nullpointer
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
