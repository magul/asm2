#!/bin/sh
cd ..
cd src
java -cp . uk.co.rtds.asm.db.MakeClassFromTable $1
