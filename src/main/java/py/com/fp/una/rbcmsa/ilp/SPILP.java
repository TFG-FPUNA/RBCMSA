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
import org.jgrapht.GraphPath;
import org.jgrapht.alg.KShortestPaths;
import org.jgrapht.graph.*;
import static py.com.fp.una.rbcmsa.ilp.SLILP.isNumeric;

public class SPILP {
    
    public static Integer numeroNodo = 4 ;
    public static Double  ghzF = 12.5;
    public static Integer numeroTransponder;
    public static Integer ftmax = 20;
    public static Double ferc = 0.0;
    public static Integer nl =4;
    public static Double alphax=1.0;
    public static String topologia = "dfn_bwin";
    
    

    public void ILP() throws IOException {
        
        Configuracion config = leerConfiguraciones();
        String datos[] = new String[3];
        datos[0] = numeroNodo.toString();
        datos[1]= "1";
        System.out.println("----- CANTIDAD DE PRUEBAS: 3 -----");
        System.out.println("----- CANTIDAD DE NODOS: " + Integer.parseInt(datos[0]) + "-----");
        
        for (int pruebas = 0; pruebas</*3*/1; pruebas++){
        
            limpiar();
            CSVFinlaWriter.generarCabeceraCSVEstatico(pruebas+1);
          
            long tiempoIni, tiempoFin, tiempo;
            Integer numTotalTransponder, espectroTotal;
            Integer valorCarga = 50;
            
            System.out.println("----- PRUEBA " + pruebas + "-----");
            

            //Guardar en una lista de resultados estaticos.
            List<ResultadoEstatico> results = new ArrayList<ResultadoEstatico>();

            for (int carga = 0; carga</*10*/1; carga++){//10 tipos de carga  
                
                System.out.println("----- EJECUCION:  " + carga + "-----");
                System.out.println("----- CANTIDAD DE SOLICITUDES: " + valorCarga + "-----");

                datos[2] = valorCarga.toString();
                //CargaAleatoria.main(datos); para dejar uno dijo se comenta

                tiempoIni = System.currentTimeMillis();

                //capaVirtual("./src/resultados/solicitudesrandom" + 1 + ".txt", 1);//Metodo que creÃ³ Santi...
                ArrayList<Solicitud> listaSolicitudes = new ArrayList();
                try{
                 FileReader fr = new FileReader("./src/resultados/solicitudesilp"+1+".txt");// este es para que sea fijo
                    String[] edgeText;

                    try (BufferedReader entrada = new BufferedReader(fr)) {
                        String line;
                        while ((line = entrada.readLine()) != null) {
                            edgeText = line.split("\t");
                            Solicitud sol = new Solicitud( edgeText[0],edgeText[1], Double.parseDouble(edgeText[2]));
                            listaSolicitudes.add(sol);
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

                espectroTotal = cplex(config, 1,listaSolicitudes);
                //numTotalTransponder = capaTransponder(config,1,listaSolicitudes);
                //espectroTotal = leer archivo de salida 1 para carga total
//                tiempoFin = System.currentTimeMillis();
//                tiempo = tiempoFin - tiempoIni;
//
//                ResultadoEstatico resultado = new ResultadoEstatico();
//                resultado.espectro = espectroTotal;
//                //resultado.numTotalTransponder = numTotalTransponder;
//                resultado.tiempo = tiempo;
//                resultado.cantSolicitudes = valorCarga;
//
//                results.add( resultado);
//
//                CSVFinlaWriter.addToCSVEstatico(resultado, pruebas+1);
//
//                valorCarga+=50;
            }
        }
        
    }
    
    public static void limpiar() {
        String direccion = "./src/resultados/";

        
        int k=0;
        while (k<=6){
            String directoriostr = new String(direccion);
            if (k!=0){
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
        k=1;
        while (k<=6){
            String directoriostr = new String(direccion);
            
                directoriostr += "k" + k + "/";
            File directorio = new File (directoriostr);
            directorio.mkdir();
            k++;
        }
        direccion = "./src/resultadosT/";
         k=0;
        while (k<=6){
            String directoriostr = new String(direccion);
            if (k!=0){
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
        k=1;
        while (k<=6){
            String directoriostr = new String(direccion);
            
                directoriostr += "k" + k + "/";
            File directorio = new File (directoriostr);
            directorio.mkdir();
            k++;
        }

    }
    
    private static void cargarGrafo(SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph) {
        try {

            FileReader fr = new FileReader("edges" + topologia +".txt");
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
            FileWriter fw = new FileWriter("./src/resultados/kshortestpath"+y+".txt");
            FileWriter fw2;
            if (contadorEscritura == 0) {
                fw2 = new FileWriter("./src/resultados/kshortestpathCompleto"+y+".txt");
            } else {
                fw2 = new FileWriter("./src/resultados/kshortestpathCompleto"+y+".txt", true);
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
                    int nivelModulacionCorrespondiente = leerNivelModulacionCorrespondiente(dist, listaParams,y);
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

            FileReader fr = new FileReader("./src/resultados/solicitudesilp"+y+".txt");
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    Solicitud sol = new Solicitud( edgeText[0],edgeText[1], Double.parseDouble(edgeText[2]));
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
                int nivelModulacionCorrespondiente = leerNivelModulacionCorrespondiente(dist, listaParams,y);
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

            FileReader fr = new FileReader("modulacion.txt");
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

            FileReader fr = new FileReader("configuraciones.txt");
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

            FileReader fr = new FileReader("./src/resultados/kshortestpath"+y+".txt");
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

            FileReader fr = new FileReader("./src/resultados/kshortestpathCompleto"+y+".txt");
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
            FileReader fr = new FileReader("./src/resultados/kshortestpathCompleto"+y+".txt");
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

    private static ArrayList<Integer> leerSalidaCplex(int k, int cantTotalCaminos, int cantSolicitudes, int y) throws FileNotFoundException {
        ArrayList<Integer> listaCaminosElegidos = new ArrayList();
        FileReader fr = new FileReader("./src/resultados/salidaCplexSP-ILP_1"+y+".txt");
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
            FileReader fr = new FileReader("./src/resultados/kshortestpathCompleto"+y+".txt");
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
    
    public static void  capaVirtual(String nombreArchivo, int y) throws 
            IOException{
        //en El route.dat tres dagtos el cual tenes escribir en el y salidaRout.txt tenemos que leer
        // n nodos, t tamanho de sltos en GHZ, d demanda, C Listado de capacitados,S listados de nodos inicios,E listados de nodos fin
         double divisionSolicitud = 0.0;
        double divisionSolicitud2 = 0.0;
         int indiceInicio1 = 0;
        crearRouteData(nombreArchivo, y);
         long time_start1, time_end1;
                String argumentos[] = {"-v", "route.mod", "./src/resultados/route"+y+".dat", "./src/resultados/salidaRoute"+y+".txt"};
                time_start1 = System.currentTimeMillis();
                OplRunILP.main(argumentos);
                time_end1 = System.currentTimeMillis();
                System.out.println("La cota de la EON ha tomado " + (time_end1 - time_start1) + " milisegundos");
                String text = leerArchivo("./src/resultados/salidaRoute"+y+".txt");
                String flujo = text.split("enlaceUsado")[0].split("=")[1];
                //String enlacesUsados = text.split("=")[2];
                String[] enlacesUsados2 = flujo.split("]");
                 int i = 0;
                  FileWriter fw2 = new FileWriter("./src/resultados/solicitudesilp"+y+".txt");
            BufferedWriter bw2 = new BufferedWriter(fw2);
             try (PrintWriter salida2 = new PrintWriter(bw2)) {
                for(String pru : enlacesUsados2){
                 String[] prueba = pru.split("\\[");
                 if(prueba.length > 1){
                  for(String num : prueba){
                      String [] s = num.split(" ");
                      if(s.length>0){
                          int j = 0;
                           for (String var: s){
                          if(isNumeric(var)){
                           double numero = Double.parseDouble(var);
                           if(numero != 0){
                               if((int)numero-((ftmax-1)*alphax)>0){
                               divisionSolicitud = numero/(double)((ftmax-1)*alphax);
                               divisionSolicitud2 = Math.ceil(divisionSolicitud);
                               for(int ij=0; ij<divisionSolicitud2; ij++){
                                   if(ij==divisionSolicitud2-1){
                                   int num12= (int)numero -(int)(Math.floor(divisionSolicitud)*((ftmax-1)*alphax));
                                   if(num12==0){
                                         salida2.println(i+"\t"+j+"\t"+(int)((ftmax-1)*alphax));
                               System.out.println(i+"\t"+j+"\t"+(int)((ftmax-1)*alphax));
                                   }else{
                                       salida2.println(i+"\t"+j+"\t"+num12);
                               System.out.println(i+"\t"+j+"\t"+num12);
                                   }
                                    
                                   }else{
                                       int num12= (int)((ftmax-1)*alphax);
                                        salida2.println(i+"\t"+j+"\t"+num12);
                                    System.out.println(i+"\t"+j+"\t"+num12);
                                   }
                               }
                               }else{
                                salida2.println(i+"\t"+j+"\t"+var);
                               System.out.println(i+"\t"+j+"\t"+var);
                               }
                              
                           }
                          
                          j = j+1;
                          }
                      }
                      }
                     
                  }
                  i = i +1;
                 }
                }
                 }
           }
    
   
    
    private static void crearRouteData(String nombreArchivo, int y){
         ArrayList<Solicitud> listaSolicitudes = new ArrayList();
        try {

            FileReader fr = new FileReader(nombreArchivo);
            
            
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                
                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    Double velocidadGHZ = (Double.parseDouble(edgeText[3])+(Double.parseDouble(edgeText[3])*ferc));
                    Solicitud sol = new Solicitud(Integer.parseInt(edgeText[0]),edgeText[1], edgeText[2], velocidadGHZ, Integer.parseInt(edgeText[4]));
                    listaSolicitudes.add(sol);
                  
                    
                }
            }
            FileWriter fw2 = new FileWriter("./src/resultados/route"+y+".dat");
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
    private static String leerArchivoTransponder(String nombreArchivo) {
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
    private static int getNumeroSolicitudesSalidas(ArrayList<Solicitud> listaSolicitudes,int numnod,String [] cantidadSlots, int ftmax){
        int countNumSolS = 0;
        double divisionSolicitud = 0.0;
        for(int numsol=0; numsol<listaSolicitudes.size(); numsol++){
                 
                   
                       if(Integer.parseInt(listaSolicitudes.get(numsol).getOrigen())== numnod){
                           if(Integer.parseInt(cantidadSlots[numsol])> ftmax){
                               divisionSolicitud = Double.parseDouble(cantidadSlots[numsol])/(double)ftmax;
                               divisionSolicitud =  Math.ceil(divisionSolicitud);
                               countNumSolS = countNumSolS+ (int) divisionSolicitud;
                           }else{
                           countNumSolS++;
                           }
                       
                      }
                       
                       
                   
                  
              }
    return countNumSolS;
    }
     private static int getNumeroSolicitudesEntrantes(ArrayList<Solicitud> listaSolicitudes,int numnod,String [] cantidadSlots, int ftmax){
        int countNumSolE = 0;
        double divisionSolicitud = 0.0;
        for(int numsol=0; numsol<listaSolicitudes.size(); numsol++){
                 if(Integer.parseInt(listaSolicitudes.get(numsol).getDestino())== numnod){
                        if(Integer.parseInt(cantidadSlots[numsol])> ftmax){
                               divisionSolicitud = Double.parseDouble(cantidadSlots[numsol])/(double)ftmax;
                               divisionSolicitud =  Math.ceil(divisionSolicitud);
                               countNumSolE =countNumSolE+ (int) divisionSolicitud;
                           }else{
                           countNumSolE++;
                           }
                      }
                       
                   
                  
              }
    return countNumSolE;
    }
     
     private static String [] getEntradasYSalidas (ArrayList<Solicitud> listaSolicitudes,int numnod,int countNumSolS ,int countNumSolE,String [] cantidadSlots,String [] indiceInicio, int ftmax ){
     int [] ISalientes = new int[countNumSolS];
        int [] OSalientes = new int[countNumSolS];
        int [] IEntrantes = new int[countNumSolE];
        int [] OEntrantes = new int[countNumSolE];
        double divisionSolicitud = 0.0;
        double divisionSolicitud2 = 0.0;
        int indiceInicio1 = 0;
        String [] resultado = new String[4];
        countNumSolS = 0;
        countNumSolE = 0;
              for(int numsol=0; numsol<listaSolicitudes.size(); numsol++){
                 if(Integer.parseInt(listaSolicitudes.get(numsol).getOrigen())== numnod){
                           if(Integer.parseInt(cantidadSlots[numsol])> ftmax){
                               divisionSolicitud = Double.parseDouble(cantidadSlots[numsol])/(double)ftmax;
                               divisionSolicitud2 =  Math.ceil(divisionSolicitud);
                               indiceInicio1 =Integer.parseInt(indiceInicio[numsol]);
                               for(int nu=0; nu < (int)divisionSolicitud2; nu++){
                               if(nu == ((int)divisionSolicitud2)-1){
                                int numer = Integer.parseInt(cantidadSlots[numsol]) -(int)(divisionSolicitud2*ftmax);
                                if(numer == 0){
                                    ISalientes[countNumSolS]= indiceInicio1;
                                    OSalientes[countNumSolS]=  indiceInicio1+ftmax-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolS++;
                                }else{
                                    ISalientes[countNumSolS]= indiceInicio1;
                                    OSalientes[countNumSolS]=  indiceInicio1+(Integer.parseInt(cantidadSlots[numsol])-1 -(int)Math.floor(divisionSolicitud)*ftmax);
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolS++;
                                }
                               }else{
                                    ISalientes[countNumSolS]= indiceInicio1;
                                    OSalientes[countNumSolS]=  indiceInicio1+ftmax-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolS++;
                               }
                               }
                               
                           }else{
                            ISalientes[countNumSolS]= Integer.parseInt(indiceInicio[numsol]);
                            OSalientes[countNumSolS]=  Integer.parseInt(indiceInicio[numsol])+Integer.parseInt(cantidadSlots[numsol])-1;
                            countNumSolS++;
                           }
                      
                       }
                       if(Integer.parseInt(listaSolicitudes.get(numsol).getDestino())== numnod){
                           if(Integer.parseInt(cantidadSlots[numsol])> ftmax){
                              divisionSolicitud = Double.parseDouble(cantidadSlots[numsol])/(double)ftmax;
                               divisionSolicitud2 =  Math.ceil(divisionSolicitud);
                               indiceInicio1 =Integer.parseInt(indiceInicio[numsol]);
                               for(int nu=0; nu < (int)divisionSolicitud2; nu++){
                               if(nu == ((int)divisionSolicitud2)-1){
                                int numer = Integer.parseInt(cantidadSlots[numsol]) -(int)(divisionSolicitud2*ftmax);
                                if(numer == 0){
                                    IEntrantes[countNumSolE]= indiceInicio1;
                                    OEntrantes[countNumSolE]=  indiceInicio1+ftmax-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolE++;
                                }else{
                                    IEntrantes[countNumSolE]= indiceInicio1;
                                    OEntrantes[countNumSolE]=  indiceInicio1+(Integer.parseInt(cantidadSlots[numsol])-1 -(int)Math.floor(divisionSolicitud)*ftmax);
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolE++;
                                }
                               }else{
                                    IEntrantes[countNumSolE]= indiceInicio1;
                                    OEntrantes[countNumSolE]=  indiceInicio1+ftmax-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolE++;
                               }
                               }
                               
                           }else{
                           IEntrantes[countNumSolE]= Integer.parseInt(indiceInicio[numsol]);
                       OEntrantes[countNumSolE]=  Integer.parseInt(indiceInicio[numsol])+Integer.parseInt(cantidadSlots[numsol])-1;
                       countNumSolE++;
                           }
                       
                       
                       }
                       
                   
                  
              }
              String Is ="I=[";
              String Os ="O=[";
              for(int numsol=0; numsol<countNumSolS; numsol++){
                  if(numsol==countNumSolS-1){
                      Is = Is + " " + ISalientes[numsol] ;
                      Os = Os + " " + OSalientes[numsol] ;
                  }else{
                      Is = Is + " " + ISalientes[numsol] + ",";
                      Os = Os + " " + OSalientes[numsol] + ",";
                  }
                  
              }
              Is = Is+"];";
              Os = Os+"];";
               String Ie ="I=[";
              String Oe ="O=[";
              for(int numsol=0; numsol<countNumSolE; numsol++){
                  if(numsol==countNumSolE-1){
                      Ie = Ie + " " + IEntrantes[numsol] ;
                      Oe = Oe + " " + OEntrantes[numsol] ;
                  }else{
                      Ie = Ie + " " + IEntrantes[numsol] + ",";
                      Oe = Oe + " " + OEntrantes[numsol] + ",";
                  }
                  
              }
              Ie = Ie+"];";
              Oe = Oe+"];";
              resultado[0]= Is;
              resultado[1]= Os;
              resultado[2]= Ie;
              resultado[3]= Oe;
              return resultado;
     }
     
     private static ArrayList<int []> getIOEntrantesYSalientes (ArrayList<Solicitud> listaSolicitudes,int numnod,int countNumSolS ,int countNumSolE,String [] cantidadSlots,String [] indiceInicio, int ftmax ){
     int [] ISalientes = new int[countNumSolS];
        int [] OSalientes = new int[countNumSolS];
        int [] IEntrantes = new int[countNumSolE];
        int [] OEntrantes = new int[countNumSolE];
        ArrayList<int []> resultado = new ArrayList<>();
        countNumSolS = 0;
        countNumSolE = 0;
        double divisionSolicitud;
        double divisionSolicitud2;
        int indiceInicio1 = 0;
              for(int numsol=0; numsol<listaSolicitudes.size(); numsol++){
                  if(Integer.parseInt(listaSolicitudes.get(numsol).getOrigen())== numnod){
                        if(Integer.parseInt(cantidadSlots[numsol])> ftmax){
                               divisionSolicitud = Double.parseDouble(cantidadSlots[numsol])/(double)ftmax;
                               divisionSolicitud2 =  Math.ceil(divisionSolicitud);
                               indiceInicio1 =Integer.parseInt(indiceInicio[numsol]);
                               for(int nu=0; nu < (int)divisionSolicitud2; nu++){
                               if(nu == ((int)divisionSolicitud2)-1){
                                int numer = Integer.parseInt(cantidadSlots[numsol]) -(int)(divisionSolicitud2*ftmax);
                                if(numer == 0){
                                    ISalientes[countNumSolS]= indiceInicio1;
                                    OSalientes[countNumSolS]=  indiceInicio1+ftmax-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolS++;
                                }else{
                                    ISalientes[countNumSolS]= indiceInicio1;
                                    OSalientes[countNumSolS]=  indiceInicio1+(Integer.parseInt(cantidadSlots[numsol]) -(int)Math.floor(divisionSolicitud)*ftmax)-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolS++;
                                }
                               }else{
                                    ISalientes[countNumSolS]= indiceInicio1;
                                    OSalientes[countNumSolS]=  indiceInicio1+ftmax-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolS++;
                               }
                               }
                               
                           }else{
                            ISalientes[countNumSolS]= Integer.parseInt(indiceInicio[numsol]);
                       OSalientes[countNumSolS]=  Integer.parseInt(indiceInicio[numsol])+Integer.parseInt(cantidadSlots[numsol])-1;
                       countNumSolS++;
                           }
                      
                       }
                       if(Integer.parseInt(listaSolicitudes.get(numsol).getDestino())== numnod){
                        if(Integer.parseInt(cantidadSlots[numsol])> ftmax){
                              divisionSolicitud = Double.parseDouble(cantidadSlots[numsol])/(double)ftmax;
                               divisionSolicitud2 =  Math.ceil(divisionSolicitud);
                               indiceInicio1 =Integer.parseInt(indiceInicio[numsol]);
                               for(int nu=0; nu < (int)divisionSolicitud2; nu++){
                               if(nu == ((int)divisionSolicitud2)-1){
                                int numer = Integer.parseInt(cantidadSlots[numsol]) -(int)(divisionSolicitud2*ftmax);
                                if(numer == 0){
                                    IEntrantes[countNumSolE]= indiceInicio1;
                                    OEntrantes[countNumSolE]=  indiceInicio1+ftmax-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolE++;
                                }else{
                                    IEntrantes[countNumSolE]= indiceInicio1;
                                    OEntrantes[countNumSolE]=  indiceInicio1+(Integer.parseInt(cantidadSlots[numsol]) -(int)Math.floor(divisionSolicitud)*ftmax)-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolE++;
                                }
                               }else{
                                    IEntrantes[countNumSolE]= indiceInicio1;
                                    OEntrantes[countNumSolE]=  indiceInicio1+ftmax-1;
                                    indiceInicio1= indiceInicio1+ftmax+1;
                                    countNumSolE++;
                               }
                               }
                               
                           }else{
                           IEntrantes[countNumSolE]= Integer.parseInt(indiceInicio[numsol]);
                       OEntrantes[countNumSolE]=  Integer.parseInt(indiceInicio[numsol])+Integer.parseInt(cantidadSlots[numsol])-1;
                       countNumSolE++;
                           }
                       
                       
                       }
                       
                   
                  
              }
              
              resultado.add(ISalientes);
              resultado.add(OSalientes);
              resultado.add(IEntrantes);
              resultado.add(OEntrantes);
              return resultado;
     }
     
     private static String generarMatrizP(int numSol, int[] in, int [] o){
         int [][] salida = new int [numSol][numSol];

        for (int s=0; s<numSol; s++){
            salida[s][s] = 1;
            o[s] +=1;
        }
        for (int s1=0; s1<numSol-1; s1++){
            for (int s2= s1+1; s2<numSol; s2++){
                if (in[s1]>in[s2] && o[s2]<in[s1]){
                    salida[s1][s2] = 0;
                    salida[s2][s1] = 0;
                }else if (in[s1]<in[s2] && o[s1]<in[s2]){
                    salida[s1][s2] = 0;
                    salida[s2][s1] = 0;
                }else{
                    salida[s1][s2] = 1;
                    salida[s2][s1] = 1;
                }
            }
        }
        String print = "[";
        for (int i=0; i<numSol;i++){
            print+="\n[";
            for (int j=0; j<numSol;j++){
                print+=salida[i][j];
                if(j!=numSol-1){
                    print+= ",";
                }
            }
            print+="]";
        }
        print+="]";
        return print;
     } 
     
    private static int capaTransponder(Configuracion config, int y,  ArrayList<Solicitud> listaSolicitudes) throws IOException{
         
        FileReader fr;
        int [] numtransponderXNodo= new int[numeroNodo];
        int totalNumeroTransponder = 0;
        String f = leerArchivo("./src/resultados/salidaCplexSP-ILP_2"+y+".txt");
        f = f.substring(f.indexOf("f")+5, f.indexOf("]"));
        int countf = 0;
        String [] indiceInicio1 = f.split(" ");
        String [] indiceInicio = new String[listaSolicitudes.size()];
        for(int o = 0; o < indiceInicio1.length; o++){
        if(!indiceInicio1[o].equals("") || !indiceInicio1[o].equals("")){
         indiceInicio1[o]=indiceInicio1[o].replaceAll("(\\r|\\n)", "");   
         indiceInicio[countf]= indiceInicio1[o];
         countf++;
        }
        }
        int countNumSolS =0;
        int countNumSolE =0;
       SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph = new SimpleDirectedGraph(DefaultWeightedEdge.class);
        cargarGrafo(directedGraph);
        fr = new FileReader("./src/resultados/kshortestpathCompleto"+y+".txt");
        int cantTotalCaminos = 0;
        try (BufferedReader entrada = new BufferedReader(fr)) {
            while (entrada.readLine() != null) {
                cantTotalCaminos++;
            }
        }
        ArrayList<Integer> indiceCaminosElegidos = leerSalidaCplex(config.getK(), cantTotalCaminos, listaSolicitudes.size(), y);
        ArrayList<ArrayList<String>> caminosElegidos = leerSoloCaminosElegidos(indiceCaminosElegidos, y);
        String alfaSegundaFase = leerSolicitudesSegundaFase(directedGraph, indiceCaminosElegidos, y);
        String  f1 = alfaSegundaFase.substring(alfaSegundaFase.indexOf("")+1,alfaSegundaFase.indexOf("]"));
        String [] cantidadSlots = f1.split(",");
        ArrayList<int []> resultadoIO = new ArrayList<int []>();
        
        for(int numnod=0; numnod<numeroNodo; numnod++){
             String[] entradasYSalidasIO = new String[4];
              countNumSolS = getNumeroSolicitudesSalidas(listaSolicitudes,numnod,cantidadSlots,ftmax);
              countNumSolE = getNumeroSolicitudesEntrantes(listaSolicitudes,numnod,cantidadSlots,ftmax);
              entradasYSalidasIO = getEntradasYSalidas(listaSolicitudes, numnod,countNumSolS ,countNumSolE,cantidadSlots,indiceInicio,ftmax);
              //Funcion Migue para hallar la pEntrada en String
              resultadoIO = getIOEntrantesYSalientes(listaSolicitudes, numnod,countNumSolS ,countNumSolE,cantidadSlots,indiceInicio,ftmax);
              String p1 = generarMatrizP(countNumSolS, resultadoIO.get(0), resultadoIO.get(1));
              String p2 = generarMatrizP(countNumSolE, resultadoIO.get(2), resultadoIO.get(3));
//usa funcionn getIOEntrantesYSalientes y countNumSol
             String archivo = generarArchivostrasponderIlp(y,1,countNumSolS,entradasYSalidasIO[0],entradasYSalidasIO[1],p1, config,numnod);
              //Funcion Procesarresultado En cuanto entrada archivo tiene que tirar la cantidad de transpondedores
              numtransponderXNodo[numnod] = procesarArchivosTransponderIlp(archivo);
             archivo= generarArchivostrasponderIlp(y,2,countNumSolE,entradasYSalidasIO[2],entradasYSalidasIO[3],p2,config,numnod);
              //Funcion Procesarresultado En cuanto salidad archivo tiene que tirar la cantidad de transpondedores
              numtransponderXNodo[numnod] = numtransponderXNodo[numnod]+procesarArchivosTransponderIlp(archivo);
              } 
        BufferedWriter bwt = new BufferedWriter(new FileWriter("./src/resultados/reportetransponder"+y+".txt"));
        bwt.write("Cantidad de transpondedores por nodo ILP+ ILP:\n");
        for (int i=0; i<numtransponderXNodo.length; i++){
            totalNumeroTransponder =totalNumeroTransponder + numtransponderXNodo[i];
            bwt.write("NodoEntrantes[" + i + "]= " + numtransponderXNodo[i] + "\n");
        }
        bwt.write("Número total de transpondedores= " + totalNumeroTransponder+ "\n");
        bwt.close();
        return totalNumeroTransponder;
    }
    private static int procesarArchivosTransponderIlp(String archivo){
        String f = leerArchivoTransponder(archivo);
        f = f.substring(f.indexOf("XT")+6, f.indexOf("]"));
        String finaltr = "";
        int numNodo = 0;
        for (int i=0; i<f.length(); i++){
            if (f.substring(i, i+1).equals("0") || f.substring(i, i+1).equals("1")){
                int nodo =Integer.parseInt( f.substring(i, i+1));
                numNodo = numNodo + nodo;
            }
        }
        return numNodo;
    }
    private static String generarArchivostrasponderIlp(int y,int numArch, int numSol, String entradaI, String entradaO, String p,Configuracion config, int numnod)throws IOException{
        long time_start1, time_end1;
    FileWriter fw = new FileWriter("./src/resultados/transponderilp"+y+"_"+numArch+"_nodo"+numnod+".dat");

        BufferedWriter bw = new BufferedWriter(fw);
        try (PrintWriter salida = new PrintWriter(bw)) {
            System.out.println("ftmax = " + ftmax + ";");
            salida.println("ftmax = " + ftmax + ";");
            System.out.println("Ftotal = " + config.getfTotal() + ";");
            salida.println("Ftotal = " + config.getfTotal() + ";");
            System.out.println("T = " + numeroTransponder + ";");
            salida.println("T = " + numeroTransponder + ";");
            System.out.println("s = " + numSol + ";");
            salida.println("s = " + numSol + ";");
            System.out.println("p = " + p + ";");
            salida.println("p = " + p + ";");
            System.out.println(entradaI);
            salida.println(entradaI);
            System.out.println(entradaO);
            salida.println(entradaO);
            
        }
         /* Probamos la conexiÃ³n de Java con CPLEX */
        String argumentos[] = {"-v", "./src/transponder/transponderilp.mod", "./src/resultados/transponderilp"+y+"_"+numArch+"_nodo"+numnod+".dat", "./src/resultados/salidaTransponderIlp"+y+"_"+numArch+"_nodo"+numnod+".txt"};
        
        time_start1 = System.currentTimeMillis();
                
        OplRunILP.main(argumentos);
        
        time_end1 = System.currentTimeMillis();
        
        System.out.println("La cota del transponderILP  ha tomado " + (time_end1 - time_start1) + " milisegundos");
    return( "./src/resultados/salidaTransponderIlp"+y+"_"+numArch+"_nodo"+numnod+".txt");
    
    }
    
    
    private static Integer cplex( Configuracion config, int y, ArrayList<Solicitud> listaSolicitudes) throws IOException{
         long time_start1, time_end1, time_start2, time_end2;
        
        FileReader fsolicitudes;
        FileReader fr;
    ////CPLEX
        // Definimos e instanciamos el grafo con pesos
        SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph =
                new SimpleDirectedGraph(DefaultWeightedEdge.class);
        cargarGrafo(directedGraph);
        String alfa = leerSolicitudes(directedGraph, config.getK(),y,listaSolicitudes);
        fsolicitudes = new FileReader("./src/resultados/solicitudesilp"+y+".txt");
        int cantSolicitudes = 0;
        
        try (BufferedReader entrada = new BufferedReader(fsolicitudes)) {

            while (entrada.readLine() != null) {
                cantSolicitudes++;
            }
        }
        fsolicitudes.close();
        fr = new FileReader("edges" + topologia +".txt");
        int cantEnlaces = 0;
        try (BufferedReader entrada = new BufferedReader(fr)) {


            while (entrada.readLine() != null) {
                cantEnlaces++;
            }
        }


        FileWriter fw = new FileWriter("./src/resultados/SP-ILP_1"+y+".dat");

        BufferedWriter bw = new BufferedWriter(fw);
        try (PrintWriter salida = new PrintWriter(bw)) {
            System.out.println("K = " + config.getK() + ";");
            salida.println("K = " + config.getK() + ";");
            System.out.println("SD = " + cantSolicitudes + ";");
            salida.println("SD = " + cantSolicitudes + ";");
            System.out.println("E = " + cantEnlaces + ";");
            salida.println("E = " + cantEnlaces + ";");
            System.out.println("G = " + config.getG() + ";");
            salida.println("G = " + config.getG() + ";");
            System.out.println("alfa = " + alfa);
            salida.println("alfa = " + alfa);


            ArrayList<ArrayList<String>> listaTotalCaminos = leerTotalCaminos(y);
            Set<DefaultWeightedEdge> listaTotalEnlaces = directedGraph.edgeSet();
            //ArrayList<ArrayList<String>> listaTotalCaminos = directedGraph.edgeSet();

            System.out.println("R = [");
            salida.println("R = [");

            for (Iterator<ArrayList<String>> it4 = listaTotalCaminos.iterator(); it4.hasNext();) {
                String caminoComparador = it4.next().toString();
                //System.out.println("caminoComparador = " + caminoComparador + "\n");
                System.out.print("[");
                salida.print("[");
                for (Iterator<DefaultWeightedEdge> it = listaTotalEnlaces.iterator(); it.hasNext();) {
                    String enlaceAux = it.next().toString();
                    String enlace = enlaceAux.substring(1, enlaceAux.length() - 1);
                    //System.out.println("enlace = " + enlace + "\n");

                    String result;
                    if (caminoComparador.toString().contains(enlace.toString())) {
                        result = "1";
                    } else {
                        result = "0";
                    }
                    /*for (String enlace : camino) {
                     //System.out.println("enlace = " + enlace + "\n");
                     if (caminoComparador.contains(enlace)) 
                     result = "1";
                     else
                     result = "0";

                     }*/
                    if (it.hasNext()) {
                        System.out.print(result + ",");
                        salida.print(result + ",");
                    } else {
                        System.out.print(result);
                        salida.print(result);
                    }
                }
                if (it4.hasNext()) {
                    System.out.println("],");
                    salida.println("],");
                } else {
                    System.out.println("]];");
                    salida.println("]];");
                }
            }
            salida.close();
        }



        /* Probamos la conexiÃ³n de Java con CPLEX */
        String argumentos[] = {"-v", "./src/SPILP_1/SP-ILP_1.mod", "./src/resultados/SP-ILP_1"+y+".dat", "./src/resultados/salidaCplexSP-ILP_1"+y+".txt"};
        
        time_start1 = System.currentTimeMillis();
                
        OplRunILP.main(argumentos);
        
        time_end1 = System.currentTimeMillis();
        
        System.out.println("La cota de la ILP 2 ha tomado " + (time_end1 - time_start1) + " milisegundos");

        fr = new FileReader("./src/resultados/kshortestpathCompleto"+y+".txt");
        int cantTotalCaminos = 0;
        try (BufferedReader entrada = new BufferedReader(fr)) {


            while (entrada.readLine() != null) {
                cantTotalCaminos++;
            }
        }

        Integer espectroTotal = calcularEspectroTotal();
        ArrayList<Integer> indiceCaminosElegidos = leerSalidaCplex(config.getK(), cantTotalCaminos, cantSolicitudes, y);

        System.out.println("indiceCaminosElegidos.size() = " + indiceCaminosElegidos.size());
        for (int x = 0; x < indiceCaminosElegidos.size(); x++) {
            System.out.println(indiceCaminosElegidos.get(x));
        }

        ArrayList<ArrayList<String>> caminosElegidos = leerSoloCaminosElegidos(indiceCaminosElegidos, y);

        for (int x = 0; x < caminosElegidos.size(); x++) {
            System.out.println(caminosElegidos.get(x));
        }

        String alfaSegundaFase = leerSolicitudesSegundaFase(directedGraph, indiceCaminosElegidos, y);

        FileWriter fw2 = new FileWriter("./src/resultados/SP-ILP_2"+y+".dat");

        BufferedWriter bw2 = new BufferedWriter(fw2);
        try (PrintWriter salida2 = new PrintWriter(bw2)) {
            System.out.println("K = " + config.getK() + ";");
            salida2.println("K = " + config.getK() + ";");
            System.out.println("SD = " + cantSolicitudes + ";");
            salida2.println("SD = " + cantSolicitudes + ";");
            System.out.println("Ftotal = " + config.getfTotal() + ";");
            salida2.println("Ftotal = " + config.getfTotal() + ";");
            System.out.println("G = " + config.getG() + ";");
            salida2.println("G = " + config.getG() + ";");
            System.out.println("alfa = " + alfaSegundaFase);
            salida2.println("alfa = " + alfaSegundaFase);


            System.out.println("l = [");
            salida2.println("l = [");
            for (Iterator<ArrayList<String>> it = caminosElegidos.iterator(); it.hasNext();) {
                ArrayList<String> camino = it.next();
                System.out.print("[");
                salida2.print("[");
                for (Iterator<ArrayList<String>> it4 = caminosElegidos.iterator(); it4.hasNext();) {
                    ArrayList<String> caminoComparador = it4.next();
                    String result = "0";
                    for (String enlace : camino) {

                        if (caminoComparador.contains(enlace)) {
                            result = "1";
                            break;
                        }

                    }
                    if (it4.hasNext()) {
                        System.out.print(result + ",");
                        salida2.print(result + ",");
                    } else {
                        System.out.print(result);
                        salida2.print(result);
                    }
                }
                if (it.hasNext()) {
                    System.out.println("],");
                    salida2.println("],");
                } else {
                    System.out.println("]];");
                    salida2.println("]];");
                }
            }
        }

        /* Probamos la conexiÃ³n de Java con CPLEX */
        String argumentos2[] = {"-v", "./src/SPILP_2/SP-ILP_2.mod", "./src/resultados/SP-ILP_2"+y+".dat", "./src/resultados/salidaCplexSP-ILP_2"+y+".txt"};
        
        time_start2 = System.currentTimeMillis();
        OplRunILP.main(argumentos2);

        time_end2 = System.currentTimeMillis();
        
        System.out.println("La fase 2 de ILP 2 ha tomado " + (time_end2 - time_start2) + " milisegundos");
        System.out.println("EL ILP 2 ha tomado " + (time_end1 - time_start1 + time_end2 - time_start2) + " milisegundos");
        
        String f = leerArchivo("./src/resultados/salidaCplexSP-ILP_2"+y+".txt");
        f = f.substring(f.indexOf("f")+5, f.indexOf("]"));
        String [] t2 = f.split(" ");
        int numero = 0;
        for(String t1 : t2){
            if(!t1.equals("")){
             numero++;
            }
        }
        String [] t = new String[numero];
        int canti = 0;
         for(String t1 : t2){
            if(!t1.equals("")){
             t[canti] = t1;
             canti++;
            }
        }
        
        //System.out.println(t[0] + "-" + t[1] + "-" +  t[2]);
        
         FileReader nodos = new FileReader("./src/resultados/solicitudesilp"+y+".txt");
         String nodosstr = "";
            BufferedReader n = new BufferedReader(nodos);
                String line;
                while ((line = n.readLine()) != null) {
                    if(!line.equals("")){
                         nodosstr += line + "\n";
                    }
                   
                }
        String [] lineas = nodosstr.split("\n");
        String [] inicio = new String[t.length];
        String [] fin = new String[t.length];;
        
        int cont = 0;
        for (String temp: lineas){
            String [] temp2;
            temp2 = temp.split("\t");
            inicio[cont] = temp2[0];
            fin[cont]= temp2[1];
            cont++;
        }
        alfaSegundaFase = alfaSegundaFase.substring(1,alfaSegundaFase.length()-2);
        String[] alfax = alfaSegundaFase.split(",");
        
        return espectroTotal;
        
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
        String [] lista = texto.split("F = \\[");
        lista = lista[1].split("\\];");        
        String espectrostmp = lista[0].replace("[", "").replace("]", "");
        String [] espectros = espectrostmp.split(" ");
        Integer sumaEspectro = 0;
        for (String tmp: espectros){
            if (!"".equals(tmp) && !"0".equals(tmp)){
                sumaEspectro+=Integer.parseInt(tmp);
            }
        }
        return sumaEspectro;
    }
    
    
}
