#!/bin/sh

cd `dirname $0`/../build

# Make a big fat jnlp jar
mkdir jnlp
cd jnlp
unzip ../asm.jar
rm -rf META-INF
unzip ../../lib/hsqldb.jar
rm -rf META-INF
unzip ../../lib/charting-0.94.jar
rm -rf META-INF
unzip ../../lib/mysql.jar
rm -rf META-INF
unzip ../../lib/postgresql.jar
rm -rf META-INF
unzip ../../lib/edtftpj.jar
rm -rf META-INF
zip -r9 ../fatone.jar *

cd ..
rm -rf jnlp

# Sign it
jarsigner -keystore ../scripts/compstore -storepass ab123d -keypass kpi555 -signedjar asm-jnlp.jar fatone.jar signFiles
rm -f fatone.jar

# Create JNLP file
echo '<?xml version="1.0" encoding="UTF-8"?>
<jnlp spec="1.0+"
  codebase="http://sheltermanager.com/jnlp"
>
<information>
  <title>Animal Shelter Manager</title>
  <vendor>Robin Rawson-Tetley</vendor>
  <homepage href="http://sheltermanager.com" />
  <description>Animal Shelter Manager is a feature-packed, open source computer package. It is designed to manage all aspects of an animal shelter, including intake, adoptions, internet publishing, medical treatments, diary, reporting, accounts, etc.</description>
</information>
<offline-allowed/>
<security>
  <all-permissions/>
</security>
<resources>
  <j2se version="1.5+" />
  <jar href="asm-jnlp.jar"/>
</resources>
<application-desc main-class="net.sourceforge.sheltermanager.asm.startup.Startup" />
</jnlp>' > asm.jnlp
