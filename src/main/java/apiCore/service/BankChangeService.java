package apiCore.service;

import apiCore.constants.InstituteTypes;
import apiCore.constants.RegisterConstants;
import apiCore.constants.TransactionTypes;
import apiCore.pojoClasses.RegistrationPojo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

public class BankChangeService extends BaseService {
    InstituteTypes instituteTypes = new InstituteTypes();
    RegisterConstants registerConstants = new RegisterConstants();
    TransactionTypes transactionTypes = new TransactionTypes();

    public Response bankChangeReq(Object senderInstituteId, Object senderInstituteType,
                                  Object senderCustomerId, Object senderCustomerIdType,
                                  Object transactionType, Object transactionSenderTime,
                                  Object transactionId, Object transactionLang,
                                  Object newClientId, Object newClientName, String token) {
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
            Map<String, Object> transaction = new LinkedHashMap<>();
            transaction.put("type", transactionType);
            transaction.put("senderTime", transactionSenderTime);
            transaction.put("id", transactionId);
            transaction.put("lang", transactionLang);
            transaction.put("newClientId", newClientId);
            transaction.put("newClientName", newClientName);

            // Add sender and transaction to the request
            request.put("sender", sender);
            request.put("transaction", transaction);

            // Convert to JSON string
            String jsonBody = objectMapper.writeValueAsString(request);
            return post(REGISTRATION_ENDPOINT, token, jsonBody);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response validBankChangeReq(String clientId) {
        new RegisterService().testBankSignUp(clientId);
        RegistrationPojo registrationPojo = new RegisterService().byblosBankSignUp(clientId).as(RegistrationPojo.class);

        return bankChangeReq(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(),
                registrationPojo.getSender().getClient().getCustomerId(),
                registrationPojo.getSender().getClient().getCustomerIdType(),
                transactionTypes.NB_CNF_TYPE, registrationPojo.getTransaction().getSenderTime(),
                registrationPojo.getTransaction().getId(), "ARM",
                clientId, registerConstants.NEW_CLIENT_NAME, registerConstants.BYBLOS_BANK_TOKEN);
    }

    // bank change request with invalid transaction id
    public Response invalidTransactionId(String clientId) {
        new RegisterService().testBankSignUp(clientId);
        new RegisterService().byblosBankSignUp(clientId);

        return bankChangeReq(instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                transactionTypes.NB_CNF_TYPE, registerConstants.SENDER_TIME,
                "005210", "ARM",
                clientId, registerConstants.NEW_CLIENT_NAME, registerConstants.BYBLOS_BANK_TOKEN);
    }

    // bank change request without customer id
    public Response missingCustomerId(String clientId) {
        new RegisterService().testBankSignUp(clientId);
        new RegisterService().byblosBankSignUp(clientId);

        return bankChangeReq(instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                "", registerConstants.CUSTOMER_TYPE,
                transactionTypes.NB_CNF_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, "ARM",
                clientId, registerConstants.NEW_CLIENT_NAME, registerConstants.BYBLOS_BANK_TOKEN);
    }

    //Bank change request without checking whether is the user as client  in the changed bank or not
    public Response missingByblosVerification(String clientId) {
        new RegisterService().testBankSignUp(clientId);

        return bankChangeReq(instituteTypes.BYBLOS_BANK_ID, instituteTypes.BYBLOS_BANK,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                transactionTypes.NB_CNF_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, "ARM",
                clientId, registerConstants.NEW_CLIENT_NAME, registerConstants.BYBLOS_BANK_TOKEN);
    }
}
