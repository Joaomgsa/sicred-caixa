package com.br.domain.mapper;

import com.br.application.dto.response.ParcelaDTO;
import com.br.domain.model.Parcela;

public class ParcelaMapper {

    public static Parcela toEntity(Parcela parcela){
        return null;
    }


    public ParcelaDTO toDTO(Parcela parcela){
        return new ParcelaDTO(
                parcela.getNuParcela(),
                parcela.getVrSaldodevedor(),
                parcela.getVrJuros(),
                //TODO: tirar essa conta daqui
                parcela.getVrPrestacao().subtract(parcela.getVrJuros()),
                parcela.getVrSaldodevedor().subtract(parcela.getVrPrestacao())
        );
    }
}
