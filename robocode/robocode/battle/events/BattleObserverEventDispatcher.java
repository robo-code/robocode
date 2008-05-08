package robocode.battle.events;


import robocode.common.WorkerThread;


public class BattleObserverEventDispatcher {

	private final IBattleObserver observer;

	private final WorkerThread dispatcherThread = new DispatcherThread();

	private final BattleEventQueue eventQueue = new BattleEventQueue();

	public BattleObserverEventDispatcher(IBattleObserver battleObserver) {
		assert(battleObserver != null);

		observer = battleObserver;
		
		start();
	}

	protected void finalize() throws Throwable {
		stop();
	}

	public void notify(BattleEvent event) {
		assert(event != null);

		eventQueue.add(event);
	}
	
	public void start() {
		dispatcherThread.start();
	}
	
	public void stop() {
		dispatcherThread.stop();
	}

	/*
	 public void pause() {
	 dispatcherThread.pause();
	 }

	 public void resume() {
	 dispatcherThread.resume();
	 }
	 */
	private class DispatcherThread extends WorkerThread {

		@Override
		public void loop() {
			BattleEvent event;

			while ((event = eventQueue.poll()) != null) {
				dispatchEvent(event);
			}
			try {
				Thread.sleep(5); // FIXME: Should be calculated or controlled somewhere else
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		private void dispatchEvent(BattleEvent event) {
			IBattleListener listener = observer.getBattleListener();

			if (listener == null) {
				return;
			}

			if (event instanceof BattleStartedEvent) {
				listener.onBattleStarted((BattleStartedEvent) event);
			} else if (event instanceof BattleEndedEvent) {
				listener.onBattleEnded((BattleEndedEvent) event);
			} else if (event instanceof BattlePausedEvent) {
				listener.onBattlePaused((BattlePausedEvent) event);
			} else if (event instanceof BattleResumedEvent) {
				listener.onBattleResumed((BattleResumedEvent) event);
			} else if (event instanceof RoundStartedEvent) {
				listener.onRoundStarted((RoundStartedEvent) event);
			} else if (event instanceof RoundEndedEvent) {
				listener.onRoundEnded((RoundEndedEvent) event);
			} else if (event instanceof TurnStartedEvent) {
				listener.onTurnStarted((TurnStartedEvent) event);
			} else if (event instanceof TurnEndedEvent) {
				listener.onTurnEnded((TurnEndedEvent) event);
			}
		}
	}
}
