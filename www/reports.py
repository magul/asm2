#!/usr/bin/python

import base64
import db
import i18n
import lookup

"""
        ASM reporting module, contains all code necessary for 
        generating reports and producing HTML output

        Usage:
        
        reports.execute(customreportid, [params])

"""

HEADER = 0
FOOTER = 1

def get_available_reports(dbo, include_with_criteria = True):
    """
    Returns a list of reports available for running. The return
    value is a tuple of category, ID and title.
    If include_with_criteria is false, only reports that don't
    have ASK or VAR tags are included.
    """
    reports = []
    rs = db.query(dbo, "SELECT ID, Category, Title, HTMLBody, SQLCommand " +
        "FROM customreport ORDER BY Category, Title")

    for r in rs:
        html = r["HTMLBODY"]
        sql = r["SQLCOMMAND"]

        # Ignore built in reports, mail merges and graphs
        if len(html) < 6:
            continue

        # Ignore subreports
        if sql.find("$PARENTKEY$") != -1:
            continue

        # If we're excluding reports with criteria, check now
        if not include_with_criteria:
            if sql.find("$ASK") != -1 or sql.find("$VAR") != -1:
                continue

        # We're good
        reports.append( (r["CATEGORY"], r["ID"], r["TITLE"]) )

    return reports

def get_title(dbo, customreportid):
    """
    Returns the title of a custom report from its ID
    """
    return db.query_string(dbo, "SELECT Title FROM customreport WHERE ID = %s" % str(customreportid))

def execute(dbo, customreportid, params = None):
    """
    Executes a custom report by its ID. 'params' is a tuple of 
    parameters. See the Report._SubstituteSQLParameters function for
    more info.
    """
    r = Report(dbo)
    return r.Execute(customreportid, params)

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
    dbo = None
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
    
    def __init__(self, dbo):
        self.dbo = dbo

    def _ReadReport(self, reportId):
        """
        Reads the report info from the database and populates
        our local class variables
        """
        rs = db.query(self.dbo, "SELECT Title, HTMLBody, SQLCommand, OmitCriteria, " +
            "OmitHeaderFooter FROM customreport WHERE ID = %s" % str(reportId))
        
        # Can't do anything if the ID was invalid
        if len(rs) == 0: return

        r = rs[0]
        self.title = r["TITLE"]
        self.html = r["HTMLBODY"]
        self.sql = r["SQLCOMMAND"]
        self.omitCriteria = r["OMITCRITERIA"] > 0
        self.omitHeaderFooter = r["OMITHEADERFOOTER"] > 0
        self.isSubReport = self.sql.find("PARENTKEY") != -1

    def _ReadHeader(self):
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
            # Look it up from the DB and Base64 decode it
            s = db.query_string(self.dbo, "SELECT Content FROM dbfs WHERE Name = 'head.dat' AND Path = '/reports'")
            if s != "":
                s = base64.b64decode(s)
            s = self._SubstituteTemplateHeaderFooter(s)
            return s

    def _ReadFooter(self):
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
            # Look it up from the DB and Base64 decode it
            s = db.query_string(self.dbo, "SELECT Content FROM dbfs WHERE Name = 'foot.dat' AND Path = '/reports'")
            if s != "":
                s = base64.b64decode(s)
            s = self._SubstituteTemplateHeaderFooter(s)
            return s

    def _Append(self, s):
        self.output += str(s)
        return self.output

    def _p(self, s):
        self._Append("<p>%s</p>" % s)

    def _hr(self):
        self._Append("<hr />")

    def _DisplayValue(self, v):
        """
        Returns the display version of any value
        """
        if v == None: return ""
        if (str(v)).find("00.00") != -1:
            return i18n.python2display(v)
        return str(v)

    def _OutputGroupBlock(self, gd, headfoot, rs, rowindex):
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
            out = out.replace("$" + k, self._DisplayValue(v))
            out = out.replace("$" + k.lower(), self._DisplayValue(v))

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
                value = str(gd.lastGroupEndPosition - gd.lastGroupStartPosition + 1)

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
                        num += 1
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
                            matched += 1
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
                value = str(rs[gd.lastGroupStartPosition][fields[1]])

            # {LAST.field}
            if key.lower().startswith("last"):
                valid = True
                fields = key.lower().split(".")
                value = str(rs[gd.lastGroupStartPosition][fields[1]])

            # {SQL.sql} - arbitrary sql command, output first
            # column of first row
            if key.lower().startswith("sql"):
                valid = True
                asql = key[4:]
                if asql.lower().startswith("select"):
                    # Select - return first row/column
                    try:
                        x = db.query_tuple(self.dbo, asql)
                        value = str(x[0][0])
                    except Exception, e:
                        value = e
                else:
                    # Action query, run it
                    try:
                        value = ""
                        db.execute(self.dbo, asql)
                    except Exception, e:
                        value = e

            # {IMAGE.animalid} - substitutes a link to the image.psp
            # page to direct the browser to retrieve an image
            if key.lower().startswith("image"):
                valid = True
                animalid = key[key.find(".")+1:]
                value = "image.psp?type=animal&id=%s" % animalid


            # {SUBREPORT.[title].[parentField]} - embed a subreport
            if key.lower().startswith("subreport"):
                valid = True
                fields = key.lower().split(".")
                
                # Get custom report ID from title
                crid = db.query_int(self.dbo, "SELECT ID FROM customreport WHERE Title LIKE '" + fields[1] + "'");

                # Get the content from it
                r = Report(self.dbo)
                value = r.Execute(crid, [("PARENTKEY", fields[2])] )

            # Modify our block with the token value
            if valid:
                out = out[0:startkey] + value + out[endkey+1:];

            # Find the next key
            startkey = out.find("{", startkey+1)

        # Output the HTML to the report
        self._Append(out)

    def _SubstituteTemplateHeaderFooter(self, s):
        """
        Substitutes special tokens in the report template
        header and footer. 's' is the header/footer to
        find tokens in, return value is the substituted 
        header/footer.
        """
        s = s.replace("$$TITLE$$", self.title)
        s = s.replace("$$DATE$$", i18n.python2display(db.today()))
        s = s.replace("$$VERSION$$", i18n.get_version())
        s = s.replace("$$USER$$", "TODO") # TODO:
        s = s.replace("$$REGISTEREDTO$$", lookup.config_get(self.dbo, "Organisation"))
        return s

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
        self._OutputGroupBlock(gd, headfoot, rs, rowindex);

    def _SubstituteSQLParameters(self, params):
        """
        Substitutes tokens in the report SQL.
        'params' is expected to be a list of parameters, each
        parameter is a tuple, containing the variable name and a substitution value. 
        If the parameter wasn't from a var tag, the variable name will contain 
        ASK<x> where <x> is the nth ASK tag in the SQL. In addition, the
        PARENTKEY type is used for passing a value to a subreport.
        The return value is the substituted SQL.
        """

        s = self.sql
        sp = s.find("$")
        asktagsseen = 0
        while sp != -1:
            
            ep = s.find("$", sp+1)
            token = s[sp+1:ep]
            value = ""

            # ASK tag
            if token.startswith("ASK"):
                asktagsseen += 1

                # Loop through the list of parameters, skipping
                # ASK tags until we get to the correct value
                pop = asktagsseen
                for p in params:
                    if p[0].startswith("ASK"):
                        pop -= 1
                        if pop == 0: 
                            value = p[1]
                            break

            # VAR tag
            if token.startswith("VAR"):
                # Just remove it from the SQL altogether
                value = ""

            # Variable replacement
            if token.startswith("@"):
                vname = token[1:]
                for p in params:
                    if p[0] == vname:
                        value = p[1]
                        break

            # CURRENT_DATE
            if token.startswith("CURRENT_DATE"):
                value = db.python2db(db.today())

            # USER
            if token.startswith("USER"):
                value = "" # TODO: Need to sort this out

            # PARENTKEY
            if token.startswith("PARENTKEY"):
                for p in params:
                    if p[0] == "PARENTKEY":
                        value = p[1]
                        break

            # Do the replace
            s = s[0:sp] + value + s[ep+1:]

            # Next token
            sp = s.find("$", sp)

        self.sql = s

    def GetParams(self, reportId):
        """
        Returns a list of parameters required for a report, 
        with their types
        'reportId' is the ID of the report to get parameters for.
        Returns a list of parameters, each item is a list containing a
        variable name (or ASK for a one-shot ask), a type 
        (DATE, ANIMAL, LITTER, SPECIES, LOCATION, TYPE, NUMBER, STRING)
        and a question string.
        """
        self._ReadReport(reportId)
        params = []

        s = self.sql
        sp = s.find("$")
        while sp != -1:
            
            # Has to be ASK or VAR - if it isn't, keep looking
            if s[sp:sp+4] != "$ASK" and s[sp:sp+4] != "$VAR":
                sp = s.find("$", sp+1)
                continue

            ep = s.find("$", sp+1)
            token = s[sp+1:ep]
            paramtype = ""
            varname = ""
            question = ""

            # ASK
            if token.startswith("ASK"):                
                varname = "ASK"

                # Get the type
                nsp = token.find(" ", 5)
                if nsp == -1: nsp = len(token)
                paramtype = s[5:nsp]

                # Does the type need a string?
                if paramtype == "DATE" or paramtype == "NUMBER" or paramtype == "STRING":
                    question = token[nsp+1:]

            # VAR
            if token.startswith("VAR"):
                fields = token.split(" ")
                
                # Get the name
                varname = fields[1]

                # The type
                paramtype = fields[2]

                # And the string if it needs one
                if paramtype == "DATE" or paramtype == "NUMBER" or paramtype == "STRING":
                    sp1 = token.find(" ", 5)
                    sp2 = token.find(sp1 + 1)
                    question = token[sp2+1:]

            # Bundle them up
            params.append((varname, paramtype, question))

            # Next token
            sp = s.find("$", ep+1)

        return params

    def Execute(self, reportId, params = None):
        """
        Executes a report
        'reportId' is the ID of the report to run, 'params' is a list
        of values in order to substitute tokens in the report SQL for.
        They should all be strings and will be literally replaced.
        Return value is the HTML output of the report.
        """
        self._ReadReport(reportId)
        self.output = ""

        # Substitute our parameters in the SQL
        self._SubstituteSQLParameters(params)

        # Is it a graph? We aren't doing anything with those right now
        if self.html.upper().strip() == "GRAPH":
            # TODO: Implement graphs
            return

        # Is it a mail merge? Can't do anything with those either
        if self.html.upper().strip() == "MAIL":
            # TODO: Implement mail merge
            return

        # Add the HTML header to our report
        self._Append(self._ReadHeader())

        # Generate the report
        self._Generate(reportId, params)

        # Add the HTML footer to our report
        self._Append(self._ReadFooter())

        # We're done
        return self.output

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
            gd.fieldName = ghtml[8:ghstart-6].strip().upper();
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
        if not queries[-1].lower().startswith("select") and \
           not queries[-1].lower().startswith("(select"):
            self._p("There must be at least one SELECT query and it must be last to run")
            return

        # Run the queries
        rs = None
        for q in queries:
            if q.lower().startswith("select"):
                # SELECT for results
                try:
                    rs = db.query(self.dbo, q)
                except Exception,e:
                    self._p(e)
            else:
                # Action
                try:
                    db.execute(self.dbo, q)
                except Exception,e:
                    self._p(e)

        first_record = True

        # If there are no records, show a message to say so
        # but only if it's not a subreport
        if rs == None or len(rs) == 0:
            if not self.isSubReport:
                self._p("No data to show on the report.")
            return

        # Add the header to the report
        self._SubstituteHeaderFooter(HEADER, cheader, rs, 0)

        # Construct our report
        for row in range(0, len(rs)):

            # If an outer group has changed, we need to end
            # the inner groups first
            if not first_record:
                # This same flag is used to determine whether or
                # not to update the header
                cascade = False

                # Loop through the groups in ascending order.
                # If the switch value for an outer group changes,
                # we need to force finishing of its inner groups.
                for gd in groups:
                    if cascade or not gd.lastFieldValue == rs[row][gd.fieldName]:
                        # Mark this one for update
                        gd.forceFinish = True
                        gd.lastGroupEndPosition = row - 1
                        cascade = True
                    else:
                        gd.forceFinish = False

                # Now do each group footer in reverse order
                for gd in reversed(groups):
                    if gd.forceFinish:
                        # Output the footer, switching the
                        # field values and calculating any totals
                        self._OutputGroupBlock(gd, FOOTER, rs, row)

            # Do each header in ascending order
            for gd in groups:
                if gd.forceFinish or first_record:
                    # Mark the position
                    gd.lastGroupStartPosition = row
                    gd.lastGroupEndPosition = row

                    # Output the header, switching field values
                    # and calculating any totals
                    self._OutputGroupBlock(gd, HEADER, rs, row)

            first_record = False

            # Make a temp string to hold the body block 
            # while we substitute fields for tags
            tempbody = cbody
            for k, v in rs[row].iteritems():
                tempbody = tempbody.replace("$" + k, self._DisplayValue(v))
                tempbody = tempbody.replace("$" + k.lower(), self._DisplayValue(v))

            # Update the last value for each group
            for gd in groups:
                gd.lastFieldValue = rs[row][gd.fieldName]

            # Deal with any non-field/calculation keys
            startkey = tempbody.find("{")
            while startkey != -1:
                endkey = tempbody.find("}", startkey)
                key = tempbody[startkey+1:endkey]
                value = ""
                valid = False

                # {SQL.sql}
                if key.lower().startswith("sql"):
                    valid = True
                    asql = key[4:]
                    if asql.lower().startswith("select"):
                        # Select - return first row/column
                        try:
                            x = db.query_tuple(self.dbo, asql)
                            value = str(x[0][0])
                        except Exception, e:
                            value = e
                    else:
                        # Action query, run it
                        try:
                            value = ""
                            db.execute(self.dbo, asql)
                        except Exception, e:
                            value = e

                # {IMAGE.animalid} - substitutes a link to the image.psp
                # page to direct the browser to retrieve an image
                if key.lower().startswith("image"):
                    valid = True
                    animalid = key[key.find(".")+1:]
                    value = "image.psp?type=animal&id=%s" % animalid


                # {SUBREPORT.[title].[parentField]} - embed a subreport
                if key.lower().startswith("subreport"):
                    valid = True
                    fields = key.lower().split(".")
                    
                    # Get custom report ID from title
                    crid = db.query_int(self.dbo, "SELECT ID FROM customreport WHERE Title LIKE '" + fields[1] + "'");

                    # Get the content from it
                    r = Report(self.dbo)
                    value = r.Execute(crid, [("PARENTKEY", fields[2])] )

                if valid:
                    tempbody = tempbody[0:startkey] + value + tempbody[endkey+1:]

                # next key
                startkey = tempbody.find("{", startkey+1)

            # Add the substituted body block to our report
            self._Append(tempbody)

        # Add the final group footers if there are any
        row = len(rs) - 1
        for gd in reversed(groups):
            gd.lastGroupEndPosition = row
            self._OutputGroupBlock(gd, FOOTER, rs, row)

        # And the report footer
        self._SubstituteHeaderFooter(FOOTER, cfooter, rs, row)


