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
        
        String base_uri = "http://datos.uchile.cl/recurso/";
	String owlUri = "http://datos.uchile.cl/ontologia/";
	String rdfUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	String dcUri = "http://purl.org/dc/elements/1.1/";
	String dctUri = "http://purl.org/dc/terms/";
	String bioUri = "http://vocab.org/bio/0.1/";
	String frbrerUri = "http://iflastandards.info/ns/fr/frbr/frbrer/";
	String schemaUri = "http://schema.org/";
	String rdfsUri = "http://www.w3.org/2000/01/rdf-schema#";
        String foafUri = "http://xmlns.com/foaf/0.1/";
        
        
	public ETLTest(String inputFilename, String outputFilename) throws XMLStreamException, FileNotFoundException {
		this.inputFactory = XMLInputFactory.newInstance();
		this.reader = this.inputFactory.createXMLStreamReader(new FileInputStream(inputFilename), "UTF8");
		this.outputFactory = XMLOutputFactory.newInstance();
		this.writer = this.outputFactory.createXMLStreamWriter(new FileOutputStream(outputFilename), "UTF8");
	}
        public static void main(String args[] ) throws XMLStreamException, FileNotFoundException {
            System.out.println("hola");
            
            Scanner scan1= new Scanner(System.in);
            String text_in= scan1.nextLine();           
            Scanner scan2= new Scanner(System.in);
            //For string
            String text_out= scan2.nextLine();
            //System.out.println(text);
            ETLTest e = new ETLTest(text_in,text_out);
            e.parseAndWrite();
            System.out.println("chaao");
        }
        

	/**
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * 
	 */
	public void parseAndWrite() throws XMLStreamException, FileNotFoundException {
		String id = "";
		String tagname;

		//XMLStreamWriter obraWriter = this.writer.get(0);

                
		Element FoodElement = new Element();
		
		this.writer.writeStartDocument();
		this.writer.setPrefix( "rdf", rdfUri );
		this.writer.writeStartElement( rdfUri, "RDF" );
		// XML namespaces
		this.writer.writeNamespace( "dc", dcUri );
		this.writer.writeNamespace( "dct", dctUri );
		this.writer.writeNamespace( "frbrer", frbrerUri );
		this.writer.writeNamespace( "owl", owlUri );
		this.writer.writeNamespace( "rdf", rdfUri );
                this.writer.writeNamespace( "rdfs", rdfsUri );
                this.writer.writeNamespace( "foaf", foafUri );

		
		boolean isFirst = true;
		boolean isAsset = false;
		
		//Elastic elastic = new Elastic();
		//NameParser nameParser = new NameParser();
		int a=0;
                
		while(this.reader.hasNext()) {
                        //this.writer.flush();
                        //System.out.println("while");
			if (this.reader.next() != XMLStreamConstants.START_ELEMENT) continue; 
                        tagname = this.reader.getName().toString();	
                        this.reader.next();
                        this.writer.flush();
			//String attributeValueType = this.reader.getAttributeValue("", "type");
			//String attributeValueName = this.reader.getAttributeValue("", "name");
			//ignorar informaci?n de la carpeta
			/*if (tagname.equals("recipe")){				
				continue;
			}*/
			
                        /*if (tagname.equals("head")){				
				continue;
			}*/
			if( tagname.equals( "title" ) ) {                            
                            System.out.println("title");
                            String name = this.reader.getText();                                
                            Element titleElement = new Element();
                            titleElement.setPrefix( "foaf" );
                            titleElement.setUri( foafUri );
                            titleElement.setElementName( "name" );
                            titleElement.setText( name );
                            FoodElement.appendElement( titleElement );
                            System.out.println(name);
                            titleElement.write(this.writer);
                            //this.writer.writeEndElement();
                        }                                           
                        if(tagname.equals("cat")){                           
                            System.out.println("cat");
                            String cat = this.reader.getText();
                            Element catElement=new Element();
                            catElement.setPrefix("owl");
                            catElement.setUri(owlUri);
                            catElement.setElementName("category");
                            catElement.setText(cat);
                            FoodElement.appendElement(catElement);
                            System.out.println(cat);
                            catElement.write(this.writer);
                             
                        }
                        //FoodElement.write(this.writer);
			// fin del ciclo
		}                
                System.out.println("printeando /RDF");
                this.writer.writeEndElement();
                this.writer.writeCharacters("</rdf:RDF>");
                String p1=this.writer.getNamespaceContext().toString();
                System.out.println(p1);
		this.writer.writeEndDocument();
                
		/*elastic.close();
		// terminar ?ltimo elemento
		obraWriter.writeEndElement();
		expWriter.writeEndElement();
		manifWriter.writeEndElement();
		// cerrar archivos
		obraWriter.writeEndDocument();
		obraWriter.close();
		expWriter.writeEndDocument();
		expWriter.close();
		manifWriter.writeEndDocument();
		manifWriter.close();
        */

	}

}
