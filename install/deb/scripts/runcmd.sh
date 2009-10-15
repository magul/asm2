#!/bin/sh
export LD_LIBRARY_PATH=/usr/share/asm/lib/
java -Xmx256m -cp /usr/share/asm/asm.jar:/usr/share/asm/lib/mysql.jar:/usr/share/asm/lib/charting-0.94.jar:/usr/share/asm/lib/postgresql.jar:/usr/share/asm/lib/hsqldb.jar:/usr/share/asm/lib/swingwt.jar:/usr/share/asm/lib/swt.jar:/usr/share/asm/lib/swt-pi.jar:/usr/share/asm/lib/swt-mozilla.jar net.sourceforge.sheltermanager.asm.script.Startup /usr/share/asm/data $@
