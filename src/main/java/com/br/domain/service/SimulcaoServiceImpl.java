package com.br.domain.service;

import com.br.application.dto.ProdutoDTO;
import com.br.application.dto.request.SimulacaoRequestDTO;
import com.br.application.dto.response.ParcelaDTO;
import com.br.application.dto.response.SimulacaoDTO;
import com.br.application.dto.response.SimulacaoResponseDTO;
import com.br.domain.mapper.ProdutoMapper;
import com.br.domain.model.Produto;
import com.br.domain.model.Simulacao;
import com.br.domain.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class SimulcaoServiceImpl implements SimulacaoService {


    @Inject
    ProdutoService produtoService;

    @Inject
    ParcelaService parcelaService;

    @Inject
    ProdutoMapper produtoMapper;


    /**
     *
     * @param simulacao
     * 1-Buscar produto - vai ser dentro da parcela com o devido tratamento de exceção
     * 2-Persistir simulação
     * 3-Calcular parcelas
     * 4-Persistir parcelas
     * 5-Retornar resposta
     * @return
     */

    @Override
    @Transactional
    public SimulacaoResponseDTO simular(SimulacaoRequestDTO simulacao) {

        ProdutoDTO produtoDTO = produtoService.buscarProdutoDTOPorCodigo(simulacao.idProduto());
        var produto = produtoMapper.dtoToEntity(produtoDTO, true);
        Simulacao simulacaoPersistida = persistirSimulacao(simulacao, produto);

        SimulacaoRequestDTO dtoComIdSimulacao = simulacao.withIdSimulacao(simulacaoPersistida.getNuSimulacao().toString());

        List<ParcelaDTO> parcelas =  parcelaService.calcularParcelasDTO(simulacao, produtoDTO);

        var response = montarResponseSimulacao(parcelas, simulacaoPersistida, produto);

        return response;
    }


    /**
     * Persiste a simulação no banco de dados.
     * @param dto     Dados da simulação.
     * @param produto Produto associado à simulação.
     * @return A simulação persistida.
     *
     */
    @Transactional
    private Simulacao persistirSimulacao(SimulacaoRequestDTO dto, Produto produto) {

        Simulacao simulacao = new Simulacao();
        simulacao.setProduto(produto);
        simulacao.setVrSimulacao(dto.valorSolicitado());
        simulacao.setPrSimulacao(dto.prazoMeses());
        simulacao.setVrSimulacao(dto.valorSolicitado());

        return simulacao;
    }


    private SimulacaoDTO montarDTO(Simulacao simulacao, List<ParcelaDTO> parcelas) {
        return new SimulacaoDTO(
                simulacao.getVrSimulacao(),
                simulacao.getPrSimulacao(),
                simulacao.getVrSimulacao(),
                simulacao.getVrTotal(),
                simulacao.getVrParcela(),
                parcelas
        );
    }


    /**
     * Monta a resposta da simulação.
     * 1- Converter as parcelas para o formato esperado na resposta.
     * 2- Calcular totais  da simulação
     * 3- Criar Id da simulação
     * 4- Retornar o DTO de resposta.
     * @param parcelas
     * @param simulacao
     * @param produto
     * @return
     */
    private SimulacaoResponseDTO montarResponseSimulacao(List<ParcelaDTO> parcelas, Simulacao simulacao, Produto produto) {

        var dtoSimulacao = montarDTO(simulacao, parcelas);
        return new SimulacaoResponseDTO(
                produtoMapper.toResponseDTO(produto),
                dtoSimulacao);
    }

}
