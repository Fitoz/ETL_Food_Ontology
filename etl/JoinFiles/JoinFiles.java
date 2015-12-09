package joinfiles;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.FileNotFoundException;

import java.util.Collection;
import java.util.Scanner;
import java.util.Iterator;

public class JoinFiles {
	
	private BufferedWriter bw;

	public JoinFiles( String string,String head) throws IOException {
		FileWriter fileWriter = new FileWriter(string);
		bw = new BufferedWriter(fileWriter);
		bw.write(head);
		bw.newLine();
	}

	
	public static void main(String args[] ) throws IOException {
		System.out.println("Ingrese directorio raiz");
		Scanner scanInput= new Scanner(System.in);
		String text_in= scanInput.nextLine();
		System.out.println("Ingrese archivo para guardar resultados");
		Scanner scanOutput= new Scanner(System.in);
		String text_out= scanOutput.nextLine();
		System.out.println("Ingrese head");
		Scanner head1= new Scanner(System.in);
		String head= head1.nextLine();
		File dir= new File(text_in);
		Collection listFiles= FileUtils.listFiles(dir, new String[]{"rdf"},true);
		Iterator fileIterator =listFiles.iterator();
		JoinFiles e = new JoinFiles(text_out+".rdf",head);
		while (fileIterator.hasNext()){
			File file= (File) fileIterator.next();			
			e.AddRdf(file.getAbsolutePath());
			//e.parseAndWrite();
		}
		e.close();
	}
	private void close() throws IOException {
		bw.write("</rdf:RDF>");
		bw.close();
	}
	private void AddRdf(String fileName) throws IOException {
		try {
			FileReader fileReader =new FileReader(fileName);
			BufferedReader br = new BufferedReader(fileReader);
			String line = null;
			 while((line = br.readLine()) != null) {
			 	if (line.contains("xml")){
			 		continue;
			 	}
			 	else if(line.contains("</fo:Recipe></rdf:RDF>")){
			 		bw.write("</fo:Recipe>");
			 		bw.newLine();
			 	}
			 	else{
			 		bw.write(line);
			 		bw.newLine();
			 	}
			 }
			 br.close();
			
		}
		catch(FileNotFoundException  ex) {
            System.out.println(
                    "Error finding file '" 
                    + fileName + "'");
		}
		
	}
}
