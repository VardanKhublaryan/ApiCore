package apiCore.service;

import apiCore.helper.CustomListeners;
import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class BaseService {

    protected static String BASE_URL = getPropValues("base_url");
    private static final String CACERTS_PATH = getPropValues("cacerts_path");
    private static final String KEYSTORE_PATH = getPropValues("keystore_path");
    private static final String KEYSTORE_PASS = getPropValues("keystore_pass");
    protected final String REGISTRATION_ENDPOINT = getPropValues("register_endpoint");
    protected final String CHANGE_INFO_ENDPOINT = getPropValues("change_info_endpoint");
    protected final String DEACTIVATION_ENDPOINT = getPropValues("deactivation_endpoint");
    protected final String CHECK_CONTACT_ENDPOINT = getPropValues("check_contact_endpoint");
    protected final String AUTHORISATION_ENDPOINT = getPropValues("authorisation_endpoint");
    protected final String CHECK_TRANSACTION_ENDPOINT = getPropValues("check_transaction_endpoint");

    // ThreadLocal to store the response
    protected static ThreadLocal<Response> responseThreadLocal = new ThreadLocal<>();

    public static Response getThreadLocalResponse() {
        return responseThreadLocal.get();
    }

    public static void removeThreadLocalResponse() {
        responseThreadLocal.remove();
    }

    private static RequestSpecification baseConfigRequest() {
        return RestAssured.given()
//                .filter(new RequestLoggingFilter())
//                .filter(new ResponseLoggingFilter())
                .contentType(ContentType.JSON)
                .baseUri(BASE_URL)
                .config(RestAssured.config().sslConfig(
                        new SSLConfig().allowAllHostnames()
                                .trustStore(CACERTS_PATH, KEYSTORE_PASS)
                                .keyStore(KEYSTORE_PATH, KEYSTORE_PASS)
                ));
    }

    protected Response post(String endPoint, String token, String body) {
        Response response = baseConfigRequest()
                .header("Authorization", token)
                .body(body)
                .post(endPoint);
        responseThreadLocal.set(response);
        return getThreadLocalResponse();
    }

    protected Response get(String endPoint, String token) {
        Response response = baseConfigRequest()
                .header("Authorization", token)
                .when()
                .get(endPoint);
        responseThreadLocal.set(response);
        return getThreadLocalResponse();
    }

    /**
     * For read data from config.properties file
     */
    private static Properties getPropValues() throws IOException {
        InputStream inputStream = null;
        Properties prop = new Properties();
        try {
            String propFileName = "config.properties";
            inputStream = BaseService.class.getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                prop = null;
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (Exception e) {
            CustomListeners.printInfo(e);
        } finally {
            assert inputStream != null;
            inputStream.close();
        }
        return prop;
    }

    /**
     * For read data from config.properties file
     */
    private static String getPropValues(String key) {

        if (System.getProperty(key) == null) {
            try {
                return getPropValues().getProperty(key);
            } catch (IOException e) {
                CustomListeners.printInfo(e);
            }
        }
        return System.getProperty(key);
    }
}
