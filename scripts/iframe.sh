for F in `grep -rl 'javax.swing.JInternalFrame' uk`; do
	cat $F | sed 's/javax.swing.JInternalFrame/uk.co.rtds.aaswing.AAInternalFrame/g' > $F.tmp
	mv $F.tmp $F;
done



