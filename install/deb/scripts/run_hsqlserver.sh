#!/bin/sh
INSTDIR=/usr/share/asm
java -Xmx256m -cp $INSTDIR/lib/hsqldb.jar org.hsqldb.Server -database.0 file:$HOME/.asm/localdb -dbname.0 asm

