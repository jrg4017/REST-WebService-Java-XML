package business;

import xml.*;
import java.util.*;
import components.data.*;

/**
 * created to keep the AppointmentService's code minimal
 * @author Julianna Gabler
 */
public class Main{
/**********************************************************************************************************************/
    /********** ATTRIBUTE *************************************************************************************************/
    Database database = null;
/**********************************************************************************************************************/
/************ METHODS *************************************************************************************************/
    /**
     * intialize the database
     * @return boolean
     */
    public boolean initialize(){
        this.database = new Database();
        return this.database.load();
    }//end intiialize

    /**
     * get all of the appointments in the database
     * @return xml String
     */
    public String getAllAppointments(){
        //grab the list of appointments from database
        List<Object> obj;
        do{
         obj = this.database.getDB().getData("Appointment","");
         if(obj == null){ this.initialize(); } //reintialize if list is null
        }while(obj == null);
        
        //create objects for appt and xml
        Appointment appt = new Appointment();
        XML xml = new XML();
        String xmlStr = xml.getStartTag() + xml.getTagNameStart();//add starting tags
        //for each appointment grab the XML tags corresponding it
        for(Object objs : obj){
            xml.setAppointment((Appointment)objs);
            xmlStr += xml.appointmentXML();
        }
        //add the </AppointmentList> ending
        xmlStr += xml.getTagNameEnd();
        return xmlStr;
    }//end getAllAppointments

    /**
     * gets the appointment in xml string format
     * @param appointNumber the number of the appt
     * @return String
     */
    public String getAppointment(String appointNumber){
        List<Object> obj = this.database.getAppointment(appointNumber);
        Object rtn = null;
        for(Object objs : obj){
            rtn = objs;
        }
        //if null or empty, return the error string
        if(rtn == "" || rtn == null){
            XML xml = new XML();
            return xml.error();
        }
        Appointment appt = (Appointment)rtn;
        XML xml = new XML(appt);
        //return it in full
        return xml.getFullXml();
    }//end getAppointment

    /**
     * adds the appointment to the database or gets the next available appointment
     * from the database
     * @param xmlStyle
     * @return apptXML
     */
    public String addAppointment(String xmlStyle){
        try {
            //parse the XML document and grab necessary information
            ParseXML parseXML = new ParseXML(xmlStyle);
            //validate data
            DataValidation dv = new DataValidation(parseXML.getApptInfo(), parseXML.getLabTests());
            //add the appointment or get the next appointment
            boolean added = dv.apptRequirements();

            XML xml = new XML();

            if(added) {
                xml.setAppointment(dv.getNewAppt()); //set the appointment value
                return xml.getFullXml();
            }
            return xml.error();

        }catch(NullPointerException npe){}
        catch(Exception e){}
        XML x = new XML(); //print out error if caught at this stage
        return x.error();
    }//end addAppointment
}//end Main class