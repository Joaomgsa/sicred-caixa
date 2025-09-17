package com.br.domain.repository;

import com.br.domain.model.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.Optional;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {


    //TODO: Implementar os metodos que serao usados no service
    /*
    public Produto findByPrazo(Integer prazo) {
        return find("select p from Produto p where p.nuMinimoParcelas <= ?1 and p.nuMaximoParcelas >= ?1", prazo)
                .firstResult();
    }

    public Optional<Produto> findByPrazoAndValor(Integer prazo, BigDecimal valor) {
        return find("SELECT p FROM Produto p WHERE " +
                "p.nuMinimoParcelas <= ?1 AND p.nuMaximoParcelas >= ?1 AND " +
                "p.vrMinimo <= ?2 AND p.vrMaximo >= ?2 ", prazo, valor)
                .firstResultOptional();
    }
     */
}
