# Generates the manual in PDF and HTML formats

LANG="en"

all: clean build 

latex:
	@echo "    [latex-$(LANG)]"
	@echo "         asm.tex"
	@lyx asm.lyx -e latex 2> /dev/null

pdf:
	@echo "    [pdf-$(LANG)]"
	@echo "        asm_$(LANG).pdf"
	@lyx asm.lyx -e pdf2 2> /dev/null
	@mv asm.pdf asm_$(LANG).pdf

cover:
	@echo "        cover.png cover.jpg"
	@inkscape -h 3450 -w 2700 -b ffffffff -e cover.png cover.svg 2> /dev/null
	@inkscape -h 3450 -w 2700 -b ffffffff -e cover_back.png cover_back.svg 2> /dev/null
	@convert cover.png -resize 2700x3450\! cover.jpg
	@convert cover_back.png -resize 2700x3450\! cover_back.jpg

html:	latex
	@echo "    [html-$(LANG)]"
	@echo "        latex2html"
	@latex2html -local_icons -title "Animal Shelter Manager Manual" -address http://sheltermanager.sf.net asm.tex
	@rm -f asm/index.html
	@echo "     fix_html_size_tags.sh"
	@sh fix_html_size_tags.sh
	@mv asm help_$(LANG)

build: latex pdf html
	@echo "    [build-$(LANG)]"
	@echo "        zip manual_html_$(LANG).zip"	
	@zip -r9 manual_html_$(LANG).zip help* > /dev/null
	@echo "        zip manual_pdf_$(LANG).zip"
	@zip -r9 manual_pdf_$(LANG).zip asm*.pdf > /dev/null
	@echo "        mkdir build"
	@mkdir -p build
	@rm -rf build/*
	@echo "        cp *.pdf *.zip build"
	@cp *.pdf *.zip build

dist:	build
	@echo "    [dist-$(LANG)]"
	@echo "        cp build/* ../../../build"
	@cp build/* ../../../build

clean:
	@echo "    [clean-$(LANG)]"
	@echo "        rm cover*jpg cover*png *.eps *.zip *.pdf *.tex help_$(LANG)"
	@rm -rf cover*jpg cover*png *.eps *.zip *.pdf *.tex help* build

