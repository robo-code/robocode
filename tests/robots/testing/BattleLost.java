package testing;
import robocode.*;

/**
 * BattleEnd - a robot by (your name here)
 */
public class BattleLost extends Robot
{
    @Override
	public void run() {
        while (true) {
            ahead(1); // Move ahead 100
            turnGunRight(360); // Spin gun around
            back(1); // Move back 100
            turnGunRight(360); // Spin gun around
        }
	}

    @Override
    public void onWin(WinEvent e) {
		out.println("Won!");
	}

    @Override
	public void onDeath(DeathEvent e) {
		out.println("Died");
	}

    @Override
	public void onBattleEnded(BattleEndedEvent event) {
		out.println("Battle ended "+event.getAborted());
	}
}
