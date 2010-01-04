#!/bin/sh

# Go back to root of Asm tree
cd ..

# Change all 2000-2008 text to 2000-2009 
for F in `find src srcui -name '*.java'`; do
	cat $F | sed 's/2000-2009/2000-2010/g' > $F.tmp
	mv $F.tmp $F;
done

