package robocode.sound;


import robocode.battle.events.BattleAdaptor;
import robocode.battle.events.IBattleListener;
import robocode.battle.events.IBattleObserver;
import robocode.battle.events.TurnEndedEvent;
import robocode.battle.snapshot.BattleSnapshot;
import robocode.battle.snapshot.BulletSnapshot;
import robocode.battle.snapshot.RobotSnapshot;
import robocode.manager.RobocodeManager;


public class BattleSoundPlayer implements IBattleObserver {

	private final RobocodeManager manager;

	private IBattleListener battleListener = new BattleEventHandler();

	public BattleSoundPlayer(RobocodeManager manager) {
		assert(manager != null);

		this.manager = manager;
	}
	
	public IBattleListener getBattleListener() {
		return battleListener;
	}

	private class BattleEventHandler extends BattleAdaptor {

		public void onTurnEnded(TurnEndedEvent event) {
			playSounds(event.getBattleSnapshot());
		}
	}

	private void playSounds(BattleSnapshot battleSnapshot) {
		if (!manager.isSoundEnabled()) {
			return;
		}

		for (BulletSnapshot bullet : battleSnapshot.getBullets()) {
			if (bullet.getFrame() == 0) {
				manager.getSoundManager().playBulletSound(bullet);
			}
		}

		boolean playedRobotHitRobot = false;

		for (RobotSnapshot robot : battleSnapshot.getRobots()) {
			// Make sure that robot-hit-robot events do not play twice (one per colliding robot)
			if (robot.getState().isHitRobot()) {
				if (playedRobotHitRobot) {
					continue;
				}
				playedRobotHitRobot = true;
			}

			manager.getSoundManager().playRobotSound(robot);
		}
	}
}
