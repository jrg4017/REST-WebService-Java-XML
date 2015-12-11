package business;

import components.data.*;
import java.util.*;

/**
 * gets the id from a name or gets the object from the id
 * @author Julianna Gabler
 */
public class GetInfo {
/**********************************************************************************************************************/
    /******** ATTRIBUTE ***************************************************************************************************/
    private IComponentsData db; //the db to grab information
/**********************************************************************************************************************/
    /******** CONSTRUCTOR *************************************************************************************************/
    public GetInfo(IComponentsData db){ this.db = db; }
/**********************************************************************************************************************/
    /*********** GET OBJ METHODS ******************************************************************************************/
    public Phlebotomist getPhlebotomist(String id){ return (Phlebotomist)this.getObject("Phlebotomist", "id", id); }
    public PSC getPSC(String id){ return (PSC)this.getObject("PSC", "id", id); }
    public Physician getPhysician(String id){ return (Physician)this.getObject("Physician", "id", id); }
    public Patient getPatient(String id){ return (Patient)this.getObject("Patient", "id", id); }
    public Diagnosis getDiagnosis(String code){ return (Diagnosis)this.getObject("Diagnosis", "code", code); }
    public LabTest getLabTest(String id){ return (LabTest)this.getObject("LabTest", "id", id); }
/**********************************************************************************************************************/
/************** GET ID FROM NAME METHODS ******************************************************************************/
    /**
     * @param name the name of psc
     * @return String id / empty if doesn't exist
     */
    public String PscIdFromName(String name) {
        Object o = this.getObject("PSC", "anem", name);
        if(o != "") {
            PSC psc = (PSC)o;
            return psc.getId();
        } else return "";
    }//end PscIdFromName
    /**
     * @param name of the physician
     * @return String id / empty if doesn't exist
     */
    public String PhysicianIdFromName(String name) {
        Object o = this.getObject("Physician", "name", name);
        if(o != "") {
            Physician pn = (Physician)o;
            return pn.getId();
        } else return "";
    }//end PhysicianIdFromName
    /**
     * @param name of the phlebotomist
     * @return String id / empty if doesn't exist
     */
    protected String PhlebIdFromName(String name) {
        Object o = this.getObject("Phlebotomist", "name", name);
        if(o != "") {
            Phlebotomist p = (Phlebotomist)o;
            return p.getId();
        } else {
            return "";
        }
    }//end PhlebIdFromName
    /**
     * @param name of the patient
     * @return String id / empty if doesn't exist
     */
    public String PatientIdFromName(String name) {
        Object o = this.getObject("Patient", "name", name);
        if(o != "") {
            Patient p = (Patient)o;
            return p.getId();
        } else  return "";
    }//end PatientIdFromName
    /**
     * @param name of the LabTest
     * @return String id / empty if doesn't exist
     */
    public String LabTestIdFromName(String name) {
        Object o = this.getObject("LabTest", "name", name);
        if(o != "") {
            LabTest lt = (LabTest)o;
            return lt.getId();
        } else return "";
    }//end LabTestIdFromName
/**********************************************************************************************************************/
/******* METHODS ******************************************************************************************************/
    /**
     * gets the object from the value passed in
     * @param object the table name
     * @param paramName the parameter name
     * @param paramValue the parameter valie
     * @return
     */
    protected  Object getObject(String object, String paramName, String paramValue) {
        String param = paramName + "='" + paramValue + "\'";
        List objs = this.db.getData(object, param);
        if(objs.size() != 1) {
            return "";
        } else {
            Object rtn = null;

            Object obj;
            for(Iterator var6 = objs.iterator(); var6.hasNext(); rtn = obj) {
                obj = var6.next();
            }
            return rtn;
        }
    }//end getObject

    /**
     * grab the keys from the names
     * @param apptInfo <code>ArrayList<String></code>
     * @return ids <code>HashMap<String, String></code>
     */
    public HashMap<String, String> validateGetIds(ArrayList<String> apptInfo){
        HashMap<String, String> ids = new HashMap<String, String>();
        //return the ids in the form of a hashmap
        ids.put("Patient", this.PatientIdFromName(apptInfo.get(0)));
        ids.put("Phlebotomist" ,this.PhlebIdFromName(apptInfo.get(1)));
        ids.put("Physician", this.PhysicianIdFromName(apptInfo.get(2)));
        ids.put("PSC", this.PscIdFromName(apptInfo.get(4)));
        //return ids of all neccessary information whether
        return ids;
    }//end validateGetIds
}//end class