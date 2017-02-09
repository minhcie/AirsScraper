package src.main.java;

import java.awt.event.KeyEvent;
import java.awt.Robot;
//import java.io.*;
//import java.net.*;
//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardWatchEventKinds;
//import java.nio.file.WatchEvent;
//import java.nio.file.WatchKey;
//import java.nio.file.WatchService;
//import java.util.concurrent.TimeUnit;
import java.util.HashMap;

//import com.google.common.base.Function;;
import com.google.common.base.Predicate;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.Keys;
//import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.FluentWait;
//import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.apache.log4j.Logger;

public class AirsScraper {
    private static final Logger log = Logger.getLogger(AirsScraper.class);
    //private static final int BUFFER_SIZE = 4096;
    private static WebDriver driver = null;

	public static void main(String[] args) {
		try {
            initWebDriver();
            login();
            downloadAirsXml();
            close();
		}
		catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

    /**
     * Initialize webdriver for Chrome.
     *
     * @throws Exception
     */
    private static void initWebDriver() throws Exception {
		log.info("Initializing WebDriver for Chrome");
		String workingDir = System.getProperty("user.dir");
		System.setProperty("webdriver.chrome.driver", workingDir + "/chromedriver.exe");

		// Set default download folder for Chrome.
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", "C:\\Downloads");
        //chromePrefs.put("download.prompt_for_download", false);
        //chromePrefs.put("download.directory_upgrade", true);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", chromePrefs);
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		cap.setCapability(ChromeOptions.CAPABILITY, options);

        // Create webdriver instance.
		driver = new ChromeDriver(cap);
    }

	/**
	 * Navigates to 2-1-1 taxonomy website and log in using provided email and password.
	 *
	 * @throws Exception
	 */
	private static void login() throws Exception {
        // Navigate to 2-1-1 taxonomy website.
		log.info("Opening 2-1-1 taxonomy website");
		driver.navigate().to("https://211taxonomy.org/register/?return_url=%2f");

        // Log in using email and password.
		log.info("Signing in 2-1-1 taxonomy website");
		WebElement email_editbox = driver.findElement(By.id("email"));
		WebElement password_editbox = driver.findElement(By.id("password"));
		WebElement rights_editbox = driver.findElement(By.id("rights"));
		WebElement submit_button = driver.findElement(By.xpath("//input[@value='Log in']"));

		email_editbox.sendKeys("resourcecenter@211sandiego.org");
		password_editbox.sendKeys("inform");
		rights_editbox.sendKeys("yes");
		submit_button.click();
	}

	/**
	 * Close 2-1-1 taxonomy website and kill webdriver instance.
	 *
	 * @throws Exception
	 */
	private static void close() throws Exception {
		log.info("Closing 2-1-1 taxonomy website, kill Chrome WebDriver");
		driver.quit(); // close() doesn't kill the instance.
	}

	/**
	 * Download AIRS taxonomy XML.
	 *
	 * @throws Exception
	 */
	private static void downloadAirsXml() throws Exception {
		log.info("Downloading AIRS taxonomy XML");
		WebElement download_menu = driver.findElement(By.xpath("//a[contains(.,'Download')]"));
		download_menu.click();
		WebElement download_link = driver.findElement(By.xpath("//a[contains(.,'Download the xml file')]"));
        download_link.click();

        /*
        // Test download file from URL.
        String fileURL = download_link.getAttribute("href");
        System.out.println("URL: " + fileURL);

        boolean redirect = false;
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
        int status = httpConn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
        	if (status == HttpURLConnection.HTTP_MOVED_TEMP ||
                status == HttpURLConnection.HTTP_MOVED_PERM ||
                status == HttpURLConnection.HTTP_SEE_OTHER)
        	redirect = true;
        }

        if (redirect) {
            while (status != HttpURLConnection.HTTP_OK) {
                String newUrl = httpConn.getHeaderField("Location");
                System.out.println("Redirect to URL : " + newUrl);
                httpConn = (HttpURLConnection)new URL(newUrl).openConnection();
                status = httpConn.getResponseCode();
            }
        }

        if (status == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10, disposition.length() - 1);
                }
            }
            else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                                             fileURL.length());
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = "C:\\Downloads" + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            System.out.println("File downloaded");
        }
        else {
            System.out.println("No file to download. Server replied HTTP code: " + status);
        }
        */

        // Wait until the page is loaded.
        Predicate<WebDriver> pageLoaded = wd -> ((JavascriptExecutor) wd)
            .executeScript("return document.readyState")
            .equals("complete");
        new FluentWait<WebDriver>(driver).until(pageLoaded);

        // Open "Save As" window and save the file.
        Robot robot = new Robot();

        // Press Ctrl+S keys.
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_S);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_S);
        Thread.sleep(3000L); // Wait for "Save As" window to open.

        // Press Enter key.
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(5000L); // Wait for file to save.

        /*
		String pageSrc = download_link.getText();
        System.out.println(pageSrc);

        Actions action = new Actions(driver);
        action.keyDown(Keys.CONTROL).sendKeys("s").keyUp(Keys.CONTROL).perform();
        Thread.sleep(5000);
        action.sendKeys(Keys.ENTER).build().perform();
        Thread.sleep(5000);

        Actions action = new Actions(driver);
        action.moveToElement(download_link);
        action.contextClick(download_link).build().perform(); // Perform right-click.
        WebElement saveAs = driver.findElement(By.linkText("Save link as..."));
        saveAs.click();

        // action.sendKeys(Keys.ARROW_DOWN).build().perform();
        // action.sendKeys(Keys.ARROW_DOWN).build().perform();
        // action.sendKeys(Keys.ARROW_DOWN).build().perform();
        // action.sendKeys(Keys.ARROW_DOWN).build().perform();
        // action.sendKeys(Keys.ENTER).build().perform();
        */

        /*
        // Wait for the XML to be downloaded.
        Path download_folder = Paths.get("C:\\Downloads");
        File file = WaitForNewFile(download_folder, ".xml", 120);

        // Rename the downloaded file.
        if (file != null) {
            file.renameTo(download_folder.resolve("taxonomy.xml").toFile());
            log.info("taxonomy.xml successfully downloaded");
        }
        else {
            log.error("Error downloading taxonomy XML file");
        }
        */
	}

    /**
     * Waits for a new file to be downloaded with a file watcher.
    private static File WaitForNewFile(Path path, String extension, int timeout_sec)
    throws InterruptedException, IOException {
        long end_time = System.currentTimeMillis() + timeout_sec * 1000;
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            for (WatchKey key; null != (key = watcher.poll(end_time - System.currentTimeMillis(), TimeUnit.MILLISECONDS)); key.reset()) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    File file = path.resolve(((WatchEvent<Path>)event).context()).toFile();
                    if (file.toString().toLowerCase().endsWith(extension.toLowerCase()))
                        return file;
                }
            }
        }
        return null;
    }
     */

	/**
	 * Wait 60 seconds for an element to be present on the page, check for its
	 * present once every 15 seconds.
	private static WebElement fluentWait(final By locator) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
								   .withTimeout(60, TimeUnit.SECONDS)
								   .pollingEvery(15, TimeUnit.SECONDS)
								   .ignoring(NoSuchElementException.class);

		WebElement ele = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver wd) {
				return wd.findElement(locator);
			}
		});
		return ele;
	};
	 */
}
