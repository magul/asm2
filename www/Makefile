all:	tags compile

tags:
	@echo "[tags] ============================"
	ctags -f tags *.py

compile:
	@echo "[compile] ============================"
	pychecker *.py

smcom: 
	@echo "[smcom] ========================="
	scp *.py *.psp root@rawsoaa2.miniserver.com:/var/www/sheltermanager.com/asm/

