
import apiCore.constants.ErrorMessages;
import apiCore.constants.InstituteTypes;
import apiCore.constants.PhoneNumbers;
import apiCore.constants.ResponseCodes;
import apiCore.helper.CustomListeners;
import apiCore.pojoClasses.CheckContactPojo;
import apiCore.service.CheckContactService;
import io.restassured.response.Response;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(CustomListeners.class)
public class CheckContactTest extends  BaseTest {

    private static final CheckContactService checkContactService = new CheckContactService();
    private final ErrorMessages errorMessages = new ErrorMessages();
    private final InstituteTypes instituteTypes = new InstituteTypes();
    private final PhoneNumbers phoneNumbers = new PhoneNumbers();
    private CheckContactPojo checkContactPojo;
    private final ResponseCodes responseCodes = new ResponseCodes();
    private SoftAssert softAssert;


    @Test(description = "X01", groups = "positive")
    public void checkContact() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkArmSoftContact(phoneNumbers.ARMSOFT_CLIENT_ID);
        /* extract response with pojo class**/
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        softAssert.assertEquals(checkContactResponse.statusCode(), 200);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(), "Success");
        softAssert.assertAll();
    }

    @Test(description = "X01", groups = "negative")
    public void withOutSystemInstitute() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkVTBContact(phoneNumbers.ARMSOFT_CLIENT_ID);
        /* extract response with pojo class**/
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        softAssert.assertEquals(checkContactResponse.statusCode(), 400);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), responseCodes.NOT_MEMBER_OF_SYSTEM_CODE);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(), errorMessages.NOT_MEMBER_OF_SYSTEM_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "X01", groups = "negative")
    public void checkNonExistingContact() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkArmSoftContact(phoneNumbers.NON_EXISTING_PHONE_NUMBER);
        /* extract response with pojo class**/
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        softAssert.assertEquals(checkContactResponse.statusCode(), 400);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), responseCodes.NO_RECIPIENT_ERROR_CODE);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(),
                "No recipient found for '" + phoneNumbers.NON_EXISTING_PHONE_NUMBER + "' phone number.");
        softAssert.assertAll();
    }

    @Test(description = "X01", groups = "negative")
    public void checkContactWithoutToken() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkWithoutToken(phoneNumbers.ARMSOFT_CLIENT_ID);
        /* extract response with pojo class**/
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        softAssert.assertEquals(checkContactResponse.statusCode(), 400);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), responseCodes.UNAUTHORIZED_RES_CODE);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(), errorMessages.UNAUTHORIZED);
        softAssert.assertAll();
    }

    @Test(description = "X01", groups = "negative")
    public void withoutCustomerId() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkWithoutICustomerId(phoneNumbers.ANOTHER_FO_CLIENT_ID);
        /* extract response with pojo class**/
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        softAssert.assertEquals(checkContactResponse.statusCode(), 400);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }

    @Test(description = "X01", groups = "negative")
    public void checkWithoutTransactionId() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.invalidCheckContact(phoneNumbers.ARMSOFT_CLIENT_ID);
        /* extract response with pojo class**/
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        softAssert.assertEquals(checkContactResponse.statusCode(), 400);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), responseCodes.INCORRECT_JSON_RES_CODE);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(), errorMessages.INCORRECT_JSON_ERROR);
        softAssert.assertAll();
    }

    // check contact when receiver.institute == null
    @Test(description = "X01.1 When receiver.institute == null", groups = "positive")
    public void withoutReceiverBank() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkContact(phoneNumbers.ARMSOFT_CLIENT_ID);
        /* extract response with pojo class**/
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        softAssert.assertEquals(checkContactResponse.statusCode(), 200);
        softAssert.assertEquals(checkContactPojo.getReceiver().getInstitute().getId(), instituteTypes.ARMSOFT2_ID);
        softAssert.assertEquals(checkContactPojo.getReceiver().getInstitute().getType(), instituteTypes.ARMSOFT2);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), responseCodes.RESPONSE_CODE_SUCCESS);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(), "Success");
        softAssert.assertAll();
    }

    // check contact when receiver.institute == null
    @Test(description = "X01.1 When receiver.institute == null", groups = "negative")
    public void withNonExistingNumber() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkContact(phoneNumbers.NON_EXISTING_PHONE_NUMBER);
        /* extract response with pojo class**/
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        softAssert.assertEquals(checkContactResponse.statusCode(), 400);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), "0027");
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(),
                "Recipient with '" + phoneNumbers.NON_EXISTING_PHONE_NUMBER + "' phone number doesn't have default institute");
        softAssert.assertAll();
    }

    @Test(description = "institute = ACBA Bank, receiver.client.id = Armsoft client id", groups = "negative")
    public void ACBAWrongCheckContact() {
        softAssert = new SoftAssert();
        Response checkContactResponse = checkContactService.checkACBAContact(phoneNumbers.ARMSOFT_CLIENT_ID);
        /* extract response with pojo class**/
        checkContactPojo = checkContactResponse.as(CheckContactPojo.class);
        softAssert.assertEquals(checkContactResponse.statusCode(), 400);
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseCode(), "0027");
        softAssert.assertEquals(checkContactPojo.getTransaction().getResponseDescription(),
                "No recipient found for '" + phoneNumbers.ARMSOFT_CLIENT_ID + "' phone number.");
        softAssert.assertAll();
    }
}
