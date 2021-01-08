package py.com.fp.una.rbcmsa.ag.ysa;

import java.io.*;
import java.util.*;
import static java.lang.Math.floor;
import javax.inject.Inject;
import py.com.fp.una.rbcmsa.ag.ysa.adaptaciones.AdaptacionesAG;
import py.com.fp.una.rbcmsa.archivos.Archivo;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;
import static py.com.fp.una.rbcmsa.tr.model.Constantes.*;

/**
 * Created by ysapy on 23/02/16.
 */
public class AG {

    private static Long costoMayor;
    private static Long saltoMayor;
    private static Long espectroMayor;
    //private static final int nivelDeModulacion = 1;
    //private static final List<String> ALGORITMOS = new ArrayList<>();
    private static final String TOPOLOGIA = "nsf";

    @Inject
    AdaptacionesAG adaptacionesAG;

    @Inject
    Archivo archivoBean;

    public List<List<Solucion>> AG(List<PeticionBCM> peticionesFinales, String ruta, String nombre,
            int limite, int cantidadGeneraciones, int cantidadPoblacion,
            double mutacion, int cantidadSP) throws FileNotFoundException, IOException {
        String algoritmo = MOGA_2;
        String pathGeneral = ruta + RUTA_AG + TOPOLOGIA + FIN_DE_RUTA;
        String pathInicial = pathGeneral;
        List<List<Solucion>> todosLosConjuntos = new ArrayList<>();
        List<List<Boolean>> topologia = AGHelper.leerTopologia(pathGeneral);
        /*
        *  1. MOGA spectrum allocation random
        *  2. MOGA spectrum allocation first fit
         */
        int k = limite;
        List<DemandaInfo> demandaInfoList = new ArrayList<>();
        String pathActual;
        long TInicio, TFin; //Variables para determinar el tiempo de ejecución

        pathActual = pathInicial + K + (k) + FIN_DE_RUTA;
        archivoBean.crearDirectorio(pathActual);
        adaptacionesAG.generarEntradaAG(peticionesFinales, k, pathActual, nombre);

        demandaInfoList.addAll(llenarDemandInfo(pathActual + ARCHIVO_GA));
        espectroMayor = Long.valueOf(cantidadSP);

        int contadorConvergencia = 0;
        double fitnessAnterior = -1;
        for (int i = 0; i < cantidadGeneraciones; i++) {
            boolean cambioSolucion = false;
            TInicio = System.currentTimeMillis();
            todosLosConjuntos.add(ga(topologia, cantidadPoblacion, cantidadSP, mutacion, demandaInfoList, (/*a + 1*/k), algoritmo));
            TFin = System.currentTimeMillis();

            List<py.com.fp.una.rbcmsa.ag.ysa.Solucion> conjuntoSoluciones = todosLosConjuntos.get(todosLosConjuntos.size() - 1);
            int indice = conjuntoSoluciones.size() - 1;
            double fitnessActual = conjuntoSoluciones.get(indice).getFitness();
            if (fitnessAnterior < fitnessActual) {
                fitnessAnterior = fitnessActual;
                cambioSolucion = true;
            } else {
                cambioSolucion = false;
            }

            if (!cambioSolucion) {
                contadorConvergencia++;
            } else {
                contadorConvergencia = 0;
            }
            if (contadorConvergencia == CRITERIO_PARADA) {
                return todosLosConjuntos;
            }

            long minRunningMemory = (1024 * 1024);

            Runtime runtime = Runtime.getRuntime();

            if (runtime.freeMemory() < minRunningMemory) {
                System.gc();
            }
        }
        return todosLosConjuntos;
    }

    public static List<Solucion> generarPoblacionInicial(List<List<Boolean>> topologia, Integer cantSolucionesIniciales, Integer totalRanuras, List<DemandaInfo> demandaInfoList, String algoritmo) throws FileNotFoundException {

        List<Solucion> poblacionInicial = new ArrayList<>();

        List<DemandaInfo> demandasMayores = new ArrayList<>();
        demandasMayores.addAll(definirOrdenDemandas(demandaInfoList));

        for (int i = 0; i < cantSolucionesIniciales; i++) {
            poblacionInicial.add(generarSolucion(totalRanuras, topologia, demandasMayores, demandaInfoList, algoritmo));
        }

        return poblacionInicial;
    }

    public static List<DemandaInfo> definirOrdenDemandas(List<DemandaInfo> demandasInfo) {
        int i, r;

        List<DemandaInfo> mayores = new ArrayList<DemandaInfo>();
        List<DemandaInfo> aux;
        List<DemandaInfo> infoCopy = new ArrayList<DemandaInfo>();

        // traer la lista de demandasInfo con las filas que tienen la mayor X de cada demanda
        aux = obtenerDemandasMayores(demandasInfo);

        // ordenar aux de mayor a menor
        ordenar(aux);
        infoCopy.addAll(aux);

        // copio el 30% de los mayores a mayores
        for (i = 0; i < floor(aux.size() * 0.3); i++) {
            mayores.add(aux.get(i));
            infoCopy.remove(aux.get(i));
        }

        // copio el 70% restante en orden aleatorio
        for (i = 0; i < aux.size() - floor(aux.size() * 0.3); i++) {
            r = (int) (Math.random() * infoCopy.size());
            mayores.add(infoCopy.get(r));
            infoCopy.remove(r);
        }

        return mayores;
    }

    public static Solucion generarSolucion(int cantRanuras, List<List<Boolean>> topologia, List<DemandaInfo> mayores, List<DemandaInfo> demandasInfo, String algoritmo) {
        List<Enlace> enlacesIniciales = generarListaInicialRanuras(cantRanuras, topologia);
        Solucion solucion = new Solucion(enlacesIniciales); // inicializar con una lista de enlaces en la cual todas sus ranuras estan libres
        int j;
        boolean bloqueado;

        // selecciono demanda por demanda en el orden ya establecido del 30% y 70%
        // de cada demanda, traigo la ruta mas corta de demandasInfo e intento asignarle la cantidad de ranuras que quiere
        // si no puede con esa ruta, intenta con la siguiente ruta mas corta, etc
        // y si no puede asignar las ranuras con ninguna de sus rutas, suma una demanda bloqueada a solucion
        for (int m = 0; m < mayores.size(); m++) {

            DemandaInfo demanda = mayores.get(m);
            bloqueado = true;

            for (j = 0; j < demandasInfo.size(); j++) {
                if (demandasInfo.get(j).getOrigen() == demanda.getOrigen()
                        && demandasInfo.get(j).getDestino() == demanda.getDestino()) {
                    if (asignarRanuras(solucion, j, demandasInfo, algoritmo)) {
                        j = demandasInfo.size();
                        bloqueado = false;
                    }
                }
            }

            if (bloqueado) {
                agregarBloqueado(solucion, demanda);
                solucion.setCantBloq(solucion.getCantBloq() + 1);
            }
        }
        solucion.setSaltos(getSaltoMayor(solucion, demandasInfo));
        solucion.setEspectro(getEspectroMayor(solucion));
        solucion.setCosto(getCostoDeLaSolucion(solucion, demandasInfo));
        solucion.setFitness(solucion.getEspectro());
        return solucion;

    }

    public static List<DemandaInfo> llenarDemandInfo(String archivo) throws FileNotFoundException {

        List<DemandaInfo> demandasInfo = new ArrayList<DemandaInfo>();
        demandasInfo.addAll(AGHelper.leerDemandasInfo(archivo));

        return demandasInfo;

    }

    public static void ordenar(List<DemandaInfo> demandasInfo) {

        DemandaInfo aux;
        int j;

        for (int i = 0; i < demandasInfo.size(); i++) {
            for (j = i + 1; j < demandasInfo.size(); j++) {
                if (demandasInfo.get(i).getX() < demandasInfo.get(j).getX()) {
                    aux = demandasInfo.get(i);
                    demandasInfo.set(i, demandasInfo.get(j));
                    demandasInfo.set(j, aux);
                }
            }
        }

    }

    public static boolean asignarRanuras(Solucion solucion, int rutaNro, List<DemandaInfo> demandasInfo, String algoritmo) {
        DemandaInfo demandaInfo = demandasInfo.get(rutaNro);

        List<Integer> ranurasDisponibles = new ArrayList<>();
        ranurasDisponibles.addAll(obtenerRanurasDisponiblesParaRuta(solucion, demandaInfo));
        // elegir randomicamente una posicion e ranurasDisponibles
        // asignar esa ranura a la solucion para esta demanda

        if (ranurasDisponibles.isEmpty()) {
            return false;
        } else {
            if (MOGA_1.equals(algoritmo)) {
                // elegir enlaces aleatoriamente
                int ranuraElegida = (int) (Math.random() * (ranurasDisponibles.size() - 1));
                agregarRanurasASolucion(solucion, rutaNro, demandaInfo, ranurasDisponibles.get(ranuraElegida));

            } else if (MOGA_2.equals(algoritmo)) {
                // elegir ranuras con first fit
                agregarRanurasASolucion(solucion, rutaNro, demandaInfo, ranurasDisponibles.get(0));
            } else {
                return false;
            }
            return true;
        }
    }

    public static List<Integer> obtenerRanurasDisponiblesParaRuta(Solucion solucion, DemandaInfo demandaInfo) {
        int origen;
        int destino;
        List<Enlace> enlaces = new ArrayList<>();

        for (int i = 0; i < solucion.getEnlaces().size(); i++) {
            for (int j = 0; j < (demandaInfo.getRuta().size() - 1); j++) {
                origen = demandaInfo.getRuta().get(j);
                destino = demandaInfo.getRuta().get(j + 1);

                if (solucion.getEnlaces().get(i).getInicio() == origen
                        && solucion.getEnlaces().get(i).getFin() == destino) {
                    enlaces.add(solucion.getEnlaces().get(i));
                }
            }
        }

        List<Integer> indiceDeRanurasLibres = new ArrayList<>();

        // obtener ranuras libres del primer enlace
        for (int i = 0; i < enlaces.get(0).getRanuras().size(); i++) {
            if (ranuraEsSolucion(enlaces.get(0).getRanuras(), i, demandaInfo.getTraf() /*+ nivelDeModulacion*/)) {
                indiceDeRanurasLibres.add(i);
            }
        }

        for (int i = 1; i < enlaces.size(); i++) {
            List<Integer> copiaIndiceDeRanurasLibres = new ArrayList<>();
            copiaIndiceDeRanurasLibres.addAll(indiceDeRanurasLibres);
            for (int j = 0; j < indiceDeRanurasLibres.size(); j++) {
                if (!ranuraEsSolucion(enlaces.get(i).getRanuras(), indiceDeRanurasLibres.get(j), demandaInfo.getTraf() /*+ nivelDeModulacion*/)) {
                    copiaIndiceDeRanurasLibres.remove(indiceDeRanurasLibres.get(j));
                }
            }
            indiceDeRanurasLibres = new ArrayList<>();
            indiceDeRanurasLibres.addAll(copiaIndiceDeRanurasLibres);
        }

        return indiceDeRanurasLibres;
    }

    public static boolean ranuraEsSolucion(List<Boolean> ranuras, int ranura, int cantSolicitada) {
        if (!ranuras.get(ranura) && ranuras.size() > (ranura + cantSolicitada)) {
            for (int i = (ranura + 1); i < (ranura + cantSolicitada); i++) {
                if (ranuras.get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void agregarRanurasASolucion(Solucion solucion, int rutaNro, DemandaInfo demandaInfo, int primeraRanura) {
        int ranurasSolicitadas = demandaInfo.getTraf() /*+ nivelDeModulacion*/;
        int nodoInicio, nodoFin, ubicacion, j;
        boolean existe = false;
        int posicion = -1;

        Ruteo ruteo = new Ruteo();
        List<Integer> ranurasUsadas = new ArrayList<>();

        // crea una lista de la posicion de las ranuras que usa cada enlace
        for (int i = primeraRanura; i < ranurasSolicitadas + primeraRanura; i++) {
            ranurasUsadas.add(i);
        }

        // marca en la solucion cuales son las ranuras que van a ser ocupadas por esta demanda
        for (int i = 0; i < demandaInfo.getRuta().size() - 1; i++) {
            nodoInicio = demandaInfo.getRuta().get(i);
            nodoFin = demandaInfo.getRuta().get(i + 1);
            ubicacion = ubicarEnlace(nodoInicio, nodoFin, solucion.getEnlaces());

            for (j = primeraRanura; j < (ranurasSolicitadas + primeraRanura); j++) {
                solucion.getEnlaces().get(ubicacion).getRanuras().set(j, true);
            }
        }

        // crea el objeto que cubre esta demanda y agrega a solucion
        for (int i = 0; i < solucion.getRuteos().size(); i++) {
            if (solucion.getRuteos().get(i).getOriden() == demandaInfo.getOrigen()
                    && solucion.getRuteos().get(i).getDestino() == demandaInfo.getDestino()) {
                existe = true;
                posicion = i;
                break;
            }
        }

        if (existe) {
            solucion.getRuteos().get(posicion).setRanurasUsadas(ranurasUsadas);
        } else {
            ruteo.setDemandaId(demandaInfo.getDemandaId());
            ruteo.setOriden(demandaInfo.getOrigen());
            ruteo.setDestino(demandaInfo.getDestino());
            ruteo.setRanurasUsadas(ranurasUsadas);
            ruteo.setRutaNro(rutaNro);

            solucion.getRuteos().add(ruteo);
        }
    }

    public static boolean aplicaRanuras(int primeraRanura, int rutaNro, List<Enlace> enlaces, List<DemandaInfo> demandasInfo) {

        DemandaInfo demandaInfo = demandasInfo.get(rutaNro);
        int ranurasSolicitadas = demandaInfo.getTraf();
        int nodoInicio, nodoFin, posicion, j;
        int limite = 0;

        for (int i = 0; i < demandaInfo.getRuta().size() - 1; i++) {
            nodoInicio = demandaInfo.getRuta().get(i);
            nodoFin = demandaInfo.getRuta().get(i + 1);
            posicion = ubicarEnlace(nodoInicio, nodoFin, enlaces);
            limite = ranurasSolicitadas + primeraRanura;

            for (j = primeraRanura; j < limite; j++) {
                // si recibe como primera ranura una de las ultimas y sumando las ranuras solicitadas alcanza mas del total de ranuras
                // envia falso porque no aplica
                if (j == enlaces.get(posicion).getRanuras().size() || enlaces.get(posicion).getRanuras().get(j)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static int ubicarEnlace(int inicio, int fin, List<Enlace> enlaces) {

        for (int i = 0; i < enlaces.size(); i++) {
            if (enlaces.get(i).getInicio() == inicio && enlaces.get(i).getFin() == fin) {
                return i;
            }
        }

        return -1;
    }

    /*
    * Aca empieza el purete
    *
    * */
    public static List<Solucion> ga(List<List<Boolean>> topologia, int cantSolucionesIniciales,
            int totalRanuras, double probabilidaMutacion, List<DemandaInfo> demandasInfo, int k,
            String algoritmo) throws FileNotFoundException {

        int j, reproductor1, reproductor2, candidato1 = -1, candidato2 = -1;
        Solucion hijo1, hijo2; // inicializar con enlaces con ranuras vacias

        List<Solucion> poblacionInicial = generarPoblacionInicial(topologia, cantSolucionesIniciales, totalRanuras, demandasInfo, algoritmo);
        List<Solucion> poblacionActual = new ArrayList<>();
        poblacionActual.addAll(poblacionInicial);
        List<Solucion> poblacionNueva;
        List<Solucion> aux;
        Random rnd = new Random();
        Long TInicio;
        Long TFin; //Variables para determinar el tiempo de ejecución

        TInicio = System.currentTimeMillis();

        if (k == 1) {
            return poblacionInicial;
        }

        int v = 1;
        TFin = System.currentTimeMillis();

        for (int i = 0; i < cantSolucionesIniciales; i++) {
            aux = new ArrayList<>();
            poblacionNueva = new ArrayList<>();

            for (j = 0; j < poblacionInicial.size() / 4; j++) {

                hijo1 = new Solucion();
                hijo2 = new Solucion();

                // elegir reproductores aleatoriamente
                // elegir 2 candidatos aleatoriamente, de los 2 candidatos tomar el de mejor fitness para ser un reproductor
                while (candidato1 == candidato2) {
                    candidato1 = (int) (rnd.nextDouble() * poblacionActual.size());
                    candidato2 = (int) (rnd.nextDouble() * poblacionActual.size());
                }

                if (poblacionActual.get(candidato1).getFitness() > poblacionActual.get(candidato2).getFitness()) {
                    reproductor1 = candidato2;
                } else {
                    reproductor1 = candidato1;
                }

                while (candidato1 == candidato2 || candidato1 == reproductor1 || candidato2 == reproductor1) {
                    candidato1 = (int) (rnd.nextDouble() * poblacionActual.size());
                    candidato2 = (int) (rnd.nextDouble() * poblacionActual.size());
                }

                if (poblacionActual.get(candidato1).getFitness() > poblacionActual.get(candidato2).getFitness()) {
                    reproductor2 = candidato2;
                } else {
                    reproductor2 = candidato1;
                }

                cruce(poblacionActual.get(reproductor1), poblacionActual.get(reproductor2), hijo1, hijo2, topologia, totalRanuras, demandasInfo, algoritmo);

                // mantener poblacion actual para comparar con los hijos al final
                // pero eliminar los reproductores ya elegidos para no repetir
                aux.add(poblacionActual.get(reproductor1));
                aux.add(poblacionActual.get(reproductor2));

                if (reproductor1 < reproductor2) {
                    poblacionActual.remove(reproductor2);
                    poblacionActual.remove(reproductor1);
                } else {
                    poblacionActual.remove(reproductor1);
                    poblacionActual.remove(reproductor2);
                }

                mutacion(hijo1, probabilidaMutacion, topologia, totalRanuras, demandasInfo, algoritmo);
                mutacion(hijo2, probabilidaMutacion, topologia, totalRanuras, demandasInfo, algoritmo);

                poblacionNueva.add(hijo1);
                poblacionNueva.add(hijo2);

                candidato1 = -1;
                candidato2 = -1;

            }

            // de la poblacion vieja elegir solo las 2 primeras soluciones, las mejores y copiar a nueva poblacion
            // para el resto de mi poblacion nueva, cruzar el resto de las soluciones de la poblacion vieja
            elegirMejores(poblacionActual, aux, poblacionNueva);

            TFin = System.currentTimeMillis();
            v++;
        }

        return poblacionActual;

    }

    /**
     * Nuestro operador cruce utiliza el cruce de 2 puntos
     *
     * @param reproductor1
     * @param reproductor2
     * @param hijo1
     * @param hijo2
     * @param topologia
     * @param totalRanuras
     * @param demandasInfo
     */
    public static void cruce(Solucion reproductor1, Solucion reproductor2, Solucion hijo1, Solucion hijo2, List<List<Boolean>> topologia, Integer totalRanuras, List<DemandaInfo> demandasInfo, String algoritmo) {
        List<Ruteo> ruteo1 = new ArrayList<>();
        List<Ruteo> ruteo2 = new ArrayList<>();
        Ruteo ruteo = new Ruteo();
        Random rnd = new Random();

        int puntoDeCruce1 = rnd.nextInt(reproductor1.getRuteos().size() - 3) + 0;
        int puntoDeCruce2 = rnd.nextInt((reproductor1.getRuteos().size() - 2) - (puntoDeCruce1 + 1) + 1) + (puntoDeCruce1 + 1);

        for (int i = 0; i <= puntoDeCruce1; i++) {
            ruteo = new Ruteo();
            reproductor1.getRuteos().get(i).clonar(ruteo);
            ruteo1.add(ruteo);
            ruteo = new Ruteo();
            reproductor2.getRuteos().get(i).clonar(ruteo);
            ruteo2.add(ruteo);
        }

        for (int i = puntoDeCruce1 + 1; i <= puntoDeCruce2; i++) {
            ruteo = new Ruteo();
            reproductor2.getRuteos().get(i).clonar(ruteo);
            ruteo1.add(ruteo);
            ruteo = new Ruteo();
            reproductor1.getRuteos().get(i).clonar(ruteo);
            ruteo2.add(ruteo);
        }

        for (int i = puntoDeCruce2 + 1; i < reproductor1.getRuteos().size(); i++) {
            ruteo = new Ruteo();
            reproductor1.getRuteos().get(i).clonar(ruteo);
            ruteo1.add(ruteo);
            ruteo = new Ruteo();
            reproductor2.getRuteos().get(i).clonar(ruteo);
            ruteo2.add(ruteo);
        }

        hijo1.setRuteos(ruteo1);
        hijo2.setRuteos(ruteo2);

        hijo1.setEnlaces(generarListaInicialRanuras(totalRanuras, topologia));
        hijo2.setEnlaces(generarListaInicialRanuras(totalRanuras, topologia));

        recalcularRanuras(hijo1, demandasInfo, algoritmo);
        recalcularRanuras(hijo2, demandasInfo, algoritmo);

        hijo1.setSaltos(getSaltoMayor(hijo1, demandasInfo));
        hijo1.setEspectro(getEspectroMayor(hijo1));
        hijo1.setCosto(getCostoDeLaSolucion(hijo1, demandasInfo));
        hijo1.setFitness(hijo1.getEspectro());

        hijo2.setSaltos(getSaltoMayor(hijo2, demandasInfo));
        hijo2.setEspectro(getEspectroMayor(hijo2));
        hijo2.setCosto(getCostoDeLaSolucion(hijo2, demandasInfo));
        hijo2.setFitness(hijo2.getEspectro());

    }

    public static void mutacion(Solucion solucion, double probabilidadMutacion, List<List<Boolean>> topologia, Integer totalRanuras, List<DemandaInfo> demandasInfo, String algoritmo) {
        List<Integer> posiciones;
        int j, r;

        for (int i = 0; i < solucion.getRuteos().size(); i++) {
            if ((Math.random()) <= probabilidadMutacion) {
                posiciones = new ArrayList<>();
                for (j = 0; j < demandasInfo.size(); j++) {
                    if (demandasInfo.get(j).getOrigen() == solucion.getRuteos().get(i).getOriden()
                            && demandasInfo.get(j).getDestino() == solucion.getRuteos().get(i).getDestino()) {
                        posiciones.add(j);
                    }
                }

                r = (int) (Math.random() * posiciones.size());

                solucion.getRuteos().get(i).setRutaNro(posiciones.get(r));
            }
        }

        int cantEnlaces = solucion.getEnlaces().size();
        for (int i = 0; i < cantEnlaces; i++) {
            solucion.getEnlaces().remove(cantEnlaces - i - 1);
        }
        solucion.setEnlaces(generarListaInicialRanuras(totalRanuras, topologia));
        solucion.setCantBloq(0);
        solucion.setFitness(0.0);
        recalcularRanuras(solucion, demandasInfo, algoritmo);

        solucion.setSaltos(getSaltoMayor(solucion, demandasInfo));
        solucion.setEspectro(getEspectroMayor(solucion));
        solucion.setCosto(getCostoDeLaSolucion(solucion, demandasInfo));
        solucion.setFitness(solucion.getEspectro());

    }

    public static void recalcularRanuras(Solucion solucion, List<DemandaInfo> demandasInfo, String algoritmo) {
        int j, rutaNro;

        // vaciar las ranuras usadas por cada ruta
        for (int i = 0; i < solucion.getRuteos().size(); i++) {
            for (j = solucion.getRuteos().get(i).getRanurasUsadas().size() - 1; j >= 0; j--) {
                solucion.getRuteos().get(i).getRanurasUsadas().remove(j);
            }
        }

        for (int i = 0; i < solucion.getRuteos().size(); i++) {

            rutaNro = solucion.getRuteos().get(i).getRutaNro();
            if (rutaNro > -1) {
                if (!asignarRanuras(solucion, rutaNro, demandasInfo, algoritmo)) {
                    solucion.setCantBloq(solucion.getCantBloq() + 1);
                    solucion.getRuteos().get(i).setRutaNro(-1);
                }
            } else {
                solucion.setCantBloq(solucion.getCantBloq() + 1);
            }

        }
    }

    public static void elegirMejores(List<Solucion> poblacionResultado, List<Solucion> poblacionActual, List<Solucion> poblacionNueva) {
        int cantSoluciones = poblacionActual.size();
        List<Solucion> solucionesCompletas = new ArrayList<>();

        solucionesCompletas.addAll(poblacionActual);
        solucionesCompletas.addAll(poblacionNueva);
        ordenarPorBloqueados(solucionesCompletas);

        poblacionActual = new ArrayList<>();
        poblacionActual.addAll(solucionesCompletas);

        ordenarPorFitness(poblacionActual);

        for (int i = 0; i < cantSoluciones; i++) {
            poblacionResultado.add(poblacionActual.get(i));
        }

    }

    public static void ordenarPorBloqueados(List<Solucion> soluciones) {

        Solucion aux;
        int j;

        for (int i = 0; i < soluciones.size(); i++) {
            for (j = i + 1; j < soluciones.size(); j++) {
                if (soluciones.get(i).getCantBloq() > soluciones.get(j).getCantBloq()) {
                    aux = soluciones.get(i);
                    soluciones.set(i, soluciones.get(j));
                    soluciones.set(j, aux);
                }
            }
        }

    }

    public static void ordenarPorFitness(List<Solucion> soluciones) {

        List<Solucion> gruposBloq = new ArrayList<Solucion>();
        Solucion aux;
        int j, cantidad = 0, inicio = -1;

        if (soluciones.get(soluciones.size() / 2).getCantBloq() == soluciones.get((soluciones.size() / 2) - 1).getCantBloq()) {
            for (int i = 0; i < soluciones.size(); i++) {
                if (soluciones.get(soluciones.size() / 2).getCantBloq() == soluciones.get(i).getCantBloq()) {
                    if (inicio < 0) {
                        inicio = i;
                    }
                    cantidad++;
                    gruposBloq.add(soluciones.get(i));
                }
            }
        }

        for (int i = 0; i < cantidad; i++) {
            for (j = i + 1; j < cantidad; j++) {
                if (gruposBloq.get(i).getFitness() > gruposBloq.get(j).getFitness()) {
                    aux = gruposBloq.get(i);
                    gruposBloq.set(i, gruposBloq.get(j));
                    gruposBloq.set(j, aux);
                }
            }
        }

        for (int i = 0; i < cantidad; i++) {
            soluciones.set(i + inicio, gruposBloq.get(i));
        }

    }

    /*
    * Metodos auxiliares
    *
    * Inicializa todas las ranuras de todos los enlaces a false = no utilizado
    *
    * */
    public static List<Enlace> generarListaInicialRanuras(int cantRanuras, List<List<Boolean>> topologia) {
        int i, j, k;
        Enlace enlace;
        List<Enlace> enlaces = new ArrayList<>();
        List<Boolean> ranuras;

        for (i = 0; i < topologia.get(0).size(); i++) {
            for (j = 0; j < topologia.get(0).size(); j++) {
                if (topologia.get(i).get(j)) {
                    enlace = new Enlace();
                    ranuras = new ArrayList<>();
                    for (k = 0; k < cantRanuras; k++) {
                        ranuras.add(false);
                    }

                    enlace.setInicio(i);
                    enlace.setFin(j);
                    enlace.setRanuras(ranuras);

                    enlaces.add(enlace);
                }
            }
        }

        return enlaces;
    }

    public static List<DemandaInfo> obtenerDemandasMayores(List<DemandaInfo> demandasInfo) {
        List<DemandaInfo> aux = new ArrayList<>();
        DemandaInfo demandaInfo;
        int i = 0;

        while (i < demandasInfo.size()) {
            demandaInfo = demandasInfo.get(i);
            while (i < demandasInfo.size()
                    && demandaInfo.getOrigen() == demandasInfo.get(i).getOrigen()
                    && demandaInfo.getDestino() == demandasInfo.get(i).getDestino()) {
                if (demandasInfo.get(i).getX() > demandaInfo.getX()) {
                    demandaInfo = demandasInfo.get(i);
                }
                i++;
            }
            aux.add(demandaInfo);
        }

        return aux;
    }

    public static void obtenerRanurasLibres(List<Boolean> ranuras, List<Integer> posicionesLibres) {
        for (int i = 0; i < ranuras.size(); i++) {
            if (!ranuras.get(i)) {
                posicionesLibres.add(i);
            }
        }

    }

    public static void agregarBloqueado(Solucion solucion, DemandaInfo demandaInfo) {
        Ruteo ruteo = new Ruteo();

        ruteo.setDemandaId(demandaInfo.getDemandaId());
        ruteo.setOriden(demandaInfo.getOrigen());
        ruteo.setDestino(demandaInfo.getDestino());
        ruteo.setRutaNro(-1);

        solucion.getRuteos().add(ruteo);
    }

    // Suma las distancias de las rutas utilizadas en esta solucion
    public static Double getSaltoMayor(Solucion solucion, List<DemandaInfo> demandasInfo) {
        Double distancia = 0.0;
        for (int i = 0; i < solucion.getRuteos().size(); i++) {
            if (solucion.getRuteos().get(i).getRutaNro() > -1) {
                distancia = distancia
                        + (demandasInfo.get(solucion.getRuteos().get(i).getRutaNro()).getSaltos());
            }
        }

        return distancia;
    }

    // Suma las distancias de las rutas utilizadas en esta solucion
    public static Double getCostoDeLaSolucion(Solucion solucion, List<DemandaInfo> demandasInfo) {
        Double costo = 0.0;
        for (int i = 0; i < solucion.getRuteos().size(); i++) {
            if (solucion.getRuteos().get(i).getRutaNro() > -1) {
                costo = costo
                        + (demandasInfo.get(solucion.getRuteos().get(i).getRutaNro()).getSaltos()
                        * (demandasInfo.get(solucion.getRuteos().get(i).getRutaNro()).getTraf() /*+ nivelDeModulacion*/));
            }
        }

        return costo;
    }

    public static Double getEspectroMayor(Solucion solucion) {
        Double mayor = 0.0;
        int j;
        for (int i = 0; i < solucion.getEnlaces().size(); i++) {
            for (j = 0; j < solucion.getEnlaces().get(i).getRanuras().size(); j++) {
                if (solucion.getEnlaces().get(i).getRanuras().get(j) && j > mayor) {
                    mayor = Double.valueOf(j);
                }
            }
        }

        return mayor;
    }

    public static Double getCostoMayor(List<DemandaInfo> demandaInfoList) {
        Double mayor = 0.0;
        for (int i = 0; i < demandaInfoList.size(); i++) {
            if (demandaInfoList.get(i).getX() > mayor) {
                mayor = Double.valueOf(demandaInfoList.get(i).getX());
            }
        }
        return mayor;
    }

    public static Long getSaltoMayorDeLaRed(List<DemandaInfo> demandaInfos, int k) {
        Long mayor = Long.valueOf(0);
        Long mayorSalto = Long.valueOf(0);

        for (int i = 0; i < demandaInfos.size(); i++) {
            for (int j = i; j < k + i; j++) {
                if (demandaInfos.get(j).getSaltos() > mayor) {
                    mayor = demandaInfos.get(j).getSaltos().longValue();
                }
            }
            mayorSalto = mayorSalto + mayor;
            i = (i + k) - 1;
            mayor = Long.valueOf(0);
        }

        return mayorSalto;
    }

    public static void guardarMaximos(String archivo) throws IOException {
        File f = new File(archivo);
        FileWriter fw = new FileWriter(f);

        try {
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter wr = new PrintWriter(bw);

            //wr.write(costoMayor.toString() + SALTO_LINEA);
            //wr.write(saltoMayor.toString() + SALTO_LINEA);
            wr.write(espectroMayor.toString());
            wr.close();
            bw.close();
        } catch (IOException e) {

        }
    }

    public static int getCostoMayorDeLaRed(List<DemandaInfo> demandaInfoList) {
        int costoMayor = 0;

        for (int i = 0; i < demandaInfoList.size(); i++) {
            if ((demandaInfoList.get(i).getTraf() /*+ nivelDeModulacion*/) > costoMayor) {
                costoMayor = demandaInfoList.get(i).getTraf() /*+ nivelDeModulacion*/;
            }
        }

        return costoMayor;
    }

}
