package com.br.infrastructure.controller;


import com.br.application.dto.request.SimulacaoRequestDTO;
import com.br.domain.service.SimulacaoService;
import io.vertx.core.http.HttpServerResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/simular")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoController {

    @Inject
    HttpServerResponse httpServerResponse;
    @Inject
    private SimulacaoService simulacaoService;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response simular(SimulacaoRequestDTO request) {
        return Response.ok(simulacaoService.).build();
    }
}
