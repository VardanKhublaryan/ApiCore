
import apiCore.constants.ErrorMessages;
import apiCore.constants.PhoneNumbers;
import apiCore.constants.ResponseCodes;
import apiCore.helper.CustomListeners;
import apiCore.helper.Utils;
import apiCore.pojoClasses.ChangeInfoPojo;
import apiCore.pojoClasses.RegistrationPojo;
import apiCore.service.ChangeInfoService;
import apiCore.service.RegisterService;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(CustomListeners.class)
public class ChangeInfoTest extends BaseTest {
    private static final ChangeInfoService changeInfoService = new ChangeInfoService();
    ErrorMessages errorMessages = new ErrorMessages();
    PhoneNumbers phoneNumbers = new PhoneNumbers();
    ResponseCodes responseCodes = new ResponseCodes();
    SoftAssert softAssert;
    ChangeInfoPojo changeInoPojo;
    RegistrationPojo registrationPojo;


    /**
     * registration before change info request
     **/
    @BeforeMethod(alwaysRun = true)
    public void registration() {
        Response registerResponse = new RegisterService().byblosBankSignUp(Utils.randomNumber());
        registrationPojo = registerResponse.as(RegistrationPojo.class);
        if (registerResponse.getStatusCode() != 200) {
            Assert.fail("Registration failed with status code " + registerResponse.getStatusCode()
                    + ": Response Description: " + registrationPojo.getTransaction().getResponseDescription());
        }
    }

    @Test(description = "B03", groups = "positive")
    public void changeInfo() {
        Response chengeInfoResponse = changeInfoService.validChangeInfo(registrationPojo.getTransaction().getNewClientId(), registrationPojo);
        /* extract response with pojo class**/
        changeInoPojo = chengeInfoResponse.as(ChangeInfoPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(chengeInfoResponse.statusCode(), 200);
        softAssert.assertEquals(changeInoPojo.getSender().getClient().getCustomerId(), registrationPojo.getSender().getClient().getCustomerId());
        softAssert.assertEquals(changeInoPojo.getTransaction().getId(), registrationPojo.getTransaction().getId());
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseDescription(), "Success");
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")
    public void nonExistingClientInfo() {
        Response chengeInfoResponse = changeInfoService.nonExistingClientId(registrationPojo);
        /* extract response with pojo class**/
        changeInoPojo = chengeInfoResponse.as(ChangeInfoPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(chengeInfoResponse.statusCode(), 400);
        softAssert.assertEquals(changeInoPojo.getSender().getClient().getCustomerId(), registrationPojo.getSender().getClient().getCustomerId());
        softAssert.assertEquals(changeInoPojo.getTransaction().getId(), registrationPojo.getTransaction().getId());
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseCode(), responseCodes.NO_ACCOUNT_RES_CODE);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseDescription(), errorMessages.NO_ACCOUNT_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")
    public void changeInfoWithoutAut() {
        Response chengeInfoResponse = changeInfoService.changeInfoNoToken(registrationPojo.getTransaction().getNewClientId());
        /* extract response with pojo class**/
        changeInoPojo = chengeInfoResponse.as(ChangeInfoPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(chengeInfoResponse.statusCode(), 400);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseCode(), responseCodes.UNAUTHORIZED_RES_CODE);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseDescription(), errorMessages.UNAUTHORIZED);
        softAssert.assertAll();
    }

    /**
     * Trying to change phone number with the one that already exist in the Preferred FO DB
     */
    @Test(description = "B03", groups = "negative")
    public void existingNewClientId() {
        Response chengeInfoResponse = changeInfoService.newClientIdIsExisting(phoneNumbers.CHANGE_INFO_PHONE_NUMBER /* already exist phone number*/, registrationPojo);
        /* extract response with pojo class**/
        changeInoPojo = chengeInfoResponse.as(ChangeInfoPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(chengeInfoResponse.statusCode(), 400);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseCode(), responseCodes.PHONE_NUM_EXIST_CODE);
        softAssert.assertTrue(changeInoPojo.getTransaction().getResponseDescription()
                .contains(errorMessages.PHONE_NUM_EXIST_ERROR + phoneNumbers.CHANGE_INFO_PHONE_NUMBER));
        softAssert.assertAll();
    }

    // change info without client id
    @Test(description = "B03", groups = "negative")
    public void changeWithoutClientId() {
        Response chengeInfoResponse = changeInfoService.withoutClientId(registrationPojo);
        /* extract response with pojo class**/
        changeInoPojo = chengeInfoResponse.as(ChangeInfoPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(chengeInfoResponse.statusCode(), 400);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")
    public void changeWithoutTransactionId() {
        Response chengeInfoResponse = changeInfoService.withoutTransactionId(registrationPojo);
        /* extract response with pojo class**/
        changeInoPojo = chengeInfoResponse.as(ChangeInfoPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(chengeInfoResponse.statusCode(), 400);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "positive")
    public void changeToSameClientId() {
        Response chengeInfoResponse = changeInfoService.changeInfoSameClientId(registrationPojo);
        /* extract response with pojo class**/
        changeInoPojo = chengeInfoResponse.as(ChangeInfoPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(chengeInfoResponse.statusCode(), 200);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseDescription(), "Success");
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")
    public void changeInfoWrongCustomerId() {
        Response chengeInfoResponse = changeInfoService.changeWithWrongCustomerId(registrationPojo);
        /* extract response with pojo class**/
        changeInoPojo = chengeInfoResponse.as(ChangeInfoPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(chengeInfoResponse.statusCode(), 400);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseCode(), responseCodes.NO_ACCOUNT_RES_CODE);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseDescription(), errorMessages.NO_ACCOUNT_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")
    public void changeInfoIncorrectClientName() {
        Response chengeInfoResponse = changeInfoService.changeWithWrongClientName(registrationPojo);
        /* extract response with pojo class**/
        changeInoPojo = chengeInfoResponse.as(ChangeInfoPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(chengeInfoResponse.statusCode(), 400);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(changeInoPojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }
}

