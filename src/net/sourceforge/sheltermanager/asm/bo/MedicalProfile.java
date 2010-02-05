/*
 Animal Shelter Manager
 Copyright(c)2000-2010, R. Rawson-Tetley

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as
 published by the Free Software Foundation; either version 2 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTIBILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston
 MA 02111-1307, USA.

 Contact me by electronic mail: bobintetley@users.sourceforge.net
 */
package net.sourceforge.sheltermanager.asm.bo;

import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.cursorengine.BOValidationException;
import net.sourceforge.sheltermanager.cursorengine.CursorEngineException;
import net.sourceforge.sheltermanager.cursorengine.UserInfoBO;

import java.util.Date;


public class MedicalProfile extends UserInfoBO {
    public MedicalProfile() {
        tableName = "medicalprofile";
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public String getProfileName() throws CursorEngineException {
        return (String) rs.getField("ProfileName");
    }

    public void setProfileName(String newValue) throws CursorEngineException {
        rs.setField("ProfileName", newValue);
    }

    public String getTreatmentName() throws CursorEngineException {
        return (String) rs.getField("TreatmentName");
    }

    public void setTreatmentName(String newValue) throws CursorEngineException {
        rs.setField("TreatmentName", newValue);
    }

    public String getDosage() throws CursorEngineException {
        return (String) rs.getField("Dosage");
    }

    public void setDosage(String newValue) throws CursorEngineException {
        rs.setField("Dosage", newValue);
    }

    /** 0 for a one-off, or a number per TimingRuleFrequency */
    public Integer getTimingRule() throws CursorEngineException {
        return (Integer) rs.getField("TimingRule");
    }

    public void setTimingRule(Integer newValue) throws CursorEngineException {
        rs.setField("TimingRule", newValue);
    }

    public Double getCost() throws CursorEngineException {
        return (Double) rs.getField("Cost");
    }

    public void setCost(Double newValue) throws CursorEngineException {
        rs.setField("Cost", newValue);
    }


    /**
     * The number of timing rule frequencies. Eg: Timing rule = 4 (4 treatments
     * on frequency) TimingRuleNoFrequencies = 5 TimingRuleFrequency = Week (so
     * 4 treatments every 5 weeks)
     */
    public Integer getTimingRuleNoFrequencies() throws CursorEngineException {
        return (Integer) rs.getField("TimingRuleNoFrequencies");
    }

    public void setTimingRuleNoFrequencies(Integer newValue)
        throws CursorEngineException {
        rs.setField("TimingRuleNoFrequencies", newValue);
    }

    /**
     * 0 = Daily 1 = Weekly 2 = Monthly 3 = Annually
     */
    public Integer getTimingRuleFrequency() throws CursorEngineException {
        return (Integer) rs.getField("TimingRuleFrequency");
    }

    public void setTimingRuleFrequency(Integer newValue)
        throws CursorEngineException {
        rs.setField("TimingRuleFrequency", newValue);
    }

    /**
     * 0 = Set Length 1 = Unspecified
     */
    public Integer getTreatmentRule() throws CursorEngineException {
        return (Integer) rs.getField("TreatmentRule");
    }

    public void setTreatmentRule(Integer newValue) throws CursorEngineException {
        rs.setField("TreatmentRule", newValue);
    }

    /** If Treatment Rule == 0 (fixed length), the number of treatments required */
    public Integer getTotalNumberOfTreatments() throws CursorEngineException {
        return (Integer) rs.getField("TotalNumberOfTreatments");
    }

    public void setTotalNumberOfTreatments(Integer newValue)
        throws CursorEngineException {
        rs.setField("TotalNumberOfTreatments", newValue);
    }

    public String getComments() throws CursorEngineException {
        return (String) rs.getField("Comments");
    }

    public void setComments(String newValue) throws CursorEngineException {
        rs.setField("Comments", newValue);
    }

    public Integer getRecordVersion() throws CursorEngineException {
        return (Integer) rs.getField("RecordVersion");
    }

    public void setRecordVersion(Integer newValue) throws CursorEngineException {
        rs.setField("RecordVersion", newValue);
    }

    public String getCreatedBy() throws CursorEngineException {
        return (String) rs.getField("CreatedBy");
    }

    public void setCreatedBy(String newValue) throws CursorEngineException {
        rs.setField("CreatedBy", newValue);
    }

    public Date getCreatedDate() throws CursorEngineException {
        return (Date) rs.getField("CreatedDate");
    }

    public void setCreatedDate(Date newValue) throws CursorEngineException {
        rs.setField("CreatedDate", newValue);
    }

    public String getLastChangedBy() throws CursorEngineException {
        return (String) rs.getField("LastChangedBy");
    }

    public void setLastChangedBy(String newValue) throws CursorEngineException {
        rs.setField("LastChangedBy", newValue);
    }

    public Date getLastChangedDate() throws CursorEngineException {
        return (Date) rs.getField("LastChangedDate");
    }

    public void setLastChangedDate(Date newValue) throws CursorEngineException {
        rs.setField("LastChangedDate", newValue);
    }

    /**
     * Pulls together the TimingRule information to produce a string, like "One
     * Off" or "1 treatment every 5 weeks".
     */
    public String getNamedFrequency() throws CursorEngineException {
        if (getTimingRule().intValue() == 0) {
            return Global.i18n("bo", "One_Off");
        }

        String out = getTimingRule().toString();
        out += (" " + Global.i18n("bo", "treatment_every") + " ");
        out += (getTimingRuleNoFrequencies() + " ");

        String freq = "";

        if (getTimingRuleFrequency().intValue() == 0) {
            freq = Global.i18n("bo", "days");
        }

        if (getTimingRuleFrequency().intValue() == 1) {
            freq = Global.i18n("bo", "weeks");
        }

        if (getTimingRuleFrequency().intValue() == 2) {
            freq = Global.i18n("bo", "months");
        }

        if (getTimingRuleFrequency().intValue() == 3) {
            freq = Global.i18n("bo", "years");
        }

        return out + freq;
    }

    /**
     * Pulls together the treatment rule information to return a string like
     * "Unspecified" or "21 Treatment Periods (52 treatments)"
     */
    public String getNamedNumberOfTreatments() throws CursorEngineException {
        if (getTreatmentRule().equals("1")) {
            return Global.i18n("bo", "Unspecified");
        }

        String out = getTotalNumberOfTreatments() + " " +
            Global.i18n("uimedical", "treatment_periods");

        int no = getTimingRule().intValue();
        int fr = getTotalNumberOfTreatments().intValue();
        int tot = (no * fr);

        out += (" (" + tot + " ");

        if (tot == 1) {
            out += Global.i18n("uimedical", "treatment");
        } else {
            out += Global.i18n("uimedical", "treatments");
        }

        out += ")";

        return out;
    }

    public void validate() throws BOValidationException {
        try {
            // if (getDosage().equals("") ||
            // getDosage().equals(SQLRecordset.NULL_VALUE))
            // throw new BOValidationException(Global.i18n("bo",
            // "You_must_supply_a_dosage"));
            if ((getProfileName() == null) || getProfileName().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "Medical_profiles_must_have_a_name"));
            }

            if ((getTreatmentName() == null) || getTreatmentName().equals("")) {
                throw new BOValidationException(Global.i18n("bo",
                        "You_must_supply_a_treatment_name"));
            }

            if (getTimingRule() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "Number_of_treatments_must_be_numeric"));
            }

            if (getTimingRuleNoFrequencies() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "Number_of_frequencies_must_be_numeric"));
            }

            if (getTotalNumberOfTreatments() == null) {
                throw new BOValidationException(Global.i18n("bo",
                        "Total_number_of_treatments_must_be_numeric"));
            }
        } catch (CursorEngineException e) {
            throw new BOValidationException(e.getMessage());
        }
    }
}
