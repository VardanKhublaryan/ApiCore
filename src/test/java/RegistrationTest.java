
import apiCore.constants.ErrorMessages;
import apiCore.constants.PhoneNumbers;
import apiCore.constants.RegisterConstants;
import apiCore.constants.ResponseCodes;
import apiCore.helper.CustomListeners;
import apiCore.helper.Utils;
import apiCore.pojoClasses.RegistrationPojo;
import apiCore.service.BankChangeService;
import apiCore.service.RegisterService;
import io.restassured.response.Response;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;


@Listeners(CustomListeners.class)
public class RegistrationTest extends BaseTest {

    private final RegisterService registerService = new RegisterService();
    private final BankChangeService bankChangeService = new BankChangeService();
    private final ErrorMessages errorMessages = new ErrorMessages();
    private final PhoneNumbers phoneNumbers = new PhoneNumbers();
    private final RegisterConstants registerConstants = new RegisterConstants();
    private final ResponseCodes responseCodes = new ResponseCodes();
    private RegistrationPojo responsePojo;
    private SoftAssert softAssert;


    @Test(description = "B01.1", groups = "positive")
    public void validSignUp() {
        Response response = registerService.testBankSignUp(Utils.randomNumber());
        /* extract response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 200);
        softAssert.assertEquals(responsePojo.getSender().getClient().getCustomerId(), registerConstants.CUSTOMER_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getId(), registerConstants.TRANSACTION_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), "Success");
        softAssert.assertAll();
    }

    @Test(description = "B01.1", groups = "negative")
    public void signUpWithExistsAccount() {
        Response response = registerService.byblosBankSignUp(phoneNumbers.EXISTING_PHONE_NUMBER);
        /* extract response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getSender().getClient().getCustomerId(), registerConstants.CUSTOMER_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getId(), registerConstants.TRANSACTION_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.EXISTING_ACCOUNT_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.EXISTING_ACCOUNT_ERROR);
        softAssert.assertAll();
    }

    /**
     * The Client already has Preferred FO (different FO)
     */
    @Test(description = "B01.1", groups = "negative")
    public void existsAccountDifferentFo() {
        Response response = registerService.byblosBankSignUp(phoneNumbers.ANOTHER_FO_CLIENT_ID);
        /* extract response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getSender().getClient().getCustomerId(), registerConstants.CUSTOMER_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getId(), registerConstants.TRANSACTION_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.ANOTHER_FO_ACCOUNT_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.ANOTHER_FO_ACCOUNT_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B01.1", groups = "negative")
    public void signupWithoutInstitute() {
        Response response = registerService.signUpEmptyInstitute(phoneNumbers.ANOTHER_FO_CLIENT_ID);
        /* extract response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getSender().getClient().getCustomerId(), registerConstants.CUSTOMER_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getId(), registerConstants.TRANSACTION_ID);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.UNAUTHORIZED_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.UNAUTHORIZED);
        softAssert.assertAll();
    }

    @Test(description = "B01.1", groups = "negative")
    public void signupWithoutClientId() {
        Response response = registerService.byblosBankSignUp("");
        /* extract response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }

    // signup with incorrect clientId
    @Test(description = "B01.1", groups = "negative")
    public void signupWithIncorrectId() {
        Response response = registerService.byblosBankSignUp(phoneNumbers.INCORRECT_CLIENT_ID);
        /* extract response with pojo class **/
        responsePojo = response.as(RegistrationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(response.statusCode(), 400);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B01.3", groups = "positive")
    public void signUpWithOtp() {
        softAssert = new SoftAssert();
        String number = Utils.randomNumber();
        Response bankChangeResponse = bankChangeService.validBankChangeReq(number);
        /* extract bankChange response with pojo class **/
        responsePojo = bankChangeResponse.as(RegistrationPojo.class);
        if (bankChangeResponse.getStatusCode() != 200) {
            softAssert.fail("bankChangeResponse is fail with status code: " + bankChangeResponse.getStatusCode()
                    + ": Response description: " + responsePojo.getTransaction().getResponseDescription());
        } else {
            Response signUpOtp = registerService.byblosBankSignUpWithOtp(number, responsePojo.getTransaction().getOtp());
            /* extract signUpOtpRequest response with pojo class **/
            responsePojo = signUpOtp.as(RegistrationPojo.class);
            softAssert.assertEquals(signUpOtp.statusCode(), 200);
            softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
            softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), "Success");
        }
        softAssert.assertAll();
    }

    @Test(description = "B01.3", groups = "negative")
    public void signUpnWithWrongOtp() {
        softAssert = new SoftAssert();
        String number = Utils.randomNumber();
        Response bankChangeResponse = bankChangeService.validBankChangeReq(number);
        if (bankChangeResponse.getStatusCode() != 200) {
            softAssert.fail("bankChangeResponse is fail with status code: " + bankChangeResponse.getStatusCode());
        } else {
            Response signUpOtp = registerService.byblosBankSignUpWithOtp(number, registerConstants.INCORRECT_OTP);
            /* extract signUpOtpRequest response with pojo class **/
            responsePojo = signUpOtp.as(RegistrationPojo.class);
            softAssert.assertEquals(signUpOtp.statusCode(), 400);
            softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_OTP_CODE);
            softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_OTP_ERROR);
        }
        softAssert.assertAll();
    }

    @Test(description = "B01.3", groups = "negative")
    public void signUpWithUsedOtp() {
        softAssert = new SoftAssert();
        String number = Utils.randomNumber();
        Response bankChangeResponse = bankChangeService.validBankChangeReq(number);
        /* extract bankChange response with pojo class **/
        responsePojo = bankChangeResponse.as(RegistrationPojo.class);
        if (bankChangeResponse.getStatusCode() != 200) {
            softAssert.fail("bankChangeResponse is fail with status code: " + bankChangeResponse.getStatusCode()
                    + ": Response description: " + responsePojo.getTransaction().getResponseDescription());
        } else {
            //signup with otp
            registerService.byblosBankSignUpWithOtp(number, responsePojo.getTransaction().getOtp());
            //signup with used otp
            Response signUpOtp = registerService.byblosBankSignUpWithOtp(number, responsePojo.getTransaction().getOtp());
            /* extract signUpOtpRequest response with pojo class **/
            responsePojo = signUpOtp.as(RegistrationPojo.class);
            softAssert.assertEquals(signUpOtp.statusCode(), 400);
            softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.USED_OTP_ERROR_CODE);
            softAssert.assertEquals(responsePojo.getTransaction().getResponseDescription(), errorMessages.USED_OTP_ERROR);
        }
        softAssert.assertAll();
    }

    // sign up with incorrect transaction id
    @Test(description = "B01.3", groups = "negative")
    public void signUpOtpIncorrectId() {
        softAssert = new SoftAssert();
        String number = Utils.randomNumber();
        Response bankChangeResponse = bankChangeService.validBankChangeReq(number);
        /* extract bankChange response with pojo class **/
        responsePojo = bankChangeResponse.as(RegistrationPojo.class);
        if (bankChangeResponse.getStatusCode() != 200) {
            softAssert.fail("bankChangeResponse is fail with status code: " + bankChangeResponse.getStatusCode()
                    + ": Response description: " + responsePojo.getTransaction().getResponseDescription());
        } else {
            Response signUpOtp = registerService.signUpOtpWithIncorrectId(number, responsePojo.getTransaction().getOtp());
            /* extract signUpOtpRequest response with pojo class **/
            responsePojo = signUpOtp.as(RegistrationPojo.class);
            softAssert.assertEquals(signUpOtp.statusCode(), 400);
            softAssert.assertEquals(responsePojo.getTransaction().getResponseCode(), responseCodes.INVALID_REQ_CODE);
            softAssert.assertTrue(responsePojo.getTransaction().getResponseDescription().equalsIgnoreCase(errorMessages.INVALID_REQUEST_ERROR));
        }
        softAssert.assertAll();
    }
}