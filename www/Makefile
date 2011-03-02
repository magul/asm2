all:	tags compile clean

tags:
	@echo "[tags] ============================"
	ctags -f tags *.py

clean:
	@echo "[clean] ============================"
	rm *.pyc

compile:
	@echo "[compile] ============================"
	# 400 lines per method, 20 returns, 20 args, 60 locals
	pychecker -L 400 -R 20 -J 20 -K 60 *.py

smcom: 
	@echo "[smcom] ========================="
	scp *.py *.psp root@rawsoaa2.miniserver.com:/var/www/sheltermanager.com/asm/

pot:
	@echo "[template] ========================="
	xgettext --language=Python *.py *.psp -o po/asm.pot
