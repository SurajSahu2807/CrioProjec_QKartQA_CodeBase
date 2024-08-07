package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.Assert;

@Listeners(ListenerClass.class)
public class QKART_Tests extends ListenerClass {

    static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

    @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
    @Test(priority = 1 , groups={"Sanity"})
    @Parameters({"Username","Password"})
    public void TestCase01(@Optional("testUser")String Username,@Optional("abc@123")String Password) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(Username, Password, true);
        //status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        Assert.assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

    }

    @Test(priority = 2, groups={"Sanity"})
    @Parameters({"Username","Password"})
    public void TestCase02(@Optional("testUser")String Username,@Optional("abc@123")String Password) throws InterruptedException {
        Boolean status;
        // logStatus("Start Testcase", "Test Case 2: Verify User Registration with an existing
        // username ", "DONE");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        // logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);


        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
        Assert.assertFalse(status,
                "Registration Success . Existing user is able to re-register again");
        // logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status ? "FAIL" :
        // "PASS");
    }

    /*
     * Verify the functinality of the search text box
     */
    @Test(priority = 3, groups={"Sanity"})
    @Parameters({"ProductName6","ProductName7"})
    public void TestCase03(String ProductName6,String ProductName7) throws InterruptedException {
        // logStatus("TestCase 3", "Start test case : Verify functionality of search box ", "DONE");
        boolean status;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        //String product1 = "YONEX";
        status = homePage.searchForProduct(ProductName6);
        SoftAssert softassert = new SoftAssert();
        softassert.assertTrue(status, "Page does not contains results having name YONEX");
        // if (!status) {
        // logStatus("TestCase 3", "Test Case Failure. Unable to search for given product", "FAIL");

        // }

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        status = searchResults.isEmpty();
        softassert.assertFalse(status, "There were no results for the given searched string");
        // if (searchResults.size() == 0) {
        // logStatus("TestCase 3", "Test Case Failure. There were no results for the given search
        // string", "FAIL");
        // }

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult().toUpperCase();
            softassert.assertTrue(elementText.contains("YONEX"),
                    "Test Results contains un-expected values");
            // if (!elementText.toUpperCase().contains("YONEX")) {
            // logStatus("TestCase 3", "Test Case Failure. Test Results contains un-expected values:
            // " + elementText,
            // "FAIL");

            // }
        }

        // logStatus("Step Success", "Successfully validated the search results ", "PASS");

        // Search for product
        // String product2 = "Gesundheit";
        status = homePage.searchForProduct(ProductName7);
        softassert.assertTrue(status, "No Products found is displayed for Gesundheit");
        // if (!status) {
        // logStatus("TestCase 3", "Test Case Failure. Invalid keyword returned results", "FAIL");

        // }

        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        if (searchResults.size() == 0) {
            softassert.assertTrue(homePage.isNoResultFound(),
                    "Expected: no results , actual: Results were available");
            // if (homePage.isNoResultFound()) {
            // logStatus("Step Success", "Successfully validated that no products found message is
            // displayed", "PASS");
            // }
            // logStatus("TestCase 3", "Test Case PASS. Verified that no search results were found
            // for the given text",
            // "PASS");
        }
        // } else {
        // logStatus("TestCase 3", "Test Case Fail. Expected: no results , actual: Results were
        // available", "FAIL");

        // }
        softassert.assertAll();

    }

    /*
     * Verify the presence of size chart and check if the size chart content is as expected
     */
    @Test(priority = 4, groups={"Regression"})
    @Parameters({"ProductName8"})
    public void TestCase04(String ProductName8) throws InterruptedException {
        // logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");
        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct(ProductName8);//UNIFACTOR Mens Running Shoes
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            if (result.verifySizeChartExists()) {
                // logStatus("Step Success", "Successfully validated presence of Size Chart Link",
                // "PASS");

                // Verify if size dropdown exists
                status = result.verifyExistenceofSizeDropdown(driver);
                // logStatus("Step Success", "Validated presence of drop down", status ? "PASS" :
                // "FAIL");

                // Open the size chart
                if (result.openSizechart()) {
                    // Verify if the size chart contents matches the expected values
                    // Modified
                    SoftAssert softassert = new SoftAssert();
                    softassert.assertEquals(expectedTableHeaders, expectedTableBody,
                            "Failure while validating contents of Size Chart Link");

                    // if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody,
                    // driver)) {
                    // logStatus("Step Success", "Successfully validated contents of Size Chart
                    // Link", "PASS");
                    // } else {
                    // // logStatus("Step Failure", "Failure while validating contents of Size Chart
                    // Link", "FAIL");
                    // status = false;
                    // }

                    // Close the size chart modal
                    status = result.closeSizeChart(driver);

                } else {
                    logStatus("TestCase 4", "Test Case Fail. Failure to open Size Chart", "FAIL");
                    // return false;
                }

            } else {
                logStatus("TestCase 4", "Test Case Fail. Size Chart Link does not exist", "FAIL");
                // return false;
            }
        }
        // logStatus("TestCase 4", "End Test Case: Validated Size Chart Details", status ? "PASS" :
        // "FAIL");

    }

    /*
     * Verify the complete flow of checking out and placing order for products is
     * working correctly
     */
    
    @Test(priority = 5, groups={"Sanity"})
    @Parameters({"ProductName1","ProductName2"})
    public void TestCase05(String ProductName1 , String ProductName2) throws InterruptedException {
        Boolean status;
       // logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        //     logStatus("TestCase 5", "Test Case Failure. Happy Flow Test Failed", "FAIL");
        // }
        Assert.assertTrue(status,"User is not redirected to the Login Page");

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase", "Test Case 5: Happy Flow Test Failed : ", status ? "PASS" : "FAIL");
        // }
        Assert.assertTrue(status,"User Perform Login Failded");

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        // status = homePage.searchForProduct("YONEX");
        // homePage.addProductToCart("YONEX Smash Badminton Racquet");
        // status = homePage.searchForProduct("Tan");
        // homePage.addProductToCart("Tan Leatherette Weekender Duffle");

        status = homePage.searchForProduct(ProductName1);
        homePage.addProductToCart(ProductName1);
        status = homePage.searchForProduct(ProductName2);
        homePage.addProductToCart(ProductName2);

        // Click on the checkout button
        status = homePage.clickCheckout();
        Assert.assertTrue(status,"User is not able to redirecting to the CheckOut Page");

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue(status, "User is not re-directed to the order success page");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        //logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ", status ? "PASS" : "FAIL");
       
    }

    @Test(priority = 6, groups={"Regression"})
    @Parameters({"ProductName3","ProductName4"})
    public void TestCase06(String ProductName3,String ProductName4) throws InterruptedException {
        Boolean status;
        //logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Register Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase", "Test Case 6:  Verify that cart can be edited: ", status ? "PASS" : "FAIL");
        //     return false;
        // }
        Assert.assertTrue(status,"User Perform Register Failed");
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase", "Test Case 6:  Verify that cart can be edited: ", status ? "PASS" : "FAIL");
        //     return false;
        // }


        homePage.navigateToHome();
        status = homePage.searchForProduct(ProductName3);
        homePage.addProductToCart(ProductName3);//Xtend Smart Watch

        status = homePage.searchForProduct(ProductName4);
        homePage.addProductToCart(ProductName4);//Yarine Floor Lamp

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(ProductName3, 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(ProductName4, 0);

        // update watch quantity again to 1
        status = homePage.changeProductQuantityinCart(ProductName3, 1);
        Assert.assertTrue(status, "Xtend Smart Watch quantity in the cart is not equal to 1");

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            // System.out.println("Error while placing order in: " + e.getMessage());
            // return false;
        }

        status = driver.getCurrentUrl().endsWith("/thanks");

        homePage.navigateToHome();
        homePage.PerformLogout();

       // logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ? "PASS" : "FAIL");
        // return status;
    }

    @Test(priority = 7, groups={"Sanity"})
    @Parameters({"ProductName5"})
    public void TestCase07(String ProductName5) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase",
        //         "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough",
        //         "DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status,"User Perform Registration Failed");
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Registration Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase",
        //             "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
        //             status ? "PASS" : "FAIL");
        //     return false;
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     logStatus("End TestCase",
        //             "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
        //             status ? "PASS" : "FAIL");
        //     return false;
        // }
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(ProductName5);
        homePage.addProductToCart(ProductName5);//Stylecon 9 Seater RHS Sofa Set

        homePage.changeProductQuantityinCart(ProductName5, 10);
        WebElement actualQuantity = driver.findElement(By.xpath("//div[@data-testid='item-qty']"));
        String actual = actualQuantity.getText();
        String expected = "10";
        Assert.assertEquals(actual, expected, "The item quantity is not as expected");

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();
        Assert.assertTrue(status, "insufficient balance error is not thrown when the wallet balance is not enough");

        // logStatus("End TestCase",
        //         "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
        //         status ? "PASS" : "FAIL");
    }

    @Test(priority = 8, groups={"Regression"})
    public void TestCase08() throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        //         "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        //         "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User registration is not successfull");
        // if (!status) {
        //     logStatus("TestCase 8",
        //             "Test Case Failure. Verify that product added to cart is available when a new tab is opened",
        //             "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase09");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase9");
        //     logStatus("End TestCase",
        //             "Test Case 8:   Verify that product added to cart is available when a new tab is opened",
        //             status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        status = homePage.addProductToCart("YONEX Smash Badminton Racquet");
        Assert.assertTrue(status, "Unable to find the searched product");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);
        Assert.assertTrue(status,"Expected product is not available on the cart contents");

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        // status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "EndTestCase", "TestCase08");

        // return status;
    }

    @Test(priority = 9, groups={"Regression"})
    public void TestCase09() throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        //         "Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly ",
        //         "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");

        // Register registration = new Register(driver);
        // registration.navigateToRegisterPage();
        // status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        //     logStatus("TestCase 09",
        //             "Test Case Failure.  Verify that the Privacy Policy, About Us are displayed correctly ",
        //             "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase09");
        // }
        // Assert.assertTrue(status,"Failed to register user");
        // lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase09");
        //     logStatus("End TestCase",
        //             "Test Case 9:    Verify that the Privacy Policy, About Us are displayed correctly ",
        //             status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Thread.sleep(5000);
        status = driver.getCurrentUrl().equals(basePageURL);
        Assert.assertTrue(status, "Verifying parent page url didn't change on privacy policy link click failed");
        // if (!status) {
        //     logStatus("Step Failure", "Verifying parent page url didn't change on privacy policy link click failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase09");
        //     logStatus("End TestCase",
        //             "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        //             status ? "PASS" : "FAIL");
        // }

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        Assert.assertTrue(status,"Verifying new tab opened has Privacy Policy page heading failed");
        // if (!status) {
        //     logStatus("Step Failure", "Verifying new tab opened has Privacy Policy page heading failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase9");
        //     logStatus("End TestCase",
        //             "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        //             status ? "PASS" : "FAIL");
        // }

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        Assert.assertTrue(status,"Verifying new tab opened has Terms Of Service page heading failed");

        // if (!status) {
        //     logStatus("Step Failure", "Verifying new tab opened has Terms Of Service page heading failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase9");
        //     logStatus("End TestCase",
        //             "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        //             status ? "PASS" : "FAIL");
        // }

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // "PASS");
        // takeScreenshot(driver, "EndTestCase", "TestCase9");

        // return status;
    }

    @Test(priority = 10)
    @Parameters({"Name","Email","Message"})
    public void TestCase10(String Name,String Email,String Message) throws InterruptedException {
        // logStatus("Start TestCase",
        //         "Test Case 10: Verify that contact us option is working correctly ",
        //         "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase10");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys(Name);//"crio user"
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys(Email);//"criouser@gmail.com"
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys(Message);//"Testing the contact us page"

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        // logStatus("End TestCase",
        //         "Test Case 10: Verify that contact us option is working correctly ",
        //         "PASS");

        // takeScreenshot(driver, "EndTestCase", "TestCase10");

        // return true;
    }

    @Test(priority = 11, groups={"Sanity"})
    @Parameters({"ProductName9","Address"})
    public void TestCase11(String ProductName9,String Address) throws InterruptedException {
        Boolean status = false;
        // logStatus("Start TestCase",
        //         "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
        //         "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase11");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Failed to register user");
        // if (!status) {
        //     logStatus("TestCase 11",
        //             "Test Case Failure. Ensure that the links on the QKART advertisement are clickable",
        //             "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase11");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");
        // if (!status) {
        //     logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        //     takeScreenshot(driver, "Failure", "TestCase 11");
        //     logStatus("End TestCase",
        //             "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
        //             status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct(ProductName9);//"YONEX Smash Badminton Racquet"
        homePage.addProductToCart(ProductName9);
        homePage.changeProductQuantityinCart(ProductName9, 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(Address);//"Addr line 1  addr Line 2  addr line 3"
        checkoutPage.selectAddress(Address);
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;
        // logStatus("Step ", "Verify that 3 Advertisements are available", status ? "PASS" : "FAIL");

        WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        Assert.assertTrue(status, "Verify that Advertisement 1 is clickable");
        //logStatus("Step ", "Verify that Advertisement 1 is clickable ", status ? "PASS" : "FAIL");

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        Assert.assertTrue(status, "Verify that Advertisement 2 is clickable");
        // logStatus("Step ", "Verify that Advertisement 2 is clickable ", status ? "PASS" : "FAIL");

        // logStatus("End TestCase",
        //         "Test Case 11:  Ensure that the links on the QKART advertisement are clickable",
        //         status ? "PASS" : "FAIL");
        // return status;
    }

    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s",
                String.valueOf(java.time.LocalDateTime.now()), type, message, status));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType,
                    description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

