#!/bin/sh

cd `dirname $0`

# Pulls in binary files ready for packaging and builds the
# iso image.

# clean/build scratch area
rm -rf scratch
mkdir scratch

# copy built binaries
cp -v ../build/* scratch

# copy ASM logo over for readme
cp -v ../logo/logo.jpg scratch

# Rename Windows executable to just setup.exe for autorun.inf
mv scratch/*.exe scratch/setup.exe

# Copy readme and autorun.inf over
cp -v autorun.inf scratch
cp -v index.html scratch/readme.html

# ASM Windows icon
cp -v ../logo/asm.ico scratch/asm.ico

# Create a tar of the complete development environment:
tar --exclude build --exclude bin --exclude cdimage --exclude .svn -cvf scratch/sheltermanager-`cat ../install/deb/VERSION`_devenvironment.tar ../../sheltermanager

# Build ISO
mkisofs -R --iso-level 4 -o ../build/asm.iso scratch
