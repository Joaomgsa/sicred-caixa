package com.br.application.dto.response;

import java.math.BigDecimal;

public record ProdutoResponseDTO(Integer id, String nome, BigDecimal taxaJurosAnual, Integer prazoMaximoMeses) {

}
