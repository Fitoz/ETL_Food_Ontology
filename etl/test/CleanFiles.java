package etl.test;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by luism on 06-12-15.
 */
public class CleanFiles {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Ingrese directorio raiz");
        Scanner scanInput= new Scanner(System.in);
        String text_in= scanInput.nextLine();
        System.out.println("Ingrese carpeta para guardar resultados");
        Scanner scanOutput= new Scanner(System.in);
        String text_out= scanOutput.nextLine();
        File dir= new File(text_in);
        Collection listFiles= FileUtils.listFiles(dir, new String[]{"xml"}, true);
        Iterator fileIterator =listFiles.iterator();
        BufferedReader br;
        BufferedWriter bw;
        String currentLine;
        String newLine;
        int i=0;
        while (fileIterator.hasNext()){
            File file= (File) fileIterator.next();
            try {
                br = new BufferedReader(new FileReader(file.getAbsolutePath()));
                bw=new BufferedWriter(new FileWriter(text_out+file.getName()));
                while ((currentLine = br.readLine()) != null) {
                    newLine=currentLine.replaceAll("[^A-Za-z0-9() <>=?\"\\'\\.\\/\\-\\[\\]\n]","");
                    bw.write(newLine);
                }
                br.close();
                bw.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
}
