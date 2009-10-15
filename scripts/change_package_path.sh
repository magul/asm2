#!/bin/sh

# Go back to root of Asm tree
cd ..

# Change all uk.co.rtds to net.sourceforge.sheltermanager
for F in `find src/net -name '*.java'`; do
	echo $F
	cat $F | sed 's/uk\.co\.rtds/net\.sourceforge\.sheltermanager/g' > $F.tmp
	mv $F.tmp $F;
done

