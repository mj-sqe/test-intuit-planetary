package com.intuit.test.planetary.utils;

import java.io.IOException;

import com.intuit.test.planetary.config.Config;
import org.testng.annotations.AfterSuite;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

public class BaseTestApp {

    protected static Config config = Config.getInstance();

    @BeforeSuite(alwaysRun = true)
    @Parameters("Environment")
    public void setupEnvironment(String sEnv) throws IOException {

        // Set the environment.
        config.setEnvPrefix(sEnv);

    }

    @AfterSuite(alwaysRun = true)
    public void teardownEnvironment() {

    }
}
