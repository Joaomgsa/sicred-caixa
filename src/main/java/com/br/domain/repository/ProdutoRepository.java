package com.br.domain.repository;

import com.br.domain.model.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;


import java.util.List;


@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {

    public List<Produto> buscarProdutosAtivos() {
        return find("stAtivo", true).list();
    }
}
