package com.cbfacademy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class App {
    public static void main(String[] args) {
        try {
            URL url = new URI("https://codingblackfemales.com").toURL();
            URLConnection connection = url.openConnection();

            connection.connect();
            // Using try-with-resources to ensure resources are closed
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;

                while ((inputLine = reader.readLine()) != null) {
                    System.out.println(inputLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}