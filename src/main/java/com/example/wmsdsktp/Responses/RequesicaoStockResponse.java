package com.example.wmsdsktp.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RequesicaoStockResponse(
        Integer id,
        String estado,
        String descricao
) {}
