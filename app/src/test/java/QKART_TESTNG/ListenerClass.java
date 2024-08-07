package QKART_TESTNG;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass implements ITestListener{
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
    
    public void onStart(ITestContext context) {
        System.out.println("onStart method started");
        //System.out.println("Test Pass : "+ context.getName()+" Taking Screenshot ! ");
        // takeScreenshot(QKART_Tests.driver, "Test Case", description);
       
    }

    public void onFinish(ITestContext context) {
        System.out.println("onFinish method started");
      //  System.out.println("onFinish method started"+context.getName()+" Taking Screenshot ! ");

        
    }

    public void onTestStart(ITestResult result) {
        System.out.println("New Test Started" +result.getName()+" Taking Screenshot ! ");
        takeScreenshot(QKART_Tests.driver, "Test_Case_Start", result.getName());
    }

    public void onTestSuccess(ITestResult result) {
        System.out.println("onTestSuccess Method" +result.getName()+" Taking Screenshot ! ");
        takeScreenshot(QKART_Tests.driver, "Test_Case_Success", result.getName());
    }

    public void onTestFailure(ITestResult result) {
        System.out.println("onTestFailure Method" +result.getName()+" Taking Screenshot ! ");
        takeScreenshot(QKART_Tests.driver, "Test_Case_Failure", result.getName());
    }

    public void onTestSkipped(ITestResult result) {
        System.out.println("onTestSkipped Method" +result.getName()+" Taking Screenshot ! ");
        takeScreenshot(QKART_Tests.driver, "Test_Case_Skipped", result.getName());
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("onTestFailedButWithinSuccessPercentage" +result.getName());
    }

}