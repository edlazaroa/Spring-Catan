package com.example.catan.dto;

import java.util.List;

public record IniciarPartidaRequest(List<String> jugadors, Integer puntsPerGuanyar) { 
	
}
