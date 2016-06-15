package com.intuit.test.planetary.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intuit.test.planetary.config.Config;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class AppLib.
 */
public final class AppLib {

    /**
     * Config instance.
     */
    private static Config config = Config.getInstance();

    /**
     * The proxy configuration checker.
     */
    private static boolean proxyConfigCheck = false;

    /**
     * The proxy host to be used in the test.
     */
    private static String proxyHost = null;

    /**
     * The proxy port to be used in the test.
     */
    private static String proxyPort = null;

    /**
     * Adds the http code and message to response.
     *
     * @param responseElement
     *            the response element
     * @param conn
     *            the conn
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void addHttpCodeAndMessageToResponse(JsonElement responseElement, HttpURLConnection conn)
            throws IOException {
        if (!responseElement.isJsonNull() && !responseElement.isJsonPrimitive()) {
            if (responseElement.isJsonArray()) {
                JsonObject httpStatusJsonObj = new JsonObject();
                httpStatusJsonObj.addProperty(API.RESPONSE_HTTP_STATUS_CODE, conn.getResponseCode());
                httpStatusJsonObj.addProperty(API.RESPONSE_HTTP_STATUS_MESSAGE, conn.getResponseMessage());
                responseElement.getAsJsonArray().add(httpStatusJsonObj);
            } else if (responseElement.isJsonObject()) {
                responseElement.getAsJsonObject().addProperty(API.RESPONSE_HTTP_STATUS_CODE, conn.getResponseCode());
                responseElement.getAsJsonObject().addProperty(API.RESPONSE_HTTP_STATUS_MESSAGE,
                        conn.getResponseMessage());
            } else {
                LogUtils.logSevereMessageThenFail(
                        "getJsonResponseFromConnObj() :: The response was not in the form of a JsonObject.  The Actual response is "
                                + responseElement);
            }
        } else {
            LogUtils.logSevereMessageThenFail(
                    "getJsonResponseFromConnObj() :: Got a non Json Response for this request (" + conn.getURL()
                            + ") The Actual response is " + responseElement);
        }
    }

    /**
     * Convert input stream to string.
     *
     * @param stream
     *            the stream
     * @return the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static String convertInputStreamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        return out.toString();
    }

    /**
     * Gets the API obj.
     *
     * @return the API obj
     */
    public static API getAPIObj() {

        // Initialize a new API object.
        API api = new API();

        // Check to see if the proxy exists. If it does, set the proxy.
        if (isProxySettingsConfigured()) {
            api.setProxy(getProxy());
        }

        // Return the final API object.
        return api;
    }

    /**
     * Gets the proxy.
     * 
     * @return the proxy
     */
    public static Proxy getProxy() {
        Proxy proxy = null;
        if (isProxySettingsConfigured()) {
            proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(config.getProxyHost(), Integer.parseInt(config.getProxyPort())));
        } else {
            LogUtils.logWarningMessage(
                    "getProxy() :: Could not established a proxy as the proxy settings were not configured.");
        }

        return proxy;
    }

    /**
     * Checks if is proxy settings configured.
     * 
     * @return true, if is proxy settings configured
     */
    public static boolean isProxySettingsConfigured() {

        // eliminate extra config checks as if the property doesn't exist many
        // reporting logs will be established.
        if (!proxyConfigCheck) {
            proxyHost = config.getProxyHost();
            proxyPort = config.getProxyPort();
            proxyConfigCheck = true;
        }
        return (proxyHost != null && !proxyHost.isEmpty()) && (proxyPort != null && !proxyPort.isEmpty());
    }

    /**
     * Checks if is valid json.
     *
     * @param json
     *            the json
     * @return true, if is valid json
     */
    public static boolean isValidJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonSyntaxException jse) {
            return false;
        }
    }

    /**
     * Open custom httpurl connection.
     *
     * @param sURL
     *            the s url
     * @param sRequestType
     *            the s request type
     * @param sContentType
     *            the s content type
     * @param sCharacterSet
     *            the s character set
     * @return the http url connection
     */
    public static HttpURLConnection openCustomHTTPURLConnection(String sURL, String sRequestType, String sContentType,
            String sCharacterSet) {
        HttpURLConnection conn = null;
        try {
            // Initialize a new URL instance based on the passed URL string.
            URL url = new URL(sURL);
            // Initialize a new HttpURLConnection object.
            if (isProxySettingsConfigured()) {
                // If a proxy is detected in the properties file, set the proxy.
                conn = (HttpURLConnection) url.openConnection(getProxy());
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }

            // Set the request properties.
            conn.setRequestProperty("Content-Type", sContentType + ";" + sCharacterSet);
            // Set the request method type.
            conn.setRequestMethod(sRequestType);
        } catch (MalformedURLException e) {
            LogUtils.logSevereMessageThenFail(LogUtils.convertStackTraceToString(e));
        } catch (ProtocolException e) {
            LogUtils.logSevereMessageThenFail(LogUtils.convertStackTraceToString(e));
        } catch (IOException e) {
            LogUtils.logSevereMessageThenFail(LogUtils.convertStackTraceToString(e));
        }
        // Return the final HttPURLConnection object.
        return conn;
    }

    /**
     * Rand aplhabetic.
     * 
     * @param size
     *            the size
     * @return the string
     */
    public static String randAlphabetic(int size) {
        return RandomStringUtils.randomAlphabetic(size);
    }

    /**
     * Rand aplha numeric.
     * 
     * @param size
     *            the size
     * @return the string
     */
    public static String randAlphaNumeric(int size) {
        return RandomStringUtils.randomAlphanumeric(size);
    }

    /**
     * Rand boolean.
     * 
     * @return true, if successful
     */
    public static boolean randBoolean() {

        boolean[] bool = { true, false };
        Random random = new Random(ThreadLocalRandom.current().nextLong());
        int index = random.nextInt(bool.length);
        return bool[index];

    }

    /**
     * Rand double.
     * 
     * @param min
     *            the min
     * @param max
     *            the max
     * @return the double
     */
    public static double randDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /**
     * Rand double byte.
     * 
     * @param size
     *            the size
     * @return the string
     * @throws UnsupportedEncodingException 
     */
    public static String randDoubleByte(int size) {

        String specialCharStr = "您ëԲաحباдзহ্যালোй好שלוםЗàהע";
        Random rnd = new Random(ThreadLocalRandom.current().nextLong());

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append(specialCharStr.charAt(rnd.nextInt(specialCharStr.length())));
        }
        
        String utf8 = null;
        //String sjis = null;
        try {
            utf8 = new String(sb.toString().getBytes("UTF8"));
          //sjis = new String(sb.toString().getBytes("SJIS"));
        } catch (UnsupportedEncodingException e) {
            LogUtils.logSevereMessageThenFail(
                    "randDoubleByte() :: UnsupportedEncodingException thrown => " + LogUtils.convertStackTraceToString(e));
        }
        
        
                
        //return sb.toString();
        return utf8;
       
    }

    /**
     * Rand hex string.
     * 
     * @param size
     *            the size
     * @return the string
     */
    public static String randHexString(int size) {
        Random r = new Random(ThreadLocalRandom.current().nextLong());
        StringBuffer sb = new StringBuffer();
        while (sb.length() < size) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, size);
    }

    /**
     * Rand int.
     * 
     * @param min
     *            the min
     * @param max
     *            the max
     * @return the int
     */
    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /**
     * Rand special char.
     * 
     * @param size
     *            the size
     * @return the string
     */
    public static String randSpecialChar(int size) {

        String specialCharStr = "~!@#$%^&*()`+=[]\\ {}|;:\'<>?,./";
        Random rnd = new Random(ThreadLocalRandom.current().nextLong());

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append(specialCharStr.charAt(rnd.nextInt(specialCharStr.length())));
        }

        return sb.toString();
    }

    /**
     * Request json validation helper.
     *
     * @param url
     *            the url
     * @param payload
     *            the payload
     * @param requestType
     *            the request type
     * @return the json element
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static JsonElement requestJsonValidationHelper(String url, JsonObject payload, String requestType)
            throws IOException {

        API api = getAPIObj();

        HttpURLConnection connection = openCustomHTTPURLConnection(url, requestType, api.REQUEST_CONTENTTYPE_JSON,
                API.CHARSET_UTF8);

        JsonElement response = null;

        if (requestType == api.REQUEST_GET) {
            connection.setRequestMethod(api.REQUEST_GET);
            response = api.getRestRequest(connection);
        } else if (requestType == api.REQUEST_POST) {
            connection.setRequestMethod(api.REQUEST_POST);
            // response = api.postRestRequest(connection, payload.toString());
        } else if (requestType == api.REQUEST_PUT) {
            connection.setRequestMethod(api.REQUEST_PUT);
            // response = api.putRestRequest(connection, payload.toString());
        } else if (requestType == api.REQUEST_DELETE) {
            connection.setRequestMethod(api.REQUEST_DELETE);
            // response = api.deleteRestRequest(connection);
        } else {
            LogUtils.logSevereMessageThenFail("requestValidationHelper() :: Invalid HTTP method > " + requestType);
        }

        addHttpCodeAndMessageToResponse(response, connection);

        return response;
    }

    /**
     * Validate http status code.
     * 
     * @param response
     *            the response
     * @param expectedStatusCode
     *            the expected status code
     */
    public static void validateHTTPStatusCode(JsonElement response, int expectedStatusCode) {
        if (!response.isJsonNull()) {
            if (response.isJsonObject()) {
                if (response.getAsJsonObject().has(API.RESPONSE_HTTP_STATUS_CODE)) {
                    String statusCode = response.getAsJsonObject().get(API.RESPONSE_HTTP_STATUS_CODE).toString()
                            .replace("\"", "");
                    if (!statusCode.equals(Integer.toString(expectedStatusCode))) {
                        LogUtils.logSevereMessageThenFail("validateHTTPStatusCode() :: ACTUAL HTTP status code '"
                                + statusCode + "' did not match EXPECTED status code '" + expectedStatusCode + "'");
                    }
                } else {
                    LogUtils.logSevereMessageThenFail("validateHTTPStatusCode() :: The property "
                            + API.RESPONSE_HTTP_STATUS_CODE + " did not exist in JsonObject.");
                }
            }
        } else {
            LogUtils.logSevereMessageThenFail("validateHTTPStatusCode() :: The response parameter was null.");
        }
    }

    /**
     * Validate response err msg.
     * 
     * @param response
     *            the response
     * @param expectedResponseErrMsg
     *            the expected response err msg
     */
    public static void validateResponseErrMsg(JsonElement response, String expectedResponseErrMsg) {

        if (!response.isJsonNull()) {
            if (response.isJsonObject()) {
                if (response.getAsJsonObject().has(API.RESPONSE_HTTP_ERROR_STREAM_MESSAGE)) {
                    String errorMsg = response.getAsJsonObject().get(API.RESPONSE_HTTP_ERROR_STREAM_MESSAGE).toString()
                            .replace("\"", "");
                    String extractedErrMsg = StringUtils.substringBetween(errorMsg, "<h1>", "</h1>");
                    if (!extractedErrMsg.equals(expectedResponseErrMsg)) {
                        LogUtils.logSevereMessageThenFail("validateResponseErrMsg() :: ACTUAL response error message '"
                                + extractedErrMsg + "' did not match EXPECTED response error message '"
                                + expectedResponseErrMsg + "'");
                    }
                } else {
                    LogUtils.logSevereMessageThenFail("validateResponseErrMsg() :: The property '"
                            + API.RESPONSE_HTTP_ERROR_STREAM_MESSAGE + "' did not exist in response object.");
                }
            }
        } else {
            LogUtils.logSevereMessageThenFail("validateResponseErrMsg() :: The response object was null.");
        }
    }

    /**
     * Constructor to prevent instantiation from other classes.
     */
    private AppLib() {
    }

}