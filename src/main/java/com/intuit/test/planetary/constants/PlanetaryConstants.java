package com.intuit.test.planetary.constants;

import java.io.File;

/**
 * The Class PlanetaryConstants.
 */
public final class PlanetaryConstants {
    
    
    /**
     * The location of the schema files.
     */
    public static final String SCHEMA_FILE_DIR = "src" + File.separator + "test" + File.separator + "data"
            + File.separator + "schemas";       
    

    /** Constructor to prevent instantiation from other classes. */
    private PlanetaryConstants() {
    }
}
