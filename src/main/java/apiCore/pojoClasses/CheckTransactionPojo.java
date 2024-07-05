package apiCore.pojoClasses;

import lombok.Getter;

/**
 * checkTransaction response extracted by this class and initialize class variables
 **/

@Getter
public class CheckTransactionPojo {

    private Sender sender;
    private Receiver receiver;
    private Transaction transaction;

    @Getter
    public static class Sender {
        private Institute institute;
    }

    @Getter
    public static class Institute {
        private String id;
        private String type;
    }

    @Getter
    public static class Receiver {
        private Institute institute;
        private Client client;
        private Account account;
    }

    @Getter
    public static class Client {
        private String id;
        private String name;
        private String customerId;
        private String customerIdType;
    }

    @Getter
    public static class Account {
        private String id;
        private String type;
    }

    @Getter
    public static class Transaction {
        private String dateTime;
        private String relativeNumber;
        private int amount;
        private String currency;
        private String purpose;
        private String type;
        private String senderTime;
        private String id;
        private String responseCode;
        private String responseDescription;
        private String settlementDateNum;
    }
}
