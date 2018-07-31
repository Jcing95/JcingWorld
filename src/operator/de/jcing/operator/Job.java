package de.jcing.operator;

import java.util.Comparator;

public class Job implements Comparator<Job> {

	private Runnable runnable;
	private int priority;
	private int status;
	private boolean frequent;

	public Job(Runnable runnable, int priority, boolean frequent) {
		this.runnable = runnable;
		this.priority = priority;
		this.status = priority;
		this.frequent = frequent;
	}

	@Override
	public int compare(Job o1, Job o2) {
		return Integer.compare(((Job) o1).status, ((Job) o2).status);
	}

	public void run() {
		runnable.run();
		status = priority;
	}

	public void tick() {
		if (status > 0)
			status--;
	}

	public boolean isFrequent() {
		return frequent;
	}

}
