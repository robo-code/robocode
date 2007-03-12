package roborumble.battlesengine;


/**
 * Project RoboRumble@home
 * Coordinator - by Albert Perez
 */
public class Coordinator {
	boolean available;
	
	public Coordinator() {
		available = false;
	}
	
	public synchronized void get() {
		while (available == false) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		available = false;
		notifyAll();
	}
	
	public synchronized void put() {
		while (available == true) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
		available = true;
		notifyAll();
	} 

}
