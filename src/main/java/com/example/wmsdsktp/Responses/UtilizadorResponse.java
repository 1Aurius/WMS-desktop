package com.example.wmsdsktp.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UtilizadorResponse(
        Integer id,
        String username,
        String dn,
        Boolean admin,
        Boolean gestorRotas,
        Boolean gestor,
        Boolean loja,
        Boolean armazem
) {}