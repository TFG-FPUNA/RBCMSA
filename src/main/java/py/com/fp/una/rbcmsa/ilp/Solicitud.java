/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package py.com.fp.una.rbcmsa.ilp;

/**
 *
 * @author Ivan
 */
public class Solicitud {
    private int id;
    private int tiempo;
    private String origen;
    private String destino;
    private Double velocidad;

    public Solicitud(int id,String origen, String destino, Double velocidad, int tiempo) {
        this.id = id;
        this.tiempo = tiempo;
        this.origen = origen;
        this.destino = destino;
        this.velocidad = velocidad;
    }
    
     public Solicitud(String origen, String destino, Double velocidad) {
          this.origen = origen;
        this.destino = destino;
        this.velocidad = velocidad;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Double getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(Double velocidad) {
        this.velocidad = velocidad;
    }
     public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
     public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    @Override
    public String toString() {
        return "Solicitud{" + "origen=" + origen + ", destino=" + destino + ", velocidad=" + velocidad + '}';
    }

}
