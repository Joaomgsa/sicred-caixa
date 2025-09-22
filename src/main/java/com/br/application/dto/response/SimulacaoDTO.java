package com.br.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoDTO(

        @JsonProperty("valorSolicitado")
        BigDecimal valorSolicitado,

        @JsonProperty("prazoMezes")
        Integer prazoMezes,

        @JsonProperty("taxaJurosEfetivaMensal")
        BigDecimal taxaJurosEfetivaMensal,

        @JsonProperty("valorTotalComJuros")
        BigDecimal valorTotalComJuros,

        @JsonProperty("parcelaMensal")
        BigDecimal parcelaMensal,

        @JsonProperty("memoriaCalculo")
        List<ParcelaDTO> parcelas)
{}
