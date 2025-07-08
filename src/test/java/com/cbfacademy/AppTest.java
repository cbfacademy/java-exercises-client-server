package com.cbfacademy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayName(value = "Basic Test Suite")
public class AppTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputCapture;

    @BeforeEach
    public void setUp() {
        // Set up output capture
        outputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputCapture));
    }

    @AfterEach
    public void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("creates the app")
    public void createsAnApp() {
        final App app = new App();

        assertThat(app, is(notNullValue()));
    }

    @Test
    @DisplayName("main method outputs website content correctly")
    public void mainMethodOutputsWebsiteContent() throws Exception {
        // Call the main method (which will print to our captured output)
        App.main(new String[]{});
        
        // Get the captured output
        String actualOutput = outputCapture.toString();
        
        // Make our own request to get expected output
        String expectedOutput = getExpectedWebsiteContent();
        
        // Compare the outputs
        assertThat("Main method should output the same content as direct HTTP request", 
                   actualOutput.trim(), is(expectedOutput.trim()));
    }

    private String getExpectedWebsiteContent() throws Exception {
        URL url = new URI("https://codingblackfemales.com").toURL();
        URLConnection connection = url.openConnection();
        connection.connect();
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                content.append(inputLine);
                if (reader.ready()) {
                    content.append(System.lineSeparator());
                }
            }
        }
        
        return content.toString();
    }
}
