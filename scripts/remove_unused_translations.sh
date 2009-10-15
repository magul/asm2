#!/bin/sh

cd `dirname $0`
cd ../src

# Combine sources into one giant file
rm -f all.java
cat `find . -name '*.java'` > all.java

# Pass it all off to our python script
python ../scripts/remove_unused_translations.py all.java locale/*.properties
rm -f all.java
