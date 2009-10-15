#!/bin/sh
echo Starting Animal Shelter Manager...
#MOZILLA=/usr/lib/xulrunner-1.9
#export MOZILLA_FIVE_HOME=$MOZILLA
java -Dorg.eclipse.swt.browser.XULRunnerPath=/usr/lib/xulrunner-1.9 -Xmx256m -cp /usr/share/asm/asm.jar:/usr/share/asm/lib/mysql.jar:/usr/share/asm/lib/charting-0.94.jar:/usr/share/asm/lib/postgresql.jar:/usr/share/asm/lib/hsqldb.jar:/usr/share/asm/lib/swingwt.jar:/usr/share/asm/lib/swt.jar net.sourceforge.sheltermanager.asm.startup.Startup /usr/share/asm/data/
