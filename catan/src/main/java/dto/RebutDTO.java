package dto;
import java.util.Map;

import com.example.catan.model.TipusRecurs;

public record RebutDTO(Integer jugadorId, String nom, Map<TipusRecurs, Integer> rebut) { 
	
}