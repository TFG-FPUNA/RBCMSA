/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.grafos.model;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Richard
 */
public class Grafo {
    private List<Vertice> vertices;
    private HashMap<String, Arista> aristas;

    public List<Vertice> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertice> vertices) {
        this.vertices = vertices;
    }

    public HashMap<String, Arista> getAristas() {
        return aristas;
    }

    public void setAristas(HashMap<String, Arista> aristas) {
        this.aristas = aristas;
    }

    @Override
    public String toString() {
        return "Grafo{" + "vertices=" + vertices + ", aristas=" + aristas + '}';
    }

    
    
}
