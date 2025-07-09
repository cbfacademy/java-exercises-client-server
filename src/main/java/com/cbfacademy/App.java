package com.cbfacademy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

public class App {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new URI("https://codingblackfemales.com").toURL().openStream()))) {
            String inputLine;

            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
