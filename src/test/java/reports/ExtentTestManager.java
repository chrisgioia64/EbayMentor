package reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ExtentTestManager {
    static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
    static ExtentReports extent = ExtentManager.getInstance();

    public static synchronized ExtentTest getTest() {
        return (ExtentTest) extentTestMap.get((int) (long) (Thread.currentThread().getId()));
    }

    public static synchronized void endTest() {
        extent.flush();
    }

    public static synchronized ExtentTest startTest(ITestResult result) {
        Object descriptionObj = result.getMethod().getDescription();
        String description = descriptionObj != null ? descriptionObj.toString() : "";

        ExtentTest test = extent.createTest(getUniqueTestCase(result), description);
        extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);
        return test;
    }

    private static String getUniqueTestCase(ITestResult result) {
        return result.getInstanceName() + "." + result.getMethod().getMethodName() + "(" +
                Arrays.toString(result.getParameters()) + ")";
    }
}
