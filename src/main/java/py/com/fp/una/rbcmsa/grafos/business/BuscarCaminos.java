/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.grafos.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import py.com.fp.una.rbcmsa.grafos.model.Camino;
import py.com.fp.una.rbcmsa.grafos.model.Rutas;

/**
 *
 * @author Richard
 */
public class BuscarCaminos {

    public HashMap<String, Rutas> busqueda(int matrizAdyacencia[][]) throws CloneNotSupportedException {

        List<Rutas> rutas = new ArrayList<>();

        for (int i = 0; i < matrizAdyacencia.length; i++) {
            for (int j = 0; j < matrizAdyacencia[0].length; j++) {
                if (matrizAdyacencia[i][j] != 0) {
                    Rutas nuevaRuta = agregarRutaNueva(i, j, matrizAdyacencia[i][j]);
                    rutas.add(nuevaRuta);
                    while (true) {
                        List<Rutas> rutasExpandidas = generarExpanciones(rutas, i, j, matrizAdyacencia);
                        rutas.addAll(rutasExpandidas);
                        if (rutasExpandidas.isEmpty()) {
                            break;
                        }
                    }
                    desmarcarRuta(rutas);
                }

            }
        }
        HashMap<String, Rutas> rutasCompletas = new HashMap();
        rutasCompletas = mapearRutas(rutas);

        return rutasCompletas;
    }

    private HashMap<String, Rutas> mapearRutas(List<Rutas> rutas) throws CloneNotSupportedException {

        HashMap<String, Rutas> rutasCompletas = new HashMap();

        for (Rutas ruta : rutas) {
            if (rutasCompletas.containsKey(ruta.getOrigen() + "-" + ruta.getDestino())) {

                List<Camino> caminosAgregados = agregarCamino(rutasCompletas.get(ruta.getOrigen() + "-" + ruta.getDestino()).getCaminos(), ruta.getCaminos().get(0));
                rutasCompletas.get(ruta.getOrigen() + "-" + ruta.getDestino()).getCaminos().addAll(caminosAgregados);
                //rutasCompletas.get(ruta.getOrigen() + "-" + ruta.getDestino()).getCaminos().addAll(ruta.getCaminos());
            } else {
                rutasCompletas.put(ruta.getOrigen() + "-" + ruta.getDestino(), ruta);
            }

        }

        return rutasCompletas;
    }

    private List<Camino> agregarCamino(List<Camino> caminosActuales, Camino caminoAgregar) {
        List<Camino> caminoAgregados = new ArrayList<>();
        String camino2 = "";
        boolean ingresar = true;
        for (Integer nodo : caminoAgregar.getNodos()) {
            camino2 += nodo;
        }
        for (Camino caminoAnalizado : caminosActuales) {
            String camino1 = "";

            for (Integer nodo : caminoAnalizado.getNodos()) {
                camino1 += nodo;
            }

            if (camino1.equals(camino2)) {
                ingresar = false;
            }

        }
        if (ingresar) {
            caminoAgregados.add(caminoAgregar);
        }
        return caminoAgregados;
    }

    private Rutas agregarRutaNueva(int origen, int destino, int distancia) {
        Rutas nuevaRuta = new Rutas();
        nuevaRuta.setOrigen(origen);
        nuevaRuta.setDestino(destino);
        Camino camino = new Camino();
        camino.getNodos().add(origen);
        camino.getNodos().add(destino);
        List<Camino> caminos = new ArrayList<>();
        caminos.add(camino);
        camino.setDistancia(distancia);
        nuevaRuta.setCaminos(caminos);

        return nuevaRuta;

    }

    private Rutas expandirRutaDestinRutaoOrigenActual(Rutas rutaExpandida, Integer origenActual, Integer destinoActual, int matrizAdyacencia[][]) {
        rutaExpandida.setDestino(destinoActual);

        for (Camino caminoExpandido : rutaExpandida.getCaminos()) {
            caminoExpandido.getNodos().add(destinoActual);
            caminoExpandido.setDistancia(caminoExpandido.getDistancia() + matrizAdyacencia[origenActual][destinoActual]);
        }
        return rutaExpandida;
    }

    private Rutas expandirRutaOrigenRutaDestinoActual(Rutas rutaExpandida, Integer origenActual, Integer destinoActual, int matrizAdyacencia[][]) {
        rutaExpandida.setOrigen(origenActual);
        List<Camino> caminos = new ArrayList<>();
        for (Camino caminoExpandido : rutaExpandida.getCaminos()) {
            Camino caminoNuevo = new Camino();
            caminoNuevo.setDistancia(matrizAdyacencia[origenActual][destinoActual] + caminoExpandido.getDistancia());
            caminoNuevo.getNodos().add(origenActual);
            caminoNuevo.getNodos().addAll(caminoExpandido.getNodos());
            caminos.add(caminoNuevo);
        }
        rutaExpandida.getCaminos().clear();
        rutaExpandida.setCaminos(caminos);
        return rutaExpandida;
    }

//    private Rutas expandirRutaDestinoDestino(Rutas rutaExpandida, Integer origenActual, Integer destinoActual, int matrizAdyacencia[][]) throws CloneNotSupportedException {
//        rutaExpandida.setDestino(origenActual);
//
//        for (Camino caminoExpandido : rutaExpandida.getCaminos()) {
//            caminoExpandido.getNodos().add(origenActual);
//            caminoExpandido.setDistancia(caminoExpandido.getDistancia() + matrizAdyacencia[origenActual][destinoActual]);
//        }
//        return rutaExpandida;
//    }
    private Rutas expandirRutaOrigenOrigen(Rutas rutaExpandida, Integer origenActual, Integer destinoActual, int matrizAdyacencia[][]) throws CloneNotSupportedException {
        rutaExpandida.setOrigen(destinoActual);

        List<Camino> caminos = new ArrayList<>();
        for (Camino caminoExpandido : rutaExpandida.getCaminos()) {
            Camino caminoNuevo = new Camino();
            caminoNuevo.setDistancia(matrizAdyacencia[origenActual][destinoActual] + caminoExpandido.getDistancia());
            caminoNuevo.getNodos().add(destinoActual);
            caminoNuevo.getNodos().addAll(caminoExpandido.getNodos());
            caminos.add(caminoNuevo);
        }
        rutaExpandida.getCaminos().clear();
        rutaExpandida.setCaminos(caminos);
        return rutaExpandida;
    }

    private void desmarcarRuta(List<Rutas> rutas) {
        for (Rutas ruta : rutas) {
            ruta.setExpandido(false);//ver si puedo verificar la ruta si ya existe 
            //si no existe nomas agregar
        }
    }

    private List<Rutas> generarExpanciones(List<Rutas> rutas, Integer origenActual, Integer destinoActual,
            int matrizAdyacencia[][]) throws CloneNotSupportedException {

        List<Rutas> rutasExpandidas = new ArrayList<>();
        for (Rutas ruta : rutas) {
            if (!ruta.isExpandido()) {

                if (ruta.getDestino().equals(origenActual)) {
                    for (Camino camino : ruta.getCaminos()) {
                        if (!camino.getNodos().contains(destinoActual)) {
                            rutasExpandidas.add(expandirRutaDestinRutaoOrigenActual(ruta.clone(), origenActual, destinoActual, matrizAdyacencia));
                            ruta.setExpandido(true);
                        }
                    }
                } else if (ruta.getOrigen().equals(destinoActual)) {
                    for (Camino camino : ruta.getCaminos()) {
                        if (!camino.getNodos().contains(origenActual)) {
                            rutasExpandidas.add(expandirRutaOrigenRutaDestinoActual(ruta.clone(), origenActual, destinoActual, matrizAdyacencia));
                            ruta.setExpandido(true);
                        }
                    }
                } //                else if (ruta.getDestino().equals(destinoActual)) {
                //                    for (Camino camino : ruta.getCaminos()) {
                //                        if (!camino.getNodos().contains(origenActual)) {
                //                            rutasExpandidas.add(expandirRutaDestinoDestino(ruta.clone(), origenActual, destinoActual, matrizAdyacencia));
                //                            ruta.setExpandido(true);
                //                            //rutasExpandidas.add(expandirRutaOrigenRutaDestinoActual(ruta.clone(), origenActual, destinoActual, matrizAdyacencia));
                //                        }
                //                    }
                //                } 
                else if (ruta.getOrigen().equals(origenActual)) {
                    for (Camino camino : ruta.getCaminos()) {
                        if (!camino.getNodos().contains(destinoActual)) {
                            rutasExpandidas.add(expandirRutaOrigenOrigen(ruta.clone(), origenActual, destinoActual, matrizAdyacencia));
                            ruta.setExpandido(true);
                        }
                    }
                }
            }

        }

        return rutasExpandidas;
    }

}
