package com.br.domain.service;

import com.br.application.dto.ProdutoDTO;
import com.br.application.dto.request.ProdutoRequestDTO;
import com.br.application.dto.response.ProdutoResponseDTO;
import com.br.domain.model.Produto;

import java.util.List;


public interface ProdutoService {


    public List<ProdutoResponseDTO> listarProdutos();

    public ProdutoResponseDTO buscarProdutoPorCodigo(Long codigo);

    Produto buscarProduto(Long codigo);

    public ProdutoDTO buscarProdutoDTOPorCodigo(Long codigo);

    public ProdutoResponseDTO salvarProduto(ProdutoRequestDTO produto);

    public ProdutoResponseDTO atualizarProduto(ProdutoRequestDTO produto);

    public void deletarProduto(Long codigo);


}
