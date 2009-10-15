#!/bin/sh
cd `dirname $0`

# Create the media zip
rm media.zip
cd ../..
tar --exclude .svn -czvf media.tar.gz media > /dev/null
mkdir tmpmed
mv media.tar.gz tmpmed
cd tmpmed
tar -zxvf media.tar.gz > /dev/null
zip -r9 media.zip media > /dev/null
rm -f media.tar.gz
mv media.zip ../install/win32
cd ..
rm -rf tmpmed
cd install/win32

# Copy the nsisunz.dll file to the NSIS installation
cp -f nsisunz.dll "$HOME/.wine/drive_c/Program Files/NSIS/Plugins"

# Make a copy of the asm.nsi file and set the version number on it
cat asm.nsi | sed "s/ZZZasmversionZZZ/`cat ../../VERSION`/g" > build.nsi
wine "$HOME/.wine/drive_c/Program Files/NSIS/makensis.exe" build.nsi
rm build.nsi
