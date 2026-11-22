package com.example.catan.service;

import dto.*;
import com.example.catan.model.*;
import com.example.catan.repository.*;
import com.example.catan.service.TopologiaLoader.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepo;
    private final JugadorRepository jugadorRepo;
    private final RecursJugadorRepository recursRepo;
    private final HexagonRepository hexRepo;
    private final VertexRepository vertexRepo;
    private final ArestaRepository arestaRepo;
    private final HexagonVertexRepository hvRepo;
    private final AssentamentRepository assentRepo;
    private final PoblatRepository poblatRepo;
    private final CiutatRepository ciutatRepo;
    private final RecursService recursService;
    private final TopologiaLoader topologia;
    private final Random random = new Random();

    public PartidaService(PartidaRepository partidaRepo, JugadorRepository jugadorRepo,
                          RecursJugadorRepository recursRepo, HexagonRepository hexRepo,
                          VertexRepository vertexRepo, ArestaRepository arestaRepo,
                          HexagonVertexRepository hvRepo, AssentamentRepository assentRepo,
                          PoblatRepository poblatRepo, CiutatRepository ciutatRepo,
                          RecursService recursService, TopologiaLoader topologia) {
        this.partidaRepo = partidaRepo;
        this.jugadorRepo = jugadorRepo;
        this.recursRepo = recursRepo;
        this.hexRepo = hexRepo;
        this.vertexRepo = vertexRepo;
        this.arestaRepo = arestaRepo;
        this.hvRepo = hvRepo;
        this.assentRepo = assentRepo;
        this.poblatRepo = poblatRepo;
        this.ciutatRepo = ciutatRepo;
        this.recursService = recursService;
        this.topologia = topologia;
    }

    
    @Transactional
    public PartidaDTO iniciar(IniciarPartidaRequest req) {
        List<String> noms = req == null ? null : req.jugadors();
        if (noms == null || noms.size() < 2 || noms.size() > 4) {
            throw error(HttpStatus.BAD_REQUEST, "Hi ha d'haver entre 2 i 4 jugadors");
        }
        for (String nom : noms) {
            if (nom == null || nom.isBlank() || nom.length() > 30) {
                throw error(HttpStatus.BAD_REQUEST, "Cada jugador necessita un nom (maxim 30 caracters)");
            }
        }
        int punts = req.puntsPerGuanyar() == null ? 10 : req.puntsPerGuanyar();
        if (punts < 3 || punts > 30) {
            throw error(HttpStatus.BAD_REQUEST, "Els punts per guanyar han d'estar entre 3 i 30");
        }

        Partida p = new Partida();
        p.setPuntsPerGuanyar(punts);
        partidaRepo.save(p);

        List<String> ordre = new ArrayList<>(noms);
        Collections.shuffle(ordre, random);
        ColorJugador[] colors = ColorJugador.values();
        List<Jugador> jugadors = new ArrayList<>();
        for (int i = 0; i < ordre.size(); i++) {
            Jugador j = new Jugador();
            j.setNom(ordre.get(i).trim());
            j.setColor(colors[i]);
            j.setOrdreTorn(i + 1);
            j.setPartida(p);
            jugadorRepo.save(j);
            for (TipusRecurs t : TipusRecurs.values()) recursRepo.save(new RecursJugador(j, t, 0));
            jugadors.add(j);
        }

        List<HexRow> hexRows = topologia.hexagons();
        List<VertexRow> vertexRows = topologia.vertexs();
        List<HexVertexRow> hvRows = topologia.hexagonVertexs();
        List<ArestaRow> arestaRows = topologia.arestes();
        if (hexRows.size() != 19 || vertexRows.size() != 54 || arestaRows.size() != 72 || hvRows.size() != 114) {
            throw new IllegalStateException("Topologia incorrecta als CSV: hexagons=" + hexRows.size()
                    + ", vertexs=" + vertexRows.size() + ", hexagon_vertex=" + hvRows.size()
                    + ", arestes=" + arestaRows.size());
        }

        List<TipusTerreny> terrenys = new ArrayList<>();
        terrenys.addAll(Collections.nCopies(4, TipusTerreny.BOSC));
        terrenys.addAll(Collections.nCopies(4, TipusTerreny.PASTURES));
        terrenys.addAll(Collections.nCopies(4, TipusTerreny.CAMPS));
        terrenys.addAll(Collections.nCopies(3, TipusTerreny.TURONS));
        terrenys.addAll(Collections.nCopies(3, TipusTerreny.MUNTANYES));
        terrenys.add(TipusTerreny.DESERT);
        Collections.shuffle(terrenys, random);

        List<Integer> numeros = new ArrayList<>(List.of(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12));
        Collections.shuffle(numeros, random);
        Iterator<Integer> itNum = numeros.iterator();

        Map<Integer, Hexagon> hexPerCodi = new HashMap<>();
        List<Hexagon> hexagons = new ArrayList<>();
        Hexagon desert = null;
        for (int i = 0; i < hexRows.size(); i++) {
            HexRow row = hexRows.get(i);
            Hexagon h = new Hexagon();
            h.setCodi(row.codi());
            h.setQ(row.q());
            h.setR(row.r());
            h.setTerreny(terrenys.get(i));
            h.setNumero(h.getTerreny() == TipusTerreny.DESERT ? null : itNum.next());
            h.setPartida(p);
            if (h.getTerreny() == TipusTerreny.DESERT) desert = h;
            hexPerCodi.put(h.getCodi(), h);
            hexagons.add(h);
        }
        hexRepo.saveAll(hexagons);

        Map<Integer, Vertex> vertexPerCodi = new HashMap<>();
        for (VertexRow row : vertexRows) {
            Vertex v = new Vertex();
            v.setCodi(row.codi());
            v.setX(row.x());
            v.setY(row.y());
            v.setPartida(p);
            vertexPerCodi.put(v.getCodi(), v);
        }
        vertexRepo.saveAll(vertexPerCodi.values());

        List<HexagonVertex> hvs = new ArrayList<>();
        for (HexVertexRow row : hvRows) {
            HexagonVertex hv = new HexagonVertex();
            hv.setHexagon(requerit(hexPerCodi.get(row.hexagon()), "hexagon", row.hexagon()));
            hv.setVertex(requerit(vertexPerCodi.get(row.vertex()), "vertex", row.vertex()));
            hv.setPosicio(row.posicio());
            hvs.add(hv);
        }
        hvRepo.saveAll(hvs);

        List<Aresta> arestes = new ArrayList<>();
        for (ArestaRow row : arestaRows) {
            Aresta a = new Aresta();
            a.setCodi(row.codi());
            a.setVertexA(requerit(vertexPerCodi.get(row.vertexA()), "vertex", row.vertexA()));
            a.setVertexB(requerit(vertexPerCodi.get(row.vertexB()), "vertex", row.vertexB()));
            a.setPartida(p);
            arestes.add(a);
        }
        arestaRepo.saveAll(arestes);

        p.setLladre(desert);
        p.setJugadorActiu(jugadors.get(0));
        partidaRepo.save(p);

        return toDto(p, jugadors, hexagons);
    }

    
    @Transactional(readOnly = true)
    public List<HexagonTaulerDTO> tauler(Integer idPartida) {
        Partida p = trobar(idPartida);
        Integer lladreId = p.getLladre() == null ? null : p.getLladre().getId();

        Map<Integer, Assentament> perVertex = new HashMap<>();
        for (Assentament a : assentRepo.findByPartida(idPartida)) perVertex.put(a.getVertex().getId(), a);

        Map<Integer, List<HexagonVertex>> hvPerHex = hvRepo.findTaulerByPartida(idPartida).stream()
                .collect(Collectors.groupingBy(hv -> hv.getHexagon().getId()));

        List<HexagonTaulerDTO> res = new ArrayList<>();
        for (Hexagon h : hexRepo.findByPartidaIdOrderByCodi(idPartida)) {
            List<ConstruccioDTO> construccions = new ArrayList<>();
            hvPerHex.getOrDefault(h.getId(), List.of()).stream()
                    .sorted(Comparator.comparingInt(HexagonVertex::getPosicio))
                    .forEach(hv -> {
                        Assentament a = perVertex.get(hv.getVertex().getId());
                        if (a != null) {
                            construccions.add(new ConstruccioDTO(hv.getVertex().getId(), hv.getVertex().getCodi(),
                                    a instanceof Ciutat ? "CIUTAT" : "POBLAT",
                                    a.getPropietari().getId(), a.getPropietari().getNom()));
                        }
                    });
            res.add(new HexagonTaulerDTO(h.getId(), h.getCodi(), h.getTerreny(), h.getNumero(),
                    h.getId().equals(lladreId), construccions));
        }
        return res;
    }

   
    @Transactional
    public TiradaDTO tirarDaus(Integer idPartida) {
        Partida p = trobar(idPartida);
        if (p.getEstat() != EstatPartida.EN_CURS) {
            throw error(HttpStatus.CONFLICT, "La partida no està en curs");
        }
        if (p.isDausTirats()) {
            throw error(HttpStatus.CONFLICT, "El jugador actiu ja ha tirat els daus aquest torn");
        }

        int tirada = (random.nextInt(6) + 1) + (random.nextInt(6) + 1);
        p.setUltimaTirada(tirada);
        p.setDausTirats(true);
        partidaRepo.save(p);

        List<Jugador> jugadors = jugadorRepo.findByPartidaIdOrderByOrdreTorn(idPartida);
        Map<Integer, Map<TipusRecurs, Integer>> rebut = new LinkedHashMap<>();
        for (Jugador j : jugadors) rebut.put(j.getId(), new EnumMap<>(TipusRecurs.class));

        if (tirada != 7) {
            Integer lladreId = p.getLladre() == null ? -1 : p.getLladre().getId();
            acumular(poblatRepo.produccio(idPartida, tirada, lladreId), 1, rebut);
            acumular(ciutatRepo.produccio(idPartida, tirada, lladreId), 2, rebut);
        }

        List<RebutDTO> resum = new ArrayList<>();
        for (Jugador j : jugadors) {
            Map<TipusRecurs, Integer> m = rebut.get(j.getId());
            m.forEach((t, n) -> recursService.afegir(j, t, n));
            resum.add(new RebutDTO(j.getId(), j.getNom(), m));
        }
        return new TiradaDTO(idPartida, tirada, resum);
    }

    private void acumular(List<Object[]> files, int quantitat, Map<Integer, Map<TipusRecurs, Integer>> rebut) {
        for (Object[] f : files) {
            Jugador propietari = (Jugador) f[0];
            TipusRecurs recurs = ((Terreny) f[1]).getRecurs();
            if (recurs != null) rebut.get(propietari.getId()).merge(recurs, quantitat, Integer::sum);
        }
    }

  
    @Transactional(readOnly = true)
    public int calcularPunts(Integer jugadorId) {
        return (int) poblatRepo.countByPropietariId(jugadorId) + 2 * (int) ciutatRepo.countByPropietariId(jugadorId);
    }

    @Transactional
    public Jugador comprovarVictoria(Partida p) {
        Jugador millor = null;
        int maxPunts = -1;
        for (Jugador j : jugadorRepo.findByPartidaIdOrderByOrdreTorn(p.getId())) {
            int punts = calcularPunts(j.getId());
            if (punts > maxPunts) {
                maxPunts = punts;
                millor = j;
            }
        }
        if (millor != null && maxPunts >= p.getPuntsPerGuanyar()) {
            p.setEstat(EstatPartida.FINALITZADA);
            p.setGuanyador(millor);
            partidaRepo.save(p);
            return millor;
        }
        return null;
    }

    private Partida trobar(Integer id) {
        return partidaRepo.findById(id)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND, "La partida " + id + " no existeix"));
    }

    private <T> T requerit(T valor, String tipus, int codi) {
        if (valor == null) throw new IllegalStateException("Els CSV fan referencia a un " + tipus + " inexistent: " + codi);
        return valor;
    }

    private ResponseStatusException error(HttpStatus status, String missatge) {
        return new ResponseStatusException(status, missatge);
    }

    private JugadorResum resum(Jugador j) {
        return j == null ? null : new JugadorResum(j.getId(), j.getNom(), j.getColor(), j.getOrdreTorn());
    }

    private PartidaDTO toDto(Partida p, List<Jugador> jugadors, List<Hexagon> hexagons) {
        Integer lladreId = p.getLladre() == null ? null : p.getLladre().getId();
        return new PartidaDTO(p.getId(), p.getEstat(), p.getRonda(), p.getPuntsPerGuanyar(),
                p.getUltimaTirada(), resum(p.getJugadorActiu()),
                jugadors.stream().map(this::resum).toList(),
                hexagons.stream().map(h -> new HexagonDTO(h.getId(), h.getCodi(), h.getTerreny(),
                        h.getNumero(), h.getId().equals(lladreId))).toList());
    }
}
