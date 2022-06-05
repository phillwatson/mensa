package com.hillayes.mensa.email.templates;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class ConfigTest {
    @Inject
    public Config config;

    @Test
    public void testConfig() {
        assertNotNull(config.emails());
        assertEquals(2, config.emails().size());

        Config.EmailConfig ec = config.emails().get("user-created");
        assertEquals("user-created.email", ec.template());
        assertEquals("Complete your Mensa application", ec.subject());
        assertEquals("customer-care@mensa.co.uk", ec.sender());
    }
}
