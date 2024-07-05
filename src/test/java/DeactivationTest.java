
import apiCore.constants.*;
import apiCore.helper.CustomListeners;
import apiCore.helper.Utils;
import apiCore.pojoClasses.DeactivationPojo;
import apiCore.pojoClasses.RegistrationPojo;
import apiCore.service.DeactivationService;
import apiCore.service.RegisterService;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(CustomListeners.class)
public class DeactivationTest extends BaseTest {

    DeactivationService deactivationService = new DeactivationService();
    ChangeInfoConstants changeInfoConstants = new ChangeInfoConstants();
    RegisterConstants registerConstants = new RegisterConstants();
    ErrorMessages errorMessages = new ErrorMessages();
    ResponseCodes responseCodes = new ResponseCodes();
    DeactivationPojo responsePojo;
    RegistrationPojo registrationPojo;
    SoftAssert softAssert;

    @BeforeMethod(alwaysRun = true)
    public void registration() {
        Response registerResponse = new RegisterService().byblosBankSignUp(Utils.randomNumber());
        registrationPojo = registerResponse.as(RegistrationPojo.class);
        if (registerResponse.getStatusCode() != 200) {
            Assert.fail("Registration failed with status code " + registerResponse.getStatusCode()
                    + ": Response Description: " + registrationPojo.getTransaction().getResponseDescription());
        }
    }

    @Test(description = "B05", groups = "positive")
    public void deactivation() {
        Response response = deactivationService.validDeactivation(registrationPojo.getTransaction().getNewClientId(), registrationPojo);
        /* extract response with pojo class**/
        responsePojo = response.as(DeactivationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 200);
        softAssert.assertEquals(responsePojo.getSender().getClient().getCustomerId(), registerConstants.CUSTOMER_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getId(), registerConstants.TRANSACTION_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), "Success");
        softAssert.assertAll();
    }

    @Test(description = "B05", groups = "negative")
    public void nonExistingClientDeactivation() {
        Response response = deactivationService.validDeactivation(changeInfoConstants.NON_EXISTING_PHONE_NUM, registrationPojo);
        /* extract response with pojo class**/
        responsePojo = response.as(DeactivationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getSender().getClient().getCustomerId(), registerConstants.CUSTOMER_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getId(), registerConstants.TRANSACTION_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.NO_ACCOUNT_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.NO_ACCOUNT_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B05", groups = "negative")
    public void deactivateWithOutClientId() {
        Response response = deactivationService.validDeactivation("", registrationPojo);
        /* extract response with pojo class**/
        responsePojo = response.as(DeactivationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B05", groups = "negative")
    public void deactivateWithWrongToken() {
        Response response = deactivationService.deactivationWithWrongToken(registrationPojo.getTransaction().getNewClientId());
        /* extract response with pojo class**/
        responsePojo = response.as(DeactivationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.UNAUTHORIZED_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.UNAUTHORIZED);
        softAssert.assertAll();
    }

    @Test(description = "B05", groups = "negative")
    public void deactivationWithOutCustomerId() {
        Response response = deactivationService.withOtherCustomerId(registrationPojo.getTransaction().getNewClientId());
        /* extract response with pojo class**/
        responsePojo = response.as(DeactivationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.NO_ACCOUNT_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.NO_ACCOUNT_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B05", groups = "negative")
    public void deactivationOtherBankClientId() {
        Response response = deactivationService.withOtherCustomerId(new PhoneNumbers().ARMSOFT_CLIENT_ID);
        /* extract response with pojo class**/
        responsePojo = response.as(DeactivationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.NO_ACCOUNT_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.NO_ACCOUNT_ERROR);
        softAssert.assertAll();
    }

}
