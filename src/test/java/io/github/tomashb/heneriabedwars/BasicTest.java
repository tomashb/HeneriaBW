package io.github.tomashb.heneriabedwars;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test class to ensure the CI pipeline works correctly
 */
public class BasicTest {

    @Test
    public void testBasicFunctionality() {
        // Simple test to ensure Maven test execution works
        assertTrue(true, "Basic test should pass");
    }

    @Test
    public void testStringOperations() {
        String testString = "HeneriaBedwars";
        assertEquals("HeneriaBedwars", testString);
        assertNotNull(testString);
        assertTrue(testString.contains("Heneria"));
    }
}