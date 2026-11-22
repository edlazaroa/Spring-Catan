package com.example.catan.dto;

import java.util.List;

import com.example.catan.model.TipusTerreny;

public record HexagonTaulerDTO(Integer id, int codi, TipusTerreny terreny, Integer numero, boolean lladre, List<ConstruccioDTO> construccions) { 
	
}
