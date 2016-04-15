#!/bin/sh

# Makes a BZIP source archive for distribution

# Tar everything up
cd `dirname $0`
cd ..
tar -cjvf asm.tar.bz2 \
	--exclude .svn \
	build.xml \
	INSTALLATION \
	README \
	AUTHORS \
	COPYING \
    changelog \
    lib \
	src \
	doxygen \
	install/macosx/*.sh \
	install/macosx/*.txt \
	install/unix/LICENSE \
	install/unix/README \
	install/unix/*.sh \
    install/win32/*.nsi \
	install/win32/*.sh \
	install/win32/*exe \
	doc/help/en/README \
	doc/help/en/asm.lyx \
	doc/help/en/*.sh \
	doc/help/en/Makefile \
	scripts \
    sql \
	Makefile \
    VERSION > /dev/null
	
