package com.goddownloader.workers;

import static com.goddownloader.constants.UrlConstants.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import com.goddownloader.trackers.DownloadTracker;

import java.net.HttpURLConnection;

public class DownloadWorker implements Runnable {
	private String link;
	private File out;
	private CountDownLatch latch;

	/**
	 * Constructor
	 * 
	 * @param link
	 *            - URL link
	 * @param out
	 *            - File to save URL information
	 * @param latch
	 *            - Latch used to make thread wait until download of HTML finish
	 *            before extra processing.
	 */
	public DownloadWorker(String link, File out, CountDownLatch latch) {
		this.link = link;
		this.out = out;
		this.latch = latch;
	}

	@Override
	public void run() {
		URL url;
		HttpURLConnection http = null;
		try {
			url = new URL(link);
			http = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (http != null) {
			try (BufferedInputStream in = new BufferedInputStream(http.getInputStream());
					FileOutputStream fos = new FileOutputStream(this.out);
					BufferedOutputStream bout = new BufferedOutputStream(fos, BUFFER_1024);) {

				startDownload(http, in, bout, TEN_UPDATES);
				System.out.println("Download completed");

				/* sleepingTheThread3000(); */
				latch.countDown(); // Releasing the latch - can be investigated further on better implementations
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Download Failed.");
			}
		} else {
			System.out.println("Http is not of valid format, and equals null");
		}

	}

	/**
	 * 
	 * @param http
	 * @param in
	 * @param bout
	 * @param numPercentageUpdates
	 * @throws IOException
	 */
	private void startDownload(HttpURLConnection http, BufferedInputStream in, BufferedOutputStream bout,
			int numPercentageUpdates) throws IOException {
		DownloadTracker dt = new DownloadTracker(http, numPercentageUpdates);
		while (dt.setRead(in.read(dt.getBuffer(), 0, BUFFER_1024)) >= 0) {
			bout.write(dt.getBuffer(), 0, dt.getRead());
			dt.setDownloaded(dt.getDownloaded() + dt.getRead());
			dt.setPercentDownloaded((dt.getDownloaded() * 100) / dt.getFileSize());
			dt.setPercent();
			if ((dt.getPercentageIndex() < numPercentageUpdates)) {
				if (!dt.getSbl().getLock(dt.getPercentageIndex())) {
					int printPercent = dt.getSbl().getPercentageUpdates(dt.getPercentageIndex());
					if (dt.getPercentDownloaded() >= ((double) printPercent - 2)
							&& dt.getPercentDownloaded() <= ((double) printPercent + 2)) {
						System.out.println(dt.toString(printPercent));
						dt.setSblLockAtIndex();
						dt.incrementPercentageIndex();
					}
				}
			}
		}
	}

	/**
	 * Sleeping the thread for 3000 milliseconds
	 */
	public void sleepingTheThread3000() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.out.println("Error occured sleeping the thread");
			e.printStackTrace();
		}
	}
}
