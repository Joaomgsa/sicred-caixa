package com.br.domain.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_parcelas")
public class Parcela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nu_simulacao", nullable = false)
    private Long nuSimulacao;

    @Column(name = "nu_parcela", nullable = false)
    private Integer nuParcela;

    @Column(name = "vr_saldodevedor", precision = 15, scale = 2, nullable = false)
    private BigDecimal vrSaldodevedor;

    @Column(name = "vr_juros", precision = 15, scale = 2, nullable = false)
    private BigDecimal vrJuros;

    @Column(name = "vr_prestacao", precision = 15, scale = 2, nullable = false)
    private BigDecimal vrPrestacao;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Parcela() {}

    public Parcela(Long nuSimulacao, Integer nuParcela, BigDecimal vrSaldodevedor,
                        BigDecimal vrJuros, BigDecimal vrPrestacao) {
        this.nuSimulacao = nuSimulacao;
        this.nuParcela = nuParcela;
        this.vrSaldodevedor = vrSaldodevedor;
        this.vrJuros = vrJuros;
        this.vrPrestacao = vrPrestacao;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNuSimulacao() { return nuSimulacao; }
    public void setNuSimulacao(Long nuSimulacao) { this.nuSimulacao = nuSimulacao; }

    public Integer getNuParcela() { return nuParcela; }
    public void setNuParcela(Integer nuParcela) { this.nuParcela = nuParcela; }

    public BigDecimal getVrSaldodevedor() { return vrSaldodevedor; }
    public void setVrSaldodevedor(BigDecimal vrSaldodevedor) { this.vrSaldodevedor = vrSaldodevedor; }

    public BigDecimal getVrJuros() { return vrJuros; }
    public void setVrJuros(BigDecimal vrJuros) { this.vrJuros = vrJuros; }

    public BigDecimal getVrPrestacao() { return vrPrestacao; }
    public void setVrPrestacao(BigDecimal vrPrestacao) { this.vrPrestacao = vrPrestacao; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
