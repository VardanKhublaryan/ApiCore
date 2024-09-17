package apiCore.service;

import apiCore.constants.AuthorizationConstants;
import apiCore.constants.InstituteTypes;
import apiCore.constants.RegisterConstants;
import apiCore.constants.TransactionTypes;
import apiCore.helper.CustomListeners;
import apiCore.helper.Utils;
import apiCore.pojoClasses.AuthorizationPojo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

public class CheckTransactionService extends BaseService {
    private final AuthorizationConstants authorizationConstants = new AuthorizationConstants();
    private final InstituteTypes instituteTypes = new InstituteTypes();
    private final RegisterConstants registerConstants = new RegisterConstants();
    private final TransactionTypes transactionTypes = new TransactionTypes();

    public Response checkTransactionRequest(Object senderInstituteId, Object senderInstituteType,
                                            Object transactionType, Object transactionSenderTime,
                                            Object transactionId, Object relativeNumber,
                                            Object transactionAmount, Object transactionCurrency, String token) {
        try {
            // Create ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            // Construct the JSON structure
            Map<String, Object> request = new LinkedHashMap<>();
            // Construct the sender object
            Map<String, Object> sender = new LinkedHashMap<>();
            Map<String, Object> institute = new LinkedHashMap<>();
            sender.put("institute", institute);
            institute.put("id", senderInstituteId);
            institute.put("type", senderInstituteType);

            // Construct the transaction object
            Map<String, Object> transaction = new LinkedHashMap<>();
            transaction.put("type", transactionType);
            transaction.put("senderTime", transactionSenderTime);
            transaction.put("id", transactionId);
            transaction.put("relativeNumber", relativeNumber);
            transaction.put("amount", transactionAmount);
            transaction.put("currency", transactionCurrency);

            // Add sender and transaction to the request
            request.put("sender", sender);
            request.put("transaction", transaction);

            // Convert to JSON string
            String jsonBody = objectMapper.writeValueAsString(request);
            return post(CHECK_TRANSACTION_ENDPOINT, token, jsonBody);

        } catch (Exception e) {
            CustomListeners.printInfo(e);
            return null;
        }
    }

    public Response validCheckTransaction(AuthorizationPojo authorizationPojo, String relativeNumber) {

        return checkTransactionRequest(authorizationPojo.getSender().getInstitute().getId(),
                authorizationPojo.getSender().getInstitute().getType(),
                transactionTypes.CHECK_CONTACT_TYPE, authorizationPojo.getTransaction().getSenderTime(),
                authorizationPojo.getTransaction().getId(), relativeNumber,
                authorizationPojo.getTransaction().getAmount(), authorizationPojo.getTransaction().getCurrency(),
                registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response noFoundTransaction(String relativeNumber) {

        return checkTransactionRequest(instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, relativeNumber,
                authorizationConstants.AMOUNT, authorizationConstants.CURRENCY,
                registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response checkTransactionWithoutBank(String relativeNumber) {

        return checkTransactionRequest("", "",
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, relativeNumber,
                authorizationConstants.AMOUNT, authorizationConstants.CURRENCY,
                registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response checkTransactionWithoutId(String relativeNumber) {

        return checkTransactionRequest(instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                "", relativeNumber,
                authorizationConstants.AMOUNT, authorizationConstants.CURRENCY,
                registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response checkWithWrongId(String relativeNumber) {

        return checkTransactionRequest(instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                transactionTypes.CHECK_CONTACT_TYPE, registerConstants.SENDER_TIME,
                Utils.randomTransactionId(), relativeNumber,
                authorizationConstants.AMOUNT, authorizationConstants.CURRENCY,
                registerConstants.BYBLOS_BANK_TOKEN);
    }
}
