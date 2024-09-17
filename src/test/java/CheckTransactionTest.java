import apiCore.constants.*;
import apiCore.helper.CustomListeners;
import apiCore.helper.Utils;
import apiCore.pojoClasses.AuthorizationPojo;
import apiCore.pojoClasses.CheckContactPojo;
import apiCore.pojoClasses.CheckTransactionPojo;
import apiCore.service.AuthorizationService;
import apiCore.service.CheckContactService;
import apiCore.service.CheckTransactionService;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(CustomListeners.class)
public class CheckTransactionTest extends BaseTest {
    private SoftAssert softAssert;
    private final AuthorizationConstants authorizationConstants = new AuthorizationConstants();
    private final ChangeInfoConstants changeInfoConstants = new ChangeInfoConstants();
    private final ErrorMessages errorMessages = new ErrorMessages();
    private final PhoneNumbers phoneNumbers = new PhoneNumbers();
    private final ResponseCodes responseCodes = new ResponseCodes();
    private CheckContactPojo checkContactPojo;
    private AuthorizationPojo authorizationPojo;
    private CheckTransactionPojo checkTransactionPojo;
    private static final AuthorizationService authorizationService = new AuthorizationService();


    @Test(description = "X09", groups = "positive")
    public void checkTransaction() {
        softAssert = new SoftAssert();
        /* send checkContact request and extract response with pojo class */
        checkContactPojo = new CheckContactService().checkArmSoftContact(phoneNumbers.ARMSOFT_CLIENT_ID).as(CheckContactPojo.class);
        Response authorisationResponse = authorizationService.validAuthorisation(
                authorizationConstants.AMOUNT, checkContactPojo.getTransaction().getRelativeNumber(), checkContactPojo);
        if (authorisationResponse.getStatusCode() != 200) {
            softAssert.fail("Authorisation failed with status code " + authorisationResponse.statusCode());
        } else {
            /* extract authorization response with pojo class */
            authorizationPojo = authorisationResponse.as(AuthorizationPojo.class);
            Response checkTransactionResponse = new CheckTransactionService().validCheckTransaction(authorizationPojo,
                    authorizationPojo.getTransaction().getRelativeNumber());
            /* extract checkTransaction response with pojo class */
            checkTransactionPojo = checkTransactionResponse.as(CheckTransactionPojo.class);
            softAssert.assertEquals(checkTransactionResponse.statusCode(), 200);
            softAssert.assertEquals(checkTransactionPojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
            softAssert.assertEquals(checkTransactionPojo.getTransaction().getResponseDescription(), "Success");
        }
        softAssert.assertAll();
    }

    @Test(description = "X09", groups = "negative")
    public void failedTransaction() {
        /* send checkContact request and extract response with pojo class */
        checkContactPojo = new CheckContactService().checkArmSoftContact(phoneNumbers.ARMSOFT_CLIENT_ID).as(CheckContactPojo.class);
        // authorisation with wrong relativeNumber
        Response response = authorizationService.validAuthorisation(authorizationConstants.AMOUNT, changeInfoConstants.WRONG_RELATIVE_NUMBER, checkContactPojo);
        if (response.getStatusCode() != 400) {
            Assert.fail("Authorisation failed with status code " + response.statusCode());
        } else {
            /* extract authorization response with pojo class */
            authorizationPojo = response.as(AuthorizationPojo.class);
            Response checkTransactionResponse = new CheckTransactionService().validCheckTransaction(authorizationPojo,
                    authorizationPojo.getTransaction().getRelativeNumber());
            /* extract checkTransaction response with pojo class */
            checkTransactionPojo = checkTransactionResponse.as(CheckTransactionPojo.class);
            softAssert = new SoftAssert();
            softAssert.assertEquals(checkTransactionResponse.statusCode(), 200);
            softAssert.assertEquals(checkTransactionPojo.getTransaction().getResponseCode(), responseCodes.FAILED_TRANSACTION_CODE);
            softAssert.assertEquals(checkTransactionPojo.getTransaction().getResponseDescription(), errorMessages.FAILED_TRANSACTION);
        }
        softAssert.assertAll();
    }

    @Test(description = "X09", groups = "negative")
    public void noFoundTransaction() {
        String relativeNumber = Utils.uniqueRelativeNumber();
        Response checkTransactionResponse = new CheckTransactionService().noFoundTransaction(relativeNumber);
        /* extract checkTransaction response with pojo class */
        checkTransactionPojo = checkTransactionResponse.as(CheckTransactionPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(checkTransactionResponse.statusCode(), 400);
        softAssert.assertEquals(checkTransactionPojo.getTransaction().getResponseCode(), responseCodes.NO_FOUND_TRANSACTION_CODE);
        softAssert.assertTrue(checkTransactionPojo.getTransaction().getResponseDescription().contains(errorMessages.NO_FOUND_TRANSACTION + relativeNumber));
        softAssert.assertAll();
    }

    //  check transaction without institute
    @Test(description = "X09", groups = "negative")
    public void checkWithoutBank() {
        Response checkTransactionResponse = new CheckTransactionService().checkTransactionWithoutBank(Utils.uniqueRelativeNumber());
        /* extract checkTransaction response with pojo class */
        checkTransactionPojo = checkTransactionResponse.as(CheckTransactionPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(checkTransactionResponse.statusCode(), 400);
        softAssert.assertEquals(checkTransactionPojo.getTransaction().getResponseCode(), responseCodes.UNAUTHORIZED_RES_CODE);
        softAssert.assertEquals(checkTransactionPojo.getTransaction().getResponseDescription(), errorMessages.UNAUTHORIZED);
        softAssert.assertAll();
    }

    // check transaction without Transaction id
    @Test(description = "X09", groups = "negative")
    public void checkWithoutId() {
        Response checkTransactionResponse = new CheckTransactionService().checkTransactionWithoutId(Utils.uniqueRelativeNumber());
        /* extract checkTransaction response with pojo class */
        checkTransactionPojo = checkTransactionResponse.as(CheckTransactionPojo.class);
        softAssert = new SoftAssert();
        softAssert.assertEquals(checkTransactionResponse.statusCode(), 400);
        softAssert.assertEquals(checkTransactionPojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(checkTransactionPojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }
}
