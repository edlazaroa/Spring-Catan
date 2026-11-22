package com.example.catan.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.catan.dto.JugadorRecursosDTO;
import com.example.catan.model.Aresta;
import com.example.catan.model.Carretera;
import com.example.catan.model.Ciutat;
import com.example.catan.model.Construccio;
import com.example.catan.model.EstatPartida;
import com.example.catan.model.HexagonVertex;
import com.example.catan.model.Jugador;
import com.example.catan.model.Partida;
import com.example.catan.model.Poblat;
import com.example.catan.model.TipusRecurs;
import com.example.catan.model.Vertex;
import com.example.catan.repository.ArestaRepository;
import com.example.catan.repository.AssentamentRepository;
import com.example.catan.repository.CarreteraRepository;
import com.example.catan.repository.CiutatRepository;
import com.example.catan.repository.HexagonVertexRepository;
import com.example.catan.repository.JugadorRepository;
import com.example.catan.repository.PartidaRepository;
import com.example.catan.repository.PoblatRepository;
import com.example.catan.repository.VertexRepository;

@Service
public class JugadorService {

    private static final int MAX_CARRETERES = 15;
    private static final int MAX_POBLATS = 5;
    private static final int MAX_CIUTATS = 4;

    private final JugadorRepository jugadorRepo;
    private final PartidaRepository partidaRepo;
    private final VertexRepository vertexRepo;
    private final ArestaRepository arestaRepo;
    private final HexagonVertexRepository hvRepo;
    private final AssentamentRepository assentRepo;
    private final PoblatRepository poblatRepo;
    private final CiutatRepository ciutatRepo;
    private final CarreteraRepository carreteraRepo;
    private final RecursService recursService;
    private final PartidaService partidaService;

    public JugadorService(JugadorRepository jugadorRepo, PartidaRepository partidaRepo,
                          VertexRepository vertexRepo, ArestaRepository arestaRepo,
                          HexagonVertexRepository hvRepo, AssentamentRepository assentRepo,
                          PoblatRepository poblatRepo, CiutatRepository ciutatRepo,
                          CarreteraRepository carreteraRepo, RecursService recursService,
                          PartidaService partidaService) {
        this.jugadorRepo = jugadorRepo;
        this.partidaRepo = partidaRepo;
        this.vertexRepo = vertexRepo;
        this.arestaRepo = arestaRepo;
        this.hvRepo = hvRepo;
        this.assentRepo = assentRepo;
        this.poblatRepo = poblatRepo;
        this.ciutatRepo = ciutatRepo;
        this.carreteraRepo = carreteraRepo;
        this.recursService = recursService;
        this.partidaService = partidaService;
    }

    @Transactional(readOnly = true)
    public Optional<JugadorRecursosDTO> recursos(Integer idJugador) {
        return jugadorRepo.findById(idJugador).map(j -> new JugadorRecursosDTO(
                j.getId(), j.getNom(), j.getColor(), j.getOrdreTorn(), j.getPartida().getId(),
                recursService.inventari(j.getId())));
    }

  
    @Transactional
    public String construir(Integer idJugador, Construccio tipus, Integer posicio) {
        Jugador j = jugadorRepo.findById(idJugador).orElse(null);
        if (j == null) return "El jugador no existeix";
        Partida p = j.getPartida();

        if (p.getEstat() == EstatPartida.FINALITZADA) {
            return "La partida no està en curs";
        }
        if (p.getJugadorActiu() == null || !p.getJugadorActiu().getId().equals(j.getId())) {
            return "No és el teu torn";
        }
        boolean preparacio = p.getEstat() == EstatPartida.PREPARACIO;
        if (!preparacio && !p.isDausTirats()) {
            return "Daus no tirats: has de tirar els daus abans de construir";
        }
        if (!preparacio && p.isLladrePendent()) {
            return "Has de moure el lladre abans de fer cap altra acció";
        }

        return switch (tipus) {
            case CARRETERA -> construirCarretera(j, p, posicio, preparacio);
            case POBLAT -> construirPoblat(j, p, posicio, preparacio);
            case CIUTAT -> millorarACiutat(j, p, posicio, preparacio);
        };
    }

    private String construirCarretera(Jugador j, Partida p, Integer posicio, boolean preparacio) {
        Aresta a = arestaRepo.findById(posicio).orElse(null);
        if (a == null) return "L'aresta no existeix";
        if (!a.getPartida().getId().equals(p.getId())) return "Posició d'una altra partida";
        if (carreteraRepo.existsByArestaId(a.getId())) return "Posició ocupada: ja hi ha una carretera en aquesta aresta";

        Integer va = a.getVertexA().getId();
        Integer vb = a.getVertexB().getId();
        long carreteres = carreteraRepo.countByPropietariId(j.getId());

        if (preparacio) {
            long poblats = poblatRepo.countByPropietariId(j.getId());
            if (poblats < p.getRonda()) {
                return "No connectat: primer has de col·locar el poblat d'aquest torn";
            }
            if (carreteres >= p.getRonda()) {
                return "Límit assolit: ja has col·locat la carretera d'aquest torn";
            }
            if (poblatRepo.countSenseCarretera(j.getId(), va, vb) == 0) {
                return "No connectat: la carretera ha de tocar el poblat que acabes de col·locar";
            }
        } else {
            if (assentRepo.countPropisEnVertexs(j.getId(), va, vb) == 0
                    && carreteraRepo.countConnectades(j.getId(), va, vb) == 0) {
                return "No connectat: l'aresta ha de tocar una construcció o carretera teva";
            }
            if (!recursService.pot(j, RecursService.COST_CARRETERA)) {
                return "Sense recursos: una carretera costa 1 FUSTA + 1 MAO";
            }
            if (carreteres >= MAX_CARRETERES) {
                return "Límit assolit: màxim " + MAX_CARRETERES + " carreteres";
            }
            recursService.pagar(j, RecursService.COST_CARRETERA);
        }

        Carretera c = new Carretera();
        c.setPropietari(j);
        c.setAresta(a);
        carreteraRepo.save(c);
        return "Carretera construïda a l'aresta " + a.getId();
    }

    private String construirPoblat(Jugador j, Partida p, Integer posicio, boolean preparacio) {
        Vertex v = vertexRepo.findById(posicio).orElse(null);
        if (v == null) return "El vèrtex no existeix";
        if (!v.getPartida().getId().equals(p.getId())) return "Posició d'una altra partida";
        if (assentRepo.existsByVertexId(v.getId())) return "Posició ocupada: ja hi ha un assentament en aquest vèrtex";
        if (assentRepo.countAdjacents(v.getId()) > 0) {
            return "Regla de la distància: hi ha una construcció en un vèrtex adjacent";
        }

        long poblats = poblatRepo.countByPropietariId(j.getId());
        if (preparacio) {
            if (poblats >= p.getRonda()) {
                return "Límit assolit: ja has col·locat el poblat d'aquest torn";
            }
        } else {
            if (carreteraRepo.countAlVertex(j.getId(), v.getId()) == 0) {
                return "No connectat: el vèrtex ha de tenir una carretera teva";
            }
            if (!recursService.pot(j, RecursService.COST_POBLAT)) {
                return "Sense recursos: un poblat costa 1 FUSTA + 1 MAO + 1 LLANA + 1 BLAT";
            }
            if (poblats >= MAX_POBLATS) {
                return "Límit assolit: màxim " + MAX_POBLATS + " poblats";
            }
            recursService.pagar(j, RecursService.COST_POBLAT);
        }

        Poblat poblat = new Poblat();
        poblat.setPropietari(j);
        poblat.setVertex(v);
        poblatRepo.save(poblat);

        if (preparacio && poblats + 1 == 2) {
            for (HexagonVertex hv : hvRepo.findByVertex(v.getId())) {
                TipusRecurs r = hv.getHexagon().getTerreny().getRecurs();
                if (r != null) recursService.afegir(j, r, 1);
            }
        }
        return "Poblat construït al vèrtex " + v.getId() + victoria(p);
    }

    private String millorarACiutat(Jugador j, Partida p, Integer posicio, boolean preparacio) {
        Vertex v = vertexRepo.findById(posicio).orElse(null);
        if (v == null) return "El vèrtex no existeix";
        if (!v.getPartida().getId().equals(p.getId())) return "Posició d'una altra partida";
        if (preparacio) return "No es pot millorar a ciutat durant la preparació de la partida";

        Poblat poblat = poblatRepo.findByVertexIdAndPropietariId(v.getId(), j.getId()).orElse(null);
        if (poblat == null) return "No hi ha cap poblat teu per millorar en aquest vèrtex";
        if (!recursService.pot(j, RecursService.COST_CIUTAT)) {
            return "Sense recursos: una ciutat costa 2 BLAT + 3 MINERAL";
        }
        if (ciutatRepo.countByPropietariId(j.getId()) >= MAX_CIUTATS) {
            return "Límit assolit: màxim " + MAX_CIUTATS + " ciutats";
        }
        recursService.pagar(j, RecursService.COST_CIUTAT);

        poblatRepo.delete(poblat);
        poblatRepo.flush();
        Ciutat ciutat = new Ciutat();
        ciutat.setPropietari(j);
        ciutat.setVertex(v);
        ciutatRepo.save(ciutat);
        return "Ciutat construïda al vèrtex " + v.getId() + victoria(p);
    }

    private String victoria(Partida p) {
        Jugador guanyador = partidaService.comprovarVictoria(p);
        return guanyador == null ? "" : ". Partida finalitzada: guanya " + guanyador.getNom();
    }

    
    @Transactional
    public String passarTorn(Integer idJugador) {
        Jugador j = jugadorRepo.findById(idJugador).orElse(null);
        if (j == null) return "El jugador no existeix";
        Partida p = j.getPartida();

        if (p.getEstat() == EstatPartida.FINALITZADA) return "La partida no està en curs";
        if (p.getJugadorActiu() == null || !p.getJugadorActiu().getId().equals(j.getId())) {
            return "No és el teu torn";
        }

        long n = jugadorRepo.countByPartidaId(p.getId());
        int ordre = j.getOrdreTorn();
        int seguent;
        String extra = "";

        if (p.getEstat() == EstatPartida.PREPARACIO) {
            long poblats = poblatRepo.countByPropietariId(j.getId());
            long carreteres = carreteraRepo.countByPropietariId(j.getId());
            if (poblats < p.getRonda() || carreteres < p.getRonda()) {
                return "No pots passar el torn: has de col·locar el poblat i la carretera d'aquest torn";
            }
            if (p.getRonda() == 1) {
                if (ordre < n) {
                    seguent = ordre + 1;
                } else {
                    p.setRonda(2);
                    seguent = ordre;
                }
            } else if (ordre > 1) {
                seguent = ordre - 1;
            } else {
                p.setEstat(EstatPartida.EN_CURS);
                p.setRonda(1);
                seguent = 1;
                extra = ". La preparació ha acabat: la partida passa a EN_CURS";
            }
        } else {
            if (!p.isDausTirats()) return "No pots passar el torn: has de tirar els daus primer";
            if (p.isLladrePendent()) return "Amb el lladre pendent no es pot passar el torn";
            seguent = (int) (ordre % n) + 1;
            if (seguent == 1) p.setRonda(p.getRonda() + 1);
        }

        Jugador proper = jugadorRepo.findByPartidaIdAndOrdreTorn(p.getId(), seguent).orElseThrow();
        p.setJugadorActiu(proper);
        p.setDausTirats(false);
        partidaRepo.save(p);
        return "Torn passat. Ara juga " + proper.getNom() + " (ronda " + p.getRonda() + ")" + extra;
    }
}
