package service;

import java.util.*;
import components.data.*;
import xml.XML;
import xml.ParseXML;
import business.*;
import javax.ws.rs.core.*;
import javax.ws.rs.*;

@Path("/Services")
public class LAMSService{
   protected  Database database = null;
   /**
     * initialize the database
     * "/
     * @return msg String - whether db initialized or not
     */
   @GET
   @Produces("application/xml")
   //@Produces("text/plain")
   public String getInfo(){
        this.database = new Database();
        boolean initalized = this.database.load();
        if(initalized) return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><wadl>http://localhost:8080/LAMSService/resources/application.wadl</wadl>";
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><AppointmentList>Database failed to initalized</AppointmentList>";
        //TODO change link to wadl this.context.getBaseUri().toString() + "application.wadl";
    }//end initialize*/
    
    @Path("/Appointments")
    @GET
    @Produces("application/xml")
    //@Produces("text/plain")
     public String getAllAppointments(){
         //grab the list of appointments from database
        List<Object> obj;
        //initalize if null
        if(this.database == null) this.database = new Database();
       
        do{
         obj = this.database.getDB().getData("Appointment","");
         if(obj == null){ this.getInfo(); } //reintialize if list is null
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
     * gets & return a specfic appointment and related info
     * @param appointNumber String
     * @return xmlString String
     */
    @Path("/Appointments/{appointment}")
    @GET
    @Consumes("text/plain")
    @Produces("application/xml")
    public String getAppointment(@PathParam("appointment") String appointment){
        //initialize if for some reason null
        if(database == null) this.database = new Database();
        List<Object> obj = this.database.getAppointment(appointment);
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
     * create a new Appointment providing the required info
     * in XML and receiving XML or error message
     * @param xmlStyle String
     * @return xmlString String
     */
    @Path("/Appointments")
    @PUT
    @Consumes({"text/xml","application/xml"})
    @Produces("application/xml")
    public String addAppointment(@DefaultValue("1") @QueryParam("xmlStyle") String xmlStyle){
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

}