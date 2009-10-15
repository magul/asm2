#!/bin/sh
java -Xmx256m -cp /usr/share/asm/lib/hsqldb.jar org.hsqldb.Server -database.0 file:$HOME/.asm/localdb -dbname.0 asm

