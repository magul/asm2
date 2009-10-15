#!/bin/sh

lyx faq.lyx -e latex
latex2html -no_images -ascii_mode -split 0 faq.tex

# Update all the hyperlinks as they will point to the named
# file incorrectly
for F in `grep -rl 'faq.html' faq`; do
	cat $F | sed 's/faq\.html//g' > $F.tmp
	mv $F.tmp $F;
done


