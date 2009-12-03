#!/bin/sh

cd `dirname $0`
cd ../src

# Combine sources into one giant file
rm -f all.java
for i in `find . -name '*.java'`; do
    echo "##FILE:$i##" >> all.java
    cat $i >> all.java
done

# Pass it all off to our python script
python ../scripts/remove_unused_translations.py all.java locale/*.properties
rm -f all.java
