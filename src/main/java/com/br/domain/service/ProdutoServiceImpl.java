package com.br.domain.service;

import com.br.application.dto.ProdutoDTO;
import com.br.application.dto.request.ProdutoRequestDTO;
import com.br.application.dto.response.ProdutoResponseDTO;
import com.br.domain.mapper.ProdutoMapper;
import com.br.domain.model.Produto;
import com.br.domain.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;


import java.util.List;


@ApplicationScoped
public class ProdutoServiceImpl implements ProdutoService {

    @Inject
    private ProdutoRepository produtoRepository;

    @Inject
    private ProdutoMapper produtoMapper;

    @Override
    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoRepository.buscarProdutosAtivos()
                .stream()
                .map(produtoMapper::toResponseDTO)
                .toList();
    }


    public Produto buscarProduto(Long codigo){
        Produto produto = produtoRepository.findById(codigo);
        return produto != null ? produtoRepository.findById(codigo) : null;
    }

    @Override
    public ProdutoResponseDTO buscarProdutoPorCodigo(Long codigo){
        Produto produto = produtoRepository.findById(codigo);
        return produto != null ? produtoMapper.toResponseDTO(produto) : null;
    }

    @Override
    public ProdutoDTO buscarProdutoDTOPorCodigo(Long codigo){
        Produto produto = produtoRepository.findById(codigo);
        return produto != null ? produtoMapper.toDTO(produto) : null;
    }

    @Override
    @Transactional
    public ProdutoResponseDTO salvarProduto(ProdutoRequestDTO produto){
        Produto entity = produtoMapper.responseToEntity(produto, true);
        produtoRepository.persist(entity);
        return produtoMapper.toResponseDTO(entity);
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
        return produtoMapper.toResponseDTO(produto);
    }

    @Override
    @Transactional
    public void deletarProduto(Long codigo) {
        Produto produto = produtoRepository.findById(codigo);
        if (produto == null) {
            throw new NotFoundException("Produto não encontrado para o id: " + codigo);
        }
        produto.setStAtivo(false);
        produtoRepository.persist(produto);
    }

}
