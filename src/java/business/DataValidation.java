package business;

import components.data.*;
import java.util.*;
import java.sql.Date;
import java.sql.Time;
import java.text.*;

/**
 * deals with the data validation for the appointment service
 * @author Julianna Gabler
 */
public class DataValidation{
/**********************************************************************************************************************/
    /******** ATTRIBUTES **************************************************************************************************/
    private HashMap<String, String> apptIds = new HashMap<String, String>();
    private ArrayList<String> tests = new ArrayList<String>();
    Appointment newAppt = null;
    Database db = new Database();
/**********************************************************************************************************************/
/******************* CONSTRUCTOR **************************************************************************************/
    /**
     * initializes the getInfo class
     */
    public DataValidation(){}//end DataValidation
    /**
     * intializes the getinfo class
     * also sets the hashmaps for apptIds and tests
     * @param apptIds
     * @param tests
     */
    public DataValidation(HashMap<String, String> apptIds, ArrayList<String> tests){
        this.setApptIds(apptIds);
        this.setTests(tests);
    }//end DataValidation
/**********************************************************************************************************************/
/******************** MUTATORS  ***************************************************************************************/
    /**
     * sets the apptIDs so it can be validated
     * @param a <code>HashMap<String, String></code>
     */
    public void setApptIds(HashMap<String, String> a){ this.apptIds = a; }
    /**
     * sets the test id/dxCode so it can be validated
     * @param t <code>HashMap<String, String></code>
     */
    public void setTests(ArrayList<String> t){ this.tests = t; }

/**********************************************************************************************************************/
/******************** ACCESSORS ***************************************************************************************/
    /**
     * gets the new Appointment that has been validated
     * @return this.newAppt
     */
    public Appointment getNewAppt(){ return this.newAppt; }
/**********************************************************************************************************************/
/*************** METHODS **********************************************************************************************/
    /**
     * looks at the appointment requirements to see if requested appt is open
     * and if the appointment is scheduled far enough away from the next appointment
     * @return boolean - true = good; false= did not add
     */
    public boolean apptRequirements(){
        //validate everyone
        if(!this.validatePeople()) return false;

        boolean apptTime = this.db.isValidApptDateTime(this.apptIds);  //looks to see if appointment is open
        boolean conflict;                               //to see if appointment request is far enough away

        //if not valid, grabs next appointment time else look to see if there's a conflict
        if(!apptTime) return false;
        else conflict = this.isScheduleConflict();
        //if conflict, return the nextAvailable appointment
        if(conflict){ return false; }

        //if no conflict (false) set the appointment
        this.newAppt = this.db.setAppointment(this.apptIds, this.tests);

        this.db.addApptToDB(this.newAppt); //add to the database
        return this.validateAppointment(); //validate -> true means added
    }//end apptRequirements

    /**
     * validate theat appointment has been added to the db
     * @return
     */
    public boolean validateAppointment(){
        return this.db.isValidObject("Appointment", this.newAppt.getId());
    }//end validateAppointment

    /**
     * checks to see if there is a schedule conflict
     * @return boolean
     */
    private boolean isScheduleConflict(){
        boolean temp = true;
        //get phlebotomist and psc objects to check conflicts
        GetInfo gi = this.db.getGi();
        Phlebotomist apptPhleb = gi.getPhlebotomist(this.apptIds.get("Phlebotomist"));
        PSC apptPscId = gi.getPSC(this.apptIds.get("PSC"));

        List<Appointment> phlebAppts = apptPhleb.getAppointmentCollection();
        for(Appointment a: phlebAppts){
            //if the date is the same, calculate time difference
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String date = df.format(a.getApptdate());

            if( date.equals(this.apptIds.get("Date"))) {
                temp = this.calculateTime(a);
            }
            //if a false is ever returned, means that there is a conflict
            if(temp == false) return true; //true means IS CONFLICT
        }
        return false; //means NO CONLICT
    }//end isScheduleConflict

    /**
     * calculates to make sure at least 15 / 30 minutes apart from the next appointment
     * @param a - the appointment being looked at
     * @return boolean
     */
    private boolean calculateTime(Appointment a){
        long diffRequired = 30;
        //if they do match make sure the minute difference is 15 instead
        Phlebotomist p = a.getPhlebid(); //get the object
        if(p.getId().equals(this.apptIds.get("Phlebotomist"))) diffRequired = 15;
        //save as time for the difference
        java.sql.Time phlebTime = a.getAppttime();
        java.sql.Time apptTime = db.getTime(this.apptIds.get("Time"));

        //get the difference
        long diff = phlebTime.getTime() - apptTime.getTime();
        long diffMinutes = diff / (60*1000) % 60;
        //doesn't matter if -15 or +15 (must be atleast 15 minutes between appt
        if(Math.abs(diffMinutes) >= diffRequired) return true;
        return false;
    }//end caluclate

    /**
     * makses sure that the Patient, Phlebotomist, Physician and PSC are valid people
     * in the DB system
     * @return bool
     */
    private boolean validatePeople(){
        //validate everything and if not valid, return the message
        if( !this.db.isValidObject("Patient", this.apptIds.get("Patient") ) ||
                !this.db.isValidObject("Phlebotomist", this.apptIds.get("Phlebotomist") ) ||
                !this.db.isValidObject("Physician", this.apptIds.get("Physician") ) ||
                !this.db.isValidObject("PSC", this.apptIds.get("PSC") ) ){
            return false;
        }
        return true;
    }//end validatePeople
}//end class