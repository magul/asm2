#!/bin/sh
BINARIES="ant java sh lyx inkscape convert latex2html dpkg alien tar zip unzip bzip2 wine doxygen genisoimage prop2po recode"

for i in $BINARIES; do
	printf "    $i: "
	# Which fails with error code and will kill the build if no 
	# file on path matches name
	which $i
	if [ $? != 0 ]; then
		echo "**** NOT FOUND ****"
		exit 1
	fi
done
