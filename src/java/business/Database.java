package business;
import components.data.*;
import java.util.*;
import java.text.*;
import java.sql.Time;

/**
 *
 * @author Julianna Gabler
 */
public class Database{
/**********************************************************************************************************************/
    /******* ATTRIBUTE ****************************************************************************************************/
    IComponentsData icdDB;
    GetInfo gi;
/**********************************************************************************************************************/
/******** CONSTRUCTOR *************************************************************************************************/
    /**
     * initializes the database
     */
    public Database(){
        this.icdDB = new DB();
        this.gi = new GetInfo(this.icdDB);
    }//end Database
/**********************************************************************************************************************/
    /******** ACCESSORS **************************************************************************************************/
    public IComponentsData getDB(){ return this.icdDB; }//end getDB
    /**
     * @param date
     * @return java.sql.Date
     */
    public java.sql.Date getDate(String date){
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsed = format.parse(date);
            java.sql.Date sql = new java.sql.Date(parsed.getTime());
            return sql;
        }catch(ParseException pe){ pe.printStackTrace(); }
        java.sql.Date date2 = new java.sql.Date(0,0,0);
        return date2;
    }//end getDate
    /**
     * @param time
     * @return java.sql.Time
     */
    public java.sql.Time getTime(String time){
        try{
            time = time + ":00"; //add to end of the time
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            long ms = sdf.parse(time).getTime();
            java.sql.Time t = new Time(ms);
            return t;
        }catch(ParseException pe){ pe.printStackTrace(); }
        Time t2;
        return t2 = new java.sql.Time(0,0,0);
    }//end getTime
    /**
     * @param _id
     * @return
     */
    public List<Object> getAppointment(String _id){
        String id = "id='" + _id + "'";
        List<Object> obj = this.icdDB.getData("Appointment", id);
        return obj;
    }//end getAppointment

    /**
     * @return gi
     */
    public GetInfo getGi(){ return this.gi; }
/**********************************************************************************************************************/
/************* METHOD *************************************************************************************************/
    /**
     * loads the db
     * @return boolean
     */
    public boolean load(){ return this.icdDB.initialLoad("LAMS"); }

    /**
     * check to see if it is a valid object or not
     * used to check to see if an appointment, patient, phlebotomist, physician
     * and LabTest exist inside of the database
     * @param object - the object aka table we are looking at
     * @param paramValue - the value of the param
     * @return boolean
     */ // @param paramName = the name of the parameter
    public boolean isValidObject(String object, String paramValue){
        String param = "id='" + paramValue + "'";
        List<Object> objs = this.icdDB.getData(object, param);
        //if size zero, it means the patient doesn't exist
        if(objs.size() > 0) return true;
        return false;
    }//end isValidObject()

    /**
     * add the appointment to the database and verify it's been added
     * @param appt Appointment
     * @return boolean
     */
    public boolean addApptToDB(Appointment appt){
        if(!this.icdDB.addData(appt)) return false;
        //verify that it's been added to the database
        if(this.isValidObject("Appointment", appt.getId())) return true;
        return false;
    }//end addApptToDB

    /**
     * checks to see if date and time is taken or not
     * @param date - the requested date
     * @param time - the requested time
     * @param paramName - the requested phlebotomist
     * @param id - the requested
     * @return
     */
    public boolean isValidApptDateTime(HashMap<String, String> apptIds){
        String params = "apptdate='" +  this.getDate(apptIds.get("Date"));
        params += "' AND appttime='" + this.getTime(apptIds.get("Time")) + "'";
        params += " AND " + "phlebid" + "='" + apptIds.get("Phlebotomist") + "'";
        params += " AND " + "pscid" + "='" + apptIds.get("PSC") + "'";

        List<Object> objs = this.icdDB.getData("Appointment", params);

        //if greater than 0, then means appt exists
        if(objs.size() > 0) return false;
        return true;
    }//end isValidApptDateTime

    /**
     * gets the last appointment id
     * @return String
     */
    public String getAppointmentId(){
        //get all appointments
        List<Object> objs = this.icdDB.getData("Appointment", "");
        int highestId = 0; //for the highest id
        for(Object obj: objs){ //look at each appointment object
            Appointment appt = (Appointment)obj;
            int temp = Integer.parseInt(appt.getId()); // grab the id
            if(temp > highestId){ //if temp is higher, save as is
                highestId = temp;
            }
        }
        //add 10 to the id and return it to get the next id
        highestId += 10;
        return String.valueOf(highestId);
    }//end getLastAppointmentId

    /**
     * set the appointment's labtests
     * @param tests
     * @param newAppt
     */
    public void setAppointmentLabTests(ArrayList<String> tests, Appointment newAppt){
        List<AppointmentLabTest> list = new ArrayList<AppointmentLabTest>(); //create a new list

        for(int i=0; i < list.size(); i++) {
            //create an appointment object with necessary information
            AppointmentLabTest test = new AppointmentLabTest(newAppt.getId(), tests.get(i), tests.get(i + 1));
            //set the diagnosis and lab tests after getting the object
            test.setDiagnosis(this.gi.getDiagnosis(tests.get(i + 1)));
            test.setLabTest(this.gi.getLabTest(tests.get(i)));
            list.add(test);//add to the list of objects
            i++; //to skip next set
        }
        newAppt.setAppointmentLabTestCollection(list); //set the labTestCollection
    }//end setAppointmentLabTest

    /**
     * sets the new Appointment object
     * @param apptIds
     * @param tests
     * @return newAppt Appointment
     */
    public Appointment setAppointment(HashMap<String, String> apptIds, ArrayList<String> tests){
        //get the date and time in sql format
        java.sql.Date d = this.getDate(apptIds.get("Date")); //get date
        java.sql.Time t = this.getTime(apptIds.get("Time")); //get time

        //create a new appointment object
        Appointment newAppt = new Appointment(this.getAppointmentId(),d, t);
        //set the patient attribute
        Patient p = this.gi.getPatient(apptIds.get("Patient"));
        newAppt.setPatientid(p);
        //set the phlebotomist attribute
        Phlebotomist ph = this.gi.getPhlebotomist(apptIds.get("Phlebotomist"));
        newAppt.setPhlebid(ph);
        //set the psc attribute
        PSC psc = this.gi.getPSC(apptIds.get("PSC"));
        newAppt.setPscid(psc);
        //set the tests
        this.setAppointmentLabTests(tests, newAppt);

        return newAppt;
    }//end setAppointment
}//end Database class