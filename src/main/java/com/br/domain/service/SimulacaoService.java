package com.br.domain.service;

import com.br.application.dto.request.SimulacaoRequestDTO;
import com.br.application.dto.response.SimulacaoResponseDTO;

public interface SimulacaoService {

    SimulacaoResponseDTO simular(SimulacaoRequestDTO simulacao);
}
