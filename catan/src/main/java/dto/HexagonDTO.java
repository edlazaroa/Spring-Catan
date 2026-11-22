package dto;

import com.example.catan.model.TipusTerreny;

public record HexagonDTO(Integer id, int codi, TipusTerreny terreny, Integer numero, boolean lladre) { 
	
}
