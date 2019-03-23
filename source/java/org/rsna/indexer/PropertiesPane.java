package org.rsna.indexer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.nio.charset.Charset;
import javax.swing.*;
import org.rsna.ui.*;
import org.rsna.util.*;

public class PropertiesPane extends JPanel implements ActionListener {

	static final Charset utf8 = Charset.forName("UTF-8");
	static final Color bgColor = new Color(0xc6d8f9);
	static PropertiesPane instance = null;
	
	CTextField dir;
	CTextField scp;
	CTextField binary;
	CButton save;
	
	public static PropertiesPane getInstance() {
		if (instance == null) {
			instance = new PropertiesPane();
		}
		return instance;
	}

    protected PropertiesPane() {
		super();
		setLayout(new BorderLayout());
		add(getPropsForm(), BorderLayout.CENTER);
	}

	public JComponent getPropsForm() {
		JScrollPane jsp = new JScrollPane();
		PropsFormPanel pfp = new PropsFormPanel();
		pfp.addRow( "Properties");
		
		Configuration c = Configuration.getInstance();
		dir = pfp.addRow( "Directory:", c.getProperty("dir", "/Data") );
		scp = pfp.addRow( "DICOM SCP:", c.getProperty("scp", "dicom://DEST:SRC@192.168.0.225:104") );
		binary = pfp.addRow( "BinaryDump:", c.getProperty("binary", "D:/Development/Tools/BinaryDump/products/BinaryDump.jar") );

		save = new CButton("Save");
		pfp.addRow(save);
		save.addActionListener(this);
		jsp.setViewportView(pfp);
		return jsp;
	}

    public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(save)) {
			Configuration c = Configuration.getInstance();
			c.setProperty("dir", dir.getText());
			c.setProperty("scp", scp.getText());
			c.setProperty("binary", binary.getText());
			c.store();
 			StatusPane.getInstance().setText("Properties stored");
		}
	}

	class PropsFormPanel extends JPanel {
		public PropsFormPanel() {
			super();
			setLayout( new RowLayout() );
			setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			setBackground(bgColor);
		}
		public void addRow( String s ) {
			CLabel label = new CLabel(s);
			label.setFont( new Font( "SansSerif", Font.BOLD, 18 ) );
			label.setAlignmentY(1.0f);
			label.setAlignmentX(0.5f);
			add( label, RowLayout.span(2) );
			add( RowLayout.crlf() );
		}

		public CTextField addRow( String s, String v ) {
			add( new CLabel(s) );
			CTextField tf = new CTextField(v);
			add( tf );
			add( RowLayout.crlf() );
			return tf;
		}

		public void addRow( CButton b ) {
			add( b, RowLayout.span(2) );
			b.setAlignmentY(1.0f);
			b.setAlignmentX(0.5f);
			add( RowLayout.crlf() );
		}

		public int getIndexOf(Component c) {
			Component[] components = getComponents();
			for (int i=0; i< components.length; i++) {
				if (components[i].equals(c)) return i;
			}
			return -1;
		}
	}

	class CLabel extends JLabel {
		public CLabel(String s) {
			super(s);
		}
	}

	class CTextField extends JTextField {
		public CTextField(String s) {
			super(150);
			setText(s);
			setFont( new Font( "Monospaced", Font.PLAIN, 12 ) );
			setAlignmentX(0.0f);
		}
	}

	class CButton extends JButton {
		public CButton(String s) {
			super(s);
		}
	}

}
