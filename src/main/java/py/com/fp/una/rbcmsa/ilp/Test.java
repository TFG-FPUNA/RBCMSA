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
import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        String request = "Y =";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("salidaCplexILP4_1.txt"));
            String line;
            int cnt1 = 0;
            while ((line = reader.readLine()) != null) {
                cnt1++;
                if (line.indexOf(request) != -1) {
                    System.out.println(cnt1);
                }
            }
            reader.close();
            
            reader = new BufferedReader(new FileReader("salidaCplexILP4_1.txt"));
            request = "N =";
            int cnt2 = 0;
            while ((line = reader.readLine()) != null) {
                cnt2++;
                if (line.indexOf(request) != -1) {
                    System.out.println(cnt2);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}