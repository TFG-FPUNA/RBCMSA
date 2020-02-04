/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.grafos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import py.com.fp.una.rbcmsa.grafos.model.Arista;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.grafos.model.Vertice;

/**
 *
 * @author Richard
 */
public class OperacionesGrafos {

    public Grafo cargaGrafo(int matrizAdyacencia[][], int cantidadSP) {
        List<Vertice> vertices = new ArrayList<>();
        HashMap<String,Arista> aristas = new HashMap<>();
        for (int i = 0; i < matrizAdyacencia.length; i++) {
            Vertice vertice = this.crearVertice(i);
            vertices.add(vertice);
            for (int j = 0; j < matrizAdyacencia[0].length; j++) {
                if (matrizAdyacencia[i][j] != 0) {
                    String identificador = i+"-"+j;
                    Arista arista = this.crearArista(i, j, cantidadSP, matrizAdyacencia[i][j], identificador);
                    aristas.put(identificador, arista);

                }
            }
        }
        
        Grafo grafo = new Grafo();
        grafo.setVertices(vertices);
        grafo.setAristas(aristas);
        
        return grafo;
    }

    private Vertice crearVertice(int identificador) {
        Vertice vertice = new Vertice();
        vertice.setIdentificador(identificador + "");
        return vertice;
    }
    
    private Arista crearArista(int verticeOrigen, int verticeDestino, int cantidadSP, int distancia, String identificador) {
        Arista arista = new Arista(cantidadSP);
        arista.setIdentificador(identificador);
        arista.setVerticeOrigen(verticeOrigen+"");
        arista.setVerticeDestino(verticeDestino+"");
        arista.setDistancia(distancia);
        return arista;
    }
}
