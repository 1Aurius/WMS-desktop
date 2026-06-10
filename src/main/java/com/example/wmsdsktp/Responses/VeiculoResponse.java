package com.example.wmsdsktp.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VeiculoResponse(
        Integer id,
        String matricula,
        Double aproxVolume,
        String ano,
        String modeloDesignacao,
        Integer postoId,
        String postoDesignacao
) {}
