package apiCore.helper;

import apiCore.service.BaseService;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class CustomListeners implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        // Fetch the response from ThreadLocal storage
        Response response = BaseService.getThreadLocalResponse();
        if (response != null) {
            printInfo("Test failed: " + result.getName());
            printInfo("Response Status Code: " + response.getStatusCode());
            response.prettyPeek();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        printInfo("Thread no: " + Thread.currentThread().getId());
        printInfo(result.getName() + " test started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestSkipped(ITestResult result) {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    public static void printInfo(Object text) {
        System.out.println("\u001B[32m" + text + "\u001B[0m");
    }
}
