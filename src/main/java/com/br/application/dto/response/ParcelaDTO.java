package com.br.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ParcelaDTO(

        @JsonProperty("mes")
        Integer mes,

        @JsonProperty("saldoDevedorInicial")
        BigDecimal saldoDevedorInicial,

        @JsonProperty("juros")
        BigDecimal juros,

        @JsonProperty("amortizacao")
        BigDecimal amortizacao,

        @JsonProperty("saldoDevedorFinal")
        BigDecimal saldoDevedorFinal)
{
}
