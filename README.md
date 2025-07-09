# Client-Server Architecture

[![Java Language](https://img.shields.io/badge/PLATFORM-OpenJDK-3A75B0.svg?style=for-the-badge)][1]
[![JUnit5 Testing Framework](https://img.shields.io/badge/testing%20framework-JUnit5-26A162.svg?style=for-the-badge)][2]
[![Maven Dependency Manager](https://img.shields.io/badge/dependency%20manager-Maven-AA215A.svg?style=for-the-badge)][3]

The goal of these programming exercises is to practise:
- communication with a remote server
- using the `java.net.*` package capabilities

For this assignment, we've provided the starter project above.

## :earth_africa: Using URLs

Modify [App.java](src/main/java/com/cbfacademy/App.java) to connect to the main CBF website at https://codingblackfemales.com, then read its content line by line and print each line to the screen.

You will need to use `HttpURLConnection`, `BufferedReader` and `InputStreamReader` classes.

Whenever possible, use the try-with-resources construct we saw earlier in the course, and ensure all resources are released.

## :white_check_mark: Verify Your Implementation

To verify that your code works as expected, run the tests. In your terminal, ensure that you are in the root of this repository, then run the following command:

```shell
./mvnw clean test
```

## :phone: Client & Server

Create two executable classes:
- a `SocketServer` class which uses a `ServerSocket` to listen for connections on `localhost:4040`, then prints message it receives on the screen.
- a `SocketClient` class which requests a connection to server, sends a simple text message to the server.

Ensure all resources created in your programmes are released appropriately. To test your code manually, you'll need to run each application in a separate terminal:

Terminal 1:
```shell
./mvnw compile exec:java -Dexec.mainClass="com.cbfacademy.SocketServer"
```

Terminal 2:
```shell
./mvnw compile exec:java -Dexec.mainClass="com.cbfacademy.SocketClient"
```

## :white_check_mark: Verify Your Implementation

To verify that your code works as expected, run the tests:

```shell
./mvnw clean test
```

#### :warning: IMPORTANT

Due to the nature of the tests needing to bind the same port, it's advisable to run the server and client tests separately until both classes are complete, i.e.:

```shell
./mvnw clean test -Dtest=SocketServerTest
```

```shell
./mvnw clean test -Dtest=SocketClientTest
```

[1]: https://docs.oracle.com/javase/21/docs/api/index.html
[2]: https://junit.org/junit5/
[3]: https://maven.apache.org/