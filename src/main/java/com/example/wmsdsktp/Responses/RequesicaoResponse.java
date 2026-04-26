package com.example.wmsdsktp.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RequesicaoResponse(
        Integer id,
        Integer requesicaoStockId,
        Integer stockId,
        String productName,
        String postoDesignacao,
        Integer quant
) {}