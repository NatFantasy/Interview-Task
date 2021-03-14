package shops;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.annotations.Parameters;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Search {

    ExtentReports extent;
    RemoteWebDriver driver;
    ChromeOptions chromeOptions;
    FirefoxOptions firefoxOptions;

    void reportResult(ExtentTest test, String expected, String actual) {

        if(actual.equals(expected)){
            test.log(Status.PASS, "'" + expected + "'" + " displays as expected");
        }
        else{
            test.log(Status.FAIL, "'" + expected + "'" + " does not display as expected");
        }        
      }
    
    @BeforeClass    
    @Parameters("browserName")
	public void setup(String browserName) throws MalformedURLException {
        
       String localHost = "http://localhost:4444/wd/hub";
       if(browserName.equalsIgnoreCase("chrome"))
        {
            //Setup the browser
            System.setProperty("webdriver.chrome.driver","C:/chromedriver/chromedriver.exe"); // Windows 
            //System.setProperty("webdriver.chrome.driver","/Users/<your username>/chromedriver/chromedriver"); // Mac
            chromeOptions = new ChromeOptions();  
            //Setting to run on local host           
            driver = new RemoteWebDriver((new URL(localHost)),chromeOptions);
        }
        else if (browserName.equalsIgnoreCase("firefox"))
        {
            //Setup the browser
            System.setProperty("webdriver.geckodriver.driver","C:/geckodriver/geckodriver.exe"); // Windows 
            firefoxOptions = new FirefoxOptions(); 
             //Setting to run on local host     
            driver = new RemoteWebDriver((new URL(localHost)),firefoxOptions);
        }     

        //Init Reporter
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dt = date.format(formatter);

        ExtentSparkReporter reporter = new ExtentSparkReporter("testReports/TestResults" + browserName + "-" + dt + ".html");
        extent = new ExtentReports();
        extent.attachReporter(reporter);      
	}

    @Test
    public void negativeSearchTest() throws Throwable {

        // Setup test variables        
        String invalidLocation = "!£$%%%%%%%%&&*!!!!!!£$%%%%%%%%&&*!!!!!";        
        ExtentTest test = extent.createTest("Negative Search Test", "A negative test to test the Search Function on CRUK /find-a-shop page - GIVEN the user enters invalid location via the search option WHEN selecting the search button THEN no location search results are returned");
        
        //Start the test
        test.log(Status.INFO, "Starting Test");
        driver.get("https://www.cancerresearchuk.org/get-involved/find-a-shop");          
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        driver.findElement(By.id("onetrust-accept-btn-handler")).click();   

        Assert.assertEquals(driver.findElement(By.tagName("h1")).getText(), "Shop search results");   
        reportResult(test, "Shop search results", driver.findElement(By.tagName("h1")).getText());

        WebElement breadcrumb = driver.findElement(By.className("breadcrumb"));

        Assert.assertEquals(breadcrumb.findElement(By.linkText("Get involved")).getText(), "Get involved");
        reportResult(test, "Get involved", breadcrumb.findElement(By.linkText("Get involved")).getText());

        driver.findElement(By.id("edit-field-shop-geocode-latlon")).sendKeys(invalidLocation);
        driver.findElement(By.id("edit-submit-shop-listing")).click();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        Assert.assertEquals(driver.findElement(By.xpath("//p[text()='No results were found. Please try a different search keyword.']")).getText(), "No results were found. Please try a different search keyword.");
        reportResult(test, "No results were found. Please try a different search keyword.", driver.findElement(By.xpath("//p[text()='No results were found. Please try a different search keyword.']")).getText());

        Assert.assertEquals(driver.findElement(By.xpath("//li[text()='The location ']")).getText(), "The location " + invalidLocation + " could not be resolved and was ignored.");
        reportResult(test, "The location " + invalidLocation + " could not be resolved and was ignored.", driver.findElement(By.xpath("//li[text()='The location ']")).getText());
        
        test.log(Status.INFO,"Test Completed");   
    } 

    @Test
    public void postiveSearchTest() throws Throwable {

        // Setup test variables        
        String validLocation = "Loughborough";        
        ExtentTest test = extent.createTest("Positive Search Test", "A positive test to test the Search Function on CRUK /find-a-shop page - GIVEN the user searches for a valid location WHEN selecting the search button THEN the location is returned with the expected contact locations");

        //Start the test
        test.log(Status.INFO, "Starting Test");
        driver.get("https://www.cancerresearchuk.org/get-involved/find-a-shop");            
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        Assert.assertEquals(driver.findElement(By.tagName("h1")).getText(), "Shop search results");   
        WebElement breadcrumb = driver.findElement(By.className("breadcrumb"));

        Assert.assertEquals(breadcrumb.findElement(By.linkText("Get involved")).getText(), "Get involved");
        reportResult(test, "Get involved", breadcrumb.findElement(By.linkText("Get involved")).getText());

        driver.findElement(By.id("edit-field-shop-geocode-latlon")).sendKeys(validLocation);
        driver.findElement(By.id("edit-submit-shop-listing")).click();
       
        Assert.assertEquals(driver.findElement(By.xpath("//div[@class='field-item']//p[1]")).getText(), "In line with government lockdown restrictions across the UK, all shops with the exception of Jersey are temporarily closed until further notice. More information.");
        reportResult(test, "In line with government lockdown restrictions across the UK, all shops with the exception of Jersey are temporarily closed until further notice. More information.", driver.findElement(By.xpath("//div[@class='field-item']//p[1]")).getText());

        driver.findElement(By.linkText("More information")).click();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        
        Assert.assertEquals(driver.findElement(By.tagName("h1")).getText(), "Ways to shop");
        reportResult(test, "Ways to shop", driver.findElement(By.tagName("h1")).getText());

        Assert.assertEquals(driver.findElement(By.linkText("COVID-19 Information")).getText(), "COVID-19 Information");
        reportResult(test, "COVID-19 Information", driver.findElement(By.linkText("COVID-19 Information")).getText());

        test.log(Status.INFO,"Test Completed");    
    }

    @AfterClass
	public void tearDown() throws Throwable
	{		
        //Write reporting logs
        extent.flush();
     
        //Tear Down
        Thread.sleep(1000);
        driver.quit();
	}

}