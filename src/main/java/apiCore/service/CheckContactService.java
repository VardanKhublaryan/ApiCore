package apiCore.service;

import apiCore.constants.AuthorizationConstants;
import apiCore.constants.InstituteTypes;
import apiCore.constants.RegisterConstants;
import apiCore.constants.TransactionTypes;
import apiCore.helper.CustomListeners;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

public class CheckContactService extends BaseService {
    private final RegisterConstants registerConstants = new RegisterConstants();
    private final InstituteTypes instituteTypes = new InstituteTypes();
    private final TransactionTypes transactionTypes = new TransactionTypes();
    private final AuthorizationConstants authorizationConstants = new AuthorizationConstants();

    public Response checkContactRequest(Object senderInstituteId, Object senderInstituteType,
                                        Object senderCustomerId, Object senderCustomerIdType,
                                        Object senderDeviceType, Object receiverInstituteId,
                                        Object receiverInstituteType, Object receiverClientId,
                                        Object transactionType, Object transactionSenderTime,
                                        Object transactionId, String token) {
        try {
            // Create ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Construct the JSON structure
            Map<String, Object> request = new LinkedHashMap<>();

            // Construct the sender object
            Map<String, Object> sender = new LinkedHashMap<>();
            Map<String, Object> senderInstitute = new LinkedHashMap<>();
            senderInstitute.put("id", senderInstituteId);
            senderInstitute.put("type", senderInstituteType);
            sender.put("institute", senderInstitute);
            Map<String, Object> senderClient = new LinkedHashMap<>();
            senderClient.put("customerId", senderCustomerId);
            senderClient.put("customerIdType", senderCustomerIdType);
            sender.put("client", senderClient);
            Map<String, Object> senderDevice = new LinkedHashMap<>();
            senderDevice.put("type", senderDeviceType);
            sender.put("device", senderDevice);

            // Construct the receiver object
            Map<String, Object> receiver = new LinkedHashMap<>();
            Map<String, Object> receiverInstitute = new LinkedHashMap<>();
            if (receiverInstituteId != null && receiverInstituteType != null) {
                receiverInstitute.put("id", receiverInstituteId);
                receiverInstitute.put("type", receiverInstituteType);
                receiver.put("institute", receiverInstitute);
            }
            Map<String, Object> receiverClient = new LinkedHashMap<>();
            receiverClient.put("id", receiverClientId);
            receiver.put("client", receiverClient);

            // Construct the transaction object
            Map<String, Object> transaction = new LinkedHashMap<>();
            transaction.put("type", transactionType);
            transaction.put("senderTime", transactionSenderTime);
            transaction.put("id", transactionId);

            // Add sender, receiver, and transaction to the request
            request.put("sender", sender);
            request.put("receiver", receiver);
            request.put("transaction", transaction);

            // Convert to JSON string
            String jsonBody = objectMapper.writeValueAsString(request);
            return post(CHECK_CONTACT_ENDPOINT, token, jsonBody);

        } catch (Exception e) {
            CustomListeners.printInfo(e);
            return null;
        }
    }

    public Response checkArmSoftContact(String clientId) {
        /* extract response with pojo class**/
        return checkContactRequest(
                instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                authorizationConstants.MOBILE_DEVICE, instituteTypes.ARMSOFT2_ID,
                instituteTypes.ARMSOFT2, clientId,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response checkTestBankContact(String clientId) {
        /* extract response with pojo class**/
        return checkContactRequest(
                instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                authorizationConstants.MOBILE_DEVICE, instituteTypes.TEST_BANK_ID,
                instituteTypes.TEST_BANK, clientId,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response checkACBAContact(String clientId) {
        /* extract response with pojo class**/
        return checkContactRequest(
                instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                authorizationConstants.MOBILE_DEVICE, instituteTypes.ACBA_BANK_ID,
                instituteTypes.ACBA, clientId,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response checkWithoutICustomerId(String clientId) {
        /* extract response with pojo class**/
        return checkContactRequest(
                instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                "", registerConstants.CUSTOMER_TYPE,
                authorizationConstants.MOBILE_DEVICE, instituteTypes.ARMSOFT2_ID,
                instituteTypes.ARMSOFT2, clientId,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response checkWithoutToken(String clientId) {
        /* extract response with pojo class**/
        return checkContactRequest(
                instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                authorizationConstants.MOBILE_DEVICE, instituteTypes.ARMSOFT2_ID,
                instituteTypes.ARMSOFT2, clientId,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, "");
    }

    public Response checkVTBContact(String clientId) {
        /* extract response with pojo class**/
        return checkContactRequest(
                instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                authorizationConstants.MOBILE_DEVICE, "20",
                "VTB Bank Armenia", clientId,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response invalidCheckContact(String clientId) {
        /* extract response with pojo class**/
        return checkContactRequest(
                instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                authorizationConstants.MOBILE_DEVICE, instituteTypes.ACBA_BANK_ID,
                instituteTypes.ACBA, clientId,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                "", registerConstants.BYBLOS_BANK_TOKEN);
    }

    // When receiver.institute is == null
    public Response checkContact(String clientId) {
        /* extract response with pojo class**/
        return checkContactRequest(
                instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                authorizationConstants.MOBILE_DEVICE, null,
                null, clientId,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }
}
