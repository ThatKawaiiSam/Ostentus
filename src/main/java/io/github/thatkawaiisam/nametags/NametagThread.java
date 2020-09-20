package io.github.thatkawaiisam.nametags;

import org.bukkit.entity.Player;

public class NametagThread extends Thread {

    private NametagHandler handler;

    /**
     * Nametag Thread.
     *
     * @param handler instance.
     */
    public NametagThread(NametagHandler handler) {
        this.handler = handler;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                tick();
                sleep(50 * handler.getTicks());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Thread Tick Logic.
     */
    private void tick() {
        if (this.handler.getAdapter() == null) {
            return;
        }

        for (Player player : this.handler.getPlugin().getServer().getOnlinePlayers()) {
            NametagBoard board = this.handler.getBoards().get(player.getUniqueId());

            // This shouldn't happen, but just in case.
            if (board != null) {
                board.update();
            }
        }
    }
}
