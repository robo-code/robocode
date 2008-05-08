package robocode.common;


public class WorkerThread implements ILoopable {

	private final ILoopable target;
	private final String name;

	private volatile Thread thread;

	private final Object monitor = new Object();

	private volatile boolean paused;

	public WorkerThread() {
		this(null, null);
	}

	public WorkerThread(String name) {
		this(null, name);
	}
	
	public WorkerThread(ILoopable target, String name) {
		this.target = target;
		this.name = name;
	}

	public void finalize() {
		stop();
	}

	public void loop() {}

	public void start() {
		stop();

		ILoopable target = (this.target != null) ? this.target : this; 
		String name = (this.name != null) ? this.name : getClass().getName(); 

		synchronized (monitor) {
			thread = new Thread(new Worker(target), name);
			thread.setDaemon(true);
			thread.start();
		}
	}

	public void stop() {
		synchronized (monitor) {
			if (thread != null) {
				Thread moribund = thread;

				thread = null;
				moribund.interrupt();
			}
		}
	}

	public void pause() {
		synchronized (monitor) {
			paused = true;
			monitor.notifyAll();
		}
	}
	
	public void resume() {
		synchronized (monitor) {
			paused = false;
			monitor.notifyAll();
		}
	}

	public void check() {
		Thread thisThread = Thread.currentThread();

		synchronized (monitor) {
			while (paused && thread == thisThread) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					// Make sure the Thread will raise the InterruptedException again as soon as it is able to
					// Described here:
					// http://java.sun.com/javase/6/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html

					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
	private final class Worker implements Runnable {

		private final ILoopable target;

		private Worker(ILoopable target) {
			if (target == null) {
				throw new NullPointerException("target is null");
			}
			this.target = target;
		}

		public void run() {
			Thread thisThread = Thread.currentThread();

			while (thread == thisThread) {
				check();
				target.loop();
			}
		}
	}

	/*
	 public static void main(String[] args) {
	 WorkerThread thread = new WorkerThread("test") {
	 
	 public void loop() {
	 check();
	 try {
	 Thread.sleep(100);
	 } catch (InterruptedException e) {
	 return;
	 }

	 System.out.print('#');
	 }
	 };

	 System.out.println("-- start --");
	 thread.start();
	 
	 try {
	 Thread.sleep(1000);
	 } catch (InterruptedException e) {
	 e.printStackTrace();
	 }
	 
	 System.out.println("-- pause --");
	 thread.pause();

	 try {
	 Thread.sleep(1000);
	 } catch (InterruptedException e) {
	 e.printStackTrace();
	 }

	 System.out.println("-- resume --");
	 thread.resume();

	 try {
	 Thread.sleep(1000);
	 } catch (InterruptedException e) {
	 e.printStackTrace();
	 }

	 System.out.println("-- stop --");
	 thread.stop();

	 try {
	 Thread.sleep(1000);
	 } catch (InterruptedException e) {
	 e.printStackTrace();
	 }

	 System.out.println("-- start --");
	 thread.start();

	 try {
	 Thread.sleep(1000);
	 } catch (InterruptedException e) {
	 e.printStackTrace();
	 }
	 
	 System.out.println("-- destroy --");
	 thread = null;
	 }*/
}
