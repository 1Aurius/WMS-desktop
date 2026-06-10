package com.example.wmsdsktp.Responses;

public record RequesicaoStockHeaderResponse(
        Integer id,
        String dhRegisto,
        String dhValidacao,
        String descricao,
        String estado,
        String utilizadorCriadorUsername,
        String utilizadorValidadorUsername
) {}
