package etl.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamConstants;
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
		this.outputFactory = XMLOutputFactory.newInstance();
		this.writer = this.outputFactory.createXMLStreamWriter(new FileOutputStream(outputFilename), "UTF8");
	}
	public static void main(String args[] ) throws XMLStreamException, FileNotFoundException {
		System.out.println("Ingrese archivo de entrada y de salida");
		Scanner scanInput= new Scanner(System.in);
		String text_in= scanInput.nextLine();
		Scanner scanOutput= new Scanner(System.in);
		//For string
		String text_out= scanOutput.nextLine();
		ETLTest e = new ETLTest(text_in,text_out);
		e.parseAndWrite();
		System.out.println("Termino");
	}
	public void writeRDFElement(String prefix, String name, String uri, Element foodElement) throws XMLStreamException {
		String text = this.reader.getText();
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
		this.writer.writeNamespace( "dc", dcUri );
		this.writer.writeNamespace("fo",foUri);
		this.writer.writeNamespace("ex",exUri);
		this.writer.writeNamespace( "dct", dctUri );
		this.writer.writeNamespace("frbrer", frbrerUri);
		this.writer.writeNamespace( "rdf", rdfUri );
		this.writer.writeNamespace("rdfs", rdfsUri);
		this.writer.writeNamespace("foaf", foafUri);

                
		while(this.reader.hasNext()) {
			if (this.reader.next() != XMLStreamConstants.START_ELEMENT) continue;

			tagname = this.reader.getName().toString();
			this.reader.next();
			this.writer.flush();
			if (tagname.equals("directions") || tagname.equals("ingredients") || tagname.equals("amt") || tagname.equals("head") || tagname.equals("categories")){
				continue;
			}
			if (tagname.equals("recipe")){
				this.writer.writeCharacters("\n");
				this.setUriandName("fo", "Recipe", foUri, recipeElement);
				this.writer.writeStartElement(foUri, "Recipe");
				this.writer.writeCharacters("\n");
			}
			else if( tagname.equals( "title" ) ) {
				this.writeRDFElement("foaf", "name", foafUri, recipeElement);
			}
			else if(tagname.equals("cat")){
				this.writeRDFElement("ex", "category", exUri, recipeElement);
			}
			/*else if (tagname.equals("yield")){
				this.writeRDFElement("ex", "yield", exUri, recipeElement);
			}
			else if (tagname.equals("ing")){
				this.setUriandName("fo","Ingredient",foUri,ingElement);
				recipeElement.appendElement(ingElement);
			}
			else if (tagname.equals("qty")){
				String text=this.reader.getText();
				ingElement.appendAttribute("fo", "quantity", text);
			}
			else if(tagname.equals("unit")){
				String text;
				if (this.reader.isStartElement()){
					text=this.reader.getText();
				}else {
					text="no unit";
				}
				ingElement.appendAttribute("fo","metric_quantity",text);
			}
			else if(tagname.equals("item")){
				//Agrega los items al principio, pero en el xml aparecen al final
				String text=this.reader.getText();
				Attribute attribute=new Attribute("food",text,"fo");
				ingElement.getAttributes().add(0,attribute);
				ingElement.write(this.writer);
				this.writer.writeCharacters("\n");
				ingElement=new Element();
			}*/
			else if (tagname.equals("step")){
				//this.writeRDFElement("fo","Step",foUri,recipeElement);
				String text=this.reader.getText();
				Element stepElement=new Element();
				this.setUriandName("fo","Step",foUri,stepElement);
				stepElement.appendAttribute("fo", "Instruction", text);
				recipeElement.appendElement(stepElement);
				stepElement.write(this.writer);
				this.writer.writeCharacters("\n");
			}
		}
		this.writer.writeCharacters("</rdf:RDF>");
		this.writer.writeEndDocument();
	}

}
