#!/bin/sh
INSTDIR=/usr/share/asm
java -Xmx256m -cp $INSTDIR/asm.jar net.sourceforge.sheltermanager.asm.startup.HSQLManager $INSTDIR
