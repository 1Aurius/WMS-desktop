package com.example.wmsdsktp.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EquipaMembroResponse(
        Long utilizadorId,
        String nome,
        String dhAssociacao
) {}
