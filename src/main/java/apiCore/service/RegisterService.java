package apiCore.service;

import apiCore.constants.InstituteTypes;
import apiCore.constants.RegisterConstants;
import apiCore.constants.TransactionTypes;
import apiCore.helper.CustomListeners;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;


public class RegisterService extends BaseService {
    private final InstituteTypes instituteType = new InstituteTypes();
    private final RegisterConstants registerConstants = new RegisterConstants();
    private final TransactionTypes transactionTypes = new TransactionTypes();


    public Response signUp(Object senderInstituteId, Object senderInstituteType,
                           Object senderCustomerId, Object senderCustomerIdType,
                           Object newClientId, Object newClientName,
                           Object transactionType, Object transactionSenderTime,
                           Object transactionId, Object otp, String token) {
        try {
            // Create ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            // Construct the JSON structure
            Map<String, Object> request = new LinkedHashMap<>();

            // Construct the sender object
            Map<String, Object> sender = new LinkedHashMap<>();
            Map<String, Object> institute = new LinkedHashMap<>();
            institute.put("id", senderInstituteId);
            institute.put("type", senderInstituteType);
            sender.put("institute", institute);
            Map<String, Object> client = new LinkedHashMap<>();
            client.put("customerId", senderCustomerId);
            client.put("customerIdType", senderCustomerIdType);
            sender.put("client", client);

            // Construct the transaction object
            Map<String, Object> transaction;
            if (otp == null) {
                transaction = new LinkedHashMap<>();
                transaction.put("newClientId", newClientId);
                transaction.put("newClientName", newClientName);
                transaction.put("type", transactionType);
                transaction.put("senderTime", transactionSenderTime);
                transaction.put("id", transactionId);
            } else {
                transaction = new LinkedHashMap<>();
                transaction.put("newClientId", newClientId);
                transaction.put("newClientName", newClientName);
                transaction.put("type", transactionType);
                transaction.put("senderTime", transactionSenderTime);
                transaction.put("otp", otp);
                transaction.put("id", transactionId);
            }

            // Add sender and transaction to the request
            request.put("sender", sender);
            request.put("transaction", transaction);

            // Convert to JSON string
            String jsonBody = objectMapper.writeValueAsString(request);
            return post(REGISTRATION_ENDPOINT, token, jsonBody);

        } catch (Exception e) {
            CustomListeners.printInfo(e);
            return null;
        }
    }

    public Response byblosBankSignUp(String clientId) {

        return signUp(instituteType.BYBLOS_BANK_ID, instituteType.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                clientId, registerConstants.NEW_CLIENT_NAME,
                transactionTypes.REGISTER_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, null, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response testBankSignUp(String clientId) {

        return signUp(instituteType.TEST_BANK_ID, instituteType.TEST_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                clientId, registerConstants.NEW_CLIENT_NAME,
                transactionTypes.REGISTER_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, null, registerConstants.TEST_BANK_TOKEN);
    }

    public Response byblosBankSignUpWithOtp(String clientId, String otp) {

        return signUp(instituteType.BYBLOS_BANK_ID, instituteType.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                clientId, registerConstants.NEW_CLIENT_NAME,
                transactionTypes.NB_OTP_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, otp, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response signUpEmptyInstitute(String clientId) {

        return signUp("", "",
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                clientId, registerConstants.NEW_CLIENT_NAME,
                transactionTypes.NB_OTP_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, null, registerConstants.BYBLOS_BANK_TOKEN);
    }

    // sign up with incorrect transaction id
    public Response signUpOtpWithIncorrectId(String clientId, String otp) {

        return signUp(instituteType.BYBLOS_BANK_ID, instituteType.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                clientId, registerConstants.NEW_CLIENT_NAME,
                transactionTypes.NB_OTP_TYPE, registerConstants.SENDER_TIME,
                "005270", otp, registerConstants.BYBLOS_BANK_TOKEN);
    }
}
