package xml;

import java.util.*;
import javax.xml.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.*;
import org.w3c.dom.NodeList;
import java.io.*;

/**
 * deals with parsing the xml string that's passed in and returns a HashMap
 */
public class ParseXML{
/**********************************************************************************************************************/
    /******** ATTRIBUTES **************************************************************************************************/
    private String xml = "";
    private Element element = null;
    private HashMap<String, String> apptInfo = new HashMap<String, String>();
    private ArrayList<String> labTests = new ArrayList<String>();
/**********************************************************************************************************************/
/********* CONSTRUCTOR ************************************************************************************************/
    /**
     * sets the xml string for parsing
     * @param xml
     */
    public ParseXML(String xml){
        this.xml = xml;
        try{
            this.parseXmlString();
        }catch(Exception e){
            XML x = new XML();
            System.out.println(x.error());
        }
    }//end parse XML
/**********************************************************************************************************************/
    /********** ACCESSORS *************************************************************************************************/
    public HashMap<String, String> getApptInfo(){ return this.apptInfo; }
    public ArrayList<String> getLabTests(){ return this.labTests; }
/**********************************************************************************************************************/
/********** METHODS ***************************************************************************************************/
    /**
     * add key, value to the apptInfo HashMap
     * @param key
     * @param value
     */
    private void addToHashMap(String key, String value){
        this.apptInfo.put(key, value);
    }//end addToHashMap
    /**
     * add key, value to the labTest HashMap
     * @param key
     * @param value
     */
    private void addToLabTest(String value){ this.labTests.add(value); }

    /**
     * gets the dom object for parsing
     * @return Document
     * @throws Exception
     */
    private Document getDocumentObject() throws Exception{
        //set up a way to grab the XML string rather than XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource();
        inputSource.setCharacterStream(new StringReader(this.xml));

        return builder.parse(inputSource);
    }//edn getDocument object

    /**
     * parses through the xml string and grabs the information passed in
     * @throws Exception
     */
    private void parseXmlString() throws Exception {
        Document doc = getDocumentObject(); //return the doc object of the XML String

        //get the nodes under appointment
        NodeList apptNodes = doc.getElementsByTagName("appointment");
        //can get data for multiple appointment objects, but not neccssary
        for(int i = 0; i < apptNodes.getLength(); i++){
            this.element = (Element) apptNodes.item(i); //set the main element
            //get each corresponding item in the xml list
            this.addNodeItem("date", "Date");
            this.addNodeItem("time", "Time");
            this.addNodeItem("patientId", "Patient");
            this.addNodeItem("physicianId", "Physician");
            this.addNodeItem("pscId", "PSC");
            this.addNodeItem("phlebotomistId", "Phlebotomist");
            addLabTest(doc);
        }//end for loop
    }//end parsXmlString

    /**
     * gets the String value for each node and inserts it into the HashMap
     * @param tagName
     * @param key
     */
    private void addNodeItem(String tagName, String key){
        NodeList nl = this.element.getElementsByTagName(tagName);
        Element line = (Element)nl.item(0);
        this.addToHashMap(key, this.elementToString(line));
    }//end addNodeItem

    /**
     * adds all of the lab tests and plugs them into the arraylist
     * @param doc
     */
    private void addLabTest(Document doc){
        Element e = (Element)doc.getElementsByTagName("labTests").item(0); //grab new element
        NodeList nl = e.getElementsByTagName("test"); //get the nodelist
        for(int i = 0; i < nl.getLength(); i++){ //grab each id and dxcode
            String line = nl.item(i).getAttributes().getNamedItem("id").getNodeValue();
            this.addToLabTest(line);
            line = nl.item(i).getAttributes().getNamedItem("dxcode").getNodeValue();
            this.addToLabTest(line);
        }
    }//end addLabTest

    /**
     * acutally conversion of the nodelist's child to a string
     * @param e - the passed in element from addNodeItem
     * @return String
     */
    private String elementToString(Element e){
        //get the data and return it as a string
        Node child = e.getFirstChild();
        if(child instanceof CharacterData){ //converts to string here
            CharacterData cd = (CharacterData)child;
            return cd.getData();
        }
        return "";
    }//end elementToString
}//end class