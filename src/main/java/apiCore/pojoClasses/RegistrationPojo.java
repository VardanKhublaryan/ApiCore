package apiCore.pojoClasses;

import lombok.Getter;

/**
 * registration response extracted by this class and initialize class variables
 **/

@Getter
public class RegistrationPojo {
    private Sender sender;
    private Transaction transaction;

    @Getter
    public static class Sender {
        private Institute institute;
        private Client client;
    }

    @Getter
    public static class Institute {
        private String id;
        private String type;
    }

    @Getter
    public static class Client {
        private String customerId;
        private String customerIdType;
    }

    @Getter
    public static class Transaction {
        private String type;
        private String senderTime;
        private String id;
        private String newClientId;
        private String newClientName;
        private String responseCode;
        private String responseDescription;
        private String lang;
        private String otp;
    }
}
