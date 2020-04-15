/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package py.com.fp.una.rbcmsa.ilp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Ivan
 */
public class ILPAuxiliar {
    

    public static void main(String args[]) throws IOException {
        FileReader fr = new FileReader("salidaCplexSL-ILP_1.txt");
        FileReader fr2 = new FileReader("salidaCplexSL-ILP_1.txt");
        String[] edgeText, edgeText2, edgeText3, edgeText4;
        String texto = "";

        try (BufferedReader entrada = new BufferedReader(fr)) {
            String line;

            while ((line = entrada.readLine()) != null) {
                texto += line;
            }
            entrada.close();
        }
        
        
        edgeText = texto.split("Y = ");
        String texto2 = edgeText[1];
        String texto3 = "texto3 =";
        //System.out.println("texto2 = " + texto2);
        texto2 = texto2.replace("                 ", " ").replace("]             [","").replace("             ", "");
        edgeText2 = texto2.split("\\]");
        for(int i=0; i < edgeText2.length; i++){
            //System.out.println(edgeText2[i]);
            texto3 += edgeText2[i];
        
        }
        //System.out.println(texto3);
        edgeText3 = texto3.split(";N");
        
        String texto4;
        texto4 = edgeText3[0];
        edgeText4 = texto4.split("\\[");
        System.out.println("Y = ");
        for(int i=3; i < edgeText4.length; i++)
            System.out.println(edgeText4[i]);
        
        String texto5 = "";
        try (BufferedReader entrada2 = new BufferedReader(fr2)) {
            String line;

            while ((line = entrada2.readLine()) != null) {
                texto5 += line;
            }
            entrada2.close();
        }
        
        edgeText = texto.split("Z = ");
        String texto6 = edgeText[1];
        //System.out.println("texto2 = " + texto2);
        texto6 = texto6.replace("]             [","\n").replace(" ", "").replace("[", "").replace("]", "").replace(";", "");
        System.out.println("Z = \n" + texto6);
    }
}
