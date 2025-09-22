package com.br.domain.service;

import com.br.application.dto.ProdutoDTO;
import com.br.application.dto.request.SimulacaoRequestDTO;
import com.br.application.dto.response.ParcelaDTO;
import com.br.domain.mapper.ParcelaMapper;
import com.br.domain.model.Parcela;
import com.br.domain.model.Produto;
import com.br.domain.repository.ParcelaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ParcelaServiceImpl implements ParcelaService {

    // TODO: Terminar Implementacao da Simulacao e planejar como guardar informacoes de simulacoes por usuario
    @Inject
    ParcelaRepository parcelaRepository;

    @Inject
    ProdutoService produtoService;

    @Inject
    ParcelaMapper parcelaMapper;


    /**
     * Calcula a lista de parcelas simuladas no formato DTO, aplicando o sistema Price.
     *
     * @param simulacao os dados da simulação, incluindo valor solicitado e prazo em meses
     * @param produto os dados do produto, incluindo taxa de juros
     * @return lista de parcelas simuladas como DTOs
     */

    @Override
    public List<ParcelaDTO> calcularParcelasDTO(SimulacaoRequestDTO simulacao, ProdutoDTO produto) {
        var parcelas = calcularEPersistirParcelas(simulacao, produto);
        return converterParaDTOs(parcelas);
    }


    private List<Parcela> calcularEPersistirParcelas(SimulacaoRequestDTO simulacao, ProdutoDTO produto) {
        var parcelas = new ArrayList<Parcela>();
        var valor = simulacao.valorSolicitado();
        var juros = produto.pcTaxaJuros().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        var n = simulacao.prazoMeses();
        var prestacao = calcularPrestacaoPrice(valor, juros, n);
        var saldo = valor;

        for (int i = 1; i <= n; i++) {
            var valorJuros = saldo.multiply(juros).setScale(2, RoundingMode.HALF_UP);
            var amortizacao = prestacao.subtract(valorJuros);
            parcelas.add(new Parcela(
                    Long.valueOf(simulacao.idSimulacao()),
                    i,
                    saldo,
                    valorJuros,
                    prestacao
            ));
            saldo = saldo.subtract(amortizacao);
            if (saldo.compareTo(BigDecimal.ZERO) < 0) saldo = BigDecimal.ZERO;
        }
        parcelaRepository.persist(parcelas); // persistência em lote
        return parcelas;
    }

    // Converte entidades para DTOs
    private List<ParcelaDTO> converterParaDTOs(List<Parcela> parcelas) {
        return parcelas.stream()
                .map(parcelaMapper::toDTO)
                .toList();
    }


    private static BigDecimal calcularPrestacaoPrice(BigDecimal valor, BigDecimal juros, int parcelas) {
        var umMaisJuros = BigDecimal.ONE.add(juros);
        var umMaisJurosPotencia = umMaisJuros.pow(parcelas);
        return valor
                .multiply(juros)
                .multiply(umMaisJurosPotencia)
                .divide(umMaisJurosPotencia.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }
}
