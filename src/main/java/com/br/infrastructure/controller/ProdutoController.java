package com.br.infrastructure.controller;


import com.br.application.dto.request.ProdutoRequestDTO;
import com.br.domain.service.ProdutoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/produtos")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoController {

    @Inject
    ProdutoService produtoService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarProdutos() {
        return Response.ok(produtoService.listarProdutos()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarProduto(
            @PathParam("id") Long id
    ) {
        return Response.ok(produtoService.buscarProdutoPorCodigo(id)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response salvarProduto(@Valid ProdutoRequestDTO request) {
        return Response.ok(produtoService.salvarProduto(request)).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizarProduto(ProdutoRequestDTO request, @PathParam("id") Long id){
        request = new ProdutoRequestDTO(id, request.nome(), request.taxaJurosAnual(), request.prazoMaximoMeses());
        return Response.accepted(produtoService.atualizarProduto(request)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response excluirProduto(@PathParam("id") Long id){
        produtoService.deletarProduto(id);
        return Response.noContent().build();
    }
}
