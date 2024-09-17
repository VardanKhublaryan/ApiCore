package apiCore.service;

import apiCore.constants.AuthorizationConstants;
import apiCore.constants.InstituteTypes;
import apiCore.constants.RegisterConstants;
import apiCore.constants.TransactionTypes;
import apiCore.helper.CustomListeners;
import apiCore.helper.Utils;
import apiCore.pojoClasses.CheckContactPojo;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.DATE_TIME;


public class AuthorizationService extends BaseService {
    private final AuthorizationConstants authorizationConstants = new AuthorizationConstants();
    private final RegisterConstants registerConstants = new RegisterConstants();
    private final InstituteTypes instituteTypes = new InstituteTypes();


    public Response authorisationRequest(Object senderInstituteId, Object senderInstituteType, Object senderCustomerId,
                                         Object senderCustomerIdType, Object accountType, Object senderDeviceType,
                                         Object receiverInstituteId, Object receiverInstituteType, Object receiverClientId,
                                         Object receiverClientName, Object receiverCustomerCustomerId, Object receiverCustomerCustomerIdType,
                                         Object transactionType, Object transactionDateTime, Object transactionSenderTime,
                                         Object transactionId, Object transactionRelativeNumber, Object transactionAmount,
                                         Object transactionCurrency, String token) {
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
            Map<String, Object> senderAccount = new LinkedHashMap<>();
            senderAccount.put("id", "");
            senderAccount.put("type", accountType);
            sender.put("account", senderAccount);
            Map<String, Object> senderDevice = new LinkedHashMap<>();
            senderDevice.put("type", senderDeviceType);
            sender.put("device", senderDevice);

            // Construct the receiver object
            Map<String, Object> receiver = new LinkedHashMap<>();
            Map<String, Object> receiverInstitute = new LinkedHashMap<>();
            receiverInstitute.put("id", receiverInstituteId);
            receiverInstitute.put("type", receiverInstituteType);
            receiver.put("institute", receiverInstitute);
            Map<String, Object> receiverClient = new LinkedHashMap<>();
            receiverClient.put("id", receiverClientId);
            receiverClient.put("name", receiverClientName);
            receiverClient.put("customerId", receiverCustomerCustomerId);
            receiverClient.put("customerIdType", receiverCustomerCustomerIdType);
            receiver.put("client", receiverClient);
            Map<String, Object> receiverAccount = new LinkedHashMap<>();
            receiverAccount.put("id", "");
            receiverAccount.put("type", "");
            receiver.put("account", receiverAccount);

            // Construct the transaction object
            Map<String, Object> transaction = new LinkedHashMap<>();
            transaction.put("type", transactionType);
            transaction.put("dateTime", transactionDateTime);
            transaction.put("senderTime", transactionSenderTime);
            transaction.put("id", transactionId);
            transaction.put("relativeNumber", transactionRelativeNumber);
            transaction.put("amount", transactionAmount);
            transaction.put("currency", transactionCurrency);
            transaction.put("purpose", "transactionPurpose");

            // Add sender, receiver, and transaction to the request
            request.put("sender", sender);
            request.put("receiver", receiver);
            request.put("transaction", transaction);

            // Convert to JSON string
            String jsonBody = objectMapper.writeValueAsString(request);

            return post(AUTHORISATION_ENDPOINT, token, jsonBody);

        } catch (Exception e) {
            CustomListeners.printInfo(e);
            return null;
        }
    }

    public Response validAuthorisation(int amount, String relativeNumber, CheckContactPojo response) {

        return authorisationRequest(response.getSender().getInstitute().getId(),
                response.getSender().getInstitute().getType(),
                response.getSender().getClient().getCustomerId(),
                response.getSender().getClient().getCustomerIdType(),
                authorizationConstants.BANK_ACCOUNT_TYPE, response.getSender().getDevice().getType(),
                response.getReceiver().getInstitute().getId(),
                response.getReceiver().getInstitute().getType(),
                response.getReceiver().getClient().getId(),
                response.getReceiver().getClient().getName(),
                response.getReceiver().getClient().getCustomerId(),
                response.getReceiver().getClient().getCustomerIdType(),
                response.getTransaction().getType(),
                response.getTransaction().getDateTime(),
                response.getTransaction().getSenderTime(),
                response.getTransaction().getId(),
                relativeNumber, amount, authorizationConstants.CURRENCY, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response withoutCheckContact(String relativeNumber, String clientId) {

        return authorisationRequest(instituteTypes.TEST_BANK_ID,
                instituteTypes.TEST_BANK, registerConstants.CUSTOMER_ID,
                registerConstants.CUSTOMER_TYPE, authorizationConstants.BANK_ACCOUNT_TYPE,
                authorizationConstants.MOBILE_DEVICE, instituteTypes.ARMSOFT2_ID,
                instituteTypes.ARMSOFT2, clientId,
                registerConstants.NEW_CLIENT_NAME, registerConstants.CUSTOMER_ID,
                registerConstants.CUSTOMER_TYPE, new TransactionTypes().CHECK_CONTACT_TYPE,
                DATE_TIME, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, relativeNumber,
                authorizationConstants.AMOUNT,
                authorizationConstants.CURRENCY,
                registerConstants.TEST_BANK_TOKEN);
    }

    public Response withWrongToken(String clientId) {

        return authorisationRequest(instituteTypes.TEST_BANK_ID,
                instituteTypes.TEST_BANK, registerConstants.CUSTOMER_ID,
                registerConstants.CUSTOMER_TYPE, authorizationConstants.BANK_ACCOUNT_TYPE,
                authorizationConstants.MOBILE_DEVICE, instituteTypes.ARMSOFT2_ID,
                instituteTypes.ARMSOFT2, clientId,
                registerConstants.NEW_CLIENT_NAME, registerConstants.CUSTOMER_ID,
                registerConstants.CUSTOMER_TYPE, new TransactionTypes().CHECK_CONTACT_TYPE,
                DATE_TIME, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, Utils.uniqueRelativeNumber(),
                authorizationConstants.AMOUNT,
                authorizationConstants.CURRENCY,
                "");
    }
}
