package de.jcing.operator;

import java.util.List;

public class Operator implements Runnable {

	private boolean running;
	private Thread th;

	private List<Runnable> runnables;

	public Operator() {
		running = false;
	}

	@Override
	public void run() {
		while (running) {

		}
	}

	public void start() {
		th = new Thread(this);
		th.start();
	}

	public void stop() {
		running = false;
		try {
			th.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
