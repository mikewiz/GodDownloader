package com.goddownloader.trackers;

import java.util.ArrayList;

public class FullRecursionTracker {

	private static ArrayList<String> urls; //array list holding all url strings
	private static ArrayList<String> fileNames_urls; //array list holding 
	private static ArrayList<String> fileNames_pdfsFinished;

	/*
	 * Static block to initialize all the array lists
	 */
	static {
		urls = new ArrayList<>();
		fileNames_urls = new ArrayList<>();
		fileNames_pdfsFinished = new ArrayList<>();
	}

	/**
	 * Empty constructor to initialize the FullRecursionTracker
	 */
	public FullRecursionTracker() {
	}

	public ArrayList<String> getUrls() {
		return urls;
	}

	/**
	 * Adds a URL at the specified index in the list
	 * 
	 * @param idx
	 * @param url
	 */
	public void setUrls(int idx, String url) {
		urls.set(idx, url);
	}

	/**
	 * This will return a list of current filenames that have been added to urls
	 * 
	 * @return a list of all filenames relating to urls
	 */
	public ArrayList<String> getFileNames_urls() {
		return fileNames_urls;
	}

	/**
	 * Sets the filename of the data that was received from the specified URL
	 * 
	 * @param index
	 *            to add the url filename
	 * @param urlFileName
	 *            the url filename that will be added to the list
	 */
	public void setFileNames_urls(int idx, String urlFileName) {
		fileNames_urls.set(idx, urlFileName);
	}

	/**
	 * Returns the list of pdf filenames
	 * 
	 * @return list of pdf filenames
	 */
	public ArrayList<String> getFileNames_pdfsFinished() {
		return fileNames_pdfsFinished;
	}

	/**
	 * Add the filename at the given index to the list of pdfs that have already
	 * been downloaded
	 * 
	 * @param idx
	 *            index to add the filename
	 * @param pdfFileName
	 *            name of the pdf file that will be added the the list of pdfs that
	 *            have already been downloaded
	 */
	public void setFileNames_pdfsFinished(int idx, String pdfFileName) {
		fileNames_pdfsFinished.set(idx, pdfFileName);
	}

	/**
	 * Searches the list of URLs and checks to see if the URL currently exists. If
	 * the URL does not exist in the list then add it to the list if the calling
	 * method has declared it should be added upon non-existence.
	 * 
	 * @param url
	 *            will be checked against the list of existing URLs
	 * @param addIfMissing
	 *            if true and URL is not currently found in the list then it will be
	 *            added to the list
	 * @return
	 */
	public String checkIfUrlExistsInList(String url, boolean addIfMissing) {
		if (urls.stream().anyMatch(s -> url.contains(s))) {
			return url;
		}
		return null;
	}

	/**
	 * Searches the list of URL filenames and checks to see if the URL filename
	 * currently exists. If the URL filename does not exist in the list then add it
	 * to the list if the calling method has declared it should be added upon
	 * non-existence.
	 * 
	 * @param urlFileName
	 *            will be checked against the list of existing URL filenames
	 * @param addIfMissing
	 *            if true and URL is not currently found in the list then it will be
	 *            added to the list
	 * @return
	 */
	public String checkIfUrlFileNameExistsInList(String urlFileName, boolean addIfMissing) {
		if (fileNames_urls.stream().anyMatch(s -> urlFileName.contains(s))) {
			return urlFileName;
		}
		return null;
	}

	/**
	 * Searches the list of PDF filenames and checks to see if the PDF filename
	 * currently exists. If the PDF filename does not exist in the list then add it
	 * to the list if the calling method has declared it should be added upon
	 * non-existence.
	 * 
	 * @param pdfFileName
	 *            will be checked against the list of existing PDF filenames
	 * @param addIfMissing
	 *            if true and URL is not currently found in the list then it will be
	 *            added to the list
	 * @return
	 */
	public String checkIfPdfFileNameExistsInList(String pdfFileName, boolean addIfMissing) {
		if (fileNames_pdfsFinished.stream().anyMatch(s -> pdfFileName.contains(s))) {
			return pdfFileName;
		}
		return null;
	}

}
