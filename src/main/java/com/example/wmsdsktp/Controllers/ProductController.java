package com.example.wmsdsktp.Controllers;

import com.example.wmsdsktp.Responses.PostoResponse;
import com.example.wmsdsktp.Responses.ProdutoResponse;
import com.example.wmsdsktp.util.RequestLauncher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.List;

public class ProductController {

    private final ObjectMapper mapper = new ObjectMapper();


    public List<ProdutoResponse> getAll(){
        HttpResponse<String> response = RequestLauncher.newRequest("produtos", "GET", null);
        if (response == null || response.statusCode() != 200) return List.of();

        try {
            return mapper.readValue(response.body(), new TypeReference<List<ProdutoResponse>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
            return List.of();
        }
    }
}
