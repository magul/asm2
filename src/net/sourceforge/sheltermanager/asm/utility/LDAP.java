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
package net.sourceforge.sheltermanager.asm.utility;

import net.sourceforge.sheltermanager.asm.bo.Configuration;
import net.sourceforge.sheltermanager.asm.globals.Global;
import net.sourceforge.sheltermanager.dbfs.Base64;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


public abstract class LDAP {
    public final static String LDAP_URL = "LDAPURL";
    public final static String LDAP_DN = "LDAPDN";
    public final static String LDAP_FILTER = "LDAPFilter";
    public final static String LDAP_USER = "LDAPUser";
    public final static String LDAP_PASS = "LDAPPass";

    /**
     * Authenticates a user against our LDAP server. 1. Does a subtree search
     * for the user 2. Attempts to bind to the tree as the found user with the
     * pass given
     *
     * @param user
     *            The username to find
     * @param pass
     *            The password
     * @return false if authentication fails
     */
    public static boolean authenticate(String user, String password) {
        Map<String, String> settings = getSettings();

        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,
            "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, settings.get(LDAP_URL));
        env.put(Context.SECURITY_PRINCIPAL, settings.get(LDAP_USER));
        env.put(Context.SECURITY_CREDENTIALS, settings.get(LDAP_PASS));

        DirContext ctx = null;

        try {
            Global.logDebug("Binding to LDAP tree as '" +
                settings.get(LDAP_USER) + "'", "LDAP.authenticate");

            // Try to bind
            ctx = new InitialDirContext(env);

            String filter = MessageFormat.format((String) settings.get(
                        LDAP_FILTER), new Object[] { user });

            Global.logDebug("Performing subtree search of '" +
                settings.get(LDAP_DN) + "' with filter '" + filter + "'",
                "LDAP.authenticate");

            // Do a subtree search from the base DN for the user
            NamingEnumeration<SearchResult> res = 
            	ctx.search((String) settings.get(LDAP_DN),
                    filter,
                    new SearchControls(SearchControls.SUBTREE_SCOPE, 0, 0,
                        null, true, true));

            // Did we get a result?
            SearchResult r = null;
            String foundDN = "";

            if (res.hasMore()) {
                r = (SearchResult) res.next();
                foundDN = r.getNameInNamespace();
                Global.logDebug("LDAP found user: " + foundDN,
                    "LDAP.authenticate");
            } else {
                // We didn't find one, forget it
                Global.logDebug("Subtree search returned no results.",
                    "LDAP.authenticate");

                return false;
            }

            // Now, try to bind to the tree as the found user with
            // the password supplied
            env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, settings.get(LDAP_URL));
            env.put(Context.SECURITY_PRINCIPAL, foundDN);
            env.put(Context.SECURITY_CREDENTIALS, password);
            Global.logDebug("Closing LDAP connection.", "LDAP.authenticate");
            ctx.close();

            Global.logDebug("Rebinding to tree as '" + foundDN + "'",
                "LDAP.authenticate");
            ctx = new InitialDirContext(env);

            // We're good
            Global.logDebug("Successfully bound as '" + foundDN + "'",
                "LDAP.authenticate");

            return true;
        } catch (Exception e) {
            Global.logException(e, LDAP.class);

            return false;
        } finally {
            Global.logDebug("Closing LDAP connection.", "LDAP.authenticate");

            try {
                ctx.close();
            } catch (Exception e) {
            }

            ctx = null;
        }
    }

    /** Returns true if we have LDAP settings */
    public static boolean isConfigured() {
        return !Configuration.getString("LDAPURL").equals("");
    }

    /** Returns a map of the LDAP settings, or null if not configured. */
    public static Map<String, String> getSettings() {
        try {
            if (!isConfigured()) {
                return null;
            }

            String ldapUrl = "";
            String ldapDn = "";
            String ldapFilter = "";
            String ldapUser = "";
            String ldapPass = "";

            try {
                ldapUrl = Configuration.getString("LDAPURL");
                ldapDn = Configuration.getString("LDAPDN");
                ldapFilter = Configuration.getString("LDAPFilter");
                ldapUser = Configuration.getString("LDAPUser");
                ldapPass = Base64.decode(Configuration.getString("LDAPPass"));
            } catch (Exception e) {
            }

            /*
             * ldapUrl = "ldap://localhost/"; ldapDn = "dc=robsdomain";
             * ldapFilter = "(cn={0})"; ldapUser = "cn=admin,dc=robsdomain";
             * ldapPass = "password";
             */
            HashMap<String, String> m = new HashMap<String, String>();
            m.put(LDAP_URL, ldapUrl);
            m.put(LDAP_DN, ldapDn);
            m.put(LDAP_FILTER, ldapFilter);
            m.put(LDAP_USER, ldapUser);
            m.put(LDAP_PASS, ldapPass);

            return m;
        } catch (Exception e) {
            Global.logException(e, LDAP.class);

            return null;
        }
    }

    /** Set new LDAP settings */
    public static void setSettings(String ldapUrl, String ldapDn,
        String ldapFilter, String ldapUser, String ldapPass) {
        try {
            Configuration.setEntry("LDAPURL", ldapUrl);
            Configuration.setEntry("LDAPDN", ldapDn);
            Configuration.setEntry("LDAPFilter", ldapFilter);
            Configuration.setEntry("LDAPUser", ldapUser);
            Configuration.setEntry("LDAPPass", Base64.encode(ldapPass));
        } catch (Exception e) {
            Global.logException(e, LDAP.class);
        }
    }
}
