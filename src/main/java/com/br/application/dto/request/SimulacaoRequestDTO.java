package com.br.application.dto.request;

import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

public record SimulacaoRequestDTO(@NotNull(message = "Valor desejado é obrigatório")
                                  @DecimalMin(value = "500.00", message = "Valor mínimo é R$ 500,00")
                                  @Digits(integer = 10, fraction = 2, message = "Valor deve ter no máximo 10 dígitos inteiros e 2 decimais")
                                  BigDecimal valorDesejado,


                                  @NotNull(message = "Prazo é obrigatório")
                                  @Min(value = 5, message = "Prazo mínimo é 5 mês")
                                  @Max(value = 240, message = "Prazo máximo é 240 meses")
                                  Integer prazo,

                                  @Schema(hidden = true)
                                  String idSimulacao
)

{
    // Método wither para adicionar ID da simulação
    public SimulacaoRequestDTO withIdSimulacao(String idSimulacao) {
        return new SimulacaoRequestDTO(this.valorDesejado, this.prazo, idSimulacao);
    }
}
