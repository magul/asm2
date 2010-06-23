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

import net.sourceforge.sheltermanager.cursorengine.*;


/**
 * Business object for Users.
 *
 * @author Robin Rawson-Tetley
 * @version 1.2
 * @see net.sourceforge.sheltermanager.cursorengine.SQLRecordset
 *
 * Note about the SecurityMap field: The security map field holds
 * letter constants, determining what the user can do, separated by
 * a space. This functionality is handled internally by this object.
 * Use the accessor methods "getSecXXXXX" and "setSecXXXXX" where
 * XXXXX is the security property you wish to alter for this user.
 * Also note, if the superuser flag is set, all of the getSec properties
 * will return true.
 *
 * Security flags:
 *
 * AddAnimal                    aa
 * ChangeAnimal                 ca
 * ViewAnimal                   va
 * ViewAnimalVet                vavet
 * DeleteAnimal                 da
 * CloneAnimal                  cloa
 * GenerateAnimalForms          gaf
 * AddAnimalVaccination         aav
 * ViewAnimalVaccination        vav
 * ChangeAnimalVaccination      cav
 * DeleteAnimalVaccination      dav
 * BulkCompleteAnimalVacc       bcav
 * AddAnimalMedical             maam
 * ChangeAnimalMedical          mcam
 * ViewAnimalMedical            mvam
 * DeleteAnimalMedical          mdam
 * BulkCompleteAnimalMedical    bcam
 * AddAnimalMedia               aam
 * ChangeAnimalMedia            cam
 * ViewAnimalMedia              vam
 * DeleteAnimalMedia            dam
 * AddAnimalDiet                daad
 * ChangeAnimalDiet             dcad
 * DeleteAnimalDiet             ddad
 * ViewAnimalDiet               dvad
 * AddAnimalCost                caad
 * ChangeAnimalCost             ccad
 * DeleteAnimalCost             cdad
 * ViewAnimalCost               cvad
 * AddAnimalMovements           aamv
 * ChangeAnimalMovements        camv
 * ViewAnimalMovements          vamv
 * DeleteAnimalMovements        damv
 * ModifyAnimalNameDatabase     mand
 * AddAccount					aac
 * ViewAccount					vac
 * ChangeAccount				cac
 * ChangeTransactions			ctrx
 * DeleteAccount				dac
 * AddOwner                     ao
 * ChangeOwner                  co
 * ViewOwners                   vo
 * ViewStaffOwners              vso
 * DeleteOwner                  do
 * MergeOwner                   mo
 * AddOwnerVoucher              vaov
 * ChangeOwnerVoucher           vcov
 * DeleteOwnerVoucher           vdov
 * ViewOwnerVoucher             vvov
 * AddOwnerDonation             oaod
 * ChangeOwnerDonation          ocod
 * DeleteOwnerDonation          odod
 * ViewOwnerDonation            ovod
 * ViewOwnerLinks               volk
 * AddLogEntry                  ale
 * ChangeLogEntry               cle
 * DeleteLogEntry               dle
 * ViewLogEntry                 vle
 * AccessSystemMenu             asm
 * ConfigureSystemOptions       cso
 * ModifyLookups                ml
 * AddSystemUsers               asu
 * EditSystemUsers              esu
 * EditDiaryTasks               edt
 * ViewDiaryNotes               vdn
 * AddDiaryNotes                adn
 * EditAllDiaryNotes            eadn
 * EditMyDiaryNotes             emdn
 * EditCompletedNotes           ecdn
 * CompleteBulkNotes            cbn
 * DeleteDiaryNotes             ddn
 * PrintDiaryNotes              pdn
 * PrintVaccinationDiary        pvd
 * AddLostAnimal                ala
 * AddFoundAnimal               afa
 * ChangeLostAnimals            cla
 * ChangeFoundAnimals           cfa
 * DeleteLostAnimals            dla
 * DeleteFoundAnimals           dfa
 * MatchLostAndFoundAnimals     mlaf
 * ViewWaitingList              vwl
 * AddWaitingList               awl
 * DeleteWaitingList            dwl
 * ChangeWaitingList            cwl
 * BulkCompleteWaitingList      bcwl
 * AddLitterLog                 all
 * ViewLitterLog                vll
 * DeleteLitterLog              dll
 * ChangeLitterLog              cll
 * RunDBUpdate                  rdbu
 * RunDBDiagnostic              rdbd
 * UseSQLInterface              usi
 * UseInternetPublisher         uipb
 * MailMergeOwners              mmeo
 * MailMergeAdoptions           mmea
 * CreateCustomReports          ccr
 * ViewCustomReports            vcr
 * ChangeCustomReports          hcr
 * DeleteCustomReports          dcr
 */
public class Users extends NormalBO<Users> {
    public Users() {
        tableName = "users";
    }

    public Users(String where) {
        this();
        openRecordset(where);
    }

    public Integer getID() throws CursorEngineException {
        return (Integer) rs.getField("ID");
    }

    public void setID(Integer newValue) throws CursorEngineException {
        rs.setField("ID", newValue);
    }

    public String getUserName() throws CursorEngineException {
        return (String) rs.getField("UserName");
    }

    public void setUserName(String newValue) throws CursorEngineException {
        rs.setField("UserName", newValue);
    }

    public String getRealName() throws CursorEngineException {
        return (String) rs.getField("RealName");
    }

    public void setRealName(String newValue) throws CursorEngineException {
        rs.setField("RealName", newValue);
    }

    public String getPassword() throws CursorEngineException {
        return (String) rs.getField("Password");
    }

    public void setPassword(String newValue) throws CursorEngineException {
        rs.setField("Password", newValue);
    }

    public Integer getSuperUser() throws CursorEngineException {
        return (Integer) rs.getField("SuperUser");
    }

    public void setSuperUser(Integer newValue) throws CursorEngineException {
        rs.setField("SuperUser", newValue);
    }

    public Integer getOwnerID() throws CursorEngineException {
        return (Integer) rs.getField("OwnerID");
    }

    public void setOwnerID(Integer newValue) throws CursorEngineException {
        rs.setField("OwnerID", newValue);
    }

    public String getSecurityMap() throws CursorEngineException {
        return (String) rs.getField("SecurityMap");
    }

    public void setSecurityMap(String newValue) throws CursorEngineException {
        rs.setField("SecurityMap", newValue);
    }

    /**
     * Sets a flag in the security map
     *
     * @param flagname
     *            The name of the flag to set
     */
    public void setSecurityFlag(String flagname) {
        try {
            String sm = getSecurityMap();

            // Is it already there?
            if (sm.indexOf(flagname + " ") != -1) {
                // It is - do nothing
                return;
            }

            // Add it
            sm = sm + flagname + " *";

            // Reset the security map
            setSecurityMap(sm);
        } catch (CursorEngineException e) {
        }
    }

    /**
     * Tests to see if a security flag is set.
     *
     * @param flagname
     *            The name of the flag to check
     * @return true if the flag is present.
     */
    public boolean getSecurityFlag(String flagname) {
        try {
            // Return true automatically for super users
            if (getSuperUser().equals(new Integer(1))) {
                return true;
            }

            String sm = getSecurityMap();

            // Return true if it is there, false if not
            return sm.indexOf(flagname + " ") != -1;
        } catch (CursorEngineException e) {
        }

        return false;
    }

    public boolean getSecAddAnimal() {
        return getSecurityFlag("aa");
    }

    public boolean getSecViewAnimal() {
        return getSecurityFlag("va");
    }

    public boolean getSecDeleteAnimal() {
        return getSecurityFlag("da");
    }

    public boolean getSecChangeAnimal() {
        return getSecurityFlag("ca");
    }

    public boolean getSecCloneAnimal() {
        return getSecurityFlag("cloa");
    }

    public boolean getSecViewAnimalVet() {
        return getSecurityFlag("vavet");
    }

    public boolean getSecGenerateAnimalForms() {
        return getSecurityFlag("gaf");
    }

    public boolean getSecAddAnimalVaccination() {
        return getSecurityFlag("aav");
    }

    public boolean getSecViewAnimalVaccination() {
        return getSecurityFlag("vav");
    }

    public boolean getSecChangeAnimalVaccination() {
        return getSecurityFlag("cav");
    }

    public boolean getSecDeleteAnimalVaccination() {
        return getSecurityFlag("dav");
    }

    public boolean getSecBulkCompleteAnimalVaccination() {
        return getSecurityFlag("bcav");
    }

    public boolean getSecAddAnimalMedical() {
        return getSecurityFlag("maam");
    }

    public boolean getSecChangeAnimalMedical() {
        return getSecurityFlag("mcam");
    }

    public boolean getSecDeleteAnimalMedical() {
        return getSecurityFlag("mdam");
    }

    public boolean getSecViewAnimalMedical() {
        return getSecurityFlag("mvam");
    }

    public boolean getSecBulkCompleteAnimalMedical() {
        return getSecurityFlag("bcam");
    }

    public boolean getSecAddAnimalMedia() {
        return getSecurityFlag("aam");
    }

    public boolean getSecChangeAnimalMedia() {
        return getSecurityFlag("cam");
    }

    public boolean getSecViewAnimalMedia() {
        return getSecurityFlag("vam");
    }

    public boolean getSecDeleteAnimalMedia() {
        return getSecurityFlag("dam");
    }

    public boolean getSecAddAnimalDiet() {
        return getSecurityFlag("daad");
    }

    public boolean getSecChangeAnimalDiet() {
        return getSecurityFlag("dcad");
    }

    public boolean getSecDeleteAnimalDiet() {
        return getSecurityFlag("ddad");
    }

    public boolean getSecViewAnimalDiet() {
        return getSecurityFlag("dvad");
    }

    public boolean getSecAddAnimalCost() {
        return getSecurityFlag("caad");
    }

    public boolean getSecChangeAnimalCost() {
        return getSecurityFlag("ccad");
    }

    public boolean getSecDeleteAnimalCost() {
        return getSecurityFlag("cdad");
    }

    public boolean getSecViewAnimalCost() {
        return getSecurityFlag("cvad");
    }

    public boolean getSecAddOwnerVoucher() {
        return getSecurityFlag("vaov");
    }

    public boolean getSecChangeOwnerVoucher() {
        return getSecurityFlag("vcov");
    }

    public boolean getSecDeleteOwnerVoucher() {
        return getSecurityFlag("vdov");
    }

    public boolean getSecViewOwnerVoucher() {
        return getSecurityFlag("vvov");
    }

    public boolean getSecAddOwnerDonation() {
        return getSecurityFlag("oaod");
    }

    public boolean getSecChangeOwnerDonation() {
        return getSecurityFlag("ocod");
    }

    public boolean getSecDeleteOwnerDonation() {
        return getSecurityFlag("odod");
    }

    public boolean getSecViewOwnerDonation() {
        return getSecurityFlag("ovod");
    }

    public boolean getSecViewOwnerLinks() {
        return getSecurityFlag("volk");
    }

    public boolean getSecAddAnimalMovements() {
        return getSecurityFlag("aamv");
    }

    public boolean getSecChangeAnimalMovements() {
        return getSecurityFlag("camv");
    }

    public boolean getSecViewAnimalMovements() {
        return getSecurityFlag("vamv");
    }

    public boolean getSecDeleteAnimalMovements() {
        return getSecurityFlag("damv");
    }

    public boolean getSecModifyAnimalNameDatabase() {
        return getSecurityFlag("mand");
    }

    public boolean getSecAddOwner() {
        return getSecurityFlag("ao");
    }

    public boolean getSecChangeOwner() {
        return getSecurityFlag("co");
    }

    public boolean getSecViewOwner() {
        return getSecurityFlag("vo");
    }

    public boolean getSecViewStaffOwners() {
        return getSecurityFlag("vso");
    }

    public boolean getSecDeleteOwner() {
        return getSecurityFlag("do");
    }

    public boolean getSecMergeOwner() {
        return getSecurityFlag("mo");
    }

    public boolean getSecAddLogEntry() {
        return getSecurityFlag("ale");
    }

    public boolean getSecChangeLogEntry() {
        return getSecurityFlag("cle");
    }

    public boolean getSecDeleteLogEntry() {
        return getSecurityFlag("dle");
    }

    public boolean getSecViewLogEntry() {
        return getSecurityFlag("vle");
    }

    public boolean getSecAccessSystemMenu() {
        return getSecurityFlag("asm");
    }

    public boolean getSecConfigureSystemOptions() {
        return getSecurityFlag("cso");
    }

    public boolean getSecModifyLookups() {
        return getSecurityFlag("ml");
    }

    public boolean getSecAddSystemUsers() {
        return getSecurityFlag("asu");
    }

    public boolean getSecEditSystemUsers() {
        return getSecurityFlag("esu");
    }

    public boolean getSecViewDiaryNotes() {
        return getSecurityFlag("vdn");
    }

    public boolean getSecAddDiaryNote() {
        return getSecurityFlag("adn");
    }

    public boolean getSecEditDiaryTasks() {
        return getSecurityFlag("edt");
    }

    public boolean getSecEditAllDiaryNotes() {
        return getSecurityFlag("eadn");
    }

    public boolean getSecEditMyDiaryNotes() {
        return getSecurityFlag("emdn");
    }

    public boolean getSecEditCompletedNotes() {
        return getSecurityFlag("ecdn");
    }

    public boolean getSecBulkCompleteNotes() {
        return getSecurityFlag("bcn");
    }

    public boolean getSecDeleteDiaryNotes() {
        return getSecurityFlag("ddn");
    }

    public boolean getSecPrintDiaryNotes() {
        return getSecurityFlag("pdn");
    }

    public boolean getSecPrintVaccinationDiary() {
        return getSecurityFlag("pvd");
    }

    public boolean getSecAddLostAnimal() {
        return getSecurityFlag("ala");
    }

    public boolean getSecAddFoundAnimal() {
        return getSecurityFlag("afa");
    }

    public boolean getSecChangeLostAnimals() {
        return getSecurityFlag("cla");
    }

    public boolean getSecChangeFoundAnimals() {
        return getSecurityFlag("cfa");
    }

    public boolean getSecDeleteLostAnimals() {
        return getSecurityFlag("dla");
    }

    public boolean getSecDeleteFoundAnimals() {
        return getSecurityFlag("dfa");
    }

    public boolean getSecMatchLostAndFoundAnimals() {
        return getSecurityFlag("mlaf");
    }

    public boolean getSecViewWaitingList() {
        return getSecurityFlag("vwl");
    }

    public boolean getSecAddWaitingList() {
        return getSecurityFlag("awl");
    }

    public boolean getSecChangeWaitingList() {
        return getSecurityFlag("cwl");
    }

    public boolean getSecDeleteWaitingList() {
        return getSecurityFlag("dwl");
    }

    public boolean getSecBulkCompleteWaitingList() {
        return getSecurityFlag("bcwl");
    }

    public boolean getSecAddLitterLog() {
        return getSecurityFlag("all");
    }

    public boolean getSecViewLitterLog() {
        return getSecurityFlag("vll");
    }

    public boolean getSecDeleteLitterLog() {
        return getSecurityFlag("dll");
    }

    public boolean getSecChangeLitterLog() {
        return getSecurityFlag("cll");
    }

    public boolean getSecRunDBUpdate() {
        return getSecurityFlag("rdbu");
    }

    public boolean getSecRunDBDiagnostic() {
        return getSecurityFlag("rdbd");
    }

    public boolean getSecUseInternetPublisher() {
        return getSecurityFlag("uipb");
    }

    public boolean getSecUseSQLInterface() {
        return getSecurityFlag("usi");
    }

    public boolean getSecMailMergeOwners() {
        return getSecurityFlag("mmeo");
    }

    public boolean getSecMailMergeAdoptions() {
        return getSecurityFlag("mmea");
    }

    public boolean getSecCreateCustomReports() {
        return getSecurityFlag("ccr");
    }

    public boolean getSecViewCustomReports() {
        return getSecurityFlag("vcr");
    }

    public boolean getSecChangeCustomReports() {
        return getSecurityFlag("hcr");
    }

    public boolean getSecDeleteCustomReports() {
        return getSecurityFlag("dcr");
    }
    
    public boolean getSecAddAccount() {
    	return getSecurityFlag("aac");
    }
    
    public boolean getSecViewAccount() {
    	return getSecurityFlag("vac");
    }
    
    public boolean getSecChangeAccount() {
    	return getSecurityFlag("cac");
    }
    
    public boolean getSecChangeTransactions() {
    	return getSecurityFlag("ctrx");
    }
    
    public boolean getSecDeleteAccount() {
    	return getSecurityFlag("dac");
    }
    
}
