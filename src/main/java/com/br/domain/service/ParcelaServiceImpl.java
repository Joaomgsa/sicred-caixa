package com.br.domain.service;

import com.br.application.dto.request.SimulacaoRequestDTO;
import com.br.domain.model.Parcela;
import com.br.domain.model.Produto;
import com.br.domain.repository.ParcelaRepository;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ParcelaServiceImpl implements ParcelaService {

    @Inject
    ParcelaRepository parcelaRepository;

    @Override
    public List<Parcela> calcularParcelas(SimulacaoRequestDTO dto, Produto produto) {
        List<Parcela> parcelasPrice = new ArrayList<>();

        BigDecimal valorFinanciado = dto.valorDesejado();
        BigDecimal txJurosMensal = produto.getPcTaxaJuros().divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        int numeroParcelas = dto.prazo();

        // Fórmula da prestação PRICE: PMT = PV * (i * (1+i)^n) / ((1+i)^n - 1)
        BigDecimal umMaisJuros = BigDecimal.ONE.add(txJurosMensal);
        BigDecimal umMaisJurosPotencia = umMaisJuros.pow(numeroParcelas);

        BigDecimal prestacaoPrice = valorFinanciado
                .multiply(txJurosMensal)
                .multiply(umMaisJurosPotencia)
                .divide(umMaisJurosPotencia.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        BigDecimal saldoDevedor = valorFinanciado;

        for (int i = 1; i <= numeroParcelas; i++) {
            // Juros sobre saldo devedor atual
            BigDecimal juros = saldoDevedor.multiply(txJurosMensal).setScale(2, RoundingMode.HALF_UP);

            // Amortização = Prestação - Juros (variável no PRICE)
            BigDecimal amortizacao = prestacaoPrice.subtract(juros);

            Parcela parcela = new Parcela(
                    Long.valueOf(dto.idSimulacao()),
                    i,
                    saldoDevedor,
                    juros,
                    prestacaoPrice
            );

            parcelasPrice.add(parcela);

            // Reduzir saldo devedor
            saldoDevedor = saldoDevedor.subtract(amortizacao);

            if (saldoDevedor.compareTo(BigDecimal.ZERO) < 0) {
                saldoDevedor = BigDecimal.ZERO;
            }
        }

        parcelaRepository.persist(parcelasPrice);
        return parcelasPrice;
    }
}
