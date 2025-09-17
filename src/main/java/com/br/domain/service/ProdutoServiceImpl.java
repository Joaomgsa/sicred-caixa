package com.br.domain.service;

import com.br.application.dto.request.ProdutoRequestDTO;
import com.br.application.dto.response.ProdutoResponseDTO;
import com.br.domain.mapper.ProdutoMapper;
import com.br.domain.model.Produto;
import com.br.domain.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProdutoServiceImpl implements ProdutoService {

    @Inject
    private ProdutoRepository produtoRepository;

    @Override
    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoRepository.listAll()
                .stream()
                .map(ProdutoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public ProdutoResponseDTO buscarProdutoPorCodigo(Long codigo){
        Produto produto = produtoRepository.findById(codigo);
        return produto != null ? ProdutoMapper.toResponseDTO(produto) : null;
    }

    @Override
    @Transactional
    public ProdutoResponseDTO salvarProduto(ProdutoRequestDTO produto){
        Produto entity = ProdutoMapper.toEntity(produto, true);
        produtoRepository.persist(entity);
        return ProdutoMapper.toResponseDTO(entity);
    }

    // TODO: Criar exception para produto nao encontrado
    @Override
    @Transactional
    public ProdutoResponseDTO  atualizarProduto(ProdutoRequestDTO request){
        Produto produto = produtoRepository.findById(request.id());
        if(produto == null){
            return  null;
        }
        produto.setNoProduto(request.nome());
        produto.setPcTaxaJuros(request.taxaJurosAnual());
        produto.setNuMaximoParcelas(request.prazoMaximoMeses().shortValue());
        produto.setStAtivo(true);
        produtoRepository.persist(produto);
        return ProdutoMapper.toResponseDTO(produto);
    }

    @Override
    @Transactional
    public void deletarProduto(Long codigo) {
        Produto produto = produtoRepository.findById(codigo);
        if (produto != null) {
            produto.setStAtivo(false);
        }
        produtoRepository.persist(produto);
    }





}
