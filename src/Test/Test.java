package Test;

// Java program to illustrate
// stopping a thread using boolean flag

class MyThread implements Runnable {

	// to stop the thread
	private boolean exit;

	private final String name;
	Thread t;

	MyThread(String threadname) {
		name = threadname;
		t = new Thread(this, name);
		System.out.println("New thread: " + t);
		exit = false;
		t.start(); // Starting the thread
	}

	// execution of thread starts from run() method
	@Override
	public void run() {
		int i = 0;
		while (!exit) {
			System.out.println(name + ": " + i);
			i++;
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				System.out.println("Caught:" + e);
			}
		}
		System.out.println(name + " Stopped.");
	}

	// for stopping the thread
	public void stop() {
		exit = true;
	}
}

// Test class
public class Test {
	public static void main(String args[]) {
		// creating two objects t1 & t2 of MyThread
		final MyThread t1 = new MyThread("First  thread");
		final MyThread t2 = new MyThread("Second thread");
		try {
			Thread.sleep(500);
			t1.stop(); // stopping thread t1
			t2.stop(); // stopping thread t2
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			System.out.println("Caught:" + e);
		}
		System.out.println("Exiting the main Thread");
	}
}
