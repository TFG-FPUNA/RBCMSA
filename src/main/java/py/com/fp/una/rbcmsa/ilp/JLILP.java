/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ilp;

/**
 *
 * @author Ivan
 */
import ilog.opl.IloOplFactory;
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

public class JLILP {

    public static void main(String args[]) throws IOException {

        long time_start, time_end;
        time_start = System.currentTimeMillis();


        try {
            Configuracion config = leerConfiguraciones();
            // Definimos e instanciamos el grafo con pesos
            SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph =
                    new SimpleDirectedGraph(DefaultWeightedEdge.class);
            int cantidadEnlaces = cargarGrafo(directedGraph);
            
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
                        .getLogger(JLILP.class
                        .getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(JLILP.class
                        .getName()).log(Level.SEVERE, null, ex);
            }


            try { 
                FileWriter fw = new FileWriter("C:\\Users\\SantiagoMiguel\\opl\\ILP3\\JL-ILP.dat");

                BufferedWriter bw = new BufferedWriter(fw);
                try (PrintWriter salida = new PrintWriter(bw)) {
                    //System.out.println("K = " + config.getK() + ";");
                    //salida.println("K = " + config.getK() + ";");
                    
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
                                .getLogger(JLILP.class
                                .getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(JLILP.class
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
                                .getLogger(JLILP.class
                                .getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(JLILP.class
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
                    System.out.println("ENLACES = " + en.toString() + ";");
                    System.out.println("VERTICES = " + vs.size() + ";");
                    salida.println("VERTICES = " + vs.size() + ";");
                    System.out.println("LISTAVERTICES = " + vs.toString() + ";");
                    salida.println("LISTAVERTICES = " + vs.toString() + ";");

                    System.out.print("NE1 = [[");
                    salida.print("\nNE1 = [\n[");
                    int indiceVertice = 0;
                    for (Iterator<String> it = vs.iterator(); it.hasNext();) {
                        //it.next();
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
                        //it.next();
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
                        //it.next();
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
                String argumentos[] = {"-v", "C:\\Users\\SantiagoMiguel\\opl\\ILP3\\JL-ILP.mod", "C:\\Users\\SantiagoMiguel\\opl\\ILP3\\JL-ILP.dat", "salidaCplexILP3.txt"};
                OplRunILP.main(argumentos);


            } catch (IOException ex) {
                Logger.getLogger(JLILP.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JLILP.class.getName()).log(Level.SEVERE, null, ex);
        }
        time_end = System.currentTimeMillis();

        System.out.println(
                "EL ILP 3 ha tomado " + (time_end - time_start) + " milisegundos");
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
                    .getLogger(JLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return cantEnlaces;
    }

    private static String leerSolicitudes(SimpleDirectedGraph<String, DefaultWeightedEdge> directedGraph, int r) {

        final double C = 154;

        //ArrayList<Parametro> listaParams = leerParametros();

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
                    .getLogger(JLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JLILP.class
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
                    .getLogger(JLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JLILP.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return config;
    }
}
