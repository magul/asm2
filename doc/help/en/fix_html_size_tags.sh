#!/bin/sh

# Throws away HTML size tags when generating ASM manual as
# HTML. This means they display full size and without nasty
# scaling in the browser.

for f in `find . -name '*.html'`; do
	cat $f | sed 's/WIDTH=\"...\"/\ /g' > $f.tmp
	mv $f.tmp $f;
	cat $f | sed 's/HEIGHT=\"...\"/\ /g' > $f.tmp
	mv $f.tmp $f;
done


