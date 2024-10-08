package base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.log4testng.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.SelectOption;

import extentListener.ExtentListeners;

public class BaseTest {

	private Playwright playwright;
	public Browser browser;
	public static Page page;
	private static Properties OR = new Properties();
	private static FileInputStream fis;
	private Logger log = Logger.getLogger(this.getClass());

	/*
	 * private static ThreadLocal<Playwright> pw = new ThreadLocal<Playwright>();
	 * 
	 * private static ThreadLocal<Browser> bw = new ThreadLocal<Browser>();
	 * 
	 * private static ThreadLocal<Page> pg = new ThreadLocal<Page>();
	 * 
	 * public static Playwright getPlaywright() { return pw.get(); }
	 * 
	 * public static Browser getBrowser() { return bw.get(); }
	 * 
	 * public static Page getPage() { return pg.get(); }
	 */
	@BeforeSuite
	public void setUp() {
		PropertyConfigurator.configure("./src/test/resources/properties/log4j.properties");

		log.info("Test Execution Started!!!");

		try {
			fis = new FileInputStream("./src/test/resources/properties/OR.properties");
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		try {
			OR.load(fis);
			log.info("OR Files Loaded");
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public Browser getBrowser(String browserName) {

		System.out.println("get browsr called");

		playwright = Playwright.create();

		System.out.println("Start Playwright");

		// pw.set(playwright);

		switch (browserName) {

		case "chrome":
			log.info("Launching Chrome Browser");
			System.out.println("Chrome Browser Launched!!");
			return playwright.chromium()
					.launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));

		case "headless":
			log.info("Launching Headless Mode");
			return playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

		case "firefox":
			log.info("Launching Firefox Browser");
			return playwright.firefox()
					.launch(new BrowserType.LaunchOptions().setChannel("firefox").setHeadless(false));

		case "webkit":
			log.info("Launching Webkit");
			return playwright.webkit().launch(new BrowserType.LaunchOptions().setChannel("webkit").setHeadless(false));

		default:

			throw new IllegalArgumentException();
		}

	}

	public void navigate(Browser browser, String url) {

		System.out.println("Navigate called");

		this.browser = browser;

		// bw.set(browser);

		page = browser.newPage();

		// pg.set(page);

		System.out.println("Url is : " + url);

		page.navigate(url);

		log.info("Navigated to : " + url);

		page.onceDialog(dialog -> {

			try {

				Thread.sleep(3000);
			} catch (Exception e) {

			}

			dialog.accept();

			System.out.println(dialog.message());
		});

	}

	public void click(String locatorKey) {
		try {
			page.locator(OR.getProperty(locatorKey)).click();

			log.info("Clicking on element :" + locatorKey);

			ExtentListeners.getExtent().info("Clicking on element : <b><span style=\"color:yellow;\">" + locatorKey);

		} catch (Throwable t) {

			log.error("Error While clicking on an Element : " + t.getMessage());

			ExtentListeners.getExtent().fail("Error While clicking on an Element : " + t.getMessage());

			Assert.assertFalse(false, t.getMessage());
		}

	}

	public void type(String locatorkey, String value) {
		try {
			page.locator(OR.getProperty(locatorkey)).fill(value);

			log.info("Typing in the field : <b>" + locatorkey + " </b> entered the value as : <b>" + value + "<b>");

			ExtentListeners.getExtent().info("Typing in the field : <b><span style=\"color:yellow;\">" + locatorkey
					+ "</span></b> entered the value as : <b><span style=\"color:#04a1f4;\">" + value + "</span");

		} catch (Throwable t) {

			log.error("Error While Entering in an Element : " + t.getMessage());

			ExtentListeners.getExtent().fail("Error While Entering in an Element : " + t.getMessage());

			Assert.assertFalse(false, t.getMessage());

		}
	}

	public void select(String locatorkey, String value) {

		try {
			page.selectOption(OR.getProperty(locatorkey), new SelectOption().setLabel(value));

			log.info("Selecting in the field : <b>" + locatorkey + " </b> selected the value as : <b>" + value + "<b>");

			ExtentListeners.getExtent().info("Selecting in the field : <b><span style=\"color:yellow;\">" + locatorkey
					+ "</span></b> selected the value as : <b><span style=\"color:#04a1f4;\">" + value + "</span");

		} catch (Throwable t) {

			log.error("Error While Selecting in an Element : " + t.getMessage());

			ExtentListeners.getExtent().fail("Error While Selecting in an Element : " + t.getMessage());

			Assert.assertFalse(false, t.getMessage());

		}
	}

	public boolean isElementPresent(String locatorKey) {

		try {
			page.locator(OR.getProperty(locatorKey)).click();

			log.info("Finding on element : " + locatorKey);

			ExtentListeners.getExtent().info("Finding on element :<b><span style=\"color:yellow;\">" + locatorKey);

			return true;
		} catch (Throwable t) {

			log.error("Error While Finding on an Element : " + t.getMessage());

			ExtentListeners.getExtent().fail("Error While Finding on an Element : " + t.getMessage());

			return false;
		}
	}

	@AfterMethod
	public void quit() {

		if (page != null & browser != null) {

			browser.close();
			page.close();
			playwright.close();
		}

	}

}
