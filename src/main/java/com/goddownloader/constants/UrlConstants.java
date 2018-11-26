package com.goddownloader.constants;

import java.util.regex.Pattern;

public final class UrlConstants {
	//Download Helper constants
	public static final int BUFFER_1024 = 1024;
	public static final int TWO_UPDATES = 2;
	public static final int FIVE_UPDATES = 5;
	public static final int TEN_UPDATES = 10;
	public static final int TWENTY_UPDATES = 20;
	public static final int THIRTY_UPDATES = 30;
	public static final double ONE_HUNDRED_PERCENT = 100.00;
	
	//Sites to test download capabilities - used in the corresponding test case.
	public static String javaCompleteReference = "http://iiti.ac.in/people/~tanimad/JavaTheCompleteReference.pdf";
	public static String listofavailablefreefillableforms = "https://www.irs.gov/e-file-providers/list-of-available-free-file-fillable-forms";
	public static String llcfilingasacorporationorpartnership = "https://www.irs.gov/businesses/small-businesses-self-employed/llc-filing-as-a-corporation-or-partnership";
	public static String IRSGlossary = "https://apps.irs.gov/app/vita/glossary.jsp";
	public static String YES_RESPONSE = "Y";
	public static String NO_RESPONSE = "N";

	//Extensions and Offsets of Extensions
	public static final String HTML_EXT = ".html";
	public static final String PDF_EXT = ".pdf";
	public static final String SEARCH_STRING_PDF_FROM_HTML = ".pdf\"";
	public static final int SEARCH_STRING_PDF_FROM_HTML_OFFSET = 4;
	public static final String HTTP_SEARCH = "http";
	public static final String AHREF_SEARCH = "a href=\"";
	public static final int AHREF_SEARCH_OFFSET = 8;

	//File Save Path Prefixes
	public static final String DOCUMENTS_DPATH = "C:\\Users\\mikep\\eclipse-workspace\\AssetManagement\\src\\main\\resources\\com\\assetmanagement\\PDFs\\";
	public static final String DOCUMENTS_HTML_SOURCE_DPATH = "C:\\Users\\mikep\\eclipse-workspace\\AssetManagement\\src\\main\\resources\\com\\assetmanagement\\HtmlSources\\";
	
	// https://stackoverflow.com/questions/5713558/detect-and-extract-url-from-a-string
	// Pattern for recognizing a URL, based off RFC 3986
	public static final Pattern urlPattern = Pattern.compile(
			"(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
					+ "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
}
