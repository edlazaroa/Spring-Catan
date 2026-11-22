package dto;

import java.util.Map;

import com.example.catan.model.ColorJugador;
import com.example.catan.model.TipusRecurs;

public record JugadorRecursosDTO(Integer id, String nom, ColorJugador color, int ordreTorn, Integer partidaId, Map<TipusRecurs, Integer> recursos) { 
	
}