package com.br.domain.service;

import com.br.application.dto.request.SimulacaoRequestDTO;
import com.br.domain.model.Parcela;
import com.br.domain.model.Produto;

import java.util.List;

public interface ParcelaService {

    public List<Parcela> calcularParcelas(SimulacaoRequestDTO dto, Produto produto);
}
