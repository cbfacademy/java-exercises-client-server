package com.cbfacademy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayName("Socket Server Test Suite")
public class SocketServerTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputCapture;
    private Thread serverThread;
    private CountDownLatch serverStarted;
    private volatile boolean serverRunning = false;

    @BeforeEach
    public void setUp() {
        // Set up output capture
        outputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputCapture));
        serverStarted = new CountDownLatch(1);
    }

    @AfterEach
    public void tearDown() {
        // Stop server thread if running
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
            try {
                serverThread.join(1000); // Wait up to 1 second for thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Restore original System.out
        System.setOut(originalOut);
        serverRunning = false;
    }

    @Test
    @DisplayName("server starts and listens on port 4040")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void serverStartsAndListens() throws Exception {
        // Skip test if SocketServer class doesn't exist
        assumeTrue(isSocketServerImplemented(), "SocketServer class must be implemented to run this test");
        
        // Start server in a separate thread
        startServerInBackground();
        
        // Wait for server to start
        assertThat("Server should start within timeout", 
                   serverStarted.await(5, TimeUnit.SECONDS));
        
        // Give additional time for output to be captured
        Thread.sleep(100);
        
        // Check that server announces it's listening
        String output = outputCapture.toString();
        assertThat("Server should announce it's listening on port 4040", 
                   output, containsString("Server is listening on port 4040"));
    }

    @Test
    @DisplayName("server receives and processes client messages")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void serverReceivesClientMessages() throws Exception {
        // Skip test if SocketServer class doesn't exist
        assumeTrue(isSocketServerImplemented(), "SocketServer class must be implemented to run this test");
        
        // Start server in background
        startServerInBackground();
        serverStarted.await(5, TimeUnit.SECONDS);
        
        // Send a message to the server
        String testMessage = "Test message from client";
        sendMessageToServer(testMessage);
        
        // Give server time to process the message
        Thread.sleep(100);
        
        // Check that server received and logged the message
        String output = outputCapture.toString();
        assertThat("Server should log received message", 
                   output, containsString("Received message from client: " + testMessage));
    }

    @Test
    @DisplayName("server handles multiple client connections")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void serverHandlesMultipleClients() throws Exception {
        // Skip test if SocketServer class doesn't exist
        assumeTrue(isSocketServerImplemented(), "SocketServer class must be implemented to run this test");
        
        // Start server in background
        startServerInBackground();
        serverStarted.await(5, TimeUnit.SECONDS);
        
        // Send multiple messages
        String message1 = "First message";
        String message2 = "Second message";
        
        sendMessageToServer(message1);
        Thread.sleep(50);
        sendMessageToServer(message2);
        Thread.sleep(100);
        
        String output = outputCapture.toString();
        assertThat("Server should receive first message", 
                   output, containsString("Received message from client: " + message1));
        assertThat("Server should receive second message", 
                   output, containsString("Received message from client: " + message2));
    }

    private void startServerInBackground() {
        serverThread = new Thread(() -> {
            try {
                serverRunning = true;
                
                // Run the server using reflection to avoid compilation dependency
                Class<?> serverClass = Class.forName("com.cbfacademy.SocketServer");
                java.lang.reflect.Method mainMethod = serverClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[]{});
            } catch (Exception e) {
                if (serverRunning && !Thread.currentThread().isInterrupted()) {
                    e.printStackTrace();
                }
            }
        });
        
        serverThread.setDaemon(true); // Make it a daemon thread
        serverThread.start();
        
        // Give the server a moment to start and print its message
        try {
            Thread.sleep(200);
            serverStarted.countDown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendMessageToServer(String message) throws IOException {
        try (Socket socket = new Socket("localhost", 4040);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            out.println(message);
        }
    }

    /**
     * Helper method to check if SocketServer class is implemented.
     * Tests will be skipped until the class exists.
     */
    static boolean isSocketServerImplemented() {
        try {
            Class.forName("com.cbfacademy.SocketServer");
            return true; // Class exists, run tests
        } catch (ClassNotFoundException e) {
            return false; // Class doesn't exist, skip tests
        }
    }
} 