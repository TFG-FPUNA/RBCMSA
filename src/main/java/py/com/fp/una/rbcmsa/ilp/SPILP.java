/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ilp;

/**
 *
 * @author Ivan
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.*;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
import static py.com.fp.una.rbcmsa.ilp.SLILP.isNumeric;
import py.com.fp.una.rbcmsa.ilp.propios.Adaptaciones;
import py.com.fp.una.rbcmsa.ilp.propios.model.AlphaR;
import py.com.fp.una.rbcmsa.peticion.model.PeticionBCM;

public class SPILP {

    @Inject
    Adaptaciones adaptacionesBean;
    public static Integer numeroNodo = 4;
    public static Double ghzF = 12.5;
    public static Integer numeroTransponder;
    public static Integer ftmax = 20;
    public static Double ferc = 0.0;
    public static Integer nl = 4;
    public static Double alphax = 1.0;
    public static String topologia = "6nodos";//"dfn_bwin";

    public void ILP(String ruta, String nombreFaseI, String nombreFaseII, Integer k, List<PeticionBCM> peticionesFinales,
            Grafo grafo, String guardBan, int fTotal) throws IOException {

        for (int pruebas = 0; pruebas </*3*/ 1; pruebas++) {

            limpiar();
            AlphaR alphaR = adaptacionesBean.preparaArchivoFaseIILP(ruta, nombreFaseI, k + "", peticionesFinales, grafo, guardBan + "");

            CSVFinlaWriter.generarCabeceraCSVEstatico(pruebas + 1);

            long tiempoIni, tiempoFin, tiempo;
            Integer numTotalTransponder;
            Integer valorCarga = 50;

            System.out.println("----- PRUEBA " + pruebas + "-----");

            //Guardar en una lista de resultados estaticos.
            List<ResultadoEstatico> results = new ArrayList<ResultadoEstatico>();

            cplexFaseI(1, k, peticionesFinales.size());
            Integer espectroTotal = calcularEspectroTotal();
            ArrayList<Integer> indiceCaminosElegidos = leerSalidaCplex(k, 1);

            System.out.println("indiceCaminosElegidos.size() = " + indiceCaminosElegidos.size());
            for (int x = 0; x < indiceCaminosElegidos.size(); x++) {
                System.out.println(indiceCaminosElegidos.get(x));
            }
            adaptacionesBean.preparaArchivoFaseIIILP(ruta, nombreFaseII, k, peticionesFinales, grafo, guardBan + "", fTotal, alphaR, indiceCaminosElegidos);
            cplexFaseII(1);
            List<Integer> posicionesFS = obtenerFFaseII(1);
            adaptacionesBean.asigarGrafoILP(indiceCaminosElegidos, posicionesFS, grafo, peticionesFinales, k);
        }

    }

    public static void limpiar() {
        String direccion = "./src/resultados/";

        int k = 0;
        while (k <= 6) {
            String directoriostr = new String(direccion);
            if (k != 0) {
                directoriostr += "k" + k + "/";
            }
            File directorio = new File(directoriostr);
            if (directorio.isDirectory()) {
                String[] files = directorio.list();
                if (files.length > 0) {
                    //System.out.println(" Directorio vacio: " + direccion);
                    for (String archivo : files) {
                        //if (!"solicitudesrandom1.txt".equals(archivo)){
                        File f = new File(directoriostr + archivo);
                        f.delete();
                        //}
                        //System.out.println(archivo);

                    }
                }
            }
            k++;
        }
        k = 1;
        while (k <= 6) {
            String directoriostr = new String(direccion);

            directoriostr += "k" + k + "/";
            File directorio = new File(directoriostr);
            directorio.mkdir();
            k++;
        }
        direccion = "./src/resultadosT/";
        k = 0;
        while (k <= 6) {
            String directoriostr = new String(direccion);
            if (k != 0) {
                directoriostr += "k" + k + "/";
            }
            File directorio = new File(directoriostr);
            if (directorio.isDirectory()) {
                String[] files = directorio.list();
                if (files.length > 0) {
                    //System.out.println(" Directorio vacio: " + direccion);
                    for (String archivo : files) {
                        //if (!"solicitudesrandom1.txt".equals(archivo)){
                        File f = new File(directoriostr + archivo);
                        f.delete();
                        //}
                        //System.out.println(archivo);

                    }
                }
            }
            k++;
        }
        k = 1;
        while (k <= 6) {
            String directoriostr = new String(direccion);

            directoriostr += "k" + k + "/";
            File directorio = new File(directoriostr);
            directorio.mkdir();
            k++;
        }

    }

    private static void cargarGrafo(SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph) {
        try {

            FileReader fr = new FileReader("./src/main/resources/ILP/entrada/edges" + topologia + ".txt");
            String[] edgeText;
            DefaultWeightedEdge ed;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    directedGraph.addVertex(edgeText[0]);
                    directedGraph.addVertex(edgeText[1]);
                    ed = directedGraph.addEdge(edgeText[0], edgeText[1]);
                    directedGraph.setEdgeWeight(ed, Double.parseDouble(edgeText[2]));
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SPILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void calcularCaminosMasCortos(SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph, String origen, String destino, int contadorEscritura, int k, int y) {
        try {
            int cont = 0;
            FileWriter fw = new FileWriter("./src/resultados/kshortestpath" + y + ".txt");
            FileWriter fw2;
            if (contadorEscritura == 0) {
                fw2 = new FileWriter("./src/resultados/kshortestpathCompleto" + y + ".txt");
            } else {
                fw2 = new FileWriter("./src/resultados/kshortestpathCompleto" + y + ".txt", true);
            }
            BufferedWriter bw = new BufferedWriter(fw);
            BufferedWriter bw2 = new BufferedWriter(fw2);
            try (PrintWriter salida = new PrintWriter(bw); PrintWriter salida2 = new PrintWriter(bw2)) {

                KShortestPaths caminosCandidatos = new KShortestPaths(directedGraph, origen, k);

                System.out.println("K-shortest path de " + origen + " a " + destino + " es ");

                List<GraphPath<String, DefaultEdge>> paths = caminosCandidatos.getPaths(destino);

                int contAux = 0;
                for (Iterator<GraphPath<String, DefaultEdge>> it = paths.iterator(); it.hasNext();) {
                    contAux = contAux + 1;
                    GraphPath<String, DefaultEdge> path = it.next();
                    System.out.print("Camino " + ++cont + ": ");
                    for (Iterator<DefaultEdge> it2 = path.getEdgeList().iterator(); it2.hasNext();) {
                        DefaultEdge edge = it2.next();
                        System.out.print("<" + edge + ">\t");
                        salida.print("<" + edge + ">\t");
                        salida2.print(edge + "\t");
                    }
                    System.out.println(": " + path.getWeight());
                    salida.println(": \t" + path.getWeight());
                    salida2.println(": \t" + path.getWeight());
                }

                if (contAux < k) {
                    while (contAux < k) {
                        Iterator<GraphPath<String, DefaultEdge>> it = caminosCandidatos.getPaths(destino).iterator();
                        contAux = contAux + 1;
                        GraphPath<String, DefaultEdge> path = it.next();
                        System.out.print("Camino " + ++cont + ": ");
                        for (Iterator<DefaultEdge> it2 = path.getEdgeList().iterator(); it2.hasNext();) {
                            DefaultEdge edge = it2.next();
                            System.out.print("<" + edge + ">\t");
                            salida.print("<" + edge + ">\t");
                            salida2.print(edge + "\t");
                        }
                        System.out.println(": " + path.getWeight());
                        salida.println(": \t" + path.getWeight());
                        salida2.println(": \t" + path.getWeight());
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(SPILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String leerSolicitudes(SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph, int k, int y, ArrayList<Solicitud> listaSolicitudes) {

        final double C = 154;

        ArrayList<Parametro> listaParams = leerParametros();

        String vectorAlfa = "[[";

        Double ftotal = 0.0;

        int contadorEscritura = 0;
        for (Iterator<Solicitud> it = listaSolicitudes.iterator(); it.hasNext();) {
            Solicitud solicitud = it.next();

            System.out.print("Origen: " + solicitud.getOrigen() + "\t"
                    + "Destino:" + solicitud.getDestino() + "\t"
                    + "FS requerida:" + solicitud.getVelocidad() + "\n");
            calcularCaminosMasCortos(directedGraph, solicitud.getOrigen(), solicitud.getDestino(), contadorEscritura, k, y);
            ArrayList<Double> listaDistancias = leerDistancias(y);
            Double tsd;
            int cont = 0;
            for (Iterator<Double> it2 = listaDistancias.iterator(); it2.hasNext();) {
                Double dist = it2.next();
                System.out.println("Distancia de camino " + ++cont + ": " + dist);
                int nivelModulacionCorrespondiente = leerNivelModulacionCorrespondiente(dist, listaParams, y);
                System.out.println("Nivel de ModulaciÃ³n correspondiente es " + nivelModulacionCorrespondiente);
                System.out.println("alfa = " + Math.ceil(solicitud.getVelocidad() * C / (nivelModulacionCorrespondiente * C)));
                Double d = Math.ceil(solicitud.getVelocidad() * C / (nivelModulacionCorrespondiente * C));

                if (it.hasNext()) {
                    if (it2.hasNext()) {
                        vectorAlfa = vectorAlfa + d.intValue() + ",";
                    } else {
                        vectorAlfa = vectorAlfa + d.intValue() + "],[";
                    }
                } else if (it2.hasNext()) {
                    vectorAlfa = vectorAlfa + d.intValue() + ",";
                } else {
                    vectorAlfa = vectorAlfa + d.intValue();
                }
                tsd = Math.ceil(solicitud.getVelocidad() * C / C);
                System.out.println("Tsd = " + tsd);
                ftotal += tsd;
            }
            contadorEscritura++;
        }

        vectorAlfa = vectorAlfa + "]];";

        //System.out.println("vectorAlfa = " + vectorAlfa);
        System.out.println("ftotal = " + ftotal / C + ";");
        //Sumatoria de todos los Tsd de los caminos / C 
        return vectorAlfa;
    }

    private static String leerSolicitudesSegundaFase(SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph, ArrayList<Integer> indiceCaminosElegidos, int y) {

        final double C = 154;

        ArrayList<Parametro> listaParams = leerParametros();

        String vectorAlfa = "[";

        ArrayList<Solicitud> listaSolicitudes = new ArrayList();
        try {

            FileReader fr = new FileReader("./src/main/resources/ILP/entrada/solicitudesilp" + y + ".txt");
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    Solicitud sol = new Solicitud(edgeText[0], edgeText[1], Double.parseDouble(edgeText[2]));
                    listaSolicitudes.add(sol);
                }
            }

            Double ftotal = 0.0;

            int cont = 0;
            System.out.println("DEBUG: indiceCaminosElegidos -> " + indiceCaminosElegidos.toString());
            System.out.println("DEBUG: listaSolicitudes -> " + listaSolicitudes.toString());
            for (Iterator<Solicitud> it = listaSolicitudes.iterator(); it.hasNext();) {
                //if (cont == indiceCaminosElegidos.size()) break;
                Solicitud solicitud = it.next();

                System.out.print("Origen: " + solicitud.getOrigen() + "\t"
                        + "Destino:" + solicitud.getDestino() + "\t"
                        + "FS requerida:" + solicitud.getVelocidad() + "\n");
                //calcularCaminosMasCortos(directedGraph, solicitud.getOrigen(), solicitud.getDestino(), contadorEscritura);
                ArrayList<Double> listaDistancias = leerDistanciasTodasSolicitudes(y);
                Double tsd;

                //for (int cont = 0; cont < indiceCaminosElegidos.size(); cont++) {
                System.out.println("DEBUG: cont -> " + cont);
                Double dist = listaDistancias.get(indiceCaminosElegidos.get(cont) - 1);
                //if(indiceCaminosElegidos.get(cont) == cont + 1) {
                cont = cont + 1;
                System.out.println("Distancia de camino " + cont + ": " + dist);
                int nivelModulacionCorrespondiente = leerNivelModulacionCorrespondiente(dist, listaParams, y);
                System.out.println("Nivel de ModulaciÃ³n correspondiente es " + nivelModulacionCorrespondiente);
                System.out.println("alfa = " + Math.ceil(solicitud.getVelocidad() * C / (nivelModulacionCorrespondiente * C)));
                Double d = Math.ceil(solicitud.getVelocidad() * C / (nivelModulacionCorrespondiente * C));

                if (it.hasNext()) {
                    vectorAlfa = vectorAlfa + d.intValue() + ",";
                } else {
                    vectorAlfa = vectorAlfa + d.intValue();
                }
                tsd = Math.ceil(solicitud.getVelocidad() * C / C);
                System.out.println("Tsd = " + tsd);
                ftotal += tsd;
                //}
                //}
            }

            vectorAlfa = vectorAlfa + "];";

            //System.out.println("vectorAlfa = " + vectorAlfa);
            System.out.println("ftotal = " + ftotal / C + ";");

        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SPILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return vectorAlfa;
    }

    private static ArrayList<Parametro> leerParametros() {

        ArrayList<Parametro> listaParametros = new ArrayList();

        try {

            FileReader fr = new FileReader("./src/main/resources/ILP/entrada/modulacion.txt");
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    Parametro param = new Parametro(Double.parseDouble(edgeText[0]), Integer.parseInt(edgeText[1]));
                    listaParametros.add(param);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SPILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return listaParametros;
    }

    public static Configuracion leerConfiguraciones() {

        Configuracion config = null;

        try {
            String ruta = "./src/main/resources/ILP/configuracion/configuraciones.txt";
            FileReader fr = new FileReader(ruta);
            String texto = new String();
            String configText[];

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    texto += line + "\t";
                }
                configText = texto.split("\t");
                config = new Configuracion(Integer.parseInt(configText[0]), Integer.parseInt(configText[1]), Integer.parseInt(configText[2]));
                numeroNodo = Integer.parseInt(configText[3]);
                ghzF = Double.parseDouble(configText[4]);
                numeroTransponder = Integer.parseInt(configText[5]);
                ftmax = Integer.parseInt(configText[6]);
                ferc = Double.parseDouble(configText[7]);
                nl = Integer.parseInt(configText[8]);
                alphax = Double.parseDouble(configText[9]);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SPILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return config;
    }

    private static ArrayList<Double> leerDistancias(int y) {

        ArrayList<Double> listaDistancias = new ArrayList();
        try {

            FileReader fr = new FileReader("./src/resultados/kshortestpath" + y + ".txt");
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    Double dist = Double.parseDouble(edgeText[edgeText.length - 1]);
                    listaDistancias.add(dist);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SPILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return listaDistancias;
    }

    private static ArrayList<Double> leerDistanciasTodasSolicitudes(int y) {

        ArrayList<Double> listaDistancias = new ArrayList();
        try {

            FileReader fr = new FileReader("./src/resultados/kshortestpathCompleto" + y + ".txt");
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    Double dist = Double.parseDouble(edgeText[edgeText.length - 1]);
                    listaDistancias.add(dist);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SPILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return listaDistancias;
    }

    private static int leerNivelModulacionCorrespondiente(Double distancia, ArrayList<Parametro> listaParams, int y) {
        int nivMod = 1;

        for (Parametro parametroMod : listaParams) {
            if (distancia <= parametroMod.getDistanciaMaxima()) {
                nivMod = parametroMod.getNivelModulacion();
            }
        }
        return nivMod;
    }

    private static ArrayList<ArrayList<String>> leerTotalCaminos(int y) {

        ArrayList<ArrayList<String>> listaCaminos = new ArrayList();

        try {
            FileReader fr = new FileReader("./src/resultados/kshortestpathCompleto" + y + ".txt");
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    ArrayList<String> listaEnlaces = new ArrayList();
                    edgeText = line.split("\\)");
                    String dist;
                    for (int i = 0; i < edgeText.length - 1; i++) {
                        dist = edgeText[i].trim().substring(1);
                        listaEnlaces.add(dist);
                    }
                    listaCaminos.add(listaEnlaces);
                }

            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SPILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return listaCaminos;
    }

    private static ArrayList<Integer> leerSalidaCplex(int k, int y) throws FileNotFoundException {
        ArrayList<Integer> listaCaminosElegidos = new ArrayList();
        FileReader fr = new FileReader("./src/resultados/salidaCplexSP-ILP_1" + y + ".txt");
        String[] edgeText;
        String texto = "";

        try (BufferedReader entrada = new BufferedReader(fr)) {
            String line;

            while ((line = entrada.readLine()) != null) {
                texto += line;
            }
            entrada.close();
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * Calculo de caminos elegidos
         */
        edgeText = texto.split("x = ");
        String texto2 = edgeText[1];
        //System.out.println("texto2 = " + texto2);
        texto2 = texto2.replace("]             [", "\n").replace(" ", "").replace("[", "").replace("]", "").replace(";", "");
        System.out.println("x = \n" + texto2);

        String[] textoZ = texto2.split("\n");

        for (int i = 0; i < textoZ.length; i++) {
            for (int j = 0; j < textoZ[i].length(); j++) {
                if (textoZ[i].charAt(j) == '1') {
                    listaCaminosElegidos.add((i + 1) * k - k + j + 1);
                    break;
                }
            }
        }
        return listaCaminosElegidos;
    }

    private static ArrayList<ArrayList<String>> leerSoloCaminosElegidos(ArrayList<Integer> indiceCaminosElegidos, int y) {
        ArrayList<ArrayList<String>> listaCaminosElegidos = new ArrayList();
        try {
            FileReader fr = new FileReader("./src/resultados/kshortestpathCompleto" + y + ".txt");
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                int contador = 1;
                while ((line = entrada.readLine()) != null) {
                    if (indiceCaminosElegidos.contains(contador)) {
                        ArrayList<String> listaEnlaces = new ArrayList();
                        edgeText = line.split("\\)");
                        String dist;
                        for (int i = 0; i < edgeText.length - 1; i++) {
                            dist = edgeText[i].trim().substring(1);
                            listaEnlaces.add(dist);
                        }
                        listaCaminosElegidos.add(listaEnlaces);
                    }
                    contador = contador + 1;
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SPILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return listaCaminosElegidos;
    }

    public static void capaVirtual(String nombreArchivo, int y) throws
            IOException {
        //en El route.dat tres dagtos el cual tenes escribir en el y salidaRout.txt tenemos que leer
        // n nodos, t tamanho de sltos en GHZ, d demanda, C Listado de capacitados,S listados de nodos inicios,E listados de nodos fin
        double divisionSolicitud = 0.0;
        double divisionSolicitud2 = 0.0;
        int indiceInicio1 = 0;
        crearRouteData(nombreArchivo, y);
        long time_start1, time_end1;
        String argumentos[] = {"-v", "route.mod", "./src/resultados/route" + y + ".dat", "./src/resultados/salidaRoute" + y + ".txt"};
        time_start1 = System.currentTimeMillis();
        OplRunILP.main(argumentos);
        time_end1 = System.currentTimeMillis();
        System.out.println("La cota de la EON ha tomado " + (time_end1 - time_start1) + " milisegundos");
        String text = leerArchivo("./src/resultados/salidaRoute" + y + ".txt");
        String flujo = text.split("enlaceUsado")[0].split("=")[1];
        //String enlacesUsados = text.split("=")[2];
        String[] enlacesUsados2 = flujo.split("]");
        int i = 0;
        FileWriter fw2 = new FileWriter("./src/resultados/solicitudesilp" + y + ".txt");
        BufferedWriter bw2 = new BufferedWriter(fw2);
        try (PrintWriter salida2 = new PrintWriter(bw2)) {
            for (String pru : enlacesUsados2) {
                String[] prueba = pru.split("\\[");
                if (prueba.length > 1) {
                    for (String num : prueba) {
                        String[] s = num.split(" ");
                        if (s.length > 0) {
                            int j = 0;
                            for (String var : s) {
                                if (isNumeric(var)) {
                                    double numero = Double.parseDouble(var);
                                    if (numero != 0) {
                                        if ((int) numero - ((ftmax - 1) * alphax) > 0) {
                                            divisionSolicitud = numero / (double) ((ftmax - 1) * alphax);
                                            divisionSolicitud2 = Math.ceil(divisionSolicitud);
                                            for (int ij = 0; ij < divisionSolicitud2; ij++) {
                                                if (ij == divisionSolicitud2 - 1) {
                                                    int num12 = (int) numero - (int) (Math.floor(divisionSolicitud) * ((ftmax - 1) * alphax));
                                                    if (num12 == 0) {
                                                        salida2.println(i + "\t" + j + "\t" + (int) ((ftmax - 1) * alphax));
                                                        System.out.println(i + "\t" + j + "\t" + (int) ((ftmax - 1) * alphax));
                                                    } else {
                                                        salida2.println(i + "\t" + j + "\t" + num12);
                                                        System.out.println(i + "\t" + j + "\t" + num12);
                                                    }

                                                } else {
                                                    int num12 = (int) ((ftmax - 1) * alphax);
                                                    salida2.println(i + "\t" + j + "\t" + num12);
                                                    System.out.println(i + "\t" + j + "\t" + num12);
                                                }
                                            }
                                        } else {
                                            salida2.println(i + "\t" + j + "\t" + var);
                                            System.out.println(i + "\t" + j + "\t" + var);
                                        }

                                    }

                                    j = j + 1;
                                }
                            }
                        }

                    }
                    i = i + 1;
                }
            }
        }
    }

    private static void crearRouteData(String nombreArchivo, int y) {
        ArrayList<Solicitud> listaSolicitudes = new ArrayList();
        try {

            FileReader fr = new FileReader(nombreArchivo);

            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;

                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    Double velocidadGHZ = (Double.parseDouble(edgeText[3]) + (Double.parseDouble(edgeText[3]) * ferc));
                    Solicitud sol = new Solicitud(Integer.parseInt(edgeText[0]), edgeText[1], edgeText[2], velocidadGHZ, Integer.parseInt(edgeText[4]));
                    listaSolicitudes.add(sol);

                }
            }
            FileWriter fw2 = new FileWriter("./src/resultados/route" + y + ".dat");
            BufferedWriter bw2 = new BufferedWriter(fw2);

            try (PrintWriter salida2 = new PrintWriter(bw2)) {
                //n Cantidad de numeros de nodos dentro de la red
                System.out.println("n = " + numeroNodo + ";");
                salida2.println("n = " + numeroNodo + ";");
                //t tamaÃ±o de  los sltos
                System.out.println("t = " + ghzF + ";");
                salida2.println("t = " + ghzF + ";");
                //d Cantidad de demandas
                System.out.println("d = " + listaSolicitudes.size() + ";");
                salida2.println("d = " + listaSolicitudes.size() + ";");
                // C S E
                //C GBPS de cada demanda
                System.out.print("C = [");
                salida2.print("C = [");
                for (Iterator<Solicitud> it = listaSolicitudes.iterator(); it.hasNext();) {
                    Solicitud solucitud = it.next();
                    System.out.print(solucitud.getVelocidad());
                    salida2.print(solucitud.getVelocidad());
                    if (it.hasNext()) {
                        System.out.print(",");
                        salida2.print(",");
                    } else {
                        System.out.println("];");
                        salida2.println("];");
                    }
                }
                //S nodo fuente de cada demanda
                System.out.print("S = [");
                salida2.print("S = [");
                for (Iterator<Solicitud> it = listaSolicitudes.iterator(); it.hasNext();) {
                    Solicitud solucitud = it.next();
                    System.out.print(solucitud.getOrigen());
                    salida2.print(solucitud.getOrigen());
                    if (it.hasNext()) {
                        System.out.print(",");
                        salida2.print(",");
                    } else {
                        System.out.println("];");
                        salida2.println("];");
                    }
                }
                //E no fin de cada demanda
                System.out.print("E = [");
                salida2.print("E = [");
                for (Iterator<Solicitud> it = listaSolicitudes.iterator(); it.hasNext();) {
                    Solicitud solucitud = it.next();
                    System.out.print(solucitud.getDestino());
                    salida2.print(solucitud.getDestino());
                    if (it.hasNext()) {
                        System.out.print(",");
                        salida2.print(",");
                    } else {
                        System.out.println("];");
                        salida2.println("];");
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SLILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static String leerArchivo(String nombreArchivo) {
        String texto = new String();
        try {
            FileReader fr = new FileReader(nombreArchivo);
            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                int lineaLectura = 0;
                while ((line = entrada.readLine()) != null) {
                    lineaLectura = lineaLectura + 1;
                    if (lineaLectura >= 11) {
                        texto += line + "\n";
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SLILP.class
                            .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return texto;
    }

    private void cplexFaseII(int y) throws FileNotFoundException, IOException {
        long time_start2, time_end2;
        /* Probamos la conexiÃ³n de Java con CPLEX */
        String argumentos2[] = {"-v", "./src/SPILP_2/SP-ILP_2.mod", "./src/resultados/SP-ILP_2" + y + ".dat", "./src/resultados/salidaCplexSP-ILP_2" + y + ".txt"};

        time_start2 = System.currentTimeMillis();
        OplRunILP.main(argumentos2);

        time_end2 = System.currentTimeMillis();

        System.out.println("La fase 2 de ILP 2 ha tomado " + (time_end2 - time_start2) + " milisegundos");
        //System.out.println("EL ILP 2 ha tomado " + (time_end1 - time_start1 + time_end2 - time_start2) + " milisegundos");
    }

    private List<Integer> obtenerFFaseII(int y) {
        String f = leerArchivo("./src/resultados/salidaCplexSP-ILP_2" + y + ".txt");
        f = f.substring(f.indexOf("f") + 5, f.indexOf("]"));
        System.out.println("F: " + f);
        String[] t2 = f.split(" ");
        
        List<Integer> posicionesFS = new ArrayList<>();
        for (String t1 : t2) {
            if (!t1.equals("")) {
                posicionesFS.add(Integer.parseInt(t1));
            }
        }
        
        return posicionesFS;
    }

    private Integer cplexFaseI(int y, int k, int cantSolicitudes) throws IOException {
        long time_start1, time_end1;

        FileReader fr;
        /* Probamos la conexiÃ³n de Java con CPLEX */
        String argumentos[] = {"-v", "./src/SPILP_1/SP-ILP_1.mod", "./src/resultados/SP-ILP_1" + y + ".dat", "./src/resultados/salidaCplexSP-ILP_1" + y + ".txt"};

        time_start1 = System.currentTimeMillis();

        OplRunILP.main(argumentos);

        time_end1 = System.currentTimeMillis();

        System.out.println("La cota de la ILP 2 ha tomado " + (time_end1 - time_start1) + " milisegundos");

//        String f = leerArchivo("./src/resultados/salidaCplexSP-ILP_2"+y+".txt");
//        f = f.substring(f.indexOf("f")+5, f.indexOf("]"));
//        String [] t2 = f.split(" ");
//        int numero = 0;
//        for(String t1 : t2){
//            if(!t1.equals("")){
//             numero++;
//            }
//        }
//        String [] t = new String[numero];
//        int canti = 0;
//         for(String t1 : t2){
//            if(!t1.equals("")){
//             t[canti] = t1;
//             canti++;
//            }
//        }
//        
//        //System.out.println(t[0] + "-" + t[1] + "-" +  t[2]);
//        
//         FileReader nodos = new FileReader("./src/main/resources/ILP/entrada/solicitudesilp"+y+".txt");
//         String nodosstr = "";
//            BufferedReader n = new BufferedReader(nodos);
//                String line;
//                while ((line = n.readLine()) != null) {
//                    if(!line.equals("")){
//                         nodosstr += line + "\n";
//                    }
//                   
//                }
//        String [] lineas = nodosstr.split("\n");
//        String [] inicio = new String[t.length];
//        String [] fin = new String[t.length];;
//        
//        int cont = 0;
//        for (String temp: lineas){
//            String [] temp2;
//            temp2 = temp.split("\t");
//            inicio[cont] = temp2[0];
//            fin[cont]= temp2[1];
//            cont++;
//        }
//        alfaSegundaFase = alfaSegundaFase.substring(1,alfaSegundaFase.length()-2);
//        String[] alfax = alfaSegundaFase.split(",");
//        
//        return espectroTotal;
        return null;

    }

    private static Integer calcularEspectroTotal() throws FileNotFoundException {
        FileReader fr = new FileReader("./src/resultados/salidaCplexSP-ILP_11.txt");
        String[] edgeText;
        String texto = "";

        try (BufferedReader entrada = new BufferedReader(fr)) {
            String line;

            while ((line = entrada.readLine()) != null) {
                texto += line;
            }
            entrada.close();
        } catch (IOException ex) {
            Logger.getLogger(SPILP.class.getName()).log(Level.SEVERE, null, ex);
        }

        /**
         * Calculo de suma total de espectro utilizado
         */
        String[] lista = texto.split("F = \\[");
        lista = lista[1].split("\\];");
        String espectrostmp = lista[0].replace("[", "").replace("]", "");
        String[] espectros = espectrostmp.split(" ");
        Integer sumaEspectro = 0;
        for (String tmp : espectros) {
            if (!"".equals(tmp) && !"0".equals(tmp)) {
                sumaEspectro += Integer.parseInt(tmp);
            }
        }
        return sumaEspectro;
    }

}
