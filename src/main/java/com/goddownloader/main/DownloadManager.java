package com.goddownloader.main;

import static com.goddownloader.constants.UrlConstants.*;
import static com.goddownloader.constants.Constants.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.goddownloader.trackers.FullRecursionTracker;
import com.goddownloader.workers.DownloadWorker;
import com.singalonglyrics.console.LyricsInterface;

public class DownloadManager {

	static LoggerContext context;
	static File file;
	static Logger log;

	static {
		context = (LoggerContext) LogManager.getContext(false);
		file = new File(LOG4J_PROPERTIES_PATH);
		context.setConfigLocation(file.toURI());
		// Now we can get our logger
		log = LogManager.getLogger();
	}

	/**
	 * Initiate Download Manager
	 * 
	 * @param args
	 *            - empty at the moment
	 */
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String link = returnUrlFromConsole(br);
		String fullRecursion = optionalFullRecursiveMode(br, 1);
		String fileName = getFileNameFromLink(link);
		String extension = getExtension(fileName);

		// Main implementation
		methodology(link, fullRecursion, fileName, extension);
	}

	/**
	 * @param args
	 */
	public DownloadManager(String[] args) {
		if (args.length > 0) {
			/**
			 * Find out which application is using the download manager and then provide
			 * them with appropriate dialogs to respond to and that correctly relate to said
			 * project
			 */
			for (int i = 0; i < args.length; i++) {
				if (i == 0 && args[i] == "Lyrics") {
					log.log(Level.INFO,
							"Ask questions to the user here on how he would like to process the URLs that have been passed. These will be Lyrics sites so think of that while processing");
					LyricsInterface li = new LyricsInterface();
				}
			}
		} else {
			main(args);
		}
	}

	private static void methodology(String link, String fullRecursion, String fileName, String extension) {
		if (fullRecursion.equalsIgnoreCase(YES_RESPONSE)) {
			fullRecursionHandling(link, extension, fileName);
		} else if (extension.equalsIgnoreCase(HTML_EXT)) {
			System.out.println("Handling 2: HTML");
			handleHtml(link, fileName);
		} else if (extension.equalsIgnoreCase(PDF_EXT)) {
			System.out.println("Handling 3: PDF");
			handlePdf(link, fileName);
		} else {
			System.out.println("Handling 4: HTML, but you need to check logic.");
			handleHtml(link, fileName);
		}

	}

	/**
	 * Handling for full recursion processing
	 * 
	 * @param link
	 *            - Web URL
	 * @param extension
	 *            - extension of the URL - be it an HTML/PDF/etc...
	 * @param fileName
	 *            - The fileName that will be used
	 */
	private static void fullRecursionHandling(String link, String extension, String fileName) {
		System.out.println("Handling 1: Full Website Recursion");
		FullRecursionTracker frt = new FullRecursionTracker();
		String domain = getDomainName(link);
		String fileName_domain = getFileNameFromLink(domain);
		frt.getUrls().add(domain);
		frt.getFileNames_urls().add(fileName_domain);
		if (extension.equalsIgnoreCase(PDF_EXT)) {
			handlePdf(link, fileName);
			frt.getFileNames_pdfsFinished().add(fileName);
		} else {
			frt.getUrls().add(link);
			frt.getFileNames_urls().add(fileName);
		}
		handleHtmlFullRecursion(frt);
	}

	/**
	 * Handling for fully recursing an html file for other url links
	 * 
	 * @param frt
	 *            FullRecursionTracker that is used to control the lists of URLs
	 */
	private static void handleHtmlFullRecursion(FullRecursionTracker frt) {
		int index = -1;
		for (String link : frt.getUrls()) {
			index++;
			String fileName = frt.getFileNames_urls().get(index);
			File htmlOut = getHtmlSource(link, fileName);
			/**
			 * use htmlOut to find other html on the domain. check ahrefs and append the
			 * domain to the front then check if the url is reachable --> if so add it to a
			 * list of urls to be recursed.
			 */
			ArrayList<String> availablePdfDownloads = searchFileForPDFs(htmlOut);
			ArrayList<String> pdfList = downloadPDFsInFile(link, availablePdfDownloads);
			for (String printFileName : pdfList) {
				System.out.println(printFileName);
			}
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e1) {
				System.out.println("Sleeping to see file list has failed");
				e1.printStackTrace();
			}
			for (String pdfFileLink : pdfList) {
				String pdfFileName = getFileNameFromLink(pdfFileLink);
				String downloadPath = DOCUMENTS_DPATH + pdfFileName;
				File out = new File(downloadPath);
				CountDownLatch latch = new CountDownLatch(1);
				new Thread(new DownloadWorker(pdfFileLink, out, latch)).start();
				try {
					latch.await(); // Wait for countdown
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.out.println("Latch has failed");
				}
			}
		}

	}

	private static void handlePdf(String link, String fileName) {
		String downloadPath = DOCUMENTS_DPATH + fileName;
		File out = new File(downloadPath);
		CountDownLatch latch = new CountDownLatch(1);
		new Thread(new DownloadWorker(link, out, latch)).start();
		awaitLatchToBeReleased(latch);

	}

	/**
	 * Method to handle an error if waiting for the latch fails
	 * 
	 * @param latch
	 *            used to wait for htmls to finish downloading before they are
	 *            traversed for other Urls and pdf files
	 */
	private static void awaitLatchToBeReleased(CountDownLatch latch) {
		try {
			latch.await(); // Wait for latch to be freed
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Latch has failed");
		}
	}

	private static void handleHtml(String link, String fileName) {
		File htmlOut = getHtmlSource(link, fileName);
		ArrayList<String> availablePdfDownloads = searchFileForPDFs(htmlOut);
		ArrayList<String> pdfList = downloadPDFsInFile(link, availablePdfDownloads);
		for (String printFileName : pdfList) {
			System.out.println(printFileName);
		}
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e1) {
			System.out.println("Sleeping to see file list has failed");
			e1.printStackTrace();
		}
		for (String pdfFileLink : pdfList) {
			String pdfFileName = getFileNameFromLink(pdfFileLink);
			String downloadPath = DOCUMENTS_DPATH + pdfFileName;
			File out = new File(downloadPath);
			CountDownLatch latch = new CountDownLatch(1);
			new Thread(new DownloadWorker(pdfFileLink, out, latch)).start();
			try {
				latch.await(); // Wait for countdown
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("Latch has failed");
			}
		}
	}

	private static ArrayList<String> downloadPDFsInFile(String link, ArrayList<String> availablePdfDownloads) {
		ArrayList<String> pdfList = new ArrayList<>();
		for (String line : availablePdfDownloads) {
			int currIndex = 0;
			currIndex = line.indexOf(SEARCH_STRING_PDF_FROM_HTML, currIndex);
			while (currIndex != -1) {
				int actualIndex = currIndex + SEARCH_STRING_PDF_FROM_HTML_OFFSET;
				String urlLine = line.substring(actualIndex - 150, actualIndex);
				Matcher matcher = urlPattern.matcher(urlLine);
				while (matcher.find()) {
					int matchStart = matcher.start(1);
					int matchEnd = matcher.end();
					String website = urlLine.substring(matchStart, matchEnd);
					pdfList.add(website);
				}
				if (urlLine.toLowerCase().contains(AHREF_SEARCH)
						&& (urlLine.toLowerCase().indexOf(AHREF_SEARCH) == urlLine.lastIndexOf(AHREF_SEARCH))) {
					String website_ahref = urlLine.substring(urlLine.lastIndexOf(AHREF_SEARCH) + AHREF_SEARCH_OFFSET);
					if (!website_ahref.toLowerCase().contains(HTTP_SEARCH)) {
						String domainName = getDomainName(link);
						String fullAddress = domainName + website_ahref;
						pdfList.add(fullAddress);
					}
				}
				currIndex = line.indexOf(SEARCH_STRING_PDF_FROM_HTML, actualIndex);
			}
			currIndex = 0;
		}
		return pdfList;
	}

	private static String getExtension(String fileName) {
		if (fileName.lastIndexOf(PDF_EXT) != -1) {
			// Need to add logic in here to look for other files types other than just html
			// and pdf. Currently it defaults to returning a pdf extension type
			return PDF_EXT;
		}
		// }else if (fileName.lastIndexOf(JSP_EXT) != -1) {
		// String extension = fileName.substring((fileName.lastIndexOf(".")),
		// fileName.length());
		// }
		return HTML_EXT;
	}

	public static File getHtmlSource(String link, String fileName) {
		System.out.println("Html Source URL \n");
		String downloadPath = DOCUMENTS_HTML_SOURCE_DPATH + fileName + HTML_EXT;
		File htmlOut = new File(downloadPath);
		CountDownLatch latch = new CountDownLatch(1);
		new Thread(new DownloadWorker(link, htmlOut, latch)).start();
		awaitLatchToBeReleased(latch);
		return htmlOut;
	}

	private static ArrayList<String> searchFileForPDFs(File htmlOut) {
		try (FileInputStream fis = new FileInputStream(htmlOut);
				BufferedInputStream bis = new BufferedInputStream(fis);
				DataInputStream dis = new DataInputStream(bis);) {
			ArrayList<String> list = new ArrayList<>();
			ListHelper listHelper = new ListHelper();
			listHelper.addToListFromDataInputStream(list, dis);
			return list;
		} catch (FileNotFoundException e) {
			log.error("File was not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("End of File reached.");
		}
		return null;
	}

	/**
	 * Extract the filename from the URL and return it to the calling method
	 * 
	 * @param link
	 *            where the filename will be extracted from
	 * @return the filename that was extracted from the link
	 */
	public static String getFileNameFromLink(String link) {
		String fileName = link.substring((link.lastIndexOf("/") + 1), link.length());
		return fileName;
	}

	public static int returnIntegerFromConsole(BufferedReader br) {
		System.out.print("Enter Integer: \n");
		int i = 0;
		try {
			i = Integer.parseInt(br.readLine());
		} catch (NumberFormatException | IOException nfe) {
			System.err.println("Invalid Format!");
		}

		return i;
	}

	public static String returnUrlFromConsole(BufferedReader br) {
		System.out.print("Enter URL: \n");
		log.log(Level.INFO, "Enter URL: ");
		String s = "https://www.irs.gov/e-file-providers/list-of-available-free-file-fillable-forms";
		if (s.isEmpty()) {
			try {
				s = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*
			 * Add check here for a malformed URL. Maybe attempt to connect then ask for a
			 * working URL if you cannot connect
			 */
			if (!s.isEmpty()) {
				return s;
			}
		}

		return s;
	}

	public static String optionalFullRecursiveMode(BufferedReader br, int attempt) {
		if (attempt == 1) {
			System.out.print("Would you like to attempt Full Recursion Mode (Y/N):");
		} else {
			System.out.println("Attempt#" + attempt + "Must provide answer with either (Y/N) or (y/n) Try Again:");
		}
		String s = "";
		try {
			s = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (s.equalsIgnoreCase(YES_RESPONSE)) {
			return YES_RESPONSE;
		} else if (s.equalsIgnoreCase(NO_RESPONSE)) {
			return NO_RESPONSE;
		} else {
			attempt++;

			optionalFullRecursiveMode(br, attempt);
		}

		return s;
	}

	public static String getDomainName(String url) {
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			System.err.println("Error occurred when trying to retrieve the domain name.");
			e.printStackTrace();
		}
		String domain = uri.getScheme() + "://" + uri.getAuthority();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}

	public static String getFileType(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String type = uri.getAuthority() + "\n" + uri.getQuery() + "\n" + uri.getPath() + "\n" + uri.getScheme() + "\n"
				+ uri.getFragment() + "\n" + uri.getRawPath();
		return type;
	}
}
