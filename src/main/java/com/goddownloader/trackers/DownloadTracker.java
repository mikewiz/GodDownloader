package com.goddownloader.trackers;

import static com.goddownloader.constants.UrlConstants.BUFFER_1024;

import java.net.HttpURLConnection;

import com.goddownloader.locks.SimpleBooleanLock;

public class DownloadTracker {
	// Parameters used to track each separate download
	private double downloaded, percentDownloaded, fileSize;
	private int read, percentageIndex;
	private byte[] buffer;
	private String percent;
	private SimpleBooleanLock sbl;

	public DownloadTracker(HttpURLConnection http, int numPercentageUpdates) {
		downloaded = 0.00;
		percentDownloaded = 0.00;
		read = 0;
		percentageIndex = 0;
		buffer = new byte[BUFFER_1024];
		fileSize = (double) http.getContentLengthLong();
		percent = String.format("%.4f", percentDownloaded);
		sbl = new SimpleBooleanLock(numPercentageUpdates);
	}

	public void incrementPercentageIndex() {
		percentageIndex++;
	}

	public void setSblLockAtIndex() {
		sbl.setLockAtIndex(getPercentageIndex(), true);
	}

	public int getSblLockAtIndex() {
		return sbl.getPercentageUpdates(getPercentageIndex());
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent() {
		this.percent = String.format("%.4f", getPercentDownloaded());
	}

	public double getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(double downloaded) {
		this.downloaded = downloaded;
	}

	public double getPercentDownloaded() {
		return percentDownloaded;
	}

	public void setPercentDownloaded(double percentDownloaded) {
		this.percentDownloaded = percentDownloaded;
	}

	public double getFileSize() {
		return fileSize;
	}

	public void setFileSize(double fileSize) {
		this.fileSize = fileSize;
	}

	public int getRead() {
		return read;
	}

	// Set and return the new amount of bytes read
	public int setRead(int read) {
		this.read = read;
		return this.read;
	}

	public int getPercentageIndex() {
		return percentageIndex;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public SimpleBooleanLock getSbl() {
		return sbl;
	}

	public void setSbl(SimpleBooleanLock sbl) {
		this.sbl = sbl;
	}

	@Override
	public String toString() {
		String info = "Downloaded " + getPercent() + "% of a file.";
		return info;
	}

	public String toString(int printPercent) {
		String info = "Downloaded " + printPercent + "% of a file.";
		return info;
	}
}