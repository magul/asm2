#!/bin/sh

# Use alien to convert the deb (RPM is really finicky about
# importing lots of files and I'm too lazy, plus alien seems
# to do a good job)
cd `dirname $0`
alien --to-rpm sheltermanager*amd64.deb
