package org.jcing.jcingworld.editor.test;

public class CollatzEuler {

	private static final long MAXNR = 1000000;
	private static final int NUMBEROFTHREADS = 10;

	public static void main(String[] args) {
		for (int i = 0; i < NUMBEROFTHREADS; i++) {
			new CollatzEuler(i + 1, false);
			new CollatzEuler(i + 1, true);
		}
	}

	private Thread threads[];
	private boolean finishedThreads[];

	private final boolean recursive;
	private final int numThreads;

	private int longestChainNumber = 0;
	private int longestChainLength = 0;

	public CollatzEuler(int numThreads, boolean recursive) {
		long time = System.currentTimeMillis();
		this.numThreads = numThreads;
		this.recursive = recursive;
		threads = new Thread[numThreads];
		finishedThreads = new boolean[numThreads];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Calc(i));
			threads[i].start();
		}
		while (!running()) {
			try {
				Thread.sleep(20);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (recursive)
			System.err.println("Recursive: " + longestChainLength + " Chainlength @ " + longestChainNumber + " out of " + MAXNR + "  ("
					+ (System.currentTimeMillis() - time) + "ms (+-20ms) with " + numThreads + " Threads)");
		else
			System.out.println("Linear: " + longestChainLength + " Chainlength @ " + longestChainNumber + " out of " + MAXNR + "  ("
					+ (System.currentTimeMillis() - time) + "ms (+-20ms) with " + numThreads + " Threads)");
	}

	private int calculateChainLength(long n, int len) {
		if (recursive) {
			if (n == 1)
				return len;
			len++;
			if (n % 2 == 0)
				return calculateChainLength(n / 2, len);
			else
				return calculateChainLength(3 * n + 1, len);
		} else {
			while (n != 1) {
				len++;
				if (n % 2 == 0) {
					n /= 2;
				} else {
					n = 3 * n + 1;
				}
			}
			return len;
		}
	}

	public synchronized void checkBiggestChain(int nr, int len) {
		if (len >= longestChainLength) {
			longestChainNumber = nr;
			longestChainLength = len;
		}
	}

	boolean running() {
		for (boolean b : finishedThreads) {
			if (!b)
				return false;
		}
		return true;
	}

	private class Calc implements Runnable {

		private int threadNumber;

		public Calc(int id) {
			this.threadNumber = id;
		}

		@Override
		public void run() {
			for (int i = threadNumber + 1; i < MAXNR; i += numThreads) {
				int len = calculateChainLength(i, 0);
				checkBiggestChain(i, len);
			}
			finishedThreads[threadNumber] = true;
		};

	}

}
