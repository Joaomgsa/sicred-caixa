package com.br.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoResponseDTO(
        @JsonProperty("produto")
        ProdutoResponseDTO produto,
        @JsonProperty("simulacao")
        SimulacaoDTO simulacao)
{}
