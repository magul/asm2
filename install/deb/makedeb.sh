#!/bin/sh

cd `dirname $0`

# Clear the image out
#echo "Cleaning staging area..."
rm -rf sheltermanager/usr 

# Remake the paths
#echo "Building staging area..."
mkdir -p sheltermanager/usr/share/asm/lib
mkdir -p sheltermanager/usr/share/menu
mkdir -p sheltermanager/usr/bin

# Update start scripts and icons
#echo "Updating scripts and icons in image..."
cp scripts/asm* sheltermanager/usr/bin -f
cp scripts/sheltermanager sheltermanager/usr/bin -f
cp scripts/*.sh sheltermanager/usr/share/asm -f
cp ../../logo/asm2009/asm.ico sheltermanager/usr/share/asm -f
cp ../../logo/asm2009/asm.xpm sheltermanager/usr/share/asm -f

# Update the libraries
#echo "Updating libraries in image..."
rm sheltermanager/usr/asm/lib/* -f
cp ../../lib/*.jar sheltermanager/usr/share/asm/lib -f

# Update asm jar
#echo "Updating ASM in image..."
cp ../../build/asm.jar sheltermanager/usr/share/asm/asm.jar -f

# Update help 
#echo "Updating help and media files in image..."
rm sheltermanager/usr/share/asm/data -rf
mkdir sheltermanager/usr/share/asm/data
mkdir sheltermanager/usr/share/asm/data/sql
cp ../../doc/help/en/build/*.pdf sheltermanager/usr/share/asm/data/ -f
cp ../../doc/help/en/help* sheltermanager/usr/share/asm/data/ -rf
cp ../../media sheltermanager/usr/share/asm/data/ -rf
cp ../../sql/*.sql sheltermanager/usr/share/asm/data/sql/ -rf
cp menu/sheltermanager sheltermanager/usr/share/menu

# Generate the control file
#echo "Generating control file..."
echo "Package: sheltermanager
Version: `cat ../../VERSION`
Section: contrib
Priority: optional
Architecture: all
Essential: no
Depends: menu, sun-java6-jre, xulrunner-1.9
Suggests: openoffice.org-writer
Installed-Size: `du -s -k sheltermanager | awk '{print$1}'`
Maintainer: Robin Rawson-Tetley [robin@rawsontetley.org]
Provides: sheltermanager
Description: Management solution for animal shelters and sanctuaries
 Animal Shelter Manager is the most popular, free management package
 for animal sanctuaries and welfare charities." > sheltermanager/DEBIAN/control

# Builds the debian package from a temporary location with no .svn folders
#echo "Building package..."
rm sheltermanagertmp -rf > /dev/null
tar --exclude RPM --exclude .svn -czvf sm.tar.gz sheltermanager > /dev/null
mkdir tmp
mv sm.tar.gz tmp
cd tmp
tar -zxvf sm.tar.gz > /dev/null
rm sm.tar.gz -f
cd ..
dpkg -b tmp/sheltermanager sheltermanager_`cat ../../VERSION`_all.deb

# Clean up
rm tmp -rf
rm -rf sheltermanager/usr
