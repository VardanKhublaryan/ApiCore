package apiCore.service;

import apiCore.constants.ChangeInfoConstants;
import apiCore.constants.InstituteTypes;
import apiCore.constants.RegisterConstants;
import apiCore.constants.TransactionTypes;
import apiCore.helper.CustomListeners;
import apiCore.pojoClasses.RegistrationPojo;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

public class DeactivationService extends BaseService {
    private final  ChangeInfoConstants changeInfoConstants = new ChangeInfoConstants();
    private final InstituteTypes instituteTypes = new InstituteTypes();
    private final RegisterConstants registerConstants = new RegisterConstants();
    private final TransactionTypes transactionTypes = new TransactionTypes();

    public Response deactivationRequest(Object senderInstituteId, Object senderInstituteType, Object newClientId,
                                        Object clientType, Object clientName, Object senderCustomerId,
                                        Object senderCustomerIdType, Object transactionType, Object transactionSenderTime,
                                        Object transactionId, String token) {
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
            client.put("id", newClientId);
            client.put("type", clientType);
            client.put("name", clientName);
            client.put("customerId", senderCustomerId);
            client.put("customerIdType", senderCustomerIdType);
            sender.put("client", client);

            // Construct the transaction object
            Map<String, Object> transaction = new LinkedHashMap<>();
            transaction.put("type", transactionType);
            transaction.put("senderTime", transactionSenderTime);
            transaction.put("id", transactionId);

            // Add sender and transaction to the request
            request.put("sender", sender);
            request.put("transaction", transaction);

            // Convert to JSON string
            String jsonBody = objectMapper.writeValueAsString(request);
            return post(DEACTIVATION_ENDPOINT, token, jsonBody);

        } catch (Exception e) {
            CustomListeners.printInfo(e);
            return null;
        }
    }

    public Response validDeactivation(String clientId, RegistrationPojo registrationPojo) {

        return deactivationRequest(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(),
                clientId, changeInfoConstants.CLIENT_TYPE, registrationPojo.getTransaction().getNewClientName(),
                registrationPojo.getSender().getClient().getCustomerId(),
                registrationPojo.getSender().getClient().getCustomerIdType(),
                transactionTypes.DEACTIVATION_TRANSACTION_TYPE, registrationPojo.getTransaction().getSenderTime(),
                registrationPojo.getTransaction().getId(), registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response deactivationWithWrongToken(String clientId) {
        return deactivationRequest(
                instituteTypes.ACBA_BANK_ID, instituteTypes.ACBA,
                clientId, changeInfoConstants.CLIENT_TYPE,
                changeInfoConstants.NAME, registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                transactionTypes.DEACTIVATION_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response withOtherCustomerId(String clientId) {
        return deactivationRequest(
                instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                clientId, changeInfoConstants.CLIENT_TYPE,
                changeInfoConstants.NAME, registerConstants.WRONG_CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                transactionTypes.DEACTIVATION_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }
}
