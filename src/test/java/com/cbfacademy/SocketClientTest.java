package com.cbfacademy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayName("Socket Client Test Suite")
public class SocketClientTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputCapture;
    private Thread mockServerThread;
    private CountDownLatch serverReady;
    private AtomicReference<String> receivedMessage;

    @BeforeEach
    public void setUp() {
        // Set up output capture
        outputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputCapture));
        serverReady = new CountDownLatch(1);
        receivedMessage = new AtomicReference<>();
    }

    @AfterEach
    public void tearDown() {
        // Stop mock server thread if running
        if (mockServerThread != null && mockServerThread.isAlive()) {
            mockServerThread.interrupt();
            try {
                mockServerThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("client connects and sends correct message")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void clientConnectsAndSendsMessage() throws Exception {
        // Skip test if SocketClient class doesn't exist
        assumeTrue(isSocketClientImplemented(), "SocketClient class must be implemented to run this test");

        // Start mock server
        startMockServer();

        // Wait for server to be ready
        assertThat("Mock server should start within timeout",
                serverReady.await(5, TimeUnit.SECONDS));

        // Run the client using reflection to avoid compilation dependency
        runSocketClientMain();

        // Give time for message to be processed
        Thread.sleep(100);

        // Verify client sent the expected message
        assertThat("Client should send 'Hello, World!' message",
                receivedMessage.get(), is("Hello, World!"));

        // Verify console output
        String output = outputCapture.toString();
        assertThat("Client should log sent message",
                output, containsString("Sent message to server: Hello, World!"));
    }

    @Test
    @DisplayName("client handles server connection successfully")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void clientHandlesConnection() throws Exception {
        // Skip test if SocketClient class doesn't exist
        assumeTrue(isSocketClientImplemented(), "SocketClient class must be implemented to run this test");

        // Start mock server
        startMockServer();
        serverReady.await(5, TimeUnit.SECONDS);

        // Run client using reflection
        runSocketClientMain();
        Thread.sleep(100);

        // Verify no exceptions in output (no stack trace)
        String output = outputCapture.toString();
        assertThat("Client should connect without errors",
                !output.contains("Exception") && !output.contains("Error"));
    }

    @Test
    @DisplayName("client sends message with proper formatting")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void clientSendsProperlyFormattedMessage() throws Exception {
        // Skip test if SocketClient class doesn't exist
        assumeTrue(isSocketClientImplemented(), "SocketClient class must be implemented to run this test");

        startMockServer();
        serverReady.await(5, TimeUnit.SECONDS);

        runSocketClientMain();
        Thread.sleep(100);

        // Verify exact message content
        String received = receivedMessage.get();
        assertThat("Message should be exactly 'Hello, World!'",
                received, is("Hello, World!"));
        assertThat("Message should not contain extra whitespace",
                received.trim(), is(received));
    }

    private void startMockServer() {
        mockServerThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(4040)) {
                // Signal that server is ready
                serverReady.countDown();

                // Accept one connection and read the message
                try (Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()))) {

                    String message = in.readLine();
                    receivedMessage.set(message);
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    e.printStackTrace();
                }
            }
        });

        mockServerThread.setDaemon(true);
        mockServerThread.start();
    }

    /**
     * Helper method to run SocketClient.main() using reflection to avoid
     * compilation dependency.
     */
    private void runSocketClientMain() throws Exception {
        Class<?> clientClass = Class.forName("com.cbfacademy.SocketClient");
        java.lang.reflect.Method mainMethod = clientClass.getMethod("main", String[].class);
        mainMethod.invoke(null, (Object) new String[] {});
    }

    /**
     * Helper method to check if SocketClient class is implemented.
     * Tests will be skipped until the class exists.
     */
    static boolean isSocketClientImplemented() {
        try {
            Class.forName("com.cbfacademy.SocketClient");
            return true; // Class exists, run tests
        } catch (ClassNotFoundException e) {
            return false; // Class doesn't exist, skip tests
        }
    }
}