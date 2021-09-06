package io.github.thatkawaiisam.ostentus;

import org.bukkit.entity.Player;

public class OstentusThread extends Thread {

    private Ostentus ostentus;

    /**
     * Ostentus Thread.
     *
     * @param ostentus instance.
     */
    public OstentusThread(Ostentus ostentus) {
        this.ostentus = ostentus;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.tick();
                sleep(50 * ostentus.getTicks());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Thread Tick Logic.
     */
    private void tick() {
        if (this.ostentus.getAdapter() == null) {
            return;
        }

        for (Player player : this.ostentus.getPlugin().getServer().getOnlinePlayers()) {
            OstentusBoard board = this.ostentus.getBoards().get(player.getUniqueId());

            // This shouldn't happen, but just in case.
            if (board != null) {
                board.update();
            }
        }
    }

}
