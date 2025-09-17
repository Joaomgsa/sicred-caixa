package com.br.domain.service;

import com.br.domain.repository.SimulacaoRepository;
import jakarta.inject.Inject;

public class SimulcaoServiceImpl {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    ProdutoService produtoService;
}
