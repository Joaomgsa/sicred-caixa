package com.br.domain.service;

import com.br.application.dto.ProdutoDTO;
import com.br.application.dto.request.SimulacaoRequestDTO;
import com.br.application.dto.response.ParcelaDTO;
import com.br.application.dto.response.ProdutoResponseDTO;
import com.br.application.dto.response.SimulacaoResponseDTO;
import com.br.domain.mapper.ProdutoMapper;
import com.br.domain.model.Produto;
import com.br.domain.model.Simulacao;
import com.br.domain.repository.SimulacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulcaoServiceImplTest {

    @Mock
    private ProdutoService produtoService;

    @Mock
    private ParcelaService parcelaService;

    @Mock
    private ProdutoMapper produtoMapper;

    @Mock
    private SimulacaoRepository simulacaoRepository;

    @InjectMocks
    private SimulcaoServiceImpl simulcaoService;

    private SimulacaoRequestDTO simulacaoRequestDTO;
    private ProdutoDTO produtoDTO;
    private Produto produto;
    private Simulacao simulacao;
    private List<ParcelaDTO> parcelas;

    @BeforeEach
    void setUp() {
        simulacaoRequestDTO = new SimulacaoRequestDTO(
                new BigDecimal("10000.00"),
                12,
                1L,
                null
        );

        produtoDTO = new ProdutoDTO(
                1L,
                "Crédito Pessoal",
                new BigDecimal("1.5000"),
                (short) 12
        );

        produto = new Produto();
        produto.setCoProduto(1L);
        produto.setNoProduto("Crédito Pessoal");
        produto.setPcTaxaJuros(new BigDecimal("1.5000"));
        produto.setNuMaximoParcelas((short) 12);
        produto.setStAtivo(true);

        simulacao = new Simulacao();
        simulacao.setNuSimulacao(123L);
        simulacao.setProduto(produto);
        simulacao.setVrSimulacao(new BigDecimal("10000.00"));
        simulacao.setPrSimulacao(12);
        simulacao.setVrTotal(new BigDecimal("11956.18"));
        simulacao.setVrParcela(new BigDecimal("996.35"));

        parcelas = Arrays.asList(
                new ParcelaDTO(1, new BigDecimal("10000.00"), new BigDecimal("150.00"), 
                              new BigDecimal("846.35"), new BigDecimal("9153.65")),
                new ParcelaDTO(2, new BigDecimal("9153.65"), new BigDecimal("137.30"), 
                              new BigDecimal("859.05"), new BigDecimal("8294.60"))
        );
    }

    // Teste de cenário de sucesso

    @Test
    void simular_DeveRetornarSimulacaoResponseDTO_QuandoDadosValidos() {
        // Given
        when(produtoService.buscarProdutoDTOPorCodigo(1L)).thenReturn(produtoDTO);
        when(produtoMapper.dtoToEntity(produtoDTO, true)).thenReturn(produto);
        
        // Mock da persistência da simulação - simula geração de ID
        doAnswer(invocation -> {
            Simulacao sim = invocation.getArgument(0);
            sim.setNuSimulacao(123L); // Simula a geração de ID após persistir
            return null;
        }).when(simulacaoRepository).persist(any(Simulacao.class));
        
        when(parcelaService.calcularParcelasDTO(any(SimulacaoRequestDTO.class), eq(produtoDTO)))
                .thenReturn(parcelas);
        
        // Mock do produtoMapper.toResponseDTO como método de instância
        ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO(
                produto.getCoProduto().intValue(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                produto.getNuMaximoParcelas().intValue()
        );
        when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);
        
        // When
        SimulacaoResponseDTO resultado = simulcaoService.simular(simulacaoRequestDTO);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.produto());
        assertNotNull(resultado.simulacao());
        assertEquals(produto.getCoProduto().intValue(), resultado.produto().id());
        assertEquals(produto.getNoProduto(), resultado.produto().nome());
        
        verify(produtoService, times(1)).buscarProdutoDTOPorCodigo(1L);
        verify(produtoMapper, times(1)).dtoToEntity(produtoDTO, true);
        verify(simulacaoRepository, times(1)).persist(any(Simulacao.class));
        verify(parcelaService, times(1)).calcularParcelasDTO(any(SimulacaoRequestDTO.class), eq(produtoDTO));
        verify(produtoMapper, times(1)).toResponseDTO(produto);
    }

    @Test
    void simular_DeveUtilizarIdSimulacaoGerado_QuandoSimulacaoFoiPersistida() {
        // Given
        when(produtoService.buscarProdutoDTOPorCodigo(1L)).thenReturn(produtoDTO);
        when(produtoMapper.dtoToEntity(produtoDTO, true)).thenReturn(produto);
        
        // Mock da persistência da simulação
        doAnswer(invocation -> {
            Simulacao sim = invocation.getArgument(0);
            sim.setNuSimulacao(456L); // ID gerado
            return null;
        }).when(simulacaoRepository).persist(any(Simulacao.class));
        
        when(parcelaService.calcularParcelasDTO(any(SimulacaoRequestDTO.class), eq(produtoDTO)))
                .thenReturn(parcelas);

        ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO(
                produto.getCoProduto().intValue(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                produto.getNuMaximoParcelas().intValue()
        );
        when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

        // When
        SimulacaoResponseDTO resultado = simulcaoService.simular(simulacaoRequestDTO);

        // Then
        assertNotNull(resultado);
        
        // Verifica se o método foi chamado com o DTO que deveria ter o ID da simulação
        verify(parcelaService, times(1)).calcularParcelasDTO(
            argThat(dto -> dto.idSimulacao() != null && dto.idSimulacao().equals("456")), eq(produtoDTO));
        verify(simulacaoRepository, times(1)).persist(any(Simulacao.class));
    }

    // Testes de cenários de erro

    @Test
    void simular_DeveLancarExcecao_QuandoProdutoNaoEncontrado() {
        // Given
        when(produtoService.buscarProdutoDTOPorCodigo(1L)).thenReturn(null);

        // When & Then
        assertThrows(NullPointerException.class, () -> simulcaoService.simular(simulacaoRequestDTO));
        verify(produtoService, times(1)).buscarProdutoDTOPorCodigo(1L);
        verify(parcelaService, never()).calcularParcelasDTO(any(), any());
    }

    @Test
    void simular_DeveLancarExcecao_QuandoProdutoServiceFalha() {
        // Given
        when(produtoService.buscarProdutoDTOPorCodigo(1L))
                .thenThrow(new RuntimeException("Erro no serviço de produto"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> simulcaoService.simular(simulacaoRequestDTO));
        assertEquals("Erro no serviço de produto", exception.getMessage());
        verify(produtoService, times(1)).buscarProdutoDTOPorCodigo(1L);
        verify(parcelaService, never()).calcularParcelasDTO(any(), any());
    }

    @Test
    void simular_DeveLancarExcecao_QuandoRepositorioFalha() {
        // Given
        when(produtoService.buscarProdutoDTOPorCodigo(1L)).thenReturn(produtoDTO);
        when(produtoMapper.dtoToEntity(produtoDTO, true)).thenReturn(produto);
        
        doThrow(new RuntimeException("Erro de persistência")).when(simulacaoRepository).persist(any(Simulacao.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> simulcaoService.simular(simulacaoRequestDTO));
        assertEquals("Erro de persistência", exception.getMessage());
        verify(produtoService, times(1)).buscarProdutoDTOPorCodigo(1L);
        verify(simulacaoRepository, times(1)).persist(any(Simulacao.class));
        verify(parcelaService, never()).calcularParcelasDTO(any(), any());
    }

    @Test
    void simular_DeveLancarExcecao_QuandoParcelaServiceFalha() {
        // Given
        when(produtoService.buscarProdutoDTOPorCodigo(1L)).thenReturn(produtoDTO);
        when(produtoMapper.dtoToEntity(produtoDTO, true)).thenReturn(produto);
        
        doAnswer(invocation -> {
            Simulacao sim = invocation.getArgument(0);
            sim.setNuSimulacao(123L);
            return null;
        }).when(simulacaoRepository).persist(any(Simulacao.class));
        
        when(parcelaService.calcularParcelasDTO(any(SimulacaoRequestDTO.class), eq(produtoDTO)))
                .thenThrow(new RuntimeException("Erro no cálculo de parcelas"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> simulcaoService.simular(simulacaoRequestDTO));
        assertEquals("Erro no cálculo de parcelas", exception.getMessage());
        verify(produtoService, times(1)).buscarProdutoDTOPorCodigo(1L);
        verify(simulacaoRepository, times(1)).persist(any(Simulacao.class));
        verify(parcelaService, times(1)).calcularParcelasDTO(any(SimulacaoRequestDTO.class), eq(produtoDTO));
    }

    @Test
    void simular_DeveLancarExcecao_QuandoMapperFalha() {
        // Given
        when(produtoService.buscarProdutoDTOPorCodigo(1L)).thenReturn(produtoDTO);
        when(produtoMapper.dtoToEntity(produtoDTO, true))
                .thenThrow(new RuntimeException("Erro no mapeamento"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> simulcaoService.simular(simulacaoRequestDTO));
        assertEquals("Erro no mapeamento", exception.getMessage());
        verify(produtoService, times(1)).buscarProdutoDTOPorCodigo(1L);
        verify(produtoMapper, times(1)).dtoToEntity(produtoDTO, true);
        verify(parcelaService, never()).calcularParcelasDTO(any(), any());
    }

    @Test
    void simular_DeveProcessarCorretamente_QuandoValoresMinimos() {
        // Given
        SimulacaoRequestDTO simulacaoMinima = new SimulacaoRequestDTO(
                new BigDecimal("0.01"),
                1,
                1L,
                null
        );
        
        when(produtoService.buscarProdutoDTOPorCodigo(1L)).thenReturn(produtoDTO);
        when(produtoMapper.dtoToEntity(produtoDTO, true)).thenReturn(produto);
        
        // Mock da persistência da simulação
        doAnswer(invocation -> {
            Simulacao sim = invocation.getArgument(0);
            sim.setNuSimulacao(123L);
            return null;
        }).when(simulacaoRepository).persist(any(Simulacao.class));
        
        List<ParcelaDTO> parcelaMinima = Arrays.asList(
                new ParcelaDTO(1, new BigDecimal("0.01"), new BigDecimal("0.00"), 
                              new BigDecimal("0.01"), new BigDecimal("0.00"))
        );
        
        when(parcelaService.calcularParcelasDTO(any(SimulacaoRequestDTO.class), eq(produtoDTO)))
                .thenReturn(parcelaMinima);

        ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO(
                produto.getCoProduto().intValue(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                produto.getNuMaximoParcelas().intValue()
        );
        when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

        // When
        SimulacaoResponseDTO resultado = simulcaoService.simular(simulacaoMinima);

        // Then
        assertNotNull(resultado);
        verify(produtoService, times(1)).buscarProdutoDTOPorCodigo(1L);
        verify(simulacaoRepository, times(1)).persist(any(Simulacao.class));
        verify(parcelaService, times(1)).calcularParcelasDTO(any(SimulacaoRequestDTO.class), eq(produtoDTO));
    }

    @Test
    void simular_DeveProcessarCorretamente_QuandoValoresMaximos() {
        // Given
        SimulacaoRequestDTO simulacaoMaxima = new SimulacaoRequestDTO(
                new BigDecimal("999999999.99"),
                produto.getNuMaximoParcelas().intValue(),
                1L,
                null
        );
        
        when(produtoService.buscarProdutoDTOPorCodigo(1L)).thenReturn(produtoDTO);
        when(produtoMapper.dtoToEntity(produtoDTO, true)).thenReturn(produto);
        
        // Mock da persistência da simulação
        doAnswer(invocation -> {
            Simulacao sim = invocation.getArgument(0);
            sim.setNuSimulacao(789L);
            return null;
        }).when(simulacaoRepository).persist(any(Simulacao.class));
        
        when(parcelaService.calcularParcelasDTO(any(SimulacaoRequestDTO.class), eq(produtoDTO)))
                .thenReturn(parcelas);

        ProdutoResponseDTO produtoResponseDTO = new ProdutoResponseDTO(
                produto.getCoProduto().intValue(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                produto.getNuMaximoParcelas().intValue()
        );
        when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

        // When
        SimulacaoResponseDTO resultado = simulcaoService.simular(simulacaoMaxima);

        // Then
        assertNotNull(resultado);
        verify(produtoService, times(1)).buscarProdutoDTOPorCodigo(1L);
        verify(simulacaoRepository, times(1)).persist(any(Simulacao.class));
        verify(parcelaService, times(1)).calcularParcelasDTO(any(SimulacaoRequestDTO.class), eq(produtoDTO));
    }
}