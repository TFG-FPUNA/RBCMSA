/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ilp.propios;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import py.com.fp.una.rbcmsa.archivos.Archivo;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import py.com.fp.una.rbcmsa.ilp.propios.model.AlphaR;
import py.com.fp.una.rbcmsa.peticion.model.CaminoTR;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;

/**
 *
 * @author Richard
 */
public class Adaptaciones {

    @Inject
    Archivo archivoBean;
    private static final String INICIO_LLAVE = "[";
    private static final String FIN_LLAVE = "]";
    private static final String FIN_LINEA = ";";
    private static final String SEPARADOR = ",";
    private static final String SALTO = "\n";
    private static final String UNO = "1";
    private static final String CERO = "0";
    private static final String SEPARADOR_IDENTIFICADOR = "-";
    private static final String K = "K = ";
    private static final String SD = "SD = ";
    private static final String E = "E = ";
    private static final String G = "G = ";
    private static final String ALFA = "alfa = ";
    private static final String RR = "R = ";
    private static final String FTOTAL = "Ftotal = ";
    private static final String LL = "l = ";
    private static final int INDICE_ALPHA = 1;
    private static final int INDICE_R = 2;

    public void asigarGrafoILP(List<Integer> posicionesCaminos, List<Integer> posicionesFS,
            Grafo grafo, List<PeticionBCM> peticionesFinales, int k) {
        int indicaAuxPeticion = 0;
        for (PeticionBCM peticionFinal : peticionesFinales) {
            int indice = (posicionesCaminos.get(indicaAuxPeticion) - 1) - (k * indicaAuxPeticion);
            System.out.println("peticion: " + indicaAuxPeticion + " - inidce: " + indice);
            CaminoTR caminoTR = peticionFinal.getCaminosTR().get(indice);
            for (int j = 0; j < caminoTR.getCamino().getNodos().size() - 1; j++) {
                String identificador = caminoTR.getCamino().getNodos().get(j) + SEPARADOR_IDENTIFICADOR + caminoTR.getCamino().getNodos().get(j + 1);
                for (int l = 0; l < caminoTR.getTrfinal().getTamanhoFS(); l++) {
                    int auxPosicion = l + posicionesFS.get(indicaAuxPeticion);
                    grafo.getAristas().get(identificador).getSP()[auxPosicion] = true;
                    grafo.getAristas().get(identificador).setCantidadSP(grafo.getAristas().get(identificador).getCantidadSP() - 1);
                    
                    System.out.println("Arista:" + identificador + "posicion fs usado: " + auxPosicion);
                }
            }
            indicaAuxPeticion++;
        }
    }

    public AlphaR preparaArchivoFaseIILP(String ruta, String nombre, String k, List<PeticionBCM> peticionesFinales,
            Grafo grafo, String GuardBan) throws IOException {
        List<String> archivoPrimeraFaseILP = new ArrayList<>();
        archivoPrimeraFaseILP.add(K + k + FIN_LINEA);
        archivoPrimeraFaseILP.add(SD + peticionesFinales.size() + FIN_LINEA);
        archivoPrimeraFaseILP.add(E + grafo.getAristas().size() + FIN_LINEA);
        archivoPrimeraFaseILP.add(G + GuardBan + FIN_LINEA);
        AlphaR alphaR = this.generarAlphaGenerarR(peticionesFinales, grafo);
        archivoPrimeraFaseILP.add(alphaR.getAlphaR());
        archivoBean.crearArchivo(ruta + nombre, archivoPrimeraFaseILP);

        return alphaR;

    }

    public void preparaArchivoFaseIIILP(String ruta, String nombre, int k, List<PeticionBCM> peticionesFinales,
            Grafo grafo, String GuardBan, int ftotal, AlphaR alphaR, List<Integer> posicionesCaminos) throws IOException {
        List<String> archivoPrimeraFaseILP = new ArrayList<>();
        archivoPrimeraFaseILP.add(K + k + FIN_LINEA);
        archivoPrimeraFaseILP.add(SD + peticionesFinales.size() + FIN_LINEA);
        archivoPrimeraFaseILP.add(FTOTAL + ftotal + FIN_LINEA);
        archivoPrimeraFaseILP.add(G + GuardBan + FIN_LINEA);
        archivoPrimeraFaseILP.add(this.generarAlpha2(posicionesCaminos, peticionesFinales, k) + FIN_LINEA);
        archivoPrimeraFaseILP.add(this.generarL(alphaR, posicionesCaminos, peticionesFinales.size()));
        archivoBean.crearArchivo(ruta + nombre, archivoPrimeraFaseILP);

    }

    private String generarL(AlphaR alphaR, List<Integer> posicionesCaminos, int cantidadSolicitudes) {
        List<List<String>> rLista = alphaR.getRLista();
        List<List<String>> rListaFiltrada = new ArrayList<>();
        for (Integer posicion : posicionesCaminos) {
            rListaFiltrada.add(rLista.get(posicion - 1));
        }
        for (List<String> list : rListaFiltrada) {
            System.out.println(list);
        }
        List<List<String>> LLista = new ArrayList<>();
        for (List<String> list : rListaFiltrada) {
            List<String> filaL = this.generarFilaRCerada(cantidadSolicitudes);
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < cantidadSolicitudes; j++) {
                    if (list.get(i).equals(UNO)) {
                        if (list.get(i).equals(rListaFiltrada.get(j).get(i))) {
                            filaL.set(j, UNO);
                        }
                    }

                }
            }
            LLista.add(filaL);
        }
        String L = INICIO_LLAVE + SALTO;
        for (List<String> list : LLista) {
            L = L + INICIO_LLAVE;
            for (String string : list) {
                L = L + string + SEPARADOR;
            }
            L = L.substring(0, L.length() - 1);
            L = L + FIN_LLAVE + SEPARADOR + SALTO;

        }
        L = L.substring(0, L.length() - 2);
        L = LL + L + FIN_LLAVE + FIN_LINEA;

        System.out.println(L);
        return L;
    }

    private String generarAlpha2(List<Integer> posicionesCaminos, List<PeticionBCM> peticionesFinales, int k) {
        String alpha2 = ALFA + INICIO_LLAVE;
        int indicaAuxPeticion = 0;
        for (PeticionBCM peticionFinal : peticionesFinales) {
            int indice = (posicionesCaminos.get(indicaAuxPeticion) - 1) - (k * indicaAuxPeticion);
            System.out.println("peticion: " + indicaAuxPeticion + " - inidce: " + indice);
            Integer tamanhoFs = peticionFinal.getCaminosTR().get(indice).getTrfinal().getTamanhoFS() - 1;
            alpha2 = alpha2 + tamanhoFs + SEPARADOR;
            indicaAuxPeticion++;
        }
        alpha2 = alpha2.substring(0, alpha2.length() - 1);
        alpha2 = alpha2 + FIN_LLAVE;
        System.out.println("Alpha 2: " + alpha2);
        return alpha2;
    }

    private List<String> generarFilaRCerada(int longitud) {
        List<String> fila = new ArrayList<>();
        for (int i = 0; i < longitud; i++) {
            fila.add(CERO);
        }
        return fila;
    }

    private AlphaR generarAlphaGenerarR(List<PeticionBCM> peticionesFinales, Grafo grafo) {
        System.out.println("R: " + grafo.getRILP());

        String[] spliter = grafo.getRILP().split(SEPARADOR);

        String alpha = INICIO_LLAVE;
        String R = INICIO_LLAVE + SALTO;
        int j = 0;
        List<List<String>> RLista = new ArrayList<>();

        for (PeticionBCM peticionFinal : peticionesFinales) {
            alpha = alpha + INICIO_LLAVE;

            for (CaminoTR caminoTR : peticionFinal.getCaminosTR()) {
                List<String> filaR = this.generarFilaRCerada(spliter.length);
                Integer tamanhoFs = caminoTR.getTrfinal().getTamanhoFS() - 1;
                alpha = alpha + tamanhoFs + SEPARADOR;

                for (int i = 0; i < caminoTR.getCamino().getNodos().size() - 1; i++) {
                    String identificador = caminoTR.getCamino().getNodos().get(i) + SEPARADOR_IDENTIFICADOR + caminoTR.getCamino().getNodos().get(i + 1);

                    int indiceEnlace = -1;
                    for (int l = 0; l < spliter.length; l++) {
                        if (spliter[l].equals(identificador)) {
                            indiceEnlace = l;
                        }
                    }
                    filaR.set(indiceEnlace, UNO);
                }
                RLista.add(filaR);
            }
            alpha = alpha.substring(0, alpha.length() - 1);
            alpha = alpha + FIN_LLAVE;
            j++;
        }
        for (List<String> list : RLista) {
            R = R + INICIO_LLAVE;
            for (String string : list) {
                R = R + string + SEPARADOR;
            }
            R = R.substring(0, R.length() - 1);
            R = R + FIN_LLAVE + SEPARADOR + SALTO;

        }
        R = R.substring(0, R.length() - 2);
        R = RR + R + FIN_LLAVE + FIN_LINEA;
        System.out.println("R: " + R);
        alpha = ALFA + alpha + FIN_LLAVE + FIN_LINEA + SALTO;
        String alphaR = alpha + R;
        AlphaR retorno = new AlphaR();
        retorno.setRLista(RLista);
        retorno.setAlphaR(alphaR);
        return retorno;

    }
}
