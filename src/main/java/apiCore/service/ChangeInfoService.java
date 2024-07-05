package apiCore.service;

import apiCore.constants.ChangeInfoConstants;
import apiCore.constants.InstituteTypes;
import apiCore.constants.RegisterConstants;
import apiCore.constants.TransactionTypes;
import apiCore.helper.Utils;
import apiCore.pojoClasses.RegistrationPojo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChangeInfoService extends BaseService {

    ChangeInfoConstants changeInfoConstants = new ChangeInfoConstants();
    InstituteTypes instituteTypes = new InstituteTypes();
    RegisterConstants registerConstants = new RegisterConstants();
    TransactionTypes transactionTypes = new TransactionTypes();

    public Response changeInfoRequest(Object senderInstituteId, Object senderInstituteType,
                                      Object newClientId, Object clientType, Object clientName,
                                      Object senderCustomerId, Object senderCustomerIdType,
                                      Object transactionClientId, Object transactionClientName,
                                      Object transactionType, Object transactionSenderTime,
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
            transaction.put("newClientId", transactionClientId);
            transaction.put("newClientName", transactionClientName);
            transaction.put("type", transactionType);
            transaction.put("senderTime", transactionSenderTime);
            transaction.put("id", transactionId);

            // Add sender and transaction to the request
            request.put("sender", sender);
            request.put("transaction", transaction);

            // Convert to JSON string
            String jsonBody = objectMapper.writeValueAsString(request);
            return post(CHANGE_INFO_ENDPOINT, token, jsonBody);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response validChangeInfo(String clientId, RegistrationPojo registrationPojo) {

        return changeInfoRequest(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(), registrationPojo.getTransaction().getNewClientId(),
                changeInfoConstants.CLIENT_TYPE, registrationPojo.getTransaction().getNewClientName(),
                registrationPojo.getSender().getClient().getCustomerId(),
                registrationPojo.getSender().getClient().getCustomerIdType(),
                clientId, registerConstants.NEW_CLIENT_NAME,
                transactionTypes.CHANGE_INFO_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response changeInfoNoToken(String clientId) {

        return changeInfoRequest(instituteTypes.BYBLOS_BANK_ID,
                instituteTypes.BYBLOS_BANK, clientId,
                changeInfoConstants.CLIENT_TYPE, changeInfoConstants.NAME,
                registerConstants.CUSTOMER_ID, registerConstants.CUSTOMER_TYPE,
                clientId, registerConstants.NEW_CLIENT_NAME,
                transactionTypes.CHANGE_INFO_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, "");
    }

    public Response changeInfoSameClientId(RegistrationPojo registrationPojo) {

        return changeInfoRequest(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(), registrationPojo.getTransaction().getNewClientId(),
                changeInfoConstants.CLIENT_TYPE, registrationPojo.getTransaction().getNewClientName(),
                registrationPojo.getSender().getClient().getCustomerId(),
                registrationPojo.getSender().getClient().getCustomerIdType(),
                registrationPojo.getTransaction().getNewClientId(), registerConstants.NEW_CLIENT_NAME,
                transactionTypes.CHANGE_INFO_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response nonExistingClientId(RegistrationPojo registrationPojo) {

        return changeInfoRequest(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(), Utils.randomNumber(),
                changeInfoConstants.CLIENT_TYPE, registrationPojo.getTransaction().getNewClientName(),
                registrationPojo.getSender().getClient().getCustomerId(),
                registrationPojo.getSender().getClient().getCustomerIdType(),
                registrationPojo.getTransaction().getNewClientId(), registerConstants.NEW_CLIENT_NAME,
                transactionTypes.CHANGE_INFO_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response newClientIdIsExisting(String newClientId,RegistrationPojo registrationPojo) {

        return changeInfoRequest(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(), registrationPojo.getTransaction().getNewClientId(),
                changeInfoConstants.CLIENT_TYPE, registrationPojo.getTransaction().getNewClientName(),
                registrationPojo.getSender().getClient().getCustomerId(),
                registrationPojo.getSender().getClient().getCustomerIdType(),
                newClientId, registerConstants.NEW_CLIENT_NAME,
                transactionTypes.CHANGE_INFO_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response withoutClientId(RegistrationPojo registrationPojo) {

        return changeInfoRequest(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(), "",
                changeInfoConstants.CLIENT_TYPE, registrationPojo.getTransaction().getNewClientName(),
                registrationPojo.getSender().getClient().getCustomerId(),
                registrationPojo.getSender().getClient().getCustomerIdType(),
                registrationPojo.getTransaction().getNewClientId(), registerConstants.NEW_CLIENT_NAME,
                transactionTypes.CHANGE_INFO_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response withoutTransactionId(RegistrationPojo registrationPojo) {

        return changeInfoRequest(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(), registrationPojo.getTransaction().getNewClientId(),
                changeInfoConstants.CLIENT_TYPE, registrationPojo.getTransaction().getNewClientName(),
                registrationPojo.getSender().getClient().getCustomerId(),
                registrationPojo.getSender().getClient().getCustomerIdType(),
                registrationPojo.getTransaction().getNewClientId(), registerConstants.NEW_CLIENT_NAME,
                transactionTypes.CHANGE_INFO_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                "", registerConstants.BYBLOS_BANK_TOKEN);
    }
    public Response changeWithWrongCustomerId(RegistrationPojo registrationPojo) {

        return changeInfoRequest(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(), registrationPojo.getTransaction().getNewClientId(),
                changeInfoConstants.CLIENT_TYPE, registrationPojo.getTransaction().getNewClientName(),
                registerConstants.WRONG_CUSTOMER_ID,
                registrationPojo.getSender().getClient().getCustomerIdType(),
                registrationPojo.getTransaction().getNewClientId(), registerConstants.NEW_CLIENT_NAME,
                transactionTypes.CHANGE_INFO_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

    public Response changeWithWrongClientName(RegistrationPojo registrationPojo) {

        return changeInfoRequest(registrationPojo.getSender().getInstitute().getId(),
                registrationPojo.getSender().getInstitute().getType(), registrationPojo.getTransaction().getNewClientId(),
                changeInfoConstants.CLIENT_TYPE, 105,
                registrationPojo.getSender().getClient().getCustomerId(),
                registrationPojo.getSender().getClient().getCustomerIdType(),
                registrationPojo.getTransaction().getNewClientId(), registerConstants.NEW_CLIENT_NAME,
                transactionTypes.CHANGE_INFO_TRANSACTION_TYPE, registerConstants.SENDER_TIME,
                registerConstants.TRANSACTION_ID, registerConstants.BYBLOS_BANK_TOKEN);
    }

}
