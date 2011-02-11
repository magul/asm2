#!/bin/sh

# Go back to root of Asm tree
cd ..

# Change all 2000-2010 text to 2000-2011 
for F in `find src -name '*.java'`; do
	cat $F | sed 's/2000-2010/2000-2011/g' > $F.tmp
	mv $F.tmp $F;
done

