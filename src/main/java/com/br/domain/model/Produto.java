package com.br.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "co_produto")
    private Long coProduto;

    @Column(name = "no_produto", nullable = false, length = 200)
    private String noProduto;

    @Column(name = "pc_taxa_juros_anual", nullable = false, precision = 10, scale = 9)
    private BigDecimal pcTaxaJuros;

    // Validar o numero maximo de parcelas pelo produto
    @Column(name = "nu_maximo_meses", nullable = false)
    private Short nuMaximoParcelas;

    @Column(name = "st_ativo", nullable = false)
    private Boolean stAtivo;


    public Produto() {
    }

    public Produto(Long coProduto, String noProduto, BigDecimal pcTaxaJuros, Short nuMaximoParcelas, Boolean stAtivo) {
        this.coProduto = coProduto;
        this.noProduto = noProduto;
        this.pcTaxaJuros = pcTaxaJuros;
        this.nuMaximoParcelas = nuMaximoParcelas;
        this.stAtivo = stAtivo;
    }

    public Long getCoProduto() {
        return coProduto;
    }

    public void setCoProduto(Long coProduto) {
        this.coProduto = coProduto;
    }

    public String getNoProduto() {
        return noProduto;
    }

    public void setNoProduto(String noProduto) {
        this.noProduto = noProduto;
    }

    public BigDecimal getPcTaxaJuros() {
        return pcTaxaJuros;
    }

    public void setPcTaxaJuros(BigDecimal pcTaxaJuros) {
        this.pcTaxaJuros = pcTaxaJuros;
    }

    public Short getNuMaximoParcelas() {
        return nuMaximoParcelas;
    }

    public void setNuMaximoParcelas(Short nuMaximoParcelas) {
        this.nuMaximoParcelas = nuMaximoParcelas;
    }

    public Boolean getStAtivo() {
        return stAtivo;
    }

    public void setStAtivo(Boolean stAtivo) {
        this.stAtivo = stAtivo;
    }
}
