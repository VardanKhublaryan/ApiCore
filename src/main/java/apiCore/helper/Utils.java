package apiCore.helper;

import java.util.Random;
import java.util.UUID;

public class Utils {
    public static String uniqueRelativeNumber() {
        UUID uuid = UUID.randomUUID();
        String randomString = uuid.toString();
        // Remove hyphens and prepend the prefix
        randomString = randomString.replace("-", "");
        // Insert hyphens at desired positions
        return randomString.substring(0, 8) + "-" +
                randomString.substring(8, 12) + "-" +
                randomString.substring(12, 16) + "-" +
                randomString.substring(16, 20) + "-" +
                randomString.substring(20);
    }

    public static String randomNumber() {
        Random random = new Random();
        StringBuilder phoneNumber = new StringBuilder();
        for (int i = 0; i <= 8; i++) {
            phoneNumber.append(random.nextInt(0, 9));
        }
        return "374".concat(String.valueOf(phoneNumber));
    }

    public static String randomTransactionId() {
        Random random = new Random();
        StringBuilder transactionId = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            transactionId.append(random.nextInt(0, 9));
        }
        return transactionId.toString();
    }
}
