package xml;

import java.util.*;
import components.data.*;

/**
 * deals with generating the XML tags for the data input
 * @author Julianna Gabler
 */
public class XML{
/**********************************************************************************************************************/
    /********** ATTRIBUTE *************************************************************************************************/
    private final String START_TAG = "<?xml version='1.0' encoding='UTF-8' standalone='no' ?>";
    private Appointment appt;
/**********************************************************************************************************************/
/********* CONSTRUCTOR ************************************************************************************************/
    /**
     * sets the appointment object for getting the neccessary information
     * @param appt
     */
    public XML(Appointment appt){
        this.appt = appt;
    }

    /**
     * used printing out the error only
     */
    public XML(){}
/**********************************************************************************************************************/
    /************ MUTATORS ************************************************************************************************/
    public void setAppointment(Appointment appt){ this.appt = appt; }//end setAppointmnet
/**********************************************************************************************************************/
    /*********** ACCESSORS ************************************************************************************************/
    public String getTagNameStart(){ return "<AppointmentList>"; } //end getTagNameStart
    public String getTagNameEnd(){ return "</AppointmentList>"; } //end getTagNameEnd
    public String getStartTag(){ return START_TAG; } //end getStartTad
/**********************************************************************************************************************/
/************* METHODS ************************************************************************************************/
    /**
     * gets the appointment String from the corresponding elements
     * Appointment obj, Patient obj, Phlebotomist obj, PSC obj, LabTest <code>List<obj></code>
     * @return xml String
     */
    public String appointmentXML(){
        String xml = this.getAppointmentInfo() + this.getPatientInfo();
        xml += this.getPhlebInfo() + this.getPscInfo() + this.getLabTests();
        xml += "</appointment>";
        return xml;
    }//end appointmentXML

    /**
     * gets the xml string from appointment object
     * @return xml String
     */
    private String getAppointmentInfo(){
        String xml = "<appointment date='" + this.appt.getApptdate() + "' id='" + this.appt.getId() + "' ";
        xml += "time='" + this.appt.getAppttime() + "'>";
        return xml;
    }//end getAppointment Info

    /**
     * gets the patient info and returns it in xml string
     * @return xml String
     */
    private String getPatientInfo(){
        Patient patient = this.appt.getPatientid();
        String xml = "<uri>http://localhost:8080/LAMSService/resources/Services/Appointments/" + patient.getId()
                + "</uri><patient id='" + patient.getId() + "'><uri/><name>" + patient.getName() 
                + "</name><address>" + patient.getAddress() + "</address>" + "<insurance>"
                + patient.getInsurance() +"</insurance><dob>" + patient.getDateofbirth() + "</dob></patient>";
        return xml;
    }//end getPatientInfo

    /**
     * gets the phlebotomist object information and returns it in xml string
     * @return xml String
     */
    private String getPhlebInfo(){
        Phlebotomist phlebotomist = this.appt.getPhlebid();
        String xml = "<phlebotomist id='" + phlebotomist.getId() + "'><uri/><name>" + phlebotomist.getName() + "</name>"
                    + "</phlebotomist>";
        return xml;
    }//end getPhlebInfo

    /**
     * gets the psc object info and returns it in xml string
     * @return xml String
     */
    private String getPscInfo(){
        PSC psc = this.appt.getPscid();
        String xml = "<psc id='" + psc.getId() + "'><uri/><name>" + psc.getName() + "</name></psc>";
        return xml;
    }//end getPscInfp

    /**
     * gets the lab test objects and return it in xml string
     * @return xml String
     */
    private String getLabTests(){
        List<AppointmentLabTest> alt = this.appt.getAppointmentLabTestCollection();
        String xml = "<allLabTests>";
        //goes through the List and adds neccessary string
        for(AppointmentLabTest a: alt){
            Diagnosis diagnosis = a.getDiagnosis();
            LabTest labTest = a.getLabTest();
            xml += "<appointmentLabTest apptId='" + this.appt.getId() + "' dxcode='" + diagnosis.getCode()
                    + "' labTestId='" + labTest.getId() + "'/><uri/>";
        }
        xml += "</allLabTests>";
        return xml;
    }//end getLabTests

    /**
     * gets the full xml string
     * @param xml
     * @return
     */
    public String getFullXml(){
        String xmlStr = this.getStartTag() + this.getTagNameStart() + this.appointmentXML();
        xmlStr += this.getTagNameEnd();
        return xmlStr;
    }//end getFullXml

    /**
     * returns the error string
     * @return String
     */
    public String error(){
        return START_TAG + this.getTagNameStart() + "<error>ERROR: Appointment is not available</error>" + this.getTagNameEnd();
    }//end error
}//end XML class