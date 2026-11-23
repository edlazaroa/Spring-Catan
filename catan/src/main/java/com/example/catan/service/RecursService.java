package com.example.catan.service;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.catan.model.Jugador;
import com.example.catan.model.RecursJugador;
import com.example.catan.model.TipusRecurs;
import com.example.catan.model.TipusTerreny;
import com.example.catan.repository.RecursJugadorRepository;

@Service
public class RecursService {

    public static final Map<TipusRecurs, Integer> COST_CARRETERA =
            Map.of(TipusRecurs.FUSTA, 1, TipusRecurs.MAO, 1);
    public static final Map<TipusRecurs, Integer> COST_POBLAT =
            Map.of(TipusRecurs.FUSTA, 1, TipusRecurs.MAO, 1, TipusRecurs.LLANA, 1, TipusRecurs.BLAT, 1);
    public static final Map<TipusRecurs, Integer> COST_CIUTAT =
            Map.of(TipusRecurs.BLAT, 2, TipusRecurs.MINERAL, 3);

    public static TipusRecurs recursDe(TipusTerreny t) {
        return switch (t) {
            case BOSC -> TipusRecurs.FUSTA;
            case TURONS -> TipusRecurs.MAO;
            case PASTURES -> TipusRecurs.LLANA;
            case CAMPS -> TipusRecurs.BLAT;
            case MUNTANYES -> TipusRecurs.MINERAL;
            case DESERT -> null;
        };
    }

    private final RecursJugadorRepository repo;

    public RecursService(RecursJugadorRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public Map<TipusRecurs, Integer> inventari(Integer jugadorId) {
        Map<TipusRecurs, Integer> res = new EnumMap<>(TipusRecurs.class);
        for (TipusRecurs t : TipusRecurs.values()) res.put(t, 0);
        for (RecursJugador r : repo.findByJugadorId(jugadorId)) res.put(r.getTipus(), r.getQuantitat());
        return res;
    }

    @Transactional(readOnly = true)
    public boolean pot(Jugador j, Map<TipusRecurs, Integer> cost) {
        Map<TipusRecurs, Integer> inv = inventari(j.getId());
        return cost.entrySet().stream().allMatch(e -> inv.get(e.getKey()) >= e.getValue());
    }

    @Transactional
    public void pagar(Jugador j, Map<TipusRecurs, Integer> cost) {
        cost.forEach((t, n) -> afegir(j, t, -n));
    }

    @Transactional
    public void afegir(Jugador j, TipusRecurs t, int n) {
        RecursJugador r = repo.findByJugadorIdAndTipus(j.getId(), t).orElseGet(() -> crear(j, t));
        r.setQuantitat(r.getQuantitat() + n);
        repo.save(r);
    }

    public RecursJugador crear(Jugador j, TipusRecurs t) {
        RecursJugador r = new RecursJugador();
        r.setJugador(j);
        r.setTipus(t);
        r.setQuantitat(0);
        return r;
    }
}