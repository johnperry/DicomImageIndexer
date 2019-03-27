package org.rsna.indexer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.rsna.ctp.pipeline.Status;
import org.rsna.ctp.stdstages.dicom.DicomStorageSCU;
import org.rsna.ui.*;
import org.rsna.util.*;

public class IndexEntryType implements Comparable<IndexEntryType>, ActionListener {
	
	static String margin = "    ";

	String transferSyntax;
	String transferSyntaxName;
	String photometricInterpretation;
	String planarConfiguration;
	String modality;
	String pixelPresentation;
	String pixelRepresentation;
	int hash = 0;

	public IndexEntryType(IndexEntry e) {
		this.transferSyntax = e.transferSyntax;
		this.transferSyntaxName = e.transferSyntaxName;
		this.photometricInterpretation = e.photometricInterpretation;
		this.planarConfiguration = e.planarConfiguration;
		this.modality = e.modality;
		this.pixelPresentation = e.pixelPresentation;
		this.pixelRepresentation = e.pixelRepresentation;
		this.hash = getKey().hashCode();
	}

    public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof JButton) {
			JButton jb = (JButton)source;
			String jbText = jb.getText();
			if (jbText.equals("List")) {
				ResultsPane resultsPane = ResultsPane.getInstance();
				IndexPane indexPane = IndexPane.getInstance();
				Component c = indexPane.getParent();
				if (c instanceof JTabbedPane) {
					JTabbedPane jtp = (JTabbedPane)c;
					jtp.setSelectedComponent(resultsPane);
				}
				resultsPane.showResults(indexPane.search(this));
			}
			else if (jbText.equals("Export")) {
				IndexPane indexPane = IndexPane.getInstance();
				(new Sender(indexPane.search(this))).start();
			}
		}
	}
	
	class Sender extends Thread {
		int count = 0;
		int ok = 0;
		int retry = 0;
		int fail = 0;
		DicomStorageSCU scu;
		StatusPane sp;
		LinkedList<IndexEntry> entries;
		public Sender(LinkedList<IndexEntry> entries) {
			super();
			this.entries = entries;
		}
		public void run() {
			StatusPane sp = StatusPane.getInstance();
			Configuration config = Configuration.getInstance();
			String scp = config.getProperty("scp", "dicom://DEST:SRC@127.0.0.1:104");
			scu = new DicomStorageSCU(scp, 0, true, 0, 0, 0, 0);
			for (IndexEntry entry : entries) {
				send(entry.file);
			}
			sp.setText(ok + " of " + count + " files sent successfully (fail = "+fail+"; retry = "+retry+")");
		}
		private void send(File file) {
			Status result = scu.send(file);
			sp.setText(file.getAbsolutePath() + " > " + result.toString());
			count++;
			if (result.equals(Status.OK)) ok++;
			else if (result.equals(Status.FAIL)) {
				fail++;
			}
			else if (result.equals(Status.RETRY)) {
				retry++;
			}
		}
	}

	public String getKey() {
		return (transferSyntax 
					+ photometricInterpretation 
						+ planarConfiguration 
							+ pixelPresentation 
								+ pixelRepresentation
									+ modality);
	}
	
	public int hashCode() {
		return hash;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof IndexEntryType) {
			IndexEntryType iet = (IndexEntryType)obj;
			if (!transferSyntax.equals(iet.transferSyntax)) return false;
			if (!photometricInterpretation.equals(iet.photometricInterpretation)) return false;
			if (!planarConfiguration.equals(iet.planarConfiguration)) return false;
			if (!pixelPresentation.equals(iet.pixelPresentation)) return false;
			if (!pixelRepresentation.equals(iet.pixelRepresentation)) return false;
			if (!modality.equals(iet.modality)) return false;
			return true;
		}
		return false;
	}
	
	public int compareTo(IndexEntryType iet) {
		int c;
		if ( (c=transferSyntaxName.compareTo(iet.transferSyntaxName)) != 0 ) return c;
		if ( (c=photometricInterpretation.compareTo(iet.photometricInterpretation)) != 0 ) return c;
		if ( (c=planarConfiguration.compareTo(iet.planarConfiguration)) != 0 ) return c;
		if ( (c=pixelPresentation.compareTo(iet.pixelPresentation)) != 0 ) return c;
		if ( (c=pixelRepresentation.compareTo(iet.pixelRepresentation)) != 0 ) return c;
		if ( (c=modality.compareTo(iet.modality)) != 0 ) return c;
		return 0;
	}
	
	public JPanel toPanel(int n) {
		//make the button panel
		JPanel left = new JPanel();
		left.setBackground(Color.white);
		left.setLayout(new RowLayout());
		IndexButton export = new IndexButton("Export");
		export.addActionListener(this);
		left.add(export);
		left.add(RowLayout.crlf());
		IndexButton binary = new IndexButton("List");
		binary.addActionListener(this);
		left.add(binary);
		left.add(RowLayout.crlf());
		
		//make the text component
		JPanel right = new JPanel();
		right.setBackground(Color.white);
		right.setLayout(new RowLayout());
		JTextPane jtp = new JTextPane();
		jtp.setFont(new Font("Monospaced",Font.PLAIN,12));
		jtp.setText(toString(n));
		right.add(jtp);
		right.add(RowLayout.crlf());
		
		//put them together
		JPanel both = new JPanel();
		both.setBackground(Color.white);
		both.setLayout(new RowLayout());
		both.add(left);
		both.add(right);
		both.add(RowLayout.crlf());
		
		return both;
	}

	public String toString(int n) {
		StringBuffer sb = new StringBuffer();
		sb.append(margin + "Number of files:           " + n + "\n");
		sb.append(margin + "TransferSyntax:            " + transferSyntax + "\n");
		sb.append(margin + "TransferSyntaxName         " + transferSyntaxName + "\n");
		sb.append(margin + "PixelPresentation:         " + pixelPresentation + "\n");
		sb.append(margin + "PixelRepresentation:       " + pixelRepresentation + "\n");
		sb.append(margin + "PhotometricInterpretation: " + photometricInterpretation + "\n");
		sb.append(margin + "PlanarConfiguration:       " + planarConfiguration + "\n");
		sb.append(margin + "Modality:                  " + modality + "\n");
		return sb.toString();
	}
}
