package apiCore.pojoClasses;


import lombok.Getter;

/**
 * checkContact response extracted by this class and initialize class variables
 **/
@Getter
public class CheckContactPojo {
    private Sender sender;
    private Receiver receiver;
    private Transaction transaction;

    @Getter
    public static class Sender {

        private Institute institute;
        private Client client;
        private Device device;
    }

    @Getter
    public static class Institute {
        private String id;
        private String type;
    }

    @Getter
    public static class Client {
        private String id;
        private String name;
        private String customerId;
        private String customerIdType;
    }

    @Getter
    public static class Device {
        private String type;
    }

    @Getter
    public static class Receiver {
        private Institute institute;
        private Client client;
    }

    @Getter
    public static class Transaction {
        private String type;
        private String dateTime;
        private String senderTime;
        private String id;
        private String relativeNumber;
        private String responseCode;
        private String responseDescription;
    }
}
