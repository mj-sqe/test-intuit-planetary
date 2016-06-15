package com.intuit.test.planetary.tests.sounds.get;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.intuit.test.planetary.api.sounds.Sounds;
import com.intuit.test.planetary.api.sounds.constants.SoundsConstants;
import com.intuit.test.planetary.api.sounds.error.msgs.SoundsErrorMsgs;
import com.intuit.test.planetary.constants.PlanetaryConstants;
import com.intuit.test.planetary.utils.API;
import com.intuit.test.planetary.utils.AppLib;
import com.intuit.test.planetary.utils.BaseTestApp;
import com.intuit.test.planetary.utils.JsonSchemaValidator;
import com.intuit.test.planetary.utils.LogUtils;

/**
 * The Class Verify Sounds Get.
 */
@Test(groups = { "Regression" })
public class VerifySoundsGetTest extends BaseTestApp {

    @Test(enabled = true)
    public void verifyDefault() throws IOException {

        // send request
        JsonObject response = Sounds.getSounds(Sounds.PARAM_API_KEY + config.getApiKey()).getAsJsonObject();

        // validate HTTP status code
        AppLib.validateHTTPStatusCode(response, HttpURLConnection.HTTP_OK);

        // no application response message/code but normally would validate here

        // validate count is default value
        Assert.assertEquals(response.get(SoundsConstants.SOUNDS_RESPONSE_PROPERTY_COUNT).getAsInt(),
                SoundsConstants.DEFAULT_SOUNDS_LIMIT,
                "verifyDefault() :: count did not match when it was supposed to.");

        // validate results size is default value
        Assert.assertEquals(response.get(SoundsConstants.SOUNDS_RESPONSE_PROPERTY_RESULTS).getAsJsonArray().size(),
                SoundsConstants.DEFAULT_SOUNDS_LIMIT,
                "verifyDefault() :: result size did not match when it was supposed to.");

    }

    @Test(enabled = true)
    public void verifySchema() throws IOException {

        // send request
        JsonObject response = Sounds.getSounds(Sounds.PARAM_API_KEY + config.getApiKey()).getAsJsonObject();

        // validate HTTP status code
        AppLib.validateHTTPStatusCode(response, HttpURLConnection.HTTP_OK);

        // no application response message/code but normally would validate here

        // lets remove test added HTTP properties from response object
        response.remove(API.RESPONSE_HTTP_STATUS_CODE);
        response.remove(API.RESPONSE_HTTP_STATUS_MESSAGE);

        // validate schema
        JsonSchemaValidator.jsonSchemaValidator(response,
                PlanetaryConstants.SCHEMA_FILE_DIR + File.separator + SoundsConstants.SOUNDS_SCHEMA_FILE_NAME);

    }

    @Test(enabled = true, dataProvider = "NegativePathApiKeyScenarios")
    public void verifyNegativePathWithAPIKeyParam(String description, String apiKey) throws IOException {

        // test description
        LogUtils.logInfoMessage(description);

        // send request
        JsonObject response = Sounds.getSounds(Sounds.PARAM_API_KEY + apiKey).getAsJsonObject();

        // validate HTTP status code
        AppLib.validateHTTPStatusCode(response, HttpURLConnection.HTTP_FORBIDDEN);

        // validate Application error message
        AppLib.validateResponseErrMsg(response, SoundsErrorMsgs.API_KEY_INVALID_ERROR);

    }

    @Test(enabled = true)
    public void verifyUnsupportedQryStrs() throws IOException {

        // send request
        JsonObject response = Sounds
                .getSounds(Sounds.PARAM_API_KEY + config.getApiKey() + "&" + "unsupported=" + AppLib.randAlphabetic(10))
                .getAsJsonObject();

        // validate HTTP status code
        AppLib.validateHTTPStatusCode(response, HttpURLConnection.HTTP_OK);

    }

    @Test(enabled = true, dataProvider = "NegativePathQScenarios")
    public void verifyNegativePathWithQParam(String description, String q, int limit) throws IOException {

        // test description
        LogUtils.logInfoMessage(description);

        // send request
        JsonObject response = Sounds.getSounds(q, limit).getAsJsonObject();

        // validate HTTP status code NOTE: Returning 200 for non-string values
        // which it shouldn't. This should be a defect
        AppLib.validateHTTPStatusCode(response, HttpURLConnection.HTTP_BAD_REQUEST);

        // validate Application error message
        // AppLib.validateResponseErrMsg(response, SoundsErrorMsgs.Q_INVALID_ERROR);

    }

    @Test(enabled = true, dataProvider = "QSearchFilterByIdScenarios")
    public void verifyQSearchFilterById(String description, String q, String limit) throws IOException {

        // test description
        LogUtils.logInfoMessage(description);

        // send request
        JsonObject response = Sounds.getSounds(
                Sounds.PARAM_API_KEY + config.getApiKey() + "&" + Sounds.PARAM_LIMIT + limit + "&" + Sounds.PARAM_Q + q)
                .getAsJsonObject();

        // validate HTTP status code
        AppLib.validateHTTPStatusCode(response, HttpURLConnection.HTTP_OK);

        // validate search filter worked with id
        Assert.assertEquals(response.get(SoundsConstants.SOUNDS_RESPONSE_PROPERTY_RESULTS).getAsJsonArray().get(0)
                .getAsJsonObject().get("id").getAsString(), q, "Search filter did not work as it was supposed to.");

    }

    @Test(enabled = true, dataProvider = "NegativePathLimitScenarios")
    public void verifyNegativePathWithLimitParam(String description, String q, String limit) throws IOException {

        // test description
        LogUtils.logInfoMessage(description);

        // send request
        JsonObject response = Sounds.getSounds(
                Sounds.PARAM_API_KEY + config.getApiKey() + "&" + Sounds.PARAM_LIMIT + limit + "&" + Sounds.PARAM_Q + q)
                .getAsJsonObject();

        // validate HTTP status code NOTE: Returning 500 for non-int values
        // which it shouldn't. It should be returning a 400. This should be a
        // defect
        AppLib.validateHTTPStatusCode(response, HttpURLConnection.HTTP_BAD_REQUEST);

        // validate Application error message
        // AppLib.validateResponseErrMsg(response, SoundsErrorMsgs.LIMIT_INVALID_ERROR);

    }

    @Test(enabled = true)
    public void verifyLimitParam() throws IOException {

        // generate limit under test
        int limit = AppLib.randInt(0, 50);

        // send request
        JsonObject response = Sounds.getSounds(Sounds.PARAM_API_KEY + config.getApiKey() + "&" + Sounds.PARAM_LIMIT
                + limit + "&" + Sounds.PARAM_Q + "").getAsJsonObject();

        // validate HTTP status code NOTE: Returning 500 for non-int values
        // which it shouldn't. It should be returning a 400. This should be a
        // defect
        AppLib.validateHTTPStatusCode(response, HttpURLConnection.HTTP_OK);

        // validate response size
        Assert.assertEquals(response.get(SoundsConstants.SOUNDS_RESPONSE_PROPERTY_RESULTS).getAsJsonArray().size(),
                limit, "verifyLimitParam() :: The reponse did not have the expected limit.");

    }

    @DataProvider(name = "NegativePathApiKeyScenarios")
    public static Object[][] apiKeyParametersData() {
        return new Object[][] {
                // description, apiKey
                { "[TC.1] Verify scenario with invalid api_key with alphabetic value", AppLib.randAlphabetic(10), },
                { "[TC.2] Verify scenario with invalid api_key with alphanumeric value", AppLib.randAlphaNumeric(10) },
                { "[TC.3] Verify scenario with invalid api_key with stringified int value",
                        Integer.toString(AppLib.randInt(0, 1000)) },
                { "[TC.4] Verify scenario with invalid api_key with stringified double value",
                        Double.toString(AppLib.randDouble(0, 1000)) },
                // { "[TC.5] Verify scenario with invalid api_key with special
                // chars value", AppLib.randSpecialChar(10)},
                { "[TC.6] Verify scenario with invalid api_key with hex value", AppLib.randHexString(10) },
                { "[TC.7] Verify scenario with invalid api_key with double byte", AppLib.randDoubleByte(10) },
                { "[TC.8] Verify scenario with invalid api_key with boolean", Boolean.toString(AppLib.randBoolean()) },
                { "[TC.9] Verify scenario with invalid api_key with null", null }};
    }

    @DataProvider(name = "NegativePathQScenarios")
    public static Object[][] qParametersData() {
        return new Object[][] {
                // description, q, limit
                { "[TC.1] Verify scenario with invalid q with int value", Integer.toString(AppLib.randInt(0, 1000)),
                        SoundsConstants.DEFAULT_SOUNDS_LIMIT },
                { "[TC.2] Verify scenario with invalid q with double value",
                        Double.toString(AppLib.randDouble(0, 1000)), SoundsConstants.DEFAULT_SOUNDS_LIMIT },
                { "[TC.3] Verify scenario with invalid q with double byte", AppLib.randDoubleByte(10),
                        SoundsConstants.DEFAULT_SOUNDS_LIMIT },
                { "[TC.4] Verify scenario with invalid q with boolean", Boolean.toString(AppLib.randBoolean()),
                        SoundsConstants.DEFAULT_SOUNDS_LIMIT },
                { "[TC.4] Verify scenario with invalid q with null", null, SoundsConstants.DEFAULT_SOUNDS_LIMIT } };
    }

    @DataProvider(name = "QSearchFilterByIdScenarios")
    public static Object[][] qSearchFilterByIdParametersData() {
        return new Object[][] {
                // description, q, limit
                { "[TC.1] Verify scenario with search filter id '172463128'", "172463128", "1" },
                { "[TC.2] Verify scenario with search filter id '172463124'", "172463124", "1" },
                { "[TC.3] Verify scenario with search filter id '172463122'", "172463122", "1" } };
    }

    @DataProvider(name = "NegativePathLimitScenarios")
    public static Object[][] limitParametersData() {
        return new Object[][] {
                // description, q, limit
                { "[TC.1] Verify scenario with invalid limit with double value", "",
                        Double.toString(AppLib.randDouble(1, 1000)) },
                { "[TC.2] Verify scenario with invalid limit with boolean", "",
                        Boolean.toString(AppLib.randBoolean()) },
                { "[TC.3] Verify scenario with invalid limit with null", "", null },
                { "[TC.4] Verify scenario with invalid limit with negative number", "",
                        Integer.toString(AppLib.randInt(-1000, -1)) } };
    }

}
