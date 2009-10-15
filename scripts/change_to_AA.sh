for F in `grep -rl 'javax.swing.JLabel"' uk`; do
	cat $F | sed 's/javax.swing.JLabel/uk.co.rtds.aaswing.AALabel/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JButton"' uk`; do
	cat $F | sed 's/javax.swing.JButton/uk.co.rtds.aaswing.AAButton/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JTextField"' uk`; do
	cat $F | sed 's/javax.swing.JTextField/uk.co.rtds.aaswing.AATextField/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JTextArea"' uk`; do
	cat $F | sed 's/javax.swing.JTextArea/uk.co.rtds.aaswing.AATextArea/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JEditorPane"' uk`; do
	cat $F | sed 's/javax.swing.JEditorPane/uk.co.rtds.aaswing.AAEditorPane/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JCheckBox"' uk`; do
	cat $F | sed 's/javax.swing.JCheckBox/uk.co.rtds.aaswing.AACheckBox/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JMenu"' uk`; do
	cat $F | sed 's/javax.swing.JMenu/uk.co.rtds.aaswing.AAMenu/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JMenuItem"' uk`; do
	cat $F | sed 's/javax.swing.JMenuItem/uk.co.rtds.aaswing.AAMenuItem/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JRadioButton"' uk`; do
	cat $F | sed 's/javax.swing.JRadioButton/uk.co.rtds.aaswing.AARadioButton/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JTabbedPane"' uk`; do
	cat $F | sed 's/javax.swing.JTabbedPane/uk.co.rtds.aaswing.AATabbedPane/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JProgressBar"' uk`; do
	cat $F | sed 's/javax.swing.JProgressBar/uk.co.rtds.aaswing.AAProgressBar/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JTable"' uk`; do
	cat $F | sed 's/javax.swing.JTable/uk.co.rtds.aaswing.AATable/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JComboBox"' uk`; do
	cat $F | sed 's/javax.swing.JComboBox/uk.co.rtds.aaswing.AAComboBox/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JToolBar"' uk`; do
	cat $F | sed 's/javax.swing.JToolBar/uk.co.rtds.aaswing.AAToolBar/g' > $F.tmp
	mv $F.tmp $F;
done

for F in `grep -rl 'javax.swing.JInternalFrame"' uk`; do
	cat $F | sed 's/javax.swing.JInternalFrame/uk.co.rtds.aaswing.AAInternalFrame/g' > $F.tmp
	mv $F.tmp $F;
done



