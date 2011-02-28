all:	tags

tags:
	@echo "[tags] ============================"
	ctags -f tags *.py

smcom: 
	@echo "[smcom] ========================="
	scp *.py root@rawsoaa2.miniserver.com:/var/www/sheltermanager.com/asm/

