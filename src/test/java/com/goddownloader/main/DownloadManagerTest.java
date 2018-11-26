package com.goddownloader.main;

import static com.goddownloader.constants.UrlConstants.*;
import static com.goddownloader.constants.UrlTestConstants.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.junit.Test;

import com.goddownloader.main.DownloadManager;

class DownloadManagerTest {

	public int[] run = new int[] { 0, 0 };

	@Test
	void test() {
		if (run[0] == 1) {
			ArrayList<String> urlTestList = new ArrayList<>();
			urlTestList.add(llcfilingasacorporationorpartnership);
			//urlTestList.add(NO_RESPONSE);
			urlTestList.add(listofavailablefreefillableforms);
			//urlTestList.add(NO_RESPONSE);
			urlTestList.add(javaCompleteReference);
			//urlTestList.add(NO_RESPONSE);
			urlTestList.add(IRSGlossary);
			//urlTestList.add(NO_RESPONSE);
			int testNumber = 1;
			for (String url : urlTestList) {
				System.out.println("Performing Test " + testNumber + " - with URL: " + url);
				ByteArrayInputStream in = new ByteArrayInputStream(url.getBytes());
				System.setIn(in);
				DownloadManager.main(emptyArgs);
				resetTest();
				testNumber++;
			}
		}
	}

	@Test
	void test2() {
		if (run[1] == 1) {
			ArrayList<String> urlTestList = new ArrayList<>();
			urlTestList.add(llcfilingasacorporationorpartnership);
			//urlTestList.add(YES_RESPONSE);
			int testNumber = 1;
			for (String url : urlTestList) {
				System.out.println("Performing Test " + testNumber + " - with URL: " + url);
				ByteArrayInputStream in = new ByteArrayInputStream(url.getBytes());
				System.setIn(in);
				DownloadManager.main(emptyArgs);
				resetTest();
				testNumber++;
			}
		}
	}

	private void resetTest() {
		// optionally, reset System.in to its original
		System.setIn(System.in);
	}

}
