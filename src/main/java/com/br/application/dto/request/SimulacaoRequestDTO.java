package com.br.application.dto.request;

import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;

public record SimulacaoRequestDTO(@NotBlank(message = "Valor desejado é obrigatório")
                                  @DecimalMin(value = "00.01", message = "Valor mínimo é R$ 00,01")
                                  @Digits(integer = 10, fraction = 2, message = "Valor deve ter no máximo 10 dígitos inteiros e 2 decimais")
                                  BigDecimal valorSolicitado,

                                  @Min(message = "Prazo mínimo é 1 meses", value = 1)
                                  @NotBlank(message = "Prazo é obrigatório")
                                  Integer prazoMeses,

                                  @NotBlank(message = "Id do produto é obrigatório")
                                  Long idProduto,

                                  @Schema(hidden = true)
                                  String idSimulacao



)

{
    public SimulacaoRequestDTO withIdSimulacao(String idSimulacao) {
        return new SimulacaoRequestDTO(this.valorSolicitado, this.prazoMeses,this.idProduto,  idSimulacao);
    }
}
