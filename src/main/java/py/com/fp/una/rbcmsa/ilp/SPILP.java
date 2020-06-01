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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import py.com.fp.una.rbcmsa.grafos.model.Grafo;
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

    public void JPILP(String ruta, String nombre, Integer k, List<PeticionBCM> peticionesFinales,
            Grafo grafo, String guardBan, int fTotal) throws IOException {
        
        adaptacionesBean.preparaArchivoJPILP(ruta, nombre, k+"", peticionesFinales, grafo, guardBan, fTotal);
        /* Probamos la conexión de Java con CPLEX */
        String argumentos[] = {"-v", "./src/JPILP/JP-ILP.mod", "./src/resultados/JPILP.dat", "./src/resultados/salidaCplexILP1.txt"};
        

        OplRunILP.main(argumentos);    
        ArrayList<Integer> indiceCaminosElegidos = leerSalidaCplex(k, 1);
        List<Integer> posicionesFS = obtenerFFaseII(1);
        adaptacionesBean.asigarGrafoILP(indiceCaminosElegidos, posicionesFS, grafo, peticionesFinales, k);
    }
    
    public void ILP(String ruta, String nombreFaseI, String nombreFaseII, Integer k, List<PeticionBCM> peticionesFinales,
            Grafo grafo, String guardBan, int fTotal) throws IOException {

        for (int pruebas = 0; pruebas </*3*/ 1; pruebas++) {

            AlphaR alphaR = adaptacionesBean.preparaArchivoFaseIILP(ruta, nombreFaseI, k + "", peticionesFinales, grafo, guardBan + "");

            CSVFinlaWriter.generarCabeceraCSVEstatico(pruebas + 1);

            System.out.println("----- PRUEBA " + pruebas + "-----");

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

    private static ArrayList<Integer> leerSalidaCplex(int k, int y) throws FileNotFoundException {
        ArrayList<Integer> listaCaminosElegidos = new ArrayList();
        //FileReader fr = new FileReader("./src/resultados/salidaCplexSP-ILP_1" + y + ".txt");
        FileReader fr = new FileReader("./src/resultados/salidaCplexILP1.txt");
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
        //String f = leerArchivo("./src/resultados/salidaCplexSP-ILP_2" + y + ".txt");
        String f = leerArchivo("./src/resultados/salidaCplexILP1.txt");
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
