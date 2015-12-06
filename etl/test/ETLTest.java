package etl.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import javax.sound.midi.Soundbank;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamConstants;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

public class ETLTest  {

	/**
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * 
	 */
	XMLInputFactory inputFactory;
	XMLStreamReader reader;
	XMLOutputFactory outputFactory;
	XMLStreamWriter writer; 

	String foUri= "http://purl.org/ontology/fo/";
	String dcUri = "http://purl.org/dc/elements/1.1/";
	String exUri ="http://example.org/";
	String rdfUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	String dctUri = "http://purl.org/dc/terms/";
	String frbrerUri = "http://iflastandards.info/ns/fr/frbr/frbrer/";
	String rdfsUri = "http://www.w3.org/2000/01/rdf-schema#";
	String foafUri = "http://xmlns.com/foaf/0.1/";
        
        
	public ETLTest(String inputFilename, String outputFilename) throws XMLStreamException, FileNotFoundException {
		this.inputFactory = XMLInputFactory.newInstance();
		this.reader = this.inputFactory.createXMLStreamReader(new FileInputStream(inputFilename), "UTF8");
		inputFactory.setProperty("javax.xml.stream.isCoalescing", true);
		this.outputFactory = XMLOutputFactory.newInstance();
		this.writer = this.outputFactory.createXMLStreamWriter(new FileOutputStream(outputFilename), "UTF8");
	}
	public static void main(String args[] ) throws XMLStreamException, FileNotFoundException {
		System.out.println("Ingrese directorio raiz");
		Scanner scanInput= new Scanner(System.in);
		String text_in= scanInput.nextLine();
		System.out.println("Ingrese carpeta para guardar resultados");
		Scanner scanOutput= new Scanner(System.in);
		String text_out= scanOutput.nextLine();
		File dir= new File(text_in);
		Collection listFiles= FileUtils.listFiles(dir, new String[]{"xml"},true);
		Iterator fileIterator =listFiles.iterator();
		int i=0;
		while (fileIterator.hasNext()){
			File file= (File) fileIterator.next();
			ETLTest e = new ETLTest(file.getAbsolutePath(),text_out+"Receta"+i+".rdf");
			e.parseAndWrite();
			i++;
		}
	}
	public void writeRDFElement(String prefix, String name, String uri, Element foodElement) throws XMLStreamException {
		String text = this.setBlankAttribute();
		Element tagElement=new Element();
		tagElement.setPrefix(prefix);
		tagElement.setUri(uri);
		tagElement.setElementName(name);
		tagElement.setText(text);
		foodElement.appendElement(tagElement);
		tagElement.write(this.writer);
		this.writer.writeCharacters("\n");
	}
	public void setUriandName(String prefix,String name,String uri, Element foodElement){
		foodElement.setPrefix(prefix);
		foodElement.setUri(uri);
		foodElement.setElementName(name);
	}
	/**
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * 
	 */
	public void parseAndWrite() throws XMLStreamException, FileNotFoundException {
		String tagname;
		//XMLStreamWriter obraWriter = this.writer.get(0);
		Element recipeElement = new Element();
		Element ingElement=new Element();

		this.writer.writeStartDocument();
		this.writer.setPrefix("rdf", rdfUri);
		this.writer.writeStartElement(rdfUri, "RDF");
		// XML namespaces
		this.writer.writeNamespace("dc", dcUri);
		this.writer.writeNamespace("fo", foUri);
		this.writer.writeNamespace("ex", exUri);
		this.writer.writeNamespace( "dct", dctUri );
		this.writer.writeNamespace("frbrer", frbrerUri);
		this.writer.writeNamespace( "rdf", rdfUri );
		this.writer.writeNamespace("rdfs", rdfsUri);
		this.writer.writeNamespace("foaf", foafUri);

        boolean hasTitle=false;
		while(this.reader.hasNext()) {
			if (this.reader.next() != XMLStreamConstants.START_ELEMENT) continue;

			tagname = this.reader.getName().toString();
			this.reader.next();
			this.writer.flush();
			if (tagname.equals("directions") || tagname.equals("ingredients") || tagname.equals("amt") || tagname.equals("head") || tagname.equals("categories")){
				continue;
			}
			switch (tagname) {
				case "recipe":
					this.writer.writeCharacters("\n");
					this.setUriandName("fo", "Recipe", foUri, recipeElement);
					this.writer.writeStartElement(foUri, "Recipe");
					this.writer.writeCharacters("\n");
					break;
				case "title":
					if (!hasTitle){
						//hasTitle=true;
						this.writeRDFElement("foaf", "name", foafUri, recipeElement);
						hasTitle=true;
						break;
					}
					break;
				case "cat":
					this.writeRDFElement("ex", "category", exUri, recipeElement);
					break;
				case "yield":
					this.writeRDFElement("ex", "yield", exUri, recipeElement);
					break;
				case "ing":
					this.setUriandName("fo", "Ingredient", foUri, ingElement);
					recipeElement.appendElement(ingElement);
					break;
				case "qty": {
					String text=setBlankAttribute();
					ingElement.appendAttribute("fo", "quantity", text);
					break;
				}
				case "unit": {
					String text;
					text = setBlankAttribute();
					ingElement.appendAttribute("fo", "metric_quantity", text);
					break;
				}
				case "item": {
					//Agrega los items al principio, pero en el xml aparecen al final
					String text = this.setBlankAttribute();
					Attribute attribute = new Attribute("food", text, "fo");
					ingElement.getAttributes().add(0, attribute);
					ingElement.write(this.writer);
					this.writer.writeCharacters("\n");
					ingElement = new Element();
					break;
				}
				case "step": {
					String text = this.setBlankAttribute();
					while (this.reader.hasNext()){
						int next = this.reader.next();
						if (next!= XMLStreamConstants.START_ELEMENT){
							break;
						}else {
							text =text+this.reader.getText();
						}
					}
					Element stepElement = new Element();
					this.setUriandName("fo", "Step", foUri, stepElement);
					stepElement.appendAttribute("fo", "Instruction", text);
					recipeElement.appendElement(stepElement);
					stepElement.write(this.writer);
					this.writer.writeCharacters("\n");
					break;
				}
			}
		}
		this.writer.writeEndDocument();
		this.writer.close();
		this.reader.close();
	}

	private String setBlankAttribute() {
		String text;
		try{
			text=this.reader.getText();
		}catch (IllegalStateException e){
			text="no attribute";
		}
		return text;
	}

}
