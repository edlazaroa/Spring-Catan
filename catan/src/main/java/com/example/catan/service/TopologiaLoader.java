package com.example.catan.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class TopologiaLoader {

    private static final String DIR = "topologia/";

    public record HexRow(int codi, Integer q, Integer r) { }
    public record VertexRow(int codi, Double x, Double y) { }
    public record HexVertexRow(int hexagon, int vertex, int posicio) { }
    public record ArestaRow(int codi, int vertexA, int vertexB) { }

    public List<HexRow> hexagons() {
        List<HexRow> res = new ArrayList<>();
        for (String[] t : llegir("hexagons.csv")) {
            res.add(new HexRow(Integer.parseInt(t[0]), enter(t, 1), enter(t, 2)));
        }
        return res;
    }

    public List<VertexRow> vertexs() {
        List<VertexRow> res = new ArrayList<>();
        for (String[] t : llegir("vertexs.csv")) {
            res.add(new VertexRow(Integer.parseInt(t[0]), decimal(t, 1), decimal(t, 2)));
        }
        return res;
    }

    public List<HexVertexRow> hexagonVertexs() {
        List<HexVertexRow> res = new ArrayList<>();
        for (String[] t : llegir("hexagon_vertex.csv")) {
            res.add(new HexVertexRow(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2])));
        }
        return res;
    }

    public List<ArestaRow> arestes() {
        List<ArestaRow> res = new ArrayList<>();
        int n = 1;
        for (String[] t : llegir("arestes.csv")) {
            if (t.length >= 3) {
                res.add(new ArestaRow(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2])));
            } else {
                res.add(new ArestaRow(n, Integer.parseInt(t[0]), Integer.parseInt(t[1])));
            }
            n++;
        }
        return res;
    }

    private Integer enter(String[] t, int i) {
        return (i < t.length && !t[i].isEmpty()) ? Integer.valueOf(t[i]) : null;
    }

    private Double decimal(String[] t, int i) {
        return (i < t.length && !t[i].isEmpty()) ? Double.valueOf(t[i]) : null;
    }

    private List<String[]> llegir(String fitxer) {
        List<String[]> files = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ClassPathResource(DIR + fitxer).getInputStream(), StandardCharsets.UTF_8))) {
            String linia;
            while ((linia = br.readLine()) != null) {
                linia = linia.replace("\uFEFF", "").trim();
                if (linia.isEmpty()) continue;
                String[] t = linia.split("[,;\\t]");
                for (int i = 0; i < t.length; i++) t[i] = t[i].trim();
                if (!t[0].matches("-?\\d+")) continue; // capcalera
                files.add(t);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No es pot llegir " + DIR + fitxer, e);
        }
        return files;
    }
}
