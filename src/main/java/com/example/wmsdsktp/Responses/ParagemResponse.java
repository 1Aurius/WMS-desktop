package com.example.wmsdsktp.Responses;

import java.util.Date;

public record ParagemResponse(
        Long id,
        Long rotaId,
        Long postoId,
        String postoNome,
        String postoLocal,
        Date dhChegada,
        Date dhSaida
) {}
