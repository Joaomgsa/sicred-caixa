package com.br.domain.service;

import com.br.application.dto.ProdutoDTO;
import com.br.application.dto.request.ProdutoRequestDTO;
import com.br.application.dto.response.ProdutoResponseDTO;
import com.br.domain.mapper.ProdutoMapper;
import com.br.domain.model.Produto;
import com.br.domain.repository.ProdutoRepository;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceImplTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    private Produto produto;
    private ProdutoRequestDTO produtoRequestDTO;

    private ProdutoResponseDTO produtoResponseDTO;
    private ProdutoDTO produtoDTO;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setCoProduto(1L);
        produto.setNoProduto("Crédito Pessoal");
        produto.setPcTaxaJuros(new BigDecimal("1.5000"));
        produto.setNuMaximoParcelas((short) 12);
        produto.setStAtivo(true);

        produtoRequestDTO = new ProdutoRequestDTO(
                1L,
                "Crédito Pessoal",
                new BigDecimal("1.5000"),
                (short) 12
        );

        produtoResponseDTO = new ProdutoResponseDTO(
                produto.getCoProduto().intValue(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                produto.getNuMaximoParcelas().intValue()
        );

        produtoDTO = new ProdutoDTO(
                produto.getCoProduto(),
                produto.getNoProduto(),
                produto.getPcTaxaJuros(),
                produto.getNuMaximoParcelas()
        );
    }

    // Testes para listarProdutos()

    @Test
    void listarProdutos_DeveRetornarListaDeProdutosAtivos_QuandoExistemProdutos() {
        // Given
        List<Produto> produtos = Arrays.asList(produto);
        when(produtoRepository.buscarProdutosAtivos()).thenReturn(produtos);
        when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

        // When
        List<ProdutoResponseDTO> resultado = produtoService.listarProdutos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(produto.getCoProduto().intValue(), resultado.get(0).id());
        assertEquals(produto.getNoProduto(), resultado.get(0).nome());
        verify(produtoRepository, times(1)).buscarProdutosAtivos();
        verify(produtoMapper, times(1)).toResponseDTO(produto);
    }

    @Test
    void listarProdutos_DeveRetornarListaVazia_QuandoNaoExistemProdutosAtivos() {
        // Given
        when(produtoRepository.buscarProdutosAtivos()).thenReturn(Collections.emptyList());

        // When
        List<ProdutoResponseDTO> resultado = produtoService.listarProdutos();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, times(1)).buscarProdutosAtivos();
    }

    // Testes para buscarProdutoPorCodigo()

    @Test
    void buscarProdutoPorCodigo_DeveRetornarProdutoResponseDTO_QuandoProdutoExiste() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(produto);
        when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);

        // When
        ProdutoResponseDTO resultado = produtoService.buscarProdutoPorCodigo(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(produto.getCoProduto().intValue(), resultado.id());
        assertEquals(produto.getNoProduto(), resultado.nome());
        assertEquals(produto.getPcTaxaJuros(), resultado.taxaJurosAnual());
        assertEquals(produto.getNuMaximoParcelas().intValue(), resultado.prazoMaximoMeses());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoMapper, times(1)).toResponseDTO(produto);
    }

    @Test
    void buscarProdutoPorCodigo_DeveRetornarNull_QuandoProdutoNaoExiste() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(null);

        // When
        ProdutoResponseDTO resultado = produtoService.buscarProdutoPorCodigo(1L);

        // Then
        assertNull(resultado);
        verify(produtoRepository, times(1)).findById(1L);
    }

    // Testes para buscarProdutoDTOPorCodigo()

    @Test
    void buscarProdutoDTOPorCodigo_DeveRetornarProdutoDTO_QuandoProdutoExiste() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(produto);
        when(produtoMapper.toDTO(produto)).thenReturn(produtoDTO);

        // When
        ProdutoDTO resultado = produtoService.buscarProdutoDTOPorCodigo(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(produto.getCoProduto(), resultado.coProduto());
        assertEquals(produto.getNoProduto(), resultado.noProduto());
        assertEquals(produto.getPcTaxaJuros(), resultado.pcTaxaJuros());
        assertEquals(produto.getNuMaximoParcelas(), resultado.nuMaximoParcelas());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoMapper, times(1)).toDTO(produto);
    }

    @Test
    void buscarProdutoDTOPorCodigo_DeveRetornarNull_QuandoProdutoNaoExiste() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(null);

        // When
        ProdutoDTO resultado = produtoService.buscarProdutoDTOPorCodigo(1L);

        // Then
        assertNull(resultado);
        verify(produtoRepository, times(1)).findById(1L);
    }

    // Testes para salvarProduto()

    @Test
    void salvarProduto_DevePersistirERetornarProduto_QuandoDadosValidos() {
        // Given
        Produto novoProduto = new Produto();
        when(produtoMapper.responseToEntity(produtoRequestDTO, true)).thenReturn(novoProduto);
        when(produtoMapper.toResponseDTO(novoProduto)).thenReturn(produtoResponseDTO);
        
        doAnswer(invocation -> {
            Produto produto = invocation.getArgument(0);
            produto.setCoProduto(1L); // Simula a geração do ID após persistência
            return null;
        }).when(produtoRepository).persist(novoProduto);

        // When
        ProdutoResponseDTO resultado = produtoService.salvarProduto(produtoRequestDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(produtoRequestDTO.nome(), resultado.nome());
        assertEquals(produtoRequestDTO.taxaJurosAnual(), resultado.taxaJurosAnual());
        assertEquals(produtoRequestDTO.prazoMaximoMeses().intValue(), resultado.prazoMaximoMeses());
        verify(produtoMapper, times(1)).responseToEntity(produtoRequestDTO, true);
        verify(produtoRepository, times(1)).persist(novoProduto);
        verify(produtoMapper, times(1)).toResponseDTO(novoProduto);
    }

    @Test
    void salvarProduto_DeveLancarExcecao_QuandoFalhaAoPersistir() {
        // Given
        Produto novoProduto = new Produto();
        when(produtoMapper.responseToEntity(produtoRequestDTO, true)).thenReturn(novoProduto);
        doThrow(new RuntimeException("Erro de persistência")).when(produtoRepository).persist(novoProduto);

        // When & Then
        assertThrows(RuntimeException.class, () -> produtoService.salvarProduto(produtoRequestDTO));
        verify(produtoMapper, times(1)).responseToEntity(produtoRequestDTO, true);
        verify(produtoRepository, times(1)).persist(novoProduto);
    }

    // Testes para atualizarProduto()

    @Test
    void atualizarProduto_DeveAtualizarERetornarProduto_QuandoProdutoExiste() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(produto);
        when(produtoMapper.toResponseDTO(produto)).thenReturn(produtoResponseDTO);
        doNothing().when(produtoRepository).persist(produto);

        // When
        ProdutoResponseDTO resultado = produtoService.atualizarProduto(produtoRequestDTO);

        // Then
        assertNotNull(resultado);
        assertEquals(produtoRequestDTO.nome(), produto.getNoProduto());
        assertEquals(produtoRequestDTO.taxaJurosAnual(), produto.getPcTaxaJuros());
        assertEquals(produtoRequestDTO.prazoMaximoMeses(), produto.getNuMaximoParcelas());
        assertTrue(produto.getStAtivo());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).persist(produto);
        verify(produtoMapper, times(1)).toResponseDTO(produto);
    }

    @Test
    void atualizarProduto_DeveRetornarNull_QuandoProdutoNaoExiste() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(null);

        // When
        ProdutoResponseDTO resultado = produtoService.atualizarProduto(produtoRequestDTO);

        // Then
        assertNull(resultado);
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, never()).persist(any(Produto.class));
    }

    @Test
    void atualizarProduto_DeveLancarExcecao_QuandoFalhaAoPersistir() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(produto);
        doThrow(new RuntimeException("Erro de persistência")).when(produtoRepository).persist(produto);

        // When & Then
        assertThrows(RuntimeException.class, () -> produtoService.atualizarProduto(produtoRequestDTO));
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).persist(produto);
    }

    // Testes para deletarProduto()

    @Test
    void deletarProduto_DeveDesativarProduto_QuandoProdutoExiste() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(produto);
        doNothing().when(produtoRepository).persist(produto);

        // When
        produtoService.deletarProduto(1L);

        // Then
        assertFalse(produto.getStAtivo());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).persist(produto);
    }

    @Test
    void deletarProduto_DeveLancarNotFoundException_QuandoProdutoNaoExiste() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(null);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> produtoService.deletarProduto(1L));
        assertEquals("Produto não encontrado para o id: 1", exception.getMessage());
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, never()).persist(any(Produto.class));
    }

    @Test
    void deletarProduto_DeveLancarExcecao_QuandoFalhaAoPersistir() {
        // Given
        when(produtoRepository.findById(1L)).thenReturn(produto);
        doThrow(new RuntimeException("Erro de persistência")).when(produtoRepository).persist(produto);

        // When & Then
        assertThrows(RuntimeException.class, () -> produtoService.deletarProduto(1L));
        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).persist(produto);
    }
}