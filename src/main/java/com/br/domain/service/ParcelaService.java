package com.br.domain.service;

import com.br.application.dto.ProdutoDTO;
import com.br.application.dto.request.SimulacaoRequestDTO;
import com.br.application.dto.response.ParcelaDTO;
import com.br.domain.model.Parcela;
import com.br.domain.model.Produto;

import java.util.List;

public interface ParcelaService {

    public List<ParcelaDTO> calcularParcelasDTO(SimulacaoRequestDTO simulacao, ProdutoDTO produto);
}
