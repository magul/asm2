# ASM Main buildfile
#
#	all:		Builds everything (not including cd image)
# 	checkbinaries:	Verifies all build tools available
# 	clean:		Clears down compiled files
# 	init:		Prepares for build
# 	codeformat:	Reformats the source
#       translation:    Imports po files in src/locale/po to java properties
#       template:       Builds a pot template from translate_en, java properties
# 	manual:		Builds the manual
#       scratch:        Updates the development build
# 	cd:		Builds everything and a CD image
# 	docs:		Generates doxygen docs
#	jar:		Compiles ASM and makes asm.jar
#	applet:		Makes a signed asm-applet.jar from asm.jar
#       pgapplet:       Makes a signed asm-applet.jar with just postgres
#	source:		Builds a source tarball
#	deb:		Builds an x86 Debian package
#	rpm:		Builds an x86 RPM package
#	unix:		Builds a generic UNIX tarball
#	macosx:		Builds a Mac OSX ASM.app
#	win32:		Builds a Windows installer

all:	clean manual jar source deb rpm applet unix macosx win32

checkbinaries:
	@echo "[checkbinaries] ==================="
	@sh scripts/check_build_binaries.sh

clean:	checkbinaries
	@echo "[clean] ==========================="
	rm -rf build
	rm -rf doxygen/html doxygen/doxygeng.conf
	rm -rf bin
	rm -rf install/linux/asm install/linux_amd64/asm install/unix/asm 
	rm -rf install/macosx/ASM.app install/win32/media.zip

init:	
	@echo "[init] ============================"
	mkdir -p build

manual:	init
	@echo "[manual] =========================="
	make -C doc/help/en clean dist

cd:	all
	@echo "[cd] =============================="
	sh cdimage/make.sh
	mv build/*.iso build/asm-`cat VERSION`.iso

docs:
	@echo "[docs] ============================"
	cat doxygen/doxygen.conf | sed "s/ZZZasmversionZZZ/`cat VERSION`/g" > doxygen/doxygeng.conf
	doxygen doxygen/doxygeng.conf

jar:	init
	@echo "[jar] ============================="
	cat src/locale/globals_build.properties | sed "s/ZZZbuildZZZ/`cat VERSION`\ \(`date`\)/g" > src/locale/globals.properties
	ant build
	cp -f build/asm.jar build/asm-`cat VERSION`.jar

applet:	jar
	@echo "[applet] =========================="
	scripts/makesignedapplet.sh

pgapplet: jar
	@echo "[pgapplet] ========================"
	scripts/makesignedapplet_pg.sh

codeformat:
	@echo "[codeformat] ======================"
	scripts/jalopy.sh -r src srcui

template:
	@echo "[template] ========================"
	scripts/remove_unused_translations.sh
	scripts/database_to_java_en.sh
	scripts/java_to_pot.sh
	scripts/pot_to_en_gb.py

translation:
	@echo "[translation] ====================="
	scripts/gettext_to_java.sh
	mv src/locale/po/*.properties src/locale
	scripts/java_to_database.sh

tags:
	@echo "[tags] ============================"
	ctags -f tags `find src -name '*.java'`

source:	codeformat docs
	@echo "[source] =========================="
	sh scripts/makebz2.sh
	mv asm.tar.bz2 build/sheltermanager-`cat VERSION`_src.tar.bz2

deb:	manual jar
	@echo "[deb] ============================="
	sh install/deb/makedeb.sh
	cp install/deb/sheltermanager*.deb build

rpm:	deb
	@echo "[rpm] ============================="
	sh install/deb/makerpm.sh
	rm -f install/deb/sheltermanager*.deb
	mv install/deb/sheltermanager*.rpm build

unix:	manual jar
	@echo "[unix] ============================"
	sh install/unix/make.sh
	mv install/unix/*.tar.gz build/sheltermanager-`cat VERSION`_noarch_unix.tar.gz

macosx:	manual jar
	@echo "[macosx] =========================="
	sh install/macosx/make.sh
	mv install/macosx/*.tar.gz build/sheltermanager-`cat VERSION`_ppcx86_macosx.tar.gz

win32:	manual jar
	@echo "[win32] ==========================="
	sh install/win32/make.sh
	mv install/win32/sheltermanager*.exe build/sheltermanager-`cat VERSION`_i386_win32.exe

scratch: applet
	@echo "[scratch] ========================="
	scp build/asm.jar root@rawsoaa2.miniserver.com:/var/www/sheltermanager.com/scratch/
	scp changelog root@rawsoaa2.miniserver.com:/var/www/sheltermanager.com/scratch/
	scp build/asm-applet.jar root@rawsoaa2.miniserver.com:/var/www/sheltermanager.com/applet/development/
