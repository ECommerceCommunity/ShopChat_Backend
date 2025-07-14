package com.cMall.feedShop.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Main test configuration class that imports all necessary test configurations.
 * Use this for integration tests that need full Spring context.
 */
@TestConfiguration
@Import({
    TestMailConfig.class,
    TestSecurityConfig.class
})
public class TestConfig {
    // This class serves as a central point to import all test configurations
} 