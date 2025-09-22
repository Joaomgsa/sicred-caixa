package com.br.domain.service;

import com.br.application.dto.ProdutoDTO;
import com.br.application.dto.request.SimulacaoRequestDTO;
import com.br.application.dto.response.ParcelaDTO;
import com.br.domain.mapper.ParcelaMapper;
import com.br.domain.model.Parcela;
import com.br.domain.repository.ParcelaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelaServiceImplTest {

    @Mock
    private ParcelaRepository parcelaRepository;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private ParcelaMapper parcelaMapper;

    @InjectMocks
    private ParcelaServiceImpl parcelaService;

    private SimulacaoRequestDTO simulacaoRequestDTO;
    private ProdutoDTO produtoDTO;
    private List<Parcela> parcelasEntity;
    private List<ParcelaDTO> parcelasDTO;

    @BeforeEach
    void setUp() {
        simulacaoRequestDTO = new SimulacaoRequestDTO(
                new BigDecimal("10000.00"),
                12,
                1L,
                "123"
        );

        produtoDTO = new ProdutoDTO(
                1L,
                "Crédito Pessoal",
                new BigDecimal("1.5000"),
                (short) 12
        );

        // Criando parcelas de entidade mockadas
        parcelasEntity = Arrays.asList(
                new Parcela(123L, 1, new BigDecimal("10000.00"), 
                           new BigDecimal("150.00"), new BigDecimal("996.35")),
                new Parcela(123L, 2, new BigDecimal("9153.65"), 
                           new BigDecimal("137.30"), new BigDecimal("996.35"))
        );

        // Criando parcelas DTO mockadas
        parcelasDTO = Arrays.asList(
                new ParcelaDTO(1, new BigDecimal("10000.00"), new BigDecimal("150.00"), 
                              new BigDecimal("846.35"), new BigDecimal("9153.65")),
                new ParcelaDTO(2, new BigDecimal("9153.65"), new BigDecimal("137.30"), 
                              new BigDecimal("859.05"), new BigDecimal("8294.60"))
        );
    }

    // Teste de cenário de sucesso

    @Test
    void calcularParcelasDTO_DeveRetornarListaDeParcelasDTO_QuandoDadosValidos() {
        // Given
        doNothing().when(parcelaRepository).persist(anyList());
        
        when(parcelaMapper.toDTO(any(Parcela.class)))
                .thenAnswer(invocation -> {
                    Parcela parcela = invocation.getArgument(0);
                    return new ParcelaDTO(
                            parcela.getNuParcela(),
                            parcela.getVrSaldodevedor(),
                            parcela.getVrJuros(),
                            parcela.getVrPrestacao().subtract(parcela.getVrJuros()),
                            parcela.getVrSaldodevedor().subtract(parcela.getVrPrestacao().subtract(parcela.getVrJuros()))
                    );
                });

        // When
        List<ParcelaDTO> resultado = parcelaService.calcularParcelasDTO(simulacaoRequestDTO, produtoDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(simulacaoRequestDTO.prazoMeses().intValue(), resultado.size());
        
        // Verifica se as parcelas foram persistidas
        verify(parcelaRepository, times(1)).persist(anyList());
        
        // Verifica se o mapper foi chamado para conversão
        verify(parcelaMapper, times(simulacaoRequestDTO.prazoMeses())).toDTO(any(Parcela.class));
    }

    @Test
    void calcularParcelasDTO_DeveCalcularCorreatamente_ComSistemPrice() {
        // Given
        SimulacaoRequestDTO simulacao = new SimulacaoRequestDTO(
                new BigDecimal("1000.00"),
                3,
                1L,
                "123"
        );
        
        ProdutoDTO produto = new ProdutoDTO(
                1L,
                "Teste",
                new BigDecimal("2.0000"), // 2% ao mês
                (short) 3
        );

        doNothing().when(parcelaRepository).persist(anyList());
        
        // Mock do mapper para retornar DTOs simulados
        ParcelaDTO parcela1 = new ParcelaDTO(1, new BigDecimal("1000.00"), new BigDecimal("20.00"), 
                                           new BigDecimal("340.22"), new BigDecimal("659.78"));
        ParcelaDTO parcela2 = new ParcelaDTO(2, new BigDecimal("659.78"), new BigDecimal("13.20"), 
                                           new BigDecimal("347.02"), new BigDecimal("312.76"));
        ParcelaDTO parcela3 = new ParcelaDTO(3, new BigDecimal("312.76"), new BigDecimal("6.26"), 
                                           new BigDecimal("353.96"), new BigDecimal("0.00"));
        
        when(parcelaMapper.toDTO(any(Parcela.class)))
                .thenReturn(parcela1)
                .thenReturn(parcela2)
                .thenReturn(parcela3);

        // When
        List<ParcelaDTO> resultado = parcelaService.calcularParcelasDTO(simulacao, produto);

        // Then
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        
        // Verifica se as parcelas foram calculadas e persistidas
        verify(parcelaRepository, times(1)).persist(anyList());
        verify(parcelaMapper, times(3)).toDTO(any(Parcela.class));
    }

    @Test
    void calcularParcelasDTO_DeveProcessarUmaParcela_QuandoPrazoUm() {
        // Given
        SimulacaoRequestDTO simulacaoUnica = new SimulacaoRequestDTO(
                new BigDecimal("1000.00"),
                1,
                1L,
                "123"
        );

        doNothing().when(parcelaRepository).persist(anyList());
        
        ParcelaDTO parcelaUnica = new ParcelaDTO(1, new BigDecimal("1000.00"), new BigDecimal("15.00"), 
                                                new BigDecimal("1000.00"), new BigDecimal("0.00"));
        
        when(parcelaMapper.toDTO(any(Parcela.class))).thenReturn(parcelaUnica);

        // When
        List<ParcelaDTO> resultado = parcelaService.calcularParcelasDTO(simulacaoUnica, produtoDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(parcelaRepository, times(1)).persist(anyList());
        verify(parcelaMapper, times(1)).toDTO(any(Parcela.class));
    }

    @Test
    void calcularParcelasDTO_DeveProcessarValoresDecimais_ComPrecisao() {
        // Given
        SimulacaoRequestDTO simulacaoDecimal = new SimulacaoRequestDTO(
                new BigDecimal("1000.33"),
                2,
                1L,
                "123"
        );

        doNothing().when(parcelaRepository).persist(anyList());
        
        ParcelaDTO parcela1 = new ParcelaDTO(1, new BigDecimal("1000.33"), new BigDecimal("15.00"), 
                                           new BigDecimal("500.17"), new BigDecimal("500.16"));
        ParcelaDTO parcela2 = new ParcelaDTO(2, new BigDecimal("500.16"), new BigDecimal("7.50"), 
                                           new BigDecimal("500.16"), new BigDecimal("0.00"));
        
        when(parcelaMapper.toDTO(any(Parcela.class)))
                .thenReturn(parcela1)
                .thenReturn(parcela2);

        // When
        List<ParcelaDTO> resultado = parcelaService.calcularParcelasDTO(simulacaoDecimal, produtoDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(parcelaRepository, times(1)).persist(anyList());
        verify(parcelaMapper, times(2)).toDTO(any(Parcela.class));
    }

    // Testes de cenários de erro

    @Test
    void calcularParcelasDTO_DeveLancarExcecao_QuandoRepositorioFalha() {
        // Given
        doThrow(new RuntimeException("Erro de persistência")).when(parcelaRepository).persist(anyList());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> parcelaService.calcularParcelasDTO(simulacaoRequestDTO, produtoDTO));
        assertEquals("Erro de persistência", exception.getMessage());
        verify(parcelaRepository, times(1)).persist(anyList());
    }

    @Test
    void calcularParcelasDTO_DeveLancarExcecao_QuandoMapperFalha() {
        // Given
        doNothing().when(parcelaRepository).persist(anyList());
        when(parcelaMapper.toDTO(any(Parcela.class)))
                .thenThrow(new RuntimeException("Erro no mapeamento"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> parcelaService.calcularParcelasDTO(simulacaoRequestDTO, produtoDTO));
        assertEquals("Erro no mapeamento", exception.getMessage());
        verify(parcelaRepository, times(1)).persist(anyList());
        verify(parcelaMapper, atLeastOnce()).toDTO(any(Parcela.class));
    }

    @Test
    void calcularParcelasDTO_DeveLancarExcecao_QuandoProdutoDTONulo() {
        // Given
        ProdutoDTO produtoNulo = null;

        // When & Then
        assertThrows(NullPointerException.class, 
            () -> parcelaService.calcularParcelasDTO(simulacaoRequestDTO, produtoNulo));
        verify(parcelaRepository, never()).persist(anyList());
        verify(parcelaMapper, never()).toDTO(any(Parcela.class));
    }

    @Test
    void calcularParcelasDTO_DeveLancarExcecao_QuandoSimulacaoDTONulo() {
        // Given
        SimulacaoRequestDTO simulacaoNula = null;

        // When & Then
        assertThrows(NullPointerException.class, 
            () -> parcelaService.calcularParcelasDTO(simulacaoNula, produtoDTO));
        verify(parcelaRepository, never()).persist(anyList());
        verify(parcelaMapper, never()).toDTO(any(Parcela.class));
    }

    @Test
    void calcularParcelasDTO_DeveLancarExcecao_QuandoTaxaJurosNula() {
        // Given
        ProdutoDTO produtoSemJuros = new ProdutoDTO(1L, "Teste", null, (short) 12);

        // When & Then
        assertThrows(NullPointerException.class, 
            () -> parcelaService.calcularParcelasDTO(simulacaoRequestDTO, produtoSemJuros));
        verify(parcelaRepository, never()).persist(anyList());
        verify(parcelaMapper, never()).toDTO(any(Parcela.class));
    }

    @Test
    void calcularParcelasDTO_DeveLancarExcecao_QuandoValorSolicitadoNulo() {
        // Given
        SimulacaoRequestDTO simulacaoSemValor = new SimulacaoRequestDTO(
                null, 12, 1L, "123"
        );

        // When & Then
        assertThrows(NullPointerException.class, 
            () -> parcelaService.calcularParcelasDTO(simulacaoSemValor, produtoDTO));
        verify(parcelaRepository, never()).persist(anyList());
        verify(parcelaMapper, never()).toDTO(any(Parcela.class));
    }

    @Test
    void calcularParcelasDTO_DeveTratarSaldoNegativo_CorrigindoParaZero() {
        // Given - Simulação que pode gerar saldo negativo devido aos cálculos
        SimulacaoRequestDTO simulacaoEspecial = new SimulacaoRequestDTO(
                new BigDecimal("100.00"),
                2,
                1L,
                "123"
        );
        
        ProdutoDTO produtoTaxaAlta = new ProdutoDTO(
                1L,
                "Teste Taxa Alta",
                new BigDecimal("10.0000"), // 10% ao mês - taxa alta
                (short) 2
        );

        doNothing().when(parcelaRepository).persist(anyList());
        
        ParcelaDTO parcela1 = new ParcelaDTO(1, new BigDecimal("100.00"), new BigDecimal("10.00"), 
                                           new BigDecimal("47.73"), new BigDecimal("52.27"));
        ParcelaDTO parcela2 = new ParcelaDTO(2, new BigDecimal("52.27"), new BigDecimal("5.23"), 
                                           new BigDecimal("52.27"), new BigDecimal("0.00"));
        
        when(parcelaMapper.toDTO(any(Parcela.class)))
                .thenReturn(parcela1)
                .thenReturn(parcela2);

        // When
        List<ParcelaDTO> resultado = parcelaService.calcularParcelasDTO(simulacaoEspecial, produtoTaxaAlta);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(parcelaRepository, times(1)).persist(anyList());
        verify(parcelaMapper, times(2)).toDTO(any(Parcela.class));
    }
}