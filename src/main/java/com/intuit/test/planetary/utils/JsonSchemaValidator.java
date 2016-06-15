package com.intuit.test.planetary.utils;

import java.io.IOException;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.JsonObject;


/**
 * The Class JsonSchemaValidation.  This class method libraries supports JSON Schema Framework <a>http://json-schema.org/</a>.
 */
public final class JsonSchemaValidator {

    /** Constructor to prevent instantiation from other classes. */
    private JsonSchemaValidator() {

    }

    /**
     * Validates a JSON Object against a JSON Schema.  You can generate a JSON schema template here <a>http://jsonschema.net/#/</a>.
     *
     * @param jsonObj
     *            the JsonObject used to validate against the Json schema
     * @param schemaTemplate
     *            the Json schema template file location
     */
    public static void jsonSchemaValidator(JsonObject jsonObj, String schemaTemplate) {

        try {
            String jsonSchemaStr = new String(readAllBytes(get(schemaTemplate)));
            JSONObject rawSchema = new JSONObject(new JSONTokener(jsonSchemaStr));
            Schema schema = SchemaLoader.load(rawSchema);
            schema.validate(new JSONObject(jsonObj.toString()));
        } catch (IOException e) {
            LogUtils.logSevereMessageThenFail(
                    "validateJsonSchema() :: IOException thrown => " + LogUtils.convertStackTraceToString(e));
        } catch (JSONException e) {
            LogUtils.logSevereMessageThenFail(
                    "validateJsonSchema() :: JSONException thrown => " + LogUtils.convertStackTraceToString(e));
        }

    }

}
