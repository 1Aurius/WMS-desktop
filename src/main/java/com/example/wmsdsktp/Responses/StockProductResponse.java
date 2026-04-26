package com.example.wmsdsktp.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockProductResponse(
        Integer stockId,
        Integer productId,
        String productName,
        String serialCode,
        String unidade,
        Double preco,
        Integer quantity,
        Integer postoId,
        String postoDesignacao,
        String postoTipo
) {}