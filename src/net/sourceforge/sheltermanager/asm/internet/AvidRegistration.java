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
package net.sourceforge.sheltermanager.asm.internet;

import net.sourceforge.sheltermanager.asm.bo.Adoption;
import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.asm.ui.ui.Dialog;
import net.sourceforge.sheltermanager.asm.ui.ui.HTMLViewer;
import net.sourceforge.sheltermanager.asm.utility.Utils;

import java.text.SimpleDateFormat;

import java.util.Date;


public class AvidRegistration {
    public final static String DEFAULT_PETTRAC_URL = "https://online.pettrac.com/registration/onlineregistration.aspx";
    private Adoption movement = null;
    private StringBuffer h = new StringBuffer();
    private final SimpleDateFormat avidFormat = new SimpleDateFormat("yyyyMMdd");

    public AvidRegistration(Adoption movement) {
        this.movement = movement;
    }

    /**
     * Creates an HTML form with the data pre-populated and opens it
     * in the system browser.
     * There is no point internationalising this code as it is for
     * UK users only.
     */
    public void register() {
        try {
            String avidUrl = Configuration.getString("AvidURL",
                    DEFAULT_PETTRAC_URL);
            h.append("<h1 align=\"center\">AVID Chip Registration</h1>");
            h.append("<form action=\"" + avidUrl + "\" method=\"POST\">");

            String breed = movement.getAnimal().getBreedName();

            if (breed.startsWith("Domestic Long")) {
                breed = "DLH";
            }

            if (breed.startsWith("Domestic Short")) {
                breed = "DSH";
            }

            if (breed.startsWith("Domestic Medium")) {
                breed = "DSLH";
            }

            String name = movement.getAnimal().getAnimalName();
            String microchip = movement.getAnimal().getMicrochipNumber();
            String implantdate = formatDate(movement.getAnimal()
                                                    .getMicrochipDate());
            String prefix = movement.getOwner().getOwnerTitle();
            String surname = movement.getOwner().getOwnerSurname();
            String forenames = movement.getOwner().getOwnerForenames();
            String telhome = movement.getOwner().getHomeTelephone();
            String telwork = movement.getOwner().getWorkTelephone();
            String telmobile = movement.getOwner().getMobileTelephone();
            String email = movement.getOwner().getEmailAddress();

            String address = movement.getOwner().getOwnerAddress();
            String[] add = Utils.split(address, "\n");
            String address1 = "";
            String address2 = "";
            String address3 = "";

            if (add.length > 0) {
                address1 = add[0];
            }

            if (add.length > 1) {
                address2 = add[1];
            }

            if (add.length > 2) {
                address3 = add[2];
            }

            String city = movement.getOwner().getOwnerTown();
            String county = movement.getOwner().getOwnerCounty();
            String postcode = movement.getOwner().getOwnerPostcode();
            String sex = movement.getAnimal().getSexName().substring(0, 1)
                                 .toUpperCase();
            String neutered = (movement.getAnimal().getNeutered().intValue() == 1)
                ? "true" : "false";
            String colour = movement.getAnimal().getBaseColourName();
            String dob = formatDate(movement.getAnimal().getDateOfBirth());

            String species = movement.getAnimal().getSpeciesName();

            if (species.startsWith("Dog")) {
                species = "Canine";
            } else if (species.startsWith("Cat")) {
                species = "Feline";
            } else if (species.startsWith("Bird")) {
                species = "Avian";
            } else if (species.startsWith("Horse")) {
                species = "Equine";
            } else if (species.startsWith("Reptile")) {
                species = "Reptilian";
            } else {
                species = "Other";
            }

            String orgname = Configuration.getString("AvidOrgName");
            String orgserial = Configuration.getString("AvidOrgSerial");
            String orgpostcode = Configuration.getString("AvidOrgPostcode");
            String orgpassword = Configuration.getString("AvidOrgPassword");
            String version = "1.1";

            // Verify the microchip number is correct - all AVID chips start
            // with the prefix 377
            if (!microchip.startsWith("977")) {
                Dialog.showError(
                    "Valid AVID microchips start with the prefix '977'");

                return;
            }

            if (microchip.equals("")) {
                Dialog.showError("The animal's microchip number is blank.");

                return;
            }

            if (implantdate.equals("")) {
                Dialog.showError("The animal's microchip date is blank.");

                return;
            }

            // Make sure the org info has been set
            if (orgserial.equals("") || orgpassword.equals("")) {
                Dialog.showError(
                    "You need to set your AVID organisation info under System Options.");

                return;
            }

            // Output org info
            addHidden("orgpostcode", orgpostcode);
            addHidden("orgname", orgname);
            addHidden("orgserial", orgserial);
            addHidden("orgpassword", orgpassword);
            addHidden("version", version);

            // Output chip details
            addHeader("Chip Details");
            addRow("Chip number:", "microchip", microchip);
            addRow("Implant Date:", "implantdate", implantdate);
            endTable();

            // Owner details
            addHeader("Owner Details");
            addRow("Title:", "prefix", prefix);
            addRow("Surname:", "surname", surname);
            addRow("Forenames:", "firstname", forenames);
            addRow("Address 1:", "address1", address1);
            addRow("Address 2:", "address2", address2);
            addRow("Address 3:", "address3", address3);
            addRow("City:", "city", city);
            addRow("County:", "county", county);
            addRow("Postcode:", "postcode", postcode);
            addRow("Home Phone:", "telhome", telhome);
            addRow("Work Phone:", "telwork", telwork);
            addRow("Mobile Phone:", "telmobile", telmobile);
            addRow("Alternative Phone:", "telalternative", "");
            addRow("Email Address:", "email", email);
            endTable();

            // Animal details
            addHeader("Animal Details");
            addRow("Name:", "petname", name);
            addRow("Gender:", "petgender", sex);
            addRow("DOB:", "petdob", dob);
            addRow("Species:", "petspecies", species);
            addRow("Breed:", "petbreed", breed);
            addRow("Neutered:", "petneutered", neutered);
            addRow("Colour:", "petcolour", colour);
            endTable();

            h.append(
                "<p align=\"center\"><input type=\"submit\" value=\"Submit Registration\" /></p>");
            h.append("</form></body></html>");

            // Output for the user to submit
            // String avid = Utils.createTemporaryFile(h.toString(), "html");
            // FileTypeManager.shellExecute(avid);
            Global.mainForm.addChild(new HTMLViewer(h.toString(), "text/html"));
        } catch (Exception e) {
            Global.logException(e, getClass());
            Dialog.showError(e.getMessage());
        }
    }

    private void addHidden(String name, String value) {
        h.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" +
            value + "\" />\n");
    }

    private void addRow(String title, String name, String value) {
        h.append("<tr>\n<td width=\"30%\">" + title + "</td>\n<td>")
         .append("<input type=\"text\" name=\"" + name + "\" ")
         .append("value=\"" + value + "\" />\n</td>\n</tr>");
    }

    private void addHeader(String title) {
        h.append("<h2>" + title + "</h2>\n<table width=\"100%\">\n");
    }

    private void endTable() {
        h.append("</table>\n");
    }

    private String formatDate(Date d) {
        if (d == null) {
            return "";
        }

        return avidFormat.format(d);
    }
}
