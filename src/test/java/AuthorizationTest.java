import apiCore.constants.*;
import apiCore.helper.CustomListeners;
import apiCore.pojoClasses.AuthorizationPojo;
import apiCore.pojoClasses.CheckContactPojo;
import apiCore.service.AuthorizationService;
import apiCore.service.CheckContactService;
import io.restassured.response.Response;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(CustomListeners.class)
public class AuthorizationTest extends BaseTest {

    SoftAssert softAssert;
    CheckContactPojo checkContactPojo;
    AuthorizationPojo authorizationPojo;
    AuthorizationConstants authorizationConstants = new AuthorizationConstants();
    ChangeInfoConstants changeInfoConstants = new ChangeInfoConstants();
    ErrorMessages errorMessages = new ErrorMessages();
    PhoneNumbers phoneNumbers = new PhoneNumbers();
    ResponseCodes responseCodes = new ResponseCodes();
    RegisterConstants registerConstants = new RegisterConstants();
    private static final AuthorizationService authorizationService = new AuthorizationService();
    private static final CheckContactService checkContactService = new CheckContactService();


    @Test(description = "B03", groups = "positive")
    public void authorisation() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkArmSoftContact(phoneNumbers.ARMSOFT_CLIENT_ID);
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        if (checkContactResponse.statusCode() != 200) {
            softAssert.fail("Check Contact failed with status code: " + checkContactResponse.statusCode() +
                    " Response description: " + checkContactPojo.getTransaction().getResponseDescription());
        } else {
            Response authresponse = authorizationService.validAuthorisation(
                    authorizationConstants.AMOUNT, checkContactPojo.getTransaction().getRelativeNumber(), checkContactPojo);
            /* extract Authorization response with pojo class **/
            authorizationPojo = authresponse.as(AuthorizationPojo.class);
            softAssert = new SoftAssert();
            softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
            softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(), "Success");
            softAssert.assertEquals(authresponse.statusCode(), 200);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseDescription(), "Success");
        }
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")

    public void lateAuthorizationReq() {
        Response authResponse = authorizationService.withoutCheckContact(changeInfoConstants.WRONG_RELATIVE_NUMBER, phoneNumbers.ARMSOFT_CLIENT_ID);
        /* extract Authorization response with pojo class **/
        authorizationPojo = authResponse.as(AuthorizationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(authResponse.statusCode(), 400);
        softAssert.assertEquals(authorizationPojo.getTransaction().getResponseCode(), responseCodes.LATE_REQUEST_CODE);
        softAssert.assertEquals(authorizationPojo.getTransaction().getResponseDescription(), errorMessages.LATE_REQ_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")
    public void InValidAuthorization() {
        Response authResponse = authorizationService.withoutCheckContact(registerConstants.INVALID_RELATIVE_NUMBER, phoneNumbers.ARMSOFT_CLIENT_ID);
        /* extract Authorization response with pojo class **/
        authorizationPojo = authResponse.as(AuthorizationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(authResponse.statusCode(), 400);
        softAssert.assertEquals(authorizationPojo.getTransaction().getResponseCode(), responseCodes.INVALID_REQ_CODE);
        softAssert.assertEquals(authorizationPojo.getTransaction().getResponseDescription(), errorMessages.INVALID_REQUEST_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")
    public void authMoreThenMaxAmount() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkArmSoftContact(phoneNumbers.ARMSOFT_CLIENT_ID);
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        if (checkContactResponse.statusCode() != 200) {
            softAssert.fail("Check Contact failed with status code: " + checkContactResponse.statusCode() +
                    " Response description: " + checkContactPojo.getTransaction().getResponseDescription());
        } else {
            Response authResponse = authorizationService.validAuthorisation(
                    500000007, checkContactPojo.getTransaction().getRelativeNumber(), checkContactPojo);
            /* extract Authorization response with pojo class **/
            authorizationPojo = authResponse.as(AuthorizationPojo.class);
            softAssert.assertEquals(authResponse.statusCode(), 400);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseCode(), responseCodes.MAX_AMOUNT_ERROR_CODE);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseDescription(), errorMessages.MAX_AMOUNT_ERROR);
        }
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative", enabled = false)
    public void moreThanOperationalLimit() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkArmSoftContact(phoneNumbers.ARMSOFT_CLIENT_ID);
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        if (checkContactResponse.getStatusCode() != 200) {
            softAssert.fail("Check Contact failed with status code: " + checkContactResponse.statusCode() +
                    " Response description: " + checkContactPojo.getTransaction().getResponseDescription());
        } else {
            Response authResponse = authorizationService.validAuthorisation(
                    500000007, checkContactPojo.getTransaction().getRelativeNumber(), checkContactPojo);
            /* extract Authorization response with pojo class **/
            authorizationPojo = authResponse.as(AuthorizationPojo.class);
            softAssert.assertEquals(authResponse.statusCode(), 400);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseCode(), responseCodes.MAX_OPERATIONAL_LIMIT_CODE);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseDescription(), errorMessages.MAX_OPERATIONAL_LIMIT_ERROR);
        }
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")
    public void noRecipientFound() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkACBAContact(phoneNumbers.NON_EXISTING_PHONE_NUMBER);
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        if (checkContactResponse.statusCode() != 200) {
            softAssert.fail("Check Contact failed with status code: " + checkContactResponse.statusCode() +
                    " Response description: " + checkContactPojo.getTransaction().getResponseDescription());
        } else {
            Response authResponse = authorizationService.validAuthorisation(
                    authorizationConstants.AMOUNT, checkContactPojo.getTransaction().getRelativeNumber(), checkContactPojo);
            /* extract Authorization response with pojo class **/
            authorizationPojo = authResponse.as(AuthorizationPojo.class);
            softAssert.assertEquals(authResponse.statusCode(), 400);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseCode(), responseCodes.NO_RECIPIENT_ERROR_CODE);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseDescription(), errorMessages.NO_RECIPIENT_ERROR);
        }
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "negative")
    public void authWithIncorrectToken() {
        Response authResponse = authorizationService.withWrongToken(phoneNumbers.ARMSOFT_CLIENT_ID);
        /* extract Authorization response with pojo class **/
        authorizationPojo = authResponse.as(AuthorizationPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(authResponse.statusCode(), 400);
        softAssert.assertEquals(authorizationPojo.getTransaction().getResponseCode(), responseCodes.UNAUTHORIZED_RES_CODE);
        softAssert.assertEquals(authorizationPojo.getTransaction().getResponseDescription(), errorMessages.UNAUTHORIZED);
        softAssert.assertAll();
    }

    @Test(description = "B03", groups = "positive")
    public void authWithWrongAmount() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkArmSoftContact(phoneNumbers.ARMSOFT_CLIENT_ID);
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        if (checkContactResponse.getStatusCode() != 200) {
            softAssert.fail("Check Contact failed with status code: " + checkContactResponse.statusCode() +
                    " Response description: " + checkContactPojo.getTransaction().getResponseDescription());
        } else {
            Response authresponse = authorizationService.validAuthorisation(
                    -1, checkContactPojo.getTransaction().getRelativeNumber(), checkContactPojo);
            /* extract Authorization response with pojo class **/
            authorizationPojo = authresponse.as(AuthorizationPojo.class);
            softAssert.assertEquals(authresponse.statusCode(), 400);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
            softAssert.assertEquals(authorizationPojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        }
        softAssert.assertAll();
    }
}


