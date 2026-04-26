package com.example.wmsdsktp.util;

import com.example.wmsdsktp.auth.UserSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class RequestLauncher {
    public static final String BASE_URL = "http://localhost:18081/api/";

    public static HttpResponse<String> newRequest(String url, String method, String json) {
        try {
            HttpRequest request;

            switch (method.toUpperCase()) {
                case "POST" -> request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + UserSession.getInstance().getToken())
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build();

                case "GET" -> request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + UserSession.getInstance().getToken())
                        .GET()
                        .build();

                case "DELETE" -> request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + UserSession.getInstance().getToken())
                        .DELETE()
                        .build();

                case "PUT" -> request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + UserSession.getInstance().getToken())
                        .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build();

                default -> throw new Exception("Method not supported: " + method);
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Token: " + UserSession.getInstance().getToken());
            System.out.println("[SUCCESS] on request:" + url + " | status: " + response.statusCode());
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}