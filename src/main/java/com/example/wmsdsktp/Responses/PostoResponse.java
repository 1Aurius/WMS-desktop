package com.example.wmsdsktp.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PostoResponse(
        Integer id,
        String designacao,
        String local,
        String cp,
        String tipo,
        Integer gerenteId
) {}