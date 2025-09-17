package com.br.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        @Schema(hidden = true)
        Long id,

        @NotBlank(message = "Nome do produto é obrigatório")
        @Size(min = 1, max = 100)
        String nome,

        @NotBlank(message = "Taxa de juros anual é obrigatória")
        @DecimalMin(value = "0.0000", inclusive = false, message = "Taxa de juros anual deve ser maior que 0")
        @Digits(integer = 2, fraction = 4, message = "Taxa de juros anual deve ter no máximo 2 dígitos inteiros e 4 decimais")
        BigDecimal taxaJurosAnual,

        @NotBlank(message = "Prazo máximo em meses é obrigatório")
        @DecimalMin(value = "1", message = "Prazo em meses deve ser maior que 0")
        Short prazoMaximoMeses) {

        public ProdutoRequestDTO withId(Long id) {
            return new ProdutoRequestDTO(id, this.nome, this.taxaJurosAnual, this.prazoMaximoMeses);
        }
}
