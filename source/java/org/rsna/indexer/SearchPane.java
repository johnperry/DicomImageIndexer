package org.rsna.indexer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.nio.charset.Charset;
import javax.swing.*;
import org.rsna.ui.*;
import org.rsna.util.*;

public class SearchPane extends JPanel implements ActionListener {

	static final Charset utf8 = Charset.forName("UTF-8");
	static final Color bgColor = new Color(0xc6d8f9);
	static SearchPane instance = null;
	
	SearchFormPanel searchFormPanel;
	CTextField fileName;
	CTextField transferSyntax;
	CTextField photometricInterpretation;
	CTextField planarConfiguration;
	CTextField pixelRepresentation;
	CTextField pixelPresentation;
	CTextField modality;
	CButton search;
	
	public static SearchPane getInstance() {
		if (instance == null) {
			instance = new SearchPane();
		}
		return instance;
	}

    protected SearchPane() {
		super();
		setLayout(new BorderLayout());
		add(getSearchForm(), BorderLayout.CENTER);
	}

	public JComponent getSearchForm() {
		JScrollPane jsp = new JScrollPane();
		SearchFormPanel sfp = new SearchFormPanel();
		sfp.addRow( "Search");
		fileName = sfp.addRow( "File Name:", "*" );
		transferSyntax = sfp.addRow( "Transfer Syntax UID:", "*" );
		photometricInterpretation = sfp.addRow( "Photometric Interpretation:", "*" );
		planarConfiguration = sfp.addRow( "Planar Configuration:", "*" );
		pixelRepresentation = sfp.addRow( "Pixel Representation:", "*" );
		pixelPresentation = sfp.addRow( "Pixel Presentation:", "*" );
		modality = sfp.addRow( "Modality:", "*" );
		search = new CButton("Search");
		sfp.addRow(search);
		search.addActionListener(this);
		jsp.setViewportView(sfp);
		return jsp;
	}

    public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(search)) {
			IndexPane indexPane = IndexPane.getInstance();
			ResultsPane resultsPane = ResultsPane.getInstance();
			Component c = this.getParent();
			if (c instanceof JTabbedPane) {
				JTabbedPane jtp = (JTabbedPane)c;
				jtp.setSelectedComponent(resultsPane);
			}
			resultsPane.showResults( 
				indexPane.search (
							fileName.getText().trim(),
							transferSyntax.getText().trim(),
							photometricInterpretation.getText().trim(),
							planarConfiguration.getText().trim(),
							pixelRepresentation.getText().trim(),
							pixelPresentation.getText().trim(),
							modality.getText().trim()
				)
			);
		}
	}

	class SearchFormPanel extends JPanel {
		public SearchFormPanel() {
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

		public CTextField addRow( String s, int v ) {
			add( new CLabel(s) );
			CTextField tf = new CTextField(Integer.toString(v));
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
			super(80);
			setText(s);
			setFont( new Font( "Monospaced", Font.PLAIN, 12 ) );
			setAlignmentX(0.0f);
		}
	}

	class CCheckBox extends JCheckBox {
		public CCheckBox(Color bgColor) {
			super();
			setBackground(bgColor);
		}
	}

	class CButton extends JButton {
		public CButton(String s) {
			super(s);
		}
	}

}
