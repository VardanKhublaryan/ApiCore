package apiCore.pojoClasses;


import lombok.Getter;

/**
 * deactivation response extracted by this class and initialize class variables
 **/
@Getter
public class DeactivationPojo {
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
        private String id;
        private String type;
        private String name;
        private String customerId;
        private String customerIdType;
    }

    @Getter
    public static class Transaction {
        private String type;
        private String senderTime;
        private String id;
        private String responseCode;
        private String responseDescription;
    }
}
