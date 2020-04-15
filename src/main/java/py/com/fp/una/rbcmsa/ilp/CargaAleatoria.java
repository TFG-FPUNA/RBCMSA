package py.com.fp.una.rbcmsa.ilp;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by ysapymimbi on 03/04/2017.
 */
public class CargaAleatoria {

    public static int main(String [] args) throws IOException {
        
        DecimalFormat df = new DecimalFormat("#.0");
        int nodos = Integer.parseInt(args[0]);
        int cantVeces = Integer.parseInt(args[1]);
        int cantSolicitudes = Integer.parseInt(args[2]);
        double [][] matrizUltimaDemanda = new double[nodos][nodos];
        double cantDemandaMaxima = 50;
        double cantDemandaMinima = 12.5;
        double temprnd = 0;
        Random rnd = new Random();
        int id = 0;
        int  i, j;
        
        boolean tiemposTerminados = false;
        for (int cont=1; cont<=cantVeces; cont++){
            BufferedWriter bw = new BufferedWriter(new FileWriter("./src/resultados/solicitudesrandom" + cont + ".txt"));
            /*if (cont>1){
                BufferedReader rd = new BufferedReader(new FileReader("./src/resultados/solicitudesrandom" + Integer.toString(cont-1) + ".txt"));
                //tiemposTerminados = copiarSolicitudesPendientes(bw, rd);
            }*/
            
            for (int c=0; c<cantSolicitudes; c++){
                i= rnd.nextInt(nodos);
                do{
                    j= rnd.nextInt(nodos);
                }while(j==i);
                temprnd = cantDemandaMaxima * rnd.nextDouble() + cantDemandaMinima ;
                matrizUltimaDemanda[i][j] = temprnd;
                String tmp = df.format(temprnd);
                Integer tiempo = rnd.nextInt(10) + 1;
                bw.write(id+"\t"+i + "\t" + j + "\t" + tmp.replace(',', '.') + "\t" + tiempo + "\n");
                id++;
            }
            bw.close();
        }
        return cantVeces;
    }

    public static void crearCargaAleatoria(String pathArchivo, Integer cantDemandadaMinima, Integer cantDemandadaMaxima) throws IOException {

        // este archivo tiene la lista de nodos fuente-destino de todas las demandas, sin la cantidad demandada
        FileReader fr = new FileReader(pathArchivo + "./src/resultados/solicitudes.txt");
        BufferedReader bf = new BufferedReader(fr);

        File f = new File("./src/resultados/solicitudesrandom.txt");
        FileWriter fw = new FileWriter(f);

        String fila, nuevaFila;
        int i;
        Integer carga;
        Random rnd = new Random();
        boolean finDeLinea;

        try {
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter wr = new PrintWriter(bw);
            while ((fila = bf.readLine()) != null && !fila.isEmpty() ) {
                System.out.println("Linea: " + fila);
                finDeLinea = false;
                i = 0;
                while (!finDeLinea) {
                    if (fila.length() <= i || fila.charAt(i) == '\n') {
                        carga = rnd.nextInt(cantDemandadaMaxima - cantDemandadaMinima) + cantDemandadaMinima;
                        nuevaFila = fila + "\t" + carga.toString() + "\n";
                        finDeLinea = true;
                        wr.write(nuevaFila);
                    }
                    i++;
                }
            }

            wr.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static boolean copiarSolicitudesPendientes(BufferedWriter bw, BufferedReader rd) throws IOException {
        boolean existe = false;
        //String line = "";
        String line;
        while ((line = rd.readLine()) != null) {
            if (line!=""){
                String [] temp = line.split("\t");
                Integer tiempo = Integer.parseInt(temp[4])-1;
                if (tiempo>0){
                    bw.write(temp[0] + "\t" + temp[1] + "\t" + temp[2] + "\t" + temp[3] + "\t" + tiempo + "\n");
                    if (tiempo>1) existe = true;
                } 
            }
        }
        rd.close();
        return existe;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
