package com.br.application.dto;

import java.math.BigDecimal;

public record ProdutoDTO(
        Long coProduto,
        String noProduto,
        BigDecimal pcTaxaJuros,
        Short nuMaximoParcelas
) {
}
