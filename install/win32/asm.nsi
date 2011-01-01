;NSIS Setup Script
;--------------------------------

!define VER_DISPLAY "ZZZasmversionZZZ"

;--------------------------------
;Configuration

OutFile sheltermanager_win32.exe
SetCompressor lzma

InstType "Full"

InstallDir $PROGRAMFILES\ASM
InstallDirRegKey HKLM Software\ASM ""

;--------------------------------

;--------------------------------
;Configuration

;Names
Name "Animal Shelter Manager"
Caption "Animal Shelter Manager ${VER_DISPLAY} Setup"
LicenseData license.txt

;Interface Settings
!define MUI_ABORTWARNING
!define MUI_HEADERIMAGE
!define MUI_COMPONENTSPAGE_SMALLDESC

;Pages
Page license
Page components
Page directory
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

; Variables
Var FH

;--------------------------------
;Installer Sections

Section "Animal Shelter Manager" Client

  SetDetailsPrint textonly
  DetailPrint "Installing ASM..."
  SetDetailsPrint listonly

  SectionIn 1 RO

  ; This just makes sure we don't have shortcuts from an old install
  RMDir /r $SMPROGRAMS\ASM

  SetOverwrite on
  
  SetOutPath $INSTDIR
  
  ; Core files
  File ..\..\build\asm.jar
  File ..\..\logo\asm2009\asm.ico
  File ..\..\scripts\translation_encoder.html
  
  ; Libraries
  CreateDirectory $INSTDIR\lib
  SetOutPath $INSTDIR\lib
  File ..\..\lib\charting-0.94.jar
  File ..\..\lib\mysql.jar
  File ..\..\lib\postgresql.jar
  File ..\..\lib\hsqldb.jar
  File ..\..\lib\edtftpj.jar

  SetOutPath $INSTDIR
  File java6.zip
  nsisunz::UnzipToLog "$INSTDIR\java6.zip" "$INSTDIR"
  Delete "$INSTDIR\java6.zip"

lbl_data:
  ; Data Directory
  CreateDirectory $INSTDIR\data
  CreateDirectory $INSTDIR\data\sql
  SetOutPath $INSTDIR\data\sql
  File ..\..\sql\hsqldb.sql
  File ..\..\sql\postgresql.sql
  File ..\..\sql\mysql.sql
  File ..\..\sql\translate_de.sql
  File ..\..\sql\translate_en.sql
  File ..\..\sql\translate_es.sql
  File ..\..\sql\translate_et.sql
  File ..\..\sql\translate_fr.sql
  File ..\..\sql\translate_iw_IL.sql
  File ..\..\sql\translate_lt.sql
  File ..\..\sql\translate_nl.sql
  File ..\..\sql\translate_pl.sql
  File ..\..\sql\translate_pt.sql
  File ..\..\sql\translate_ru.sql
  File ..\..\sql\translate_sk.sql
  File ..\..\sql\translate_sv.sql
  File ..\..\sql\translate_th.sql
  File ..\..\sql\translate_tr.sql

  ; Write batch files
  SetOutPath $INSTDIR

  ; Main launcher from batch file
  FileOpen $FH "$INSTDIR\asm.bat" w
  FileWrite $FH '"$INSTDIR\java\bin\java" -cp "$INSTDIR\asm.jar;$INSTDIR\lib\charting-0.94.jar;$INSTDIR\lib\mysql.jar;$INSTDIR\lib\postgresql.jar;$INSTDIR\lib\hsqldb.jar" net.sourceforge.sheltermanager.asm.startup.Startup'
  FileClose $FH

  ; Command line interface
  FileOpen $FH "$INSTDIR\asmcmd.bat" w
  FileWrite $FH '@echo off$\r$\n"$INSTDIR\java\bin\java" -cp "$INSTDIR\asm.jar;$INSTDIR\lib\charting-0.94.jar;$INSTDIR\lib\mysql.jar;$INSTDIR\lib\postgresql.jar;$INSTDIR\lib\hsqldb.jar" net.sourceforge.sheltermanager.asm.script.Startup %1 %2 %3 %4 %5 %6 %7 %8 %9'
  FileClose $FH

  ; Write the uninstaller, since the installer always insists on
  ; installing a client
  WriteUninstaller $INSTDIR\uninst-asm.exe

  ; Create shortcut to main program on start menu
  CreateDirectory "$SMPROGRAMS\Animal Shelter Manager"
  
  ; ======== START MENU ===============

  ; Start application
  CreateShortCut "$SMPROGRAMS\Animal Shelter Manager\ASM.lnk" "$INSTDIR\java\bin\javaw.exe" '-cp "$INSTDIR\asm.jar" net.sourceforge.sheltermanager.asm.startup.WindowsBoot "$INSTDIR"' "$INSTDIR\asm.ico"

  ; HSQL Database manager for those using local databases
  CreateShortCut "$SMPROGRAMS\Animal Shelter Manager\ASM Local Database Manager.lnk" "$INSTDIR\java\bin\javaw.exe" '-cp "$INSTDIR\asm.jar" net.sourceforge.sheltermanager.asm.startup.HSQLManager "$INSTDIR"'

  ; HSQL Server for sharing a local database
  CreateShortCut "$SMPROGRAMS\Animal Shelter Manager\ASM Local Database Server.lnk" "$INSTDIR\java\bin\java.exe" '-cp "$INSTDIR\lib\hsqldb.jar" org.hsqldb.Server -database.0 "file:$PROFILE\.asm\localdb" -dbname.0 asm'

   ; Shortcut to uninstaller
  CreateShortCut "$SMPROGRAMS\Animal Shelter Manager\Uninstall.lnk" "$INSTDIR\uninst-asm.exe" ""
  

  ; ============= DESKTOP ===============
  
  ; Start application
  CreateShortCut "$DESKTOP\Animal Shelter Manager.lnk" "$INSTDIR\java\bin\javaw.exe" '-cp "$INSTDIR\asm.jar" net.sourceforge.sheltermanager.asm.startup.WindowsBoot "$INSTDIR"' "$INSTDIR\asm.ico"

  ; ======= INSTALL DIRECTORY ===========

  ; Start application
  CreateShortCut "$INSTDIR\Animal Shelter Manager.lnk" "$INSTDIR\java\bin\javaw.exe" '-cp "$INSTDIR\asm.jar" net.sourceforge.sheltermanager.asm.startup.WindowsBoot "$INSTDIR"' "$INSTDIR\asm.ico"

  ; HSQL Database manager for those using local databases
  CreateShortCut "$INSTDIR\ASM Local Database Manager.lnk" "$INSTDIR\java\bin\javaw.exe" '-cp "$INSTDIR\asm.jar" net.sourceforge.sheltermanager.asm.startup.HSQLManager "$INSTDIR"'

  ; HSQL Server for sharing a local database
  CreateShortCut "$INSTDIR\ASM Local Database Server.lnk" "$INSTDIR\java\bin\java.exe" '-cp "$INSTDIR\lib\hsqldb.jar" org.hsqldb.Server -database.0 "file:$PROFILE\.asm\localdb" -dbname.0 asm'

  ; Start application, but use WindowsBootTrans for people doing
  ; translations and want to use install directory as the classpath
  ; instead of asm.jar
  CreateShortCut "$INSTDIR\Animal Shelter Manager (Translation Test).lnk" "$INSTDIR\java\bin\javaw.exe" '-cp "$INSTDIR\asm.jar" net.sourceforge.sheltermanager.asm.startup.WindowsBootTrans "$INSTDIR"' "$INSTDIR\asm.ico"

  ; Friendly shortcut for windows users to unpack the jar file if they
  ; want to do a translation the old fashioned way
  CreateShortCut "$INSTDIR\Unpack asm jar file.lnk" "$INSTDIR\data\unzip.exe" '-d "$INSTDIR" "$INSTDIR\asm.jar"' ""

  SetDetailsPrint textonly
  DetailPrint "Installation Complete."
  SetDetailsPrint listonly

SectionEnd


;--------------------------------
;Uninstaller Section

Section Uninstall

  SetDetailsPrint textonly
  DetailPrint "Deleting Registry Keys..."
  SetDetailsPrint listonly

  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\ASM"
  DeleteRegKey HKLM "Software\ASM"

  SetDetailsPrint textonly
  DetailPrint "Deleting Files..."
  SetDetailsPrint listonly

  Delete "$DESKTOP\Animal Shelter Manager.lnk"
  RMDir /r "$SMPROGRAMS\Animal Shelter Manager"
  RMDir /r "$INSTDIR"

  SetDetailsPrint both

SectionEnd
