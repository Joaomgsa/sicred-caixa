package com.br.domain.mapper;

import com.br.application.dto.ProdutoDTO;
import com.br.application.dto.request.ProdutoRequestDTO;
import com.br.application.dto.response.ProdutoResponseDTO;
import com.br.domain.model.Produto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@ApplicationScoped
public class ProdutoMapper {

    public ProdutoResponseDTO toResponseDTO(Produto produto){
        return new ProdutoResponseDTO(
                produto.getCoProduto().intValue(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                produto.getNuMaximoParcelas().intValue()
        );
    }


    public Produto responseToEntity(ProdutoRequestDTO produto, boolean statusNovo){
        Produto entity = new Produto();
        entity.setNoProduto(produto.nome());
        entity.setPcTaxaJuros(produto.taxaJurosAnual());
        entity.setPcTaxaJuros(produto.taxaJurosAnual());
        entity.setNuMaximoParcelas(produto.prazoMaximoMeses().shortValue());
        entity.setStAtivo(statusNovo);
        return entity;
    }


    public Produto dtoToEntity(ProdutoDTO produto, boolean statusNovo){
        Produto entity = new Produto();
        entity.setNoProduto(produto.noProduto());
        entity.setPcTaxaJuros(produto.pcTaxaJuros());
        entity.setNuMaximoParcelas(produto.nuMaximoParcelas());
        entity.setStAtivo(statusNovo);
        return entity;
    }

    public ProdutoDTO toDTO(Produto produto){
        return new ProdutoDTO(
                produto.getCoProduto(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                produto.getNuMaximoParcelas()
        );
    }
}
