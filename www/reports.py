#!/usr/bin/python

import db

"""
        ASM reporting module, contains all code necessary for 
        generating reports and producing HTML output
"""

class GroupDescriptor:
    """
    Contains info on report groups
    """
    fieldName = ""
    lastFieldValue = ""
    header = ""
    footer = ""
    forceFinish = False
    lastGroupStartPosition = 0
    lastGroupEndPosition = 0

class Report:

    reportId = 0
    criteria = ""
    queries = []
    title = ""
    sql = ""
    html = ""
    omitCriteria = False
    omitHeaderFooter = False
    isSubReport = False
    output = ""
    
    def _ReadReport(self, reportId):
        """
        Reads the report info from the database and populates
        our local class variables
        """
        r = db.query("SELECT * FROM customreport WHERE ID = %s" % str(reportId))
        self.title = r["Title"]
        self.html = r["HTMLBody"]
        self.sql = r["SQLCommand"]
        self.omitCriteria = r["OmitCriteria"] > 0
        self.omitHeaderFooter = r["OmitHeaderFooter"] > 0
        self.isSubReport = self.sql.find("PARENTKEY") != -1

    def _ReadHeader():
        """
        Reads the report header from the DBFS. If the omitHeaderFooter
        flag is set, returns a basic header, if it's a subreport,
        returns nothing.
        """
        if self.omitHeaderFooter:
            return "<html><head><title></title></head><body>"
        elif self.isSubReport:
            return ""
        else:
            # Look it up from the DB
            # TODO:
            pass

    def _ReadFooter():
        """
        Reads the report footer from the DBFS. If the omitHeaderFooter
        flag is set, returns a basic footer, if it's a subreport,
        returns nothing.
        """
        if self.omitHeaderFooter:
            return "</body></html>"
        elif self.isSubReport:
            return ""
        else:
            # Look it up from the DB
            # TODO:
            pass

    def _Append(s):
        output += s
        return output

    def _p(s):
        self._Append("<p>%s</p>" % s)

    def _hr(s):
        self._Append("<hr />")

    def _OutputGroupBlock(self, gd, headfoot, rs, rowindex) {
        """
        Outputs a group block, 'gd' is the group descriptor,
        'headfoot' is 0 for header, 1 for footer, 'rs' is
        the resultset and 'rowindex' is the row of results being
        looked at
        """
        
        out = gd.footer
        if headfoot == 0:
            out = gd.header

        # If there aren't any records in the set, then we might as
        # well stop now
        if len(rs) == 0:
            self._Append(out)
            return

        # Replace any fields in the block based on the last row
        # in the group
        for k, v in rs[gd.lastGroupEndPosition].iteritems():
            out = out.replace("$" + k, _DisplayValue(v))

        # Find calculation keys in our block
        startkey = out.find("{")
        while startkey != -1:

            endkey = out.find("}", startkey)
            key = out[startkey + 1:endkey]
            value = ""
            valid = False

            # {SUM.field[.round]}
            if key.lower().startswith("sum"):
                valid = True
                fields = key.lower().split(".")
                
                # rounding
                roundto = 2
                if len(fields) > 2:
                    roundto = int(fields[2])

                # Loop from start of group to end of group
                total = 0.0
                for i in range(gd.lastGroupStartPosition, gd.lastGroupEndPosition):
                    try:
                        total += rs[i][fields[1]]
                    except Exception, e:
                        # Ignore anything that wasn't a number
                        pass
                value = "0." + roundto + "%f" % total

            # {COUNT.field}
            if key.lower().startswith("count"):
                valid = True
                value = str(gd.lastGroupStartPosition - gd.lastGroupEndPosition)

            # {AVG.field[.round]}
            if key.lower().startswith("avg"):
                valid = True
                fields = key.lower().split(".")
                
                # rounding
                roundto = 2
                if len(fields) > 2:
                    roundto = int(fields[2])

                # Loop from start of group to end of group
                total = 0.0
                num = 0
                for i in range(gd.lastGroupStartPosition, gd.lastGroupEndPosition):
                    try:
                        total += rs[i][fields[1]]
                        num++
                    except Exception, e:
                        # Ignore anything that wasn't a number
                        pass
                value = "0." + roundto + "%f" % (total / num)

            # {PCT.field.match[.round]}
            if key.lower().startswith("pct"):
                valid = True
                fields = key.lower().split(".")
                
                # rounding
                roundto = 2
                if len(fields) > 3:
                    roundto = int(fields[3])

                # Loop from start of group to end of group
                matched = 0
                for i in range(gd.lastGroupStartPosition, gd.lastGroupEndPosition):
                    try:
                        if str(rs[i][fields[1]]).strip().lower() == str(fields[2]).strip().lower():
                            matched++
                    except Exception, e:
                        # Ignore errors
                        pass

                outof = gd.lastGroupEndPosition - gd.lastGroupStartPosition
                value = "0." + roundto + "%f" % ((matched / outof) * 100)

            # {MIN.field}
            if key.lower().startswith("min"):
                valid = True
                
                # Loop from start of group to end of group
                minval = 0
                for i in range(gd.lastGroupStartPosition, gd.lastGroupEndPosition):
                    try:
                        minval = min(minval, rs[i][fields[1]])
                    except Exception, e:
                        # Ignore errors
                        pass
                value = str(minval)

            # {MAX.field}
            if key.lower().startswith("max"):
                valid = True
                
                # Loop from start of group to end of group
                maxval = 0
                for i in range(gd.lastGroupStartPosition, gd.lastGroupEndPosition):
                    try:
                        maxval = max(minval, rs[i][fields[1]])
                    except Exception, e:
                        # Ignore errors
                        pass
                value = str(maxval)

            # {FIRST.field}
            if key.lower().startswith("first"):
                valid = True
                fields = key.lower().split(".")
                value = str(rs[gd.lastGroupStartPosition][fields[1])

            # {LAST.field}
            if key.lower().startswith("last"):
                valid = True
                fields = key.lower().split(".")
                value = str(rs[gd.lastGroupStartPosition][fields[1])

            # {SQL.sql} - arbitrary sql command, output first
            # column of first row
            if key.lower().startswith("sql"):
                valid = True
                asql = key[4:]
                if asql.lower().startswith("select"):
                    # Select - return first row/column
                    try:
                        x = db.query_tuple(asql)
                        value = str(x[0][0])
                    except Exception, e:
                        value = e
                else:
                    # Action query, run it
                    try:
                        value = ""
                        db.execute(asql)
                    except Exception, e:
                        value = e

            # {IMAGE.animalid} - substitutes a link to the image.cgi
            # page to direct the browser to retrieve an image
            if key.lower().startswith("image"):
                valid = True
                animalid = key[key.find(".")+1:]
                value = "%simage.cgi?type=animal&id=%s" % (db.BASE, animalid)


            # {SUBREPORT.[title].[parentField]} - embed a subreport
            if key.lower().startswith("subreport"):
                valid = True
                fields = key.lower().split(".")
                
                # Get custom report ID from title
                crid = db.query_int("SELECT ID FROM customreport WHERE Title LIKE '" + title + "'");

                # Get the content from it
                r = Report()
                value = r.Execute(crid, None)

            # Modify our block with the token value
            if valid:
                out = out[0:startkey] + value + out[endkey+1:];

            # Find the next key
            startkey = out.find("{", startkey+1)

        # Output the HTML to the report
        _Append(out)

    def _SubstituteHeaderFooter(self, headfoot, text, rs, rowindex):
        """
        Outputs the header and footer blocks, 
        'headfoot' - 0 = main header, 1 = footer
        text is the text of the block,
        'rs' is the resultset and
        'rowindex' is the current row from the recordset being looked at
        """
        gd = GroupDescriptor();
        gd.lastGroupEndPosition = len(rs) - 1
        gd.lastGroupStartPosition = 0;
        gd.footer = text;
        gd.header = text;
        self._OutputGroupBlock(gd, headfoot, rs);


    def GetParams(self, reportId):
        """
        Returns a list of parameters required for a report, 
        with their types
        'reportId' is the ID of the report to get parameters for.
        A list of parameters, each item is a list containing a
        type and a question string.
        """
        self._ReadReport(reportId)
        #TODO:
        pass


    def Execute(self, reportId, params):
        """
        Executes a report
        'reportId' is the ID of the report to run, 'params' is a list
        of values in order to substitute tokens in the report SQL for.
        They should all be strings and will be literally replaced.
        Return value is the HTML output of the report.
        """
        self._ReadReport(reportId)
        output = ""

        # Substitute our parameters in the SQL
        self._SubstituteSQLParameters(params)

        # Is it a graph? We aren't doing anything with those right now
        if html.upper().strip() == "GRAPH":
            # TODO: Implement graphs
            return

        # Is it a mail merge? Can't do anything with those either
        if html.upper().strip() == "MAIL":
            # TODO: Implement mail merge
            return

        # Add the HTML header to our report
        self._Append(self._ReadHeader())

        # Generate the report
        self._Generate(self, reportId, params)

        # Add the HTML footer to our report
        self._Append(self._ReadFooter())

    def _Generate(self, reportId, params):
        """
        Does the work of generating the report content
        """

        # String indexes within report html string to where 
        # tokens begin and end
        headerstart = 0
        headerstart = 0
        headerend = 0
        bodystart = 0
        bodyend = 0
        footerstart = 0
        footerend = 0
        groupstart = 0
        groupend = 0

        tempbody = ""
        cheader = ""
        cbody = ""
        cfooter = ""

        headerstart = self.html.find("$$HEADER");
        headerend = self.html.find("HEADER$$");

        if headerstart == -1 or headerend == -1:
            self._p("The header block of your report is invalid.")
            return
        cheader = self.html[headerstart+8:headerend];

        bodystart = self.html.find("$$BODY");
        bodyend = self.html.find("BODY$$");

        if bodystart == -1 or bodyend == -1:
            self._p("The body block of your report is invalid.")
            return
        cbody = self.html[bodystart+6:bodyend];

        footerstart = self.html.find("$$FOOTER");
        footerend = self.html.find("FOOTER$$");

        if footerstart == -1 or footerend == -1:
            self._p("The footer block of your report is invalid.")
            return
        cfooter = self.html[footerstart+8:footerend];

        # Parse all groups from the HTML
        groups = []
        groupstart = self.html.find("$$GROUP_")

        while groupstart != -1:
            groupend = self.html.find("GROUP$$", groupstart);

            if groupend == -1:
                self._p("A group block of your report is invalid (missing GROUP$$ closing tag)")
                return

            ghtml = self.html[groupstart:groupend];
            ghstart = ghtml.find("$$HEAD");
            if ghstart == -1:
                self._p("A group block of your report is invalid (no group $$HEAD)")
                return

            ghstart += 6;
            ghend = ghtml.find("$$FOOT", ghstart);

            if ghend == -1:
                self._p("A group block of your report is invalid (no group $$FOOT)")
                return

            gd = GroupDescriptor();
            gd.header = ghtml[ghstart:ghend];
            gd.footer = ghtml[ghend+6:];
            gd.fieldName = ghtml[8:ghstart-6).strip();
            groups.append(gd);
            groupstart = self.html.find("$$GROUP_", groupend);

        # Scan the ORDER BY clause to make sure the order
        # matches the grouping levels.  
        if len(groups) > 0:

            lsql = self.sql.lower();
            startorder = lsql.find("order by")

            if startorder == -1:
                self._p("You have grouping levels on this report without an ORDER BY clause.")
                return

            orderBy = lsql[startorder:]
            ok = False
            lastsort = 0

            for gd in groups:
                ok = -1 != orderBy.find(gd.fieldName.lower())
                if not ok: break

            if not ok:
                self._p("Your ORDER BY clause does not match the order of your groups.")
                return

        # Display criteria if there is some and the option is on
        if self.criteria != "" and not self.omitCriteria:
            self._p("Criteria:")
            self._p(self.criteria)
            self._hr()

        # Lets run our queries, get a list of them by splitting
        # our sql var by semi-colons
        queries = self.sql.split(";")

        # Make sure the last query is a SELECT or (SELECT
        if queries[len(queries)-1].lower().startswith("select") or
           queries[len(queries)-1].lower().startswith("(select"):
            self._p("There must be at least one SELECT query and it must be last to run")
            return

        # Run the queries
        rs = None
        for q in queries:
            if q.lower().startswith("select"):
                # SELECT for results
                try:
                    rs = db.query(q)
                except Exception,e:
                    self._p(e)
            else:
                # Action
                try:
                    db.execute(q)
                except Exception,e:
                    self._p(e)

        first_record = True

        # If there are no records, show a message to say so
        if len(rs) == 0:
            self._p("No data to show on the report.")
            return

        # Add the header to the report
        _SubstituteHeaderFooter(0, cheader, rs)


"""
        while (!rs.getEOF()) {
            // Add each group footer in reverse order, unless
            // this is the first record, in which case we haven't
            // started yet!

            // If an outer group has changed, we need to end the
            // inner groups first.
            if (!firstRecord) {
                // Loop through the groups in ascending order. If
                // the switch value for an outer group changes, we
                // need to force finishing of it's inner groups

                // This same flag is used to determine whether or
                // not to update the header
                boolean cascade = false;

                for (int i = 0; i < group.length; i++) {
                    GroupDescriptor gd = (GroupDescriptor) group[i];

                    if (gd.lastFieldValue == null) {
                        gd.lastFieldValue = "";
                    }

                    if (!gd.lastFieldValue.equals(rs.getField(gd.fieldName)) ||
                            cascade) {
                        // Mark this one for update
                        gd.forceFinish = true;
                        gd.lastGroupEndPosition = rs.getAbsolutePosition() -
                            1;
                        cascade = true;
                    } else {
                        gd.forceFinish = false;
                    }
                }

                for (int i = group.length - 1; i >= 0; i--) {
                    GroupDescriptor gd = (GroupDescriptor) group[i];

                    if (gd.forceFinish) {
                        // Output the footer, switching
                        // field values and calculating totals
                        // where necessary
                        outputGroupBlock(gd, 1, rs);
                    }
                }
            }

            // Now do each header in ascending order
            for (int i = 0; i < group.length; i++) {
                GroupDescriptor gd = (GroupDescriptor) group[i];

                if (gd.forceFinish || firstRecord) {
                    // Mark the position
                    gd.lastGroupStartPosition = rs.getAbsolutePosition();
                    gd.lastGroupEndPosition = rs.getAbsolutePosition();

                    // Output the header, switching
                    // field values and calculating totals
                    // where necessary
                    outputGroupBlock(gd, 0, rs);
                }
            }

            firstRecord = false;

            // Make a temp string to hold the body while
            // we substitute fields for tags
            tempbody = new String(cbody);

            for (int i = 1; i <= rs.getFieldCount(); i++) {
                tempbody = Utils.replace(tempbody,
                        "$" + rs.getFieldName(i),
                        displayValue(rs.getField(rs.getFieldName(i))));
            }

            // Update the last value for each group
            for (int i = 0; i < group.length; i++) {
                GroupDescriptor gd = (GroupDescriptor) group[i];
                gd.lastFieldValue = rs.getField(gd.fieldName);
            }

            // Find any non-field keys
            // Look for calculation keys
            int startKey = tempbody.indexOf("{");

            while (startKey != -1) {
                int endKey = tempbody.indexOf("}", startKey);
                String key = tempbody.substring(startKey + 1, endKey);
                String value = "";
                boolean valid = false;

                // {SQL.[sql]} - arbitrary sql command
                if (Utils.englishLower(key).startsWith("sql")) {
                    String field = key.substring(4, key.length());
                    valid = true;

                    try {
                        // If it's an action query, execute it
                        if (Utils.englishLower(field).startsWith("create") ||
                                Utils.englishLower(field).startsWith("drop") ||
                                Utils.englishLower(field)
                                         .startsWith("insert") ||
                                Utils.englishLower(field)
                                         .startsWith("update") ||
                                Utils.englishLower(field)
                                         .startsWith("delete")) {
                            DBConnection.executeAction(field);
                            value = "";
                        } else {
                            SQLRecordset rs2 = new SQLRecordset();
                            rs2.openRecordset(field, "animal");

                            if (rs2.getEOF()) {
                                value = "[EOF]";
                            } else {
                                if (rs2.getField(rs2.getFieldName(1)) == null) {
                                    value = "null";
                                } else {
                                    value = rs2.getField(rs2.getFieldName(1))
                                               .toString();
                                }
                            }
                        }
                    } catch (Exception e) {
                        value = "[" + e.getMessage() + "]";
                        Global.logException(e, getClass());
                    }
                }

                // {IMAGE.[animalid]} - retreives an animal's image from
                // the database, saves it in the temp folder and then
                // inserts the filename
                if (Utils.englishLower(key).startsWith("image")) {
                    try {
                        valid = true;

                        String animalid = key.substring(key.indexOf(".") +
                                1);

                        // If animalid isn't numeric, assume it's a fieldname
                        // instead and look it up
                        try {
                            Integer.parseInt(animalid);
                        } catch (NumberFormatException e) {
                            Global.logDebug("IMAGE parameter isn't numeric, assuming fieldname and looking up.",
                                "CustomReportExecute.run");

                            try {
                                animalid = rs.getField(animalid).toString();
                                Global.logDebug(
                                    "Looked up field, got ID = " +
                                    animalid, "CustomReportExecute.run");
                            } catch (Exception ex) {
                                // Yep, that didn't work, log and give up
                                Global.logException(ex, getClass());

                                break;
                            }
                        }

                        Global.logDebug("IMAGE tag, got animal id: " +
                            animalid, "CustomReportExecute.run");

                        Animal a = new Animal();
                        a.openRecordset("ID = " + animalid);

                        String mediaName = a.getWebMedia();

                        // If we got a blank, return a link to nopic.jpg instead
                        if (mediaName.equals("")) {
                            mediaName = "nopic.jpg";
                        }

                        DBFS dbfs = Utils.getDBFSDirectoryForLink(Media.LINKTYPE_ANIMAL,
                                Integer.parseInt(animalid));
                        dbfs.readFile(mediaName,
                            net.sourceforge.sheltermanager.asm.globals.Global.tempDirectory +
                            File.separator + mediaName);
                        value = mediaName;
                    } catch (Exception e) {
                        value = "[" + e.getMessage() + "]";
                        Global.logException(e, getClass());
                    }
                }

                // {SUBREPORT.[title].[parentField]} - embed
                // a subreport.
                if (Utils.englishLower(key).startsWith("subreport")) {
                    valid = true;

                    String body = key.substring(10, key.length());

                    // Break it up
                    String[] bits = Utils.split(body, ".");
                    String title = bits[0];
                    String parent = bits[1];
                    String parentkey = rs.getString(parent);

                    // Lookup custom report ID from title
                    Global.logDebug("Including custom report '" + title +
                        "' with key '" + parentkey + "'",
                        "CustomReportExecute.generateReport");

                    CustomReport subr = new CustomReport();
                    subr.openRecordset("Title Like '" + title + "'");

                    String id = subr.getID().toString();

                    // Call the report
                    setWaitingOnSubReport(true);
                    new CustomReportExecute(id, parentkey,
                        new ReportDoneListener() {
                            public void reportCompleted(String rep) {
                                lastSubReport = rep;

                                // If it has a <BODY> tag, then
                                // chop either side since it will be
                                // embedded
                                int bodys = lastSubReport.indexOf("<body");

                                if (bodys == -1) {
                                    bodys = lastSubReport.indexOf("<BODY");
                                }

                                if (bodys != -1) {
                                    int bodye = lastSubReport.indexOf(
                                            "</body>");

                                    if (bodye == -1) {
                                        bodye = lastSubReport.indexOf(
                                                "</BODY>");
                                    }

                                    if (bodye != -1) {
                                        lastSubReport = lastSubReport.substring(lastSubReport.indexOf(
                                                    ">", bodys) + 1, bodye);
                                    }
                                }

                                setWaitingOnSubReport(false);
                            }
                        });

                    // Wait for it to finish
                    while (isWaitingOnSubReport()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }

                    // Substitute the report value
                    value = lastSubReport;
                }

                if (valid) {
                    tempbody = tempbody.substring(0, startKey) + value +
                        tempbody.substring(endKey + 1, tempbody.length());
                }

                startKey = tempbody.indexOf("{", startKey + 1);
            }

            // Append into the report
            addHTML(tempbody);

            // Ditch string reference
            tempbody = null;

            rs.moveNext();
            incrementStatusBar();
        }

        // Add the final group footers if there are any
        for (int i = group.length - 1; i >= 0; i--) {
            GroupDescriptor gd = (GroupDescriptor) group[i];
            // Output the footer, switching
            // field values and calculating totals
            // where necessary
            gd.lastGroupEndPosition = (int) rs.getRecordCount();
            outputGroupBlock(gd, 1, rs);
        }

        // Add the report footer
        substituteHFValues(1, cfooter, rs);


    // If the report length is zero, there's nothing to display, so
    // finish gracefully and stop. This occurs when a user cancels input
    if (report.length() == 0) {
        setStatusText("");
        report = null;
        filename = null;
        tablespec = null;

        return;
    }

    if (!isSubReport) {
        report.append(getFooter());
    }


    } catch (Exception e) {
        Dialog.showError(e.getMessage());
        Global.logException(e, getClass());
    } finally {
        rs.free();
        rs = null;
        cheader = null;
        cbody = null;
        cfooter = null;
        resetStatusBar();
    }
}


    /**
     * Rounds a number to the set number of decimal places and
     * returns it as a readable string.
     */
    private String roundToDP(BigDecimal value, int dp) {
        final String zeroes = "00000000000000000000000000000000000000000";
        value = value.setScale(dp, BigDecimal.ROUND_HALF_UP);

        DecimalFormat df = new DecimalFormat("0." + zeroes.substring(0, dp));

        return df.format(value.doubleValue());
    }

    /** Converts a recordset value for display - nulls become empty strings */
    public String displayValue(Object o) {
        if (o == null) {
            return "";
        }

        if (o instanceof Date) {
            return Utils.formatDate((Date) o);
        }

        return o.toString();
    }

    /**
     * Scans the SQL code for any of our keys to substitute and processes them
     * accordingly.
     *
     *
     */
    public String substituteSQLTags(String sql) {
        // Hunt through the sql, looking for a start
        // marker to a tag
        HashMap<String, String> vars = new HashMap<String, String>();

        for (int i = 0; i < sql.length(); i++) {
            if (sql.substring(i, i + 1).equals("$")) {
                int tagstart = i;
                int tagend = sql.indexOf("$", i + 1);

                // Default string to replace the tag with
                replaceWith = "";

                // Grab the whole tag and split it into pieces
                String ftag = sql.substring(i + 1, tagend);
                String[] tagbits = Utils.split(ftag, " ");
                String tagtype = tagbits[0];

                // Mark the spaces for variable length message tags
                int firstspace = ftag.indexOf(" ");
                int secondspace = ftag.indexOf(" ", firstspace + 1);
                int thirdspace = -1;

                if (secondspace != -1) {
                    thirdspace = ftag.indexOf(" ", secondspace + 1);
                }

                // DATE tag
                if (tagtype.equalsIgnoreCase("CURRENT_DATE")) {
                    replaceWith = Utils.getSQLDate(new Date());
                }

                // USER tag
                if (tagtype.equalsIgnoreCase("USER")) {
                    replaceWith = Global.currentUserName;
                }

                // PARENTKEY tag
                if (tagtype.equalsIgnoreCase("PARENTKEY")) {
                    replaceWith = subReportParentFieldValue;
                }

                // ASK tag
                if (tagtype.equalsIgnoreCase("ASK")) {
                    // Check what they are asking for
                    String askedFor = tagbits[1];
                    String mess = "";

                    if (secondspace != -1) {
                        mess = ftag.substring(secondspace + 1, ftag.length());
                    }

                    replaceWith = handleAskTag(askedFor, mess);
                }

                // VAR tag
                if (tagtype.equalsIgnoreCase("VAR")) {
                    // Var is just like ASK, except it has a variable
                    // name before the type and message
                    String varname = tagbits[1];
                    String askedFor = tagbits[2];
                    String mess = "";

                    if (thirdspace != -1) {
                        mess = ftag.substring(thirdspace + 1, ftag.length());
                    }

                    String varvalue = handleAskTag(askedFor, mess);
                    vars.put(varname, varvalue);
                    // Var tags don't get replaced, we access the variable
                    // with a $@VARNAME$ tag
                    replaceWith = "";
                }

                // @ (variable output tag)
                if (tagtype.startsWith("@")) {
                    replaceWith = (String) vars.get(tagtype.substring(1));

                    if (replaceWith == null) {
                        replaceWith = "";
                    }
                }

                // Throw away the tag and replace it with the substitution
                // string
                sql = sql.substring(0, tagstart) + replaceWith +
                    sql.substring(tagend + 1, sql.length());
            }
        }

        return sql;
    }

    /**
     * Handles asking the user for something from an ASK or VAR tag
     * @param askedFor the kind of prompt to display
     * @param message The message to display if appropriate
     */
    private String handleAskTag(String askedFor, final String message) {
        // DATE
        if (askedFor.equalsIgnoreCase("DATE")) {
            seldate = Dialog.getDateInput(message, "Enter Date");

            if (seldate.equals("")) {
                return "CANCEL";
            }

            // Format it for replacement
            try {
                replaceWith = Utils.getSQLDate(seldate);
            } catch (Exception e) {
                replaceWith = Utils.getSQLDate(Calendar.getInstance());
            }

            // Add it to the crit list
            crit += (message + ": " + seldate + "<br/>");
        }

        // SPECIES
        if (askedFor.equalsIgnoreCase("SPECIES")) {
            replaceWith = Integer.toString(Dialog.getSpecies());

            try {
                crit += (Global.i18n("reports", "Species") + ": " +
                LookupCache.getSpeciesName(new Integer(replaceWith)) + "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // ANIMAL
        if (askedFor.equalsIgnoreCase("ANIMAL")) {
            replaceWith = Integer.toString(Dialog.getAnimal(true));

            try {
                crit += (Global.i18n("reports", "animal") + ": " +
                DBConnection.executeForString(
                    "SELECT ShelterCode FROM animal WHERE ID = " + replaceWith) +
                "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // ALLANIMAL
        if (askedFor.equalsIgnoreCase("ALLANIMAL")) {
            replaceWith = Integer.toString(Dialog.getAnimal(false));

            try {
                crit += (Global.i18n("reports", "animal") + ": " +
                DBConnection.executeForString(
                    "SELECT ShelterCode FROM animal WHERE ID = " + replaceWith) +
                "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // LITTER
        if (askedFor.equalsIgnoreCase("LITTER")) {
            replaceWith = Dialog.getLitter(0);

            try {
                crit += ((Configuration.getBoolean("AutoLitterIdentification")
                ? Global.i18n("uianimal", "litter_id")
                : Global.i18n("uianimal", "Acceptance_No:")) + " " +
                replaceWith + "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // ANIMAL TYPE
        if (askedFor.equalsIgnoreCase("TYPE")) {
            replaceWith = Integer.toString(Dialog.getAnimalType());

            try {
                crit += (Global.i18n("reports", "animaltype") + ": " +
                LookupCache.getAnimalTypeName(new Integer(replaceWith)) +
                "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // INTERNAL LOCATION
        if (askedFor.equalsIgnoreCase("LOCATION")) {
            replaceWith = Integer.toString(Dialog.getInternalLocation());

            try {
                crit += (Global.i18n("reports", "Internal_Location") + ": " +
                LookupCache.getInternalLocationName(new Integer(replaceWith)) +
                "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // DIET
        if (askedFor.equalsIgnoreCase("DIET")) {
            replaceWith = Integer.toString(Dialog.getDiet());

            try {
                crit += (Global.i18n("reports", "Diet") + ": " +
                LookupCache.getDietName(new Integer(replaceWith)) + "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // VOUCHER
        if (askedFor.equalsIgnoreCase("VOUCHER")) {
            replaceWith = Integer.toString(Dialog.getVoucher());

            try {
                crit += (Global.i18n("reports", "Voucher") + ": " +
                LookupCache.getVoucherName(new Integer(replaceWith)) + "<br/>");
            } catch (Exception e) {
                Global.logException(e, getClass());
            }
        }

        // NUMBER
        if (askedFor.equalsIgnoreCase("NUMBER")) {
            // Take the message string and use it to
            // prompt the user for the number
            selnumber = Dialog.getInput(message, "Input Number");

            // Validate that it is a number
            try {
                Double.parseDouble(selnumber);
            } catch (NumberFormatException e) {
                selnumber = "0";
            }

            // return it
            replaceWith = selnumber;
            crit += (message + ": " + selnumber + "<br/>");
        }

        // STRING
        if (askedFor.equalsIgnoreCase("STRING")) {
            // Take the message string and use it to
            // prompt the user for the string
            replaceWith = Dialog.getInput(message, "Input String");
            crit += (message + ": " + replaceWith + "<br/>");
        }

        return replaceWith;
    }

"""
