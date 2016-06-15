package com.intuit.test.planetary.api.sounds;

import java.io.IOException;
import com.google.gson.JsonElement;
import com.intuit.test.planetary.config.Config;
import com.intuit.test.planetary.utils.API;
import com.intuit.test.planetary.utils.AppLib;

/**
 * The Class Sounds.
 */
public final class Sounds {

    /** The API instance. */
    private static API api = new API();

    /** The Config instance. */
    private static Config config = Config.getInstance();

    /** The Sounds URI. */
    public static final String SOUNDS_URI = "/planetary/sounds";

    /** The constant for 'api_key'. */
    public static final String PARAM_API_KEY = "api_key=";

    /** The constant for 'q='. */
    public static final String PARAM_Q = "q=";

    /** The constant for 'limit'. */
    public static final String PARAM_LIMIT = "limit=";

    /** Constructor to prevent instantiation from other classes. */
    private Sounds() {
    }

    /**
     * Gets the sounds.
     *
     * @param qryStrs
     *            the qry strs
     * @return the sounds
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static JsonElement getSounds(String qryStrs) throws IOException {

        String url = config.getApiBaseUrl() + SOUNDS_URI + "?" + qryStrs;

        return AppLib.requestJsonValidationHelper(url, null, api.REQUEST_GET);
    }

    /**
     * Gets the sounds.
     *
     * @param q
     *            the q
     * @param limit
     *            the limit
     * @return the sounds
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static JsonElement getSounds(String q, int limit) throws IOException {

        String url = config.getApiBaseUrl() + SOUNDS_URI + "?" + PARAM_API_KEY + config.getApiKey() + "&" + PARAM_Q + q
                + "&" + PARAM_LIMIT + limit;

        return AppLib.requestJsonValidationHelper(url, null, api.REQUEST_GET);
    }

    /**
     * Gets the sounds.
     *
     * @param apiKey
     *            the api key
     * @param q
     *            the q
     * @param limit
     *            the limit
     * @return the sounds
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static JsonElement getSounds(String apiKey, String q, int limit) throws IOException {

        String url = config.getApiBaseUrl() + SOUNDS_URI + "?" + PARAM_API_KEY + apiKey + "&" + PARAM_Q + q + "&"
                + PARAM_LIMIT + limit;

        return AppLib.requestJsonValidationHelper(url, null, api.REQUEST_GET);
    }

    /**
     * Gets the sounds.
     *
     * @param apiKey
     *            the api key
     * @param q
     *            the q
     * @param limit
     *            the limit
     * @param qryStrs
     *            the qry strs
     * @return the sounds
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static JsonElement getSounds(String apiKey, String q, int limit, String qryStrs) throws IOException {

        String url = config.getApiBaseUrl() + SOUNDS_URI + "?" + PARAM_API_KEY + apiKey + "&" + PARAM_Q + q + "&"
                + PARAM_LIMIT + limit;

        if (qryStrs != null && !qryStrs.isEmpty()) {
            url = url + "&" + qryStrs;
        }

        return AppLib.requestJsonValidationHelper(url, null, api.REQUEST_GET);
    }

}
