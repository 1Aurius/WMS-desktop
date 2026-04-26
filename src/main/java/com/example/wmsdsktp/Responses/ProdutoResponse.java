package com.example.wmsdsktp.Responses;

public record ProdutoResponse(
        Integer id,
        String serialCode,
        String nome,
        String unidade,
        Double preco,
        Double aproxVolume,
        Integer utilizadorCriadorId
) {

}