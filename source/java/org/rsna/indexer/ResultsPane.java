package org.rsna.indexer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.rsna.ctp.objects.DicomObject;
import org.rsna.ui.*;
import org.rsna.util.*;

public class ResultsPane extends JPanel {

	static ResultsPane instance = null;
	static final Color bgColor = new Color(0xc6d8f9);
	
	JScrollPane jsp;

	public static ResultsPane getInstance() {
		if (instance == null) {
			instance = new ResultsPane();
		}
		return instance;
	}
	
	protected ResultsPane() {
		super();
		setLayout( new BorderLayout() );

		JPanel center = new JPanel();
		center.setLayout( new BorderLayout() );
		center.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		jsp = new JScrollPane();
		jsp.getVerticalScrollBar().setUnitIncrement(30);
		add(jsp, BorderLayout.CENTER);
	}
	
	public void showResults( LinkedList<IndexEntry> results ) {
		JPanel jp = new JPanel();
		jp.setLayout(new RowLayout());
		jp.setBackground(Color.white);
		jsp.setViewportView(jp);
		for (IndexEntry entry : results) {
			jp.add(entry.toPanel());
			jp.add(RowLayout.crlf());
		}

	}
}
