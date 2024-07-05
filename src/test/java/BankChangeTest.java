import apiCore.constants.ErrorMessages;
import apiCore.constants.ResponseCodes;
import apiCore.helper.CustomListeners;
import apiCore.helper.Utils;
import apiCore.pojoClasses.RegistrationPojo;
import apiCore.service.BankChangeService;
import io.restassured.response.Response;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(CustomListeners.class)
public class BankChangeTest extends BaseTest {
    RegistrationPojo responsePojo;
    SoftAssert softAssert;
    ErrorMessages errorMessages = new ErrorMessages();
    ResponseCodes responseCodes = new ResponseCodes();
    String clientId ;


    @Test(description = "B01.2", groups = "positive")
    public void bankChange() {
        softAssert = new SoftAssert();
        Response response = new BankChangeService().validBankChangeReq(Utils.randomNumber());
        /* extract bankChange response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert.assertEquals(response.statusCode(), 200);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), "Success");
        softAssert.assertAll();
    }

    @Test(description = "B01.2 ,Bank change with wrong transaction id", groups = "negative")
    public void invalidBankChange() {
        softAssert = new SoftAssert();
        // bank change request with invalid transaction id
        Response response = new BankChangeService().invalidTransactionId(Utils.randomNumber());
        /* extract bankChange response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.INVALID_REQ_CODE);
        softAssert.assertTrue(responsePojo.getTransaction().getResponseDescription().equalsIgnoreCase(errorMessages.INVALID_REQUEST_ERROR));
        softAssert.assertAll();
    }

    @Test(description = "B01.2", groups = "negative")
    public void bankChangeWithoutCustomerId() {
        softAssert = new SoftAssert();
        // bank change request with invalid CustomerId
        Response response = new BankChangeService().missingCustomerId(Utils.randomNumber());
        /* extract bankChange response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }

    // Bank change request without checking whether is the user as client in the changed bank or not
    @Test(description = "B01.2", groups = "negative")
    public void bankChangeNoVerifiedClient() {
        softAssert = new SoftAssert();
        // bank change request with invalid CustomerId
        Response response = new BankChangeService().missingByblosVerification(Utils.randomNumber());
        /* extract bankChange response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.INVALID_REQ_CODE);
        softAssert.assertTrue(responsePojo.getTransaction().getResponseDescription().equalsIgnoreCase(errorMessages.INVALID_REQUEST_ERROR));
        softAssert.assertAll();
    }
}
