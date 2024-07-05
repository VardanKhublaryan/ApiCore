import apiCore.service.BaseService;
import org.testng.annotations.AfterMethod;

public class BaseTest {

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        BaseService.removeThreadLocalResponse();
    }
}
