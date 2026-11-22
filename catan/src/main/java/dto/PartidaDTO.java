package dto;
import java.util.List;

import com.example.catan.model.EstatPartida;

public record PartidaDTO(Integer id, EstatPartida estat, int ronda, int puntsPerGuanyar, Integer ultimaTirada, JugadorResum jugadorActiu, List<JugadorResum> jugadors, List<HexagonDTO> hexagons) { 
	
}