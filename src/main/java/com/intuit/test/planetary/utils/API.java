package com.intuit.test.planetary.utils;

import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * The Class API.
 */
public class API {

    /** The Constant CHARSET_UTF8. */
    public static final String CHARSET_UTF8 = "charset=utf-8";
    
    /** The Constant LOGGER_API_RESPONSE_CODE_STRING. */
    private final static String LOGGER_API_RESPONSE_CODE_STRING = "API Response Code:  ";
    
    /** The Constant LOGGER_API_RESPONSE_MSG_STRING. */
    private final static String LOGGER_API_RESPONSE_MSG_STRING = "API Response Message:  ";
    
    /** The Constant LOGGER_PREFIX_STRING. */
    private final static String LOGGER_PREFIX_STRING = "API Request URL:  ";

    /** The Constant RESPONSE_HTTP_ERROR_STREAM_MESSAGE. */
    public final static String RESPONSE_HTTP_ERROR_STREAM_MESSAGE = "httpErrorStreamMsg";
    
    /** The Constant RESPONSE_HTTP_STATUS_CODE. */
    public final static String RESPONSE_HTTP_STATUS_CODE = "httpStatusCode";

    /** The Constant RESPONSE_HTTP_STATUS_MESSAGE. */
    public final static String RESPONSE_HTTP_STATUS_MESSAGE = "httpStatusMsg";
    
    /** The proxy. */
    private Proxy proxy = null;
    
    /** The request contenttype json. */
    public final String REQUEST_CONTENTTYPE_JSON = "application/json";

    /** The request delete. */
    public String REQUEST_DELETE = "DELETE";
    
    /** The request get. */
    public String REQUEST_GET = "GET";
    
    /** The request post. */
    public String REQUEST_POST = "POST";

    /** The request put. */
    public String REQUEST_PUT = "PUT";

    /**
     * Gets the HTTP response code.
     *
     * @param url the url
     * @return the HTTP response code
     * @throws Exception the exception
     */
    public int getHTTPResponseCode(String url) throws Exception {
        int code = 0;
        LogUtils.logInfoMessage(" ");
        LogUtils.logInfoMessage(LOGGER_PREFIX_STRING + url);
        HttpURLConnection conn = processRestRequest(url, REQUEST_GET);
        if (conn != null) {
            code = conn.getResponseCode();
            LogUtils.logInfoMessage("API - getHTTPResponseCode :: HTTP Response Code from URL(" + url + "): " + code);
        } else {
            LogUtils.logSevereMessageThenFail("API - getHTTPResponseCode :: (" + url + ") not a valid http request!");
        }
        return code;
    }

    /**
     * Gets the HTTP response code.
     *
     * @param url the url
     * @param requestType the request type
     * @return the HTTP response code
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public int getHTTPResponseCode(String url, String requestType) throws IOException {
        return getHTTPResponseCode(url, requestType, REQUEST_CONTENTTYPE_JSON);

    }

    /**
     * Gets the HTTP response code.
     *
     * @param url the url
     * @param requestType the request type
     * @param contentType the content type
     * @return the HTTP response code
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public int getHTTPResponseCode(String url, String requestType, String contentType) throws IOException {
        int code = 0;
        LogUtils.logInfoMessage(" ");
        LogUtils.logInfoMessage(LOGGER_PREFIX_STRING + url);
        HttpURLConnection conn = processRestRequest(url, requestType, contentType);
        if (conn != null) {
            code = conn.getResponseCode();
            LogUtils.logInfoMessage("API - getHTTPResponseCode :: HTTP Response Code from URL(" + url + "): " + code);
        } else {
            LogUtils.logSevereMessageThenFail("API - getHTTPResponseCode :: (" + url + ") not a valid http request!");
        }
        return code;
    }

    /**
     * Gets the proxy.
     *
     * @return the proxy
     */
    public Proxy getProxy() {
        return this.proxy;
    }

    /**
     * getResponseCodeList()
     * 
     * <p>
     * Returns a List of all acceptable HTTPResponseCode, i.e. 200, 302, ect.
     * 
     * @return {@link List<Integer>} - the list of acceptable response codes
     */
    public List<Integer> getResponseCodeList() {
        List<Integer> responseCodes = new ArrayList<Integer>();
        responseCodes.add(HttpURLConnection.HTTP_OK);
        responseCodes.add(HttpURLConnection.HTTP_MOVED_TEMP);
        responseCodes.add(HttpURLConnection.HTTP_MOVED_PERM);
        responseCodes.add(HttpURLConnection.HTTP_SEE_OTHER);
        responseCodes.add(HttpURLConnection.HTTP_CREATED);
        responseCodes.add(HttpURLConnection.HTTP_ACCEPTED);
        responseCodes.add(HttpURLConnection.HTTP_RESET);
        responseCodes.add(HttpURLConnection.HTTP_PARTIAL);
        responseCodes.add(HttpURLConnection.HTTP_SEE_OTHER);
        return responseCodes;
    }

    /**
     * Gets the rest request.
     *
     * @param connection the connection
     * @return the rest request
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonElement getRestRequest(HttpURLConnection connection) throws IOException {

        if (connection != null) {

            LogUtils.logInfoMessage(" ");
            LogUtils.logInfoMessage("API - getRestRequest :: Data will be submitted as <GET>\n\n: " + connection.getURL());

            InputStream stream;

            LogUtils.logInfoMessage(LOGGER_API_RESPONSE_CODE_STRING + connection.getResponseCode());
            LogUtils.logInfoMessage(LOGGER_API_RESPONSE_MSG_STRING + connection.getResponseMessage());

            // Check to see if the response code are a valid 200 or redirect
            // codes.
            if (getResponseCodeList().contains(connection.getResponseCode())) {
                // Get the input stream.
                stream = connection.getInputStream();
            } else {
                // Get the error stream for everything else.
                stream = connection.getErrorStream();

                if (stream != null) {
                    // Get Error Stream as String
                    String errorStreamStr = AppLib.convertInputStreamToString(stream);
                    // Prints the string content read from input stream
                    LogUtils.logInfoMessage("API - getRestRequest :: Response from Error Stream <GET>\n\n" + errorStreamStr);
                    // Return ErrorStream response as Json
                    if (AppLib.isValidJson(errorStreamStr)) {
                        return new JsonParser().parse(errorStreamStr).getAsJsonObject();
                    } else {
                        JsonObject jsonObj = new JsonObject();
                        jsonObj.addProperty(RESPONSE_HTTP_ERROR_STREAM_MESSAGE, errorStreamStr);
                        return jsonObj;
                    }
                } else {
                    JsonObject jsonObj = new JsonObject();
                    jsonObj.addProperty(RESPONSE_HTTP_STATUS_CODE, connection.getResponseCode());
                    jsonObj.addProperty(RESPONSE_HTTP_STATUS_MESSAGE, connection.getResponseMessage());
                    return jsonObj;
                }

            }

            JsonReader reader = new JsonReader(new InputStreamReader(stream));
            JsonParser parser = new JsonParser();
            JsonElement rootElement = parser.parse(reader);

            LogUtils.logInfoMessage("API - getRestRequest :: Response from Server after <GET>\n\n" + rootElement.toString());
            LogUtils.logInfoMessage(" ");

            return rootElement;
        } else {
            LogUtils.logSevereMessageThenFail("API - getRestRequest Method:: Cannot process request.  Connection Object is Null.");
        }

        return null;
    }

    /**
     * Gets the rest request.
     *
     * @param url the url
     * @return the rest request
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonElement getRestRequest(String url) throws IOException {
        return getRestRequest(url, REQUEST_CONTENTTYPE_JSON);
    }

    /**
     * Gets the rest request.
     *
     * @param url the url
     * @param contentType the content type
     * @return the rest request
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public JsonElement getRestRequest(String url, String contentType) throws IOException {

        if (!url.equals("")) {
            InputStream stream;

            HttpURLConnection conn = processRestRequest(url, REQUEST_GET, contentType);

            // Check to see if the response code are a valid 200 or redirect
            // codes.

            if (getResponseCodeList().contains(conn.getResponseCode())) {
                // Get the input stream.
                stream = conn.getInputStream();
            } else {
                // Get the error stream for everything else.
                stream = conn.getErrorStream();
                // Get Error Stream as String
                String errorStreamStr = AppLib.convertInputStreamToString(stream);
                // Prints the string content read from input stream
                LogUtils.logInfoMessage("API - getRestRequest :: Response from Error Stream <GET>\n\n" + errorStreamStr);
                // Return ErrorStream response as Json
                if (AppLib.isValidJson(errorStreamStr)) {
                    return new JsonParser().parse(errorStreamStr).getAsJsonObject();
                } else {
                    JsonObject jsonObj = new JsonObject();
                    jsonObj.addProperty(RESPONSE_HTTP_ERROR_STREAM_MESSAGE, errorStreamStr);
                    return jsonObj;
                }
            }

            JsonReader reader = new JsonReader(new InputStreamReader(stream));
            JsonParser parser = new JsonParser();
            JsonElement rootElement = parser.parse(reader);

            return rootElement;
        } else {
            LogUtils.logSevereMessageThenFail("API - getRestRequest Method:: Cannot process request.  URL Parameter is empty.");
        }

        return null;
    }

    /**
     * Process rest request.
     *
     * @param url the url
     * @param type the type
     * @return the http url connection
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private HttpURLConnection processRestRequest(String url, String type) throws IOException {
        return processRestRequest(url, type, REQUEST_CONTENTTYPE_JSON);
    }

    /**
     * Process rest request.
     *
     * @param url the url
     * @param type the type
     * @param contentType the content type
     * @return the http url connection
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private HttpURLConnection processRestRequest(String url, String type, String contentType) throws IOException {

        HttpURLConnection conn;

        if (!url.equals("")) {
            LogUtils.logInfoMessage(" ");
            LogUtils.logInfoMessage(LOGGER_PREFIX_STRING + url);

            URL urlParam = new URL(url);
            if (getProxy() != null) {
                conn = (HttpURLConnection) urlParam.openConnection(getProxy());
            } else {
                conn = (HttpURLConnection) urlParam.openConnection();
            }
            conn.setDoOutput(true);
            if (type.equals(REQUEST_GET)) {
                conn.setRequestMethod(REQUEST_GET);
            } else if (type.equals(REQUEST_POST)) {
                conn.setRequestMethod(REQUEST_POST);
            } else if (type.equals(REQUEST_DELETE)) {
                conn.setRequestMethod(REQUEST_DELETE);
            } else if (type.equals(REQUEST_PUT)) {
                conn.setRequestMethod(REQUEST_PUT);
            } else {
                LogUtils.logSevereMessageThenFail("API - processRestRequest :: HTTP Request Method is blank.");
            }

            conn.setRequestProperty("Content-Type", contentType);

            LogUtils.logInfoMessage(LOGGER_API_RESPONSE_CODE_STRING + conn.getResponseCode());
            LogUtils.logInfoMessage(LOGGER_API_RESPONSE_MSG_STRING + conn.getResponseMessage());

            return conn;
        } else {
            LogUtils.logSevereMessageThenFail("API - processRestRequest :: URL is blank");
        }

        return null;
    }

    /**
     * Sets the proxy.
     *
     * @param newProxy the new proxy
     */
    public void setProxy(Proxy newProxy) {
        this.proxy = newProxy;
    }

}
