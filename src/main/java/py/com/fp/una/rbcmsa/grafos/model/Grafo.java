/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.grafos.model;

import java.util.List;

/**
 *
 * @author Richard
 */
public class Grafo {
    private List<Vertice> vertices;
    private List<Arista> atistas;

    public List<Vertice> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertice> vertices) {
        this.vertices = vertices;
    }

    public List<Arista> getAtistas() {
        return atistas;
    }

    public void setAtistas(List<Arista> atistas) {
        this.atistas = atistas;
    }

    @Override
    public String toString() {
        return "Grafo{" + "vertices=" + vertices + ", atistas=" + atistas + '}';
    }
    
}
