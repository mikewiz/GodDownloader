package com.goddownloader.locks;

import static com.goddownloader.constants.UrlConstants.*;
import static java.lang.Math.toIntExact;

public class SimpleBooleanLock {
	
	private int lockSize;
	private boolean[] lock;
	private int[] percentageUpdates;

	public SimpleBooleanLock(int lockSize) {
		// Initialize variables
		this.lockSize = lockSize;
		this.lock = new boolean[this.lockSize];
		this.percentageUpdates = new int[this.lockSize];

		// Calculate percentage points that will be echoed back to the user
		calculatePercentageUpdates(((double) this.lockSize));
	}

	private void calculatePercentageUpdates(double lockSize) {
		double percentage = 0.00;
		double factor = 1.00;
		int index = 0;
		int indexMax = this.lockSize;
		
		while ((percentage <= ONE_HUNDRED_PERCENT) && (index < indexMax)) {
			percentage = (ONE_HUNDRED_PERCENT / lockSize) * factor;
			percentageUpdates[index] = toIntExact(Math.round(percentage));
			factor++;
			index++;
		}
	}

	public boolean[] resetSimpleBooleanLock() {
		lock = new boolean[lockSize];
		return lock;
	}

	public boolean[] getLock() {
		return lock;
	}

	public int getLockSize() {
		return lockSize;
	}

	public boolean getLock(int percentageIndex) {
		return lock[percentageIndex];
	}

	public void setLockAtIndex(int percentageIndex, boolean b) {
		lock[percentageIndex] = b;
	}
	
	public int[] getPercentageUpdates() {
		return percentageUpdates;
	}

	public int getPercentageUpdates(int percentageIndex) {
		return percentageUpdates[percentageIndex];
	}
}
