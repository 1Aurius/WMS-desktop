package com.example.wmsdsktp.util;

import javafx.concurrent.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class RequestLauncher {
    public static final String BASE_URL = "http://localhost:8080/api/";

    public static HttpResponse newRequest(String url,String Method,String json){
        try{
            HttpRequest request;
            if(Method.equals("POST")){
                 request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                        .build();
            }else if (Method.equals("GET")){
                 request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + url))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();
            } else {
                request = null;
            }

            if(request == null) throw new Exception("Method not supported");


            Task<HttpResponse<String>> task = new Task<HttpResponse<String>>() {
                @Override
                protected HttpResponse<String> call() throws Exception {
                    HttpClient client = HttpClient.newHttpClient();
                    return client.send(request, HttpResponse.BodyHandlers.ofString());
                }
            };


            task.setOnSucceeded(e -> {
                HttpResponse<String> response = task.getValue();
                if (response.statusCode() == 200) {
                    System.out.println("[SUCESS] on request:"+url + " response: " + response.body());
                }
            });

            task.setOnFailed(e -> {
                System.out.println("[FAILED] on request:"+url);
            });

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response != null) {
                System.out.println("[SUCCESS] on request:" + url + " response: " + response.body());
            }
            return response;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
