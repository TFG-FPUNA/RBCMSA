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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgrapht.graph.*;

public class SLILP {

    public static void main(String args[]) throws IOException {

        long time_start1, time_start2, time_end1, time_end2;



        try {
            Configuracion config = leerConfiguraciones();
            // Definimos e instanciamos el grafo con pesos
            SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph =
                    new SimpleDirectedGraph(DefaultWeightedEdge.class);
            int cantidadEnlaces = cargarGrafo(directedGraph);
            capaVirtual();
            FileReader fr = new FileReader("solicitudes.txt"); //se vuelve a leer el archivo para contar cuantas lineas tiene el archivo
            int cantSolicitudes = 0;
            try (BufferedReader entrada = new BufferedReader(fr)) {


                while (entrada.readLine() != null) {
                    cantSolicitudes++;
                }
            }

            ArrayList<Double> listaL = new ArrayList();
            try {

                FileReader fr2 = new FileReader("edges.txt");
                String[] edgeText;

                try (BufferedReader entrada = new BufferedReader(fr2)) {
                    String line;
                    while ((line = entrada.readLine()) != null) {
                        edgeText = line.split("\t");
                        Double dist = Double.parseDouble(edgeText[edgeText.length - 1]);
                        listaL.add(dist);
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


            try {
                FileWriter fw = new FileWriter("SL-ILP_1.dat");

                BufferedWriter bw = new BufferedWriter(fw);
                try (PrintWriter salida = new PrintWriter(bw)) {

                    System.out.println("F = " + config.getfTotal() + ";");
                    salida.println("F = " + config.getfTotal() + ";");
                    System.out.println("GB = " + config.getG() + ";");
                    salida.println("GB = " + config.getG() + ";");

                    System.out.println("ENLACES = " + cantidadEnlaces + ";");
                    salida.println("ENLACES = " + cantidadEnlaces + ";");

                    ArrayList<Parametro> listaParametros = new ArrayList();
                    int cantParams = 0;

                    try {

                        FileReader fr3 = new FileReader("parametros.txt");
                        String[] edgeText;


                        try (BufferedReader entrada = new BufferedReader(fr3)) {
                            String line;
                            while ((line = entrada.readLine()) != null) {
                                edgeText = line.split("\t");
                                Parametro param = new Parametro(Double.parseDouble(edgeText[0]), Integer.parseInt(edgeText[1]));
                                listaParametros.add(param);
                                cantParams++;
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

                    String alfa = leerSolicitudes(directedGraph, cantParams);
                    System.out.println("SD = " + cantSolicitudes + ";");
                    salida.println("SD = " + cantSolicitudes + ";");

                    System.out.println("alfa = " + alfa);
                    salida.println("alfa = " + alfa);



                    System.out.print("Lmax = [");
                    salida.print("Lmax = [");

                    for (Iterator<Parametro> it = listaParametros.iterator(); it.hasNext();) {
                        Parametro param = it.next();
                        if (it.hasNext()) {
                            System.out.print(param.getDistanciaMaxima() + ",");
                            salida.print(param.getDistanciaMaxima() + ",");
                        } else {
                            System.out.print(param.getDistanciaMaxima() + "];\n");
                            salida.print(param.getDistanciaMaxima() + "];\n");
                        }
                    }

                    System.out.print("L = [");
                    salida.print("L = [");
                    for (Iterator<Double> it = listaL.iterator(); it.hasNext();) {
                        Double dist = it.next();
                        if (it.hasNext()) {
                            System.out.print(dist + ",");
                            salida.print(dist + ",");
                        } else {
                            System.out.print(dist + "];\n");
                            salida.print(dist + "];\n");
                        }
                    }

                    ArrayList<Solicitud> listaSolicitudes = new ArrayList();

                    try {

                        FileReader fr4 = new FileReader("solicitudes.txt");
                        String[] edgeText;

                        try (BufferedReader entrada = new BufferedReader(fr4)) {
                            String line;
                            while ((line = entrada.readLine()) != null) {
                                edgeText = line.split("\t");
                                Solicitud sol = new Solicitud(edgeText[0], edgeText[1], Double.parseDouble(edgeText[2]));
                                listaSolicitudes.add(sol);
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


                    System.out.print("SOLICITUDES = [");
                    salida.print("SOLICITUDES = [");



                    for (Iterator<Solicitud> it = listaSolicitudes.iterator(); it.hasNext();) {
                        Solicitud solic = it.next();
                        if (it.hasNext()) {
                            System.out.print("[" + solic.getOrigen() + "," + solic.getDestino() + "],");
                            salida.print("[" + solic.getOrigen() + "," + solic.getDestino() + "],");
                        } else {
                            System.out.print("[" + solic.getOrigen() + "," + solic.getDestino() + "]];\n");
                            salida.print("[" + solic.getOrigen() + "," + solic.getDestino() + "]];\n");
                        }
                    }

                    Set<String> vs = directedGraph.vertexSet();
                    Set<DefaultWeightedEdge> en = directedGraph.edgeSet();
                    System.out.println("VERTICES = " + vs.size() + ";");
                    salida.println("VERTICES = " + vs.size() + ";");
                    System.out.println("LISTAVERTICES = " + vs.toString() + ";");
                    salida.println("LISTAVERTICES = " + vs.toString() + ";");

                    System.out.print("NE1 = [[");
                    salida.print("\nNE1 = [\n[");
                    int indiceVertice = 0;
                    for (Iterator<String> it = vs.iterator(); it.hasNext();) {
                        
                        indiceVertice++;

                        String verticeOrigen = it.next();
                        for (Iterator<DefaultWeightedEdge> it2 = en.iterator(); it2.hasNext();) {
                            String enlaceOrigen = it2.next().toString();
                            if (it2.hasNext()) {
                                if (enlaceOrigen.contains("(" + verticeOrigen + " :")) {
                                    System.out.print("1,");
                                    salida.print("1,");
                                } else {
                                    System.out.print("0,");
                                    salida.print("0,");
                                }
                            } else if (it.hasNext()) {
                                if (enlaceOrigen.contains("(" + verticeOrigen + " :")) {
                                    System.out.print("1],\n[");
                                    salida.print("1],\n[");
                                } else {
                                    System.out.print("0],\n[");
                                    salida.print("0],\n[");
                                }
                            } else if (enlaceOrigen.contains("(" + verticeOrigen + " :")) {
                                System.out.print("1]];\n");
                                salida.print("1]];\n");
                            } else {
                                System.out.print("0]];\n");
                                salida.print("0]];\n");
                            }
                        }
                    }

                    System.out.print("NE2 = [[");
                    salida.print("\nNE2 = [\n[");
                    indiceVertice = 0;
                    for (Iterator<String> it = vs.iterator(); it.hasNext();) {
                        
                        indiceVertice++;

                        String verticeDestino = it.next();
                        for (Iterator<DefaultWeightedEdge> it2 = en.iterator(); it2.hasNext();) {
                            String enlaceOrigen = it2.next().toString();
                            if (it2.hasNext()) {
                                //System.out.println("enlaceOrigen = " + enlaceOrigen + "\n");
                                if (enlaceOrigen.contains(": " + verticeDestino + ")")) {
                                    System.out.print("1,");
                                    salida.print("1,");
                                } else {
                                    System.out.print("0,");
                                    salida.print("0,");
                                }
                            } else if (it.hasNext()) {
                                if (enlaceOrigen.contains(": " + verticeDestino + ")")) {
                                    System.out.print("1],\n[");
                                    salida.print("1],\n[");
                                } else {
                                    System.out.print("0],\n[");
                                    salida.print("0],\n[");
                                }
                            } else if (enlaceOrigen.contains(": " + verticeDestino + ")")) {
                                System.out.print("1]];\n");
                                salida.print("1]];\n");
                            } else {
                                System.out.print("0]];\n");
                                salida.print("0]];\n");
                            }
                        }
                    }

                    System.out.print("NE = [[");
                    salida.print("\nNE = [\n[");
                    indiceVertice = 0;
                    for (Iterator<String> it = vs.iterator(); it.hasNext();) {
                        
                        indiceVertice++;

                        String verticeIntermedio = it.next();
                        for (Iterator<DefaultWeightedEdge> it2 = en.iterator(); it2.hasNext();) {
                            String enlaceOrigen = it2.next().toString();
                            if (it2.hasNext()) {
                                //System.out.println("enlaceOrigen = " + enlaceOrigen + "\n");
                                if (enlaceOrigen.contains(": " + verticeIntermedio + ")") || enlaceOrigen.contains("(" + verticeIntermedio + " :")) {
                                    System.out.print("1,");
                                    salida.print("1,");
                                } else {
                                    System.out.print("0,");
                                    salida.print("0,");
                                }
                            } else if (it.hasNext()) {
                                if (enlaceOrigen.contains(": " + verticeIntermedio + ")") || enlaceOrigen.contains("(" + verticeIntermedio + " :")) {
                                    System.out.print("1],\n[");
                                    salida.print("1],\n[");
                                } else {
                                    System.out.print("0],\n[");
                                    salida.print("0],\n[");
                                }
                            } else if (enlaceOrigen.contains(verticeIntermedio + ")") || enlaceOrigen.contains("(" + verticeIntermedio)) {
                                System.out.print("1]];\n");
                                salida.print("1]];\n");
                            } else {
                                System.out.print("0]];\n");
                                salida.print("0]];\n");
                            }
                        }
                    }
                }


                /* Probamos la conexión de Java con CPLEX */
                String argumentos[] = {"-v", "SL-ILP_1.mod", "SL-ILP_1.dat", "salidaCplexSL-ILP_1.txt"};
                time_start1 = System.currentTimeMillis();
                OplRunILP.main(argumentos);
                time_end1 = System.currentTimeMillis();

                System.out.println("La cota de la SL-ILP ha tomado " + (time_end1 - time_start1) + " milisegundos");
                /*
                 String request = "Y =";

                 BufferedReader reader = new BufferedReader(new FileReader("salidaCplexILP4_1.txt"));
                 String line;
                 int cnt1 = 0;
                 while ((line = reader.readLine()) != null) {
                 cnt1++;
                 if (line.indexOf(request) != -1) {
                 //System.out.println(cnt1);
                 break;
                 }
                 }
                 reader.close();

                 reader = new BufferedReader(new FileReader("salidaCplexILP4_1.txt"));
                 request = "N =";
                 int cnt2 = 0;
                 while ((line = reader.readLine()) != null) {
                 cnt2++;
                 if (line.indexOf(request) != -1) {
                 //System.out.println(cnt2);
                 break;
                 }
                 }
                 reader.close();
                
                 reader = new BufferedReader(new FileReader("salidaCplexILP4_1.txt"));
                 int lineaLectura = 0;
                 while ((line = reader.readLine()) != null) {
                 lineaLectura++;
                 if (lineaLectura >= cnt1 && lineaLectura < cnt2) {
                 System.out.println(line);
                 }
                 }
                 reader.close();
                 */
                ArrayList<String> caminosY = leerY();
                System.out.println(caminosY);
                ArrayList<Integer> indiceCaminosElegidos = leerZ();
                ArrayList<Integer> nivelesModulacion = leerNivelModulacion();

                System.out.println("indiceCaminosElegidos.size() = " + indiceCaminosElegidos.size());
                for (int x = 0; x < indiceCaminosElegidos.size(); x++) {
                    System.out.println(indiceCaminosElegidos.get(x));
                }

                ArrayList<String> caminosElegidos = leerSoloCaminosElegidos(indiceCaminosElegidos, caminosY);

                String alfaSegundaFase = leerSolicitudesSegundaFase(directedGraph, indiceCaminosElegidos, nivelesModulacion);

                /*for (int x = 0; x < caminosElegidos.size(); x++) {
                 System.out.println(caminosElegidos.get(x));
                 }*/
                //String textoFase1 = leerSalidaCplex();

                //String textoEntrada = leerEntradaCplex();

                FileWriter fw2 = new FileWriter("SL-ILP_2.dat");

                BufferedWriter bw2 = new BufferedWriter(fw2);
                try (PrintWriter salida2 = new PrintWriter(bw2)) {
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
                    for (Iterator<String> it = caminosElegidos.iterator(); it.hasNext();) {
                        String camino = it.next();
                        camino = camino.replace(" ", "");
                        System.out.print("[");
                        salida2.print("[");
                        for (Iterator<String> it4 = caminosElegidos.iterator(); it4.hasNext();) {
                            String caminoComparador = it4.next();
                            caminoComparador = caminoComparador.replace(" ", "");
                            String result = "0";
                            for (int i = 0; i < camino.length(); i++) {
                                if (caminoComparador.charAt(i) == '1' && camino.charAt(i) == '1'){
                                    result = "1";
                                    break;
                                }    
                            }
                                
                            /*for (String enlace : camino) {

                                if (caminoComparador.contains(enlace)) {
                                    result = "1";
                                    break;
                                }

                            }*/
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

                /* Probamos la conexión de Java con CPLEX */
                String argumentos2[] = {"-v", "SL-ILP_2.mod", "SL-ILP_2.dat", "salidaCplexSL-ILP_2.txt"};
                time_start2 = System.currentTimeMillis();

                OplRunILP.main(argumentos2);

                time_end2 = System.currentTimeMillis();

                System.out.println("La fase 2 de ILP 4 ha tomado " + (time_end2 - time_start2) + " milisegundos");

                System.out.println("EL ILP 4 ha tomado " + (time_end1 - time_start1 + time_end2 - time_start2) + " milisegundos");

            } catch (IOException ex) {
                Logger.getLogger(SLILP.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SLILP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static int cargarGrafo(SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph) {
        int cantEnlaces = 0;
        try {

            FileReader fr = new FileReader("edges.txt");
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
                    cantEnlaces++;
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
        return cantEnlaces;
    }

    private static ArrayList<Integer> leerNivelModulacion() throws FileNotFoundException {
        ArrayList<Integer> listaNivelModulacion = new ArrayList();
        FileReader fr = new FileReader("salidaCplexSL-ILP_1.txt");
        String[] edgeText;
        String texto = "";

        try (BufferedReader entrada = new BufferedReader(fr)) {
            String line;

            while ((line = entrada.readLine()) != null) {
                texto += line;
            }
            entrada.close();
        } catch (IOException ex) {
            Logger.getLogger(SLILP.class.getName()).log(Level.SEVERE, null, ex);
        }

        edgeText = texto.split("Z = ");
        String texto2 = edgeText[1];
        //System.out.println("texto2 = " + texto2);
        texto2 = texto2.replace("]             [", "\n").replace(" ", "").replace("[", "").replace("]", "").replace(";", "");
        System.out.println("Z = \n" + texto2);

        String[] textoZ = texto2.split("\n");

        for (int i = 0; i < textoZ.length; i++) {
            for (int j = 0; j < textoZ[i].length(); j++) {
                if (textoZ[i].charAt(j) == '1') {
                    listaNivelModulacion.add(j + 1);
                    break;
                }
            }
        }
        return listaNivelModulacion;
    }

    private static ArrayList<Integer> leerZ() throws FileNotFoundException {
        ArrayList<Integer> listaCaminosElegidos = new ArrayList();
        FileReader fr = new FileReader("salidaCplexSL-ILP_1.txt");
        String[] edgeText;
        String texto = "";

        try (BufferedReader entrada = new BufferedReader(fr)) {
            String line;

            while ((line = entrada.readLine()) != null) {
                texto += line;
            }
            entrada.close();
        } catch (IOException ex) {
            Logger.getLogger(SLILP.class.getName()).log(Level.SEVERE, null, ex);
        }

        edgeText = texto.split("Z = ");
        String texto2 = edgeText[1];
        //System.out.println("texto2 = " + texto2);
        texto2 = texto2.replace("]             [", "\n").replace(" ", "").replace("[", "").replace("]", "").replace(";", "");
        System.out.println("Z = \n" + texto2);

        String[] textoZ = texto2.split("\n");

        for (int i = 0; i < textoZ.length; i++) {
            for (int j = 0; j < textoZ[i].length(); j++) {
                if (textoZ[i].charAt(j) == '1') {
                    listaCaminosElegidos.add((i + 1) * 3 - 3 + j + 1);
                    break;
                }
            }
        }
        return listaCaminosElegidos;
    }

    private static String leerEntradaCplex() {
        String texto = new String();
        try {
            FileReader fr = new FileReader("SL-ILP_1.dat");


            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                int lineaLectura = 0;
                while ((line = entrada.readLine()) != null) {
                    lineaLectura = lineaLectura + 1;
                    if (lineaLectura < 5 || lineaLectura > 7) {
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

    private static String leerSolicitudes(SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph, int r) {

        final double C = 154;

        String vectorAlfa = "[[";

        ArrayList<Solicitud> listaSolicitudes = new ArrayList();
        try {

            FileReader fr = new FileReader("solicitudes.txt");
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    Solicitud sol = new Solicitud(edgeText[0], edgeText[1], Double.parseDouble(edgeText[2]));
                    listaSolicitudes.add(sol);
                }
            }

            for (Iterator<Solicitud> it = listaSolicitudes.iterator(); it.hasNext();) {
                Solicitud solicitud = it.next();

                System.out.print("Origen: " + solicitud.getOrigen() + "\t"
                        + "Destino:" + solicitud.getDestino() + "\t"
                        + "Velocidad requerida:" + solicitud.getVelocidad() + "\n");

                if (it.hasNext()) {
                    for (int i = 1; i <= r; i++) {
                        Double d = Math.ceil(solicitud.getVelocidad() * C / (i * C));
                        if (i == 3) {
                            vectorAlfa = vectorAlfa + d.intValue() + "],[";
                        } else {
                            vectorAlfa = vectorAlfa + d.intValue() + ",";
                        }
                    }
                } else {
                    for (int i = 1; i <= 3; i++) {
                        Double d = Math.ceil(solicitud.getVelocidad() * C / (i * C));
                        if (i == 3) {
                            vectorAlfa = vectorAlfa + d.intValue();
                        } else {
                            vectorAlfa = vectorAlfa + d.intValue() + ",";
                        }
                    }
                }
            }

            vectorAlfa = vectorAlfa + "]];";

        } catch (FileNotFoundException ex) {
            System.out.println("Archivo no encontrado: " + ex);
            Logger
                    .getLogger(SLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }


        return vectorAlfa;
    }

    private static Configuracion leerConfiguraciones() {

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
        return config;
    }

    private static ArrayList<String> leerY() throws FileNotFoundException {
        FileReader fr = new FileReader("salidaCplexSL-ILP_1.txt");
        String[] edgeText, edgeText2, edgeText3, edgeText4;
        String texto = "";

        try (BufferedReader entrada = new BufferedReader(fr)) {
            String line;

            while ((line = entrada.readLine()) != null) {
                texto += line;
            }
            entrada.close();
        } catch (IOException ex) {
            Logger.getLogger(SLILP.class.getName()).log(Level.SEVERE, null, ex);
        }


        edgeText = texto.split("Y = ");
        String texto2 = edgeText[1];
        String texto3 = "texto3 =";
        //System.out.println("texto2 = " + texto2);
        texto2 = texto2.replace("                 ", " ").replace("]             [", "").replace("             ", "");
        edgeText2 = texto2.split("\\]");
        for (int i = 0; i < edgeText2.length; i++) {
            //System.out.println(edgeText2[i]);
            texto3 += edgeText2[i];

        }
        //System.out.println(texto3);
        edgeText3 = texto3.split(";N");

        String texto4;
        texto4 = edgeText3[0];
        edgeText4 = texto4.split("\\[");
        ArrayList<String> textoY = new ArrayList();
        for (int i = 3; i < edgeText4.length; i++) {
            textoY.add(edgeText4[i]);
        }
        return textoY;
    }
    
    private static void  capaVirtual() throws 
            IOException{
        //en El route.dat tres dagtos el cual tenes escribir en el y salidaRout.txt tenemos que leer
        // n nodos, t tamanho de sltos en GHZ, d demanda, C Listado de capacitados,S listados de nodos inicios,E listados de nodos fin
        crearRouteData();
         long time_start1, time_end1;
                String argumentos[] = {"-v", "route.mod", "route.dat", "salidaRoute.txt"};
                time_start1 = System.currentTimeMillis();
                OplRunILP.main(argumentos);
                time_end1 = System.currentTimeMillis();
                System.out.println("La cota de la EON ha tomado " + (time_end1 - time_start1) + " milisegundos");
                String text = leerSalidaCplexRoute();
                String flujo = text.split("enlaceUsado")[0].split("=")[1];
                String enlacesUsados = text.split("=")[2];
                String[] enlacesUsados2 = flujo.split("]");
                 int i = 1;
                  FileWriter fw2 = new FileWriter("solicitudes.txt");
            BufferedWriter bw2 = new BufferedWriter(fw2);
             try (PrintWriter salida2 = new PrintWriter(bw2)) {
                for(String pru : enlacesUsados2){
                 String[] prueba = pru.split("\\[");
                 if(prueba.length > 1){
                  for(String num : prueba){
                      String [] s = num.split(" ");
                      if(s.length>0){
                          int j = 1;
                           for (String var: s){
                          if(isNumeric(var)){
                           double numero = Double.parseDouble(var);
                           if(numero != 0){
                               salida2.println(i+"\t"+j+"\t"+var);
                               System.out.println(i+"\t"+j+"\t"+var);
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
    
   
    
    private static void crearRouteData(){
         ArrayList<Solicitud> listaSolicitudes = new ArrayList();
        try {

            FileReader fr = new FileReader("solicitudes2.txt");
            Integer numeroNodo = 10;
            Double  ghzF = 12.5;
            String[] edgeText;

            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                while ((line = entrada.readLine()) != null) {
                    edgeText = line.split("\t");
                    Solicitud sol = new Solicitud(edgeText[0], edgeText[1], Double.parseDouble(edgeText[2]));
                    listaSolicitudes.add(sol);
                }
            }
            FileWriter fw2 = new FileWriter("route.dat");
            BufferedWriter bw2 = new BufferedWriter(fw2);
            
                try (PrintWriter salida2 = new PrintWriter(bw2)) {
                    //n Cantidad de numeros de nodos dentro de la red
                    System.out.println("n = " + numeroNodo + ";");
                     salida2.println("n = " + numeroNodo + ";");
                     //t tamaño de  los sltos
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

    private static String leerSalidaCplex() {
        String texto = new String();
        try {
            FileReader fr = new FileReader("salidaCplexSL-ILP_1.txt");


            try (BufferedReader entrada = new BufferedReader(fr)) {
                String line;
                int lineaLectura = 0;
                while ((line = entrada.readLine()) != null) {
                    lineaLectura = lineaLectura + 1;
                    if (lineaLectura >= 12) {
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

    private static String leerSalidaCplexRoute() {
        String texto = new String();
        try {
            FileReader fr = new FileReader("salidaRoute.txt");
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
    private static ArrayList<String> leerSoloCaminosElegidos(ArrayList<Integer> indiceCaminosElegidos, ArrayList<String> caminosY) {
        ArrayList<String> listaCaminosElegidos = new ArrayList();

        for (int x = 0; x < indiceCaminosElegidos.size(); x++) {
            //System.out.println(indiceCaminosElegidos.get(x));
            //System.out.println(caminosY.get(indiceCaminosElegidos.get(x)-1));
            listaCaminosElegidos.add(caminosY.get(indiceCaminosElegidos.get(x) - 1));
        }
        return listaCaminosElegidos;
    }

    private static String leerSolicitudesSegundaFase(SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph, ArrayList<Integer> indiceCaminosElegidos, ArrayList<Integer> nivelesModulacion) {

        final double C = 154;

        ArrayList<Parametro> listaParams = leerParametros();

        String vectorAlfa = "[";

        ArrayList<Solicitud> listaSolicitudes = new ArrayList();
        try {

            FileReader fr = new FileReader("solicitudes.txt");
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
            for (Iterator<Solicitud> it = listaSolicitudes.iterator(); it.hasNext();) {
                Solicitud solicitud = it.next();

                System.out.print("Origen: " + solicitud.getOrigen() + "\t"
                        + "Destino:" + solicitud.getDestino() + "\t"
                        + "Velocidad requerida:" + solicitud.getVelocidad() + "\n");
                //calcularCaminosMasCortos(directedGraph, solicitud.getOrigen(), solicitud.getDestino(), contadorEscritura);
                //ArrayList<Double> listaDistancias = leerDistanciasTodasSolicitudes();
                Double tsd;

                //for (int cont = 0; cont < indiceCaminosElegidos.size(); cont++) {
                //Double dist = null;//listaDistancias.get(indiceCaminosElegidos.get(cont) - 1);
                //if(indiceCaminosElegidos.get(cont) == cont + 1) {
                int nivelModulacionCorrespondiente = nivelesModulacion.get(cont);
                cont = cont + 1;
                //System.out.println("Distancia de camino " + cont + ": " + dist);
                System.out.println("Nivel de Modulación correspondiente es " + nivelModulacionCorrespondiente);
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
                    .getLogger(SLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return vectorAlfa;
    }

    private static ArrayList<Parametro> leerParametros() {

        ArrayList<Parametro> listaParametros = new ArrayList();

        try {

            FileReader fr = new FileReader("parametros.txt");
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
                    .getLogger(SLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return listaParametros;
    }
    public static boolean isNumeric(String str)  
{  
  try  
  {  
    double d = Double.parseDouble(str);  
  }  
  catch(NumberFormatException nfe)  
  {  
    return false;  
  }  
  return true;  
}
}
