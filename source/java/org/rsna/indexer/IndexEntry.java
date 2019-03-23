package org.rsna.indexer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.rsna.ctp.objects.DicomObject;
import org.rsna.ctp.stdstages.dicom.DicomStorageSCU;
import org.rsna.ui.*;
import org.rsna.util.*;

public class IndexEntry implements ActionListener, Serializable {

	static final Color bgColor = new Color(0xc6d8f9);
	
	File file;
	String transferSyntax;
	String transferSyntaxName;
	String numberOfFrames;
	String rows;
	String columns;
	String photometricInterpretation;
	String planarConfiguration;
	String modality;
	String pixelPresentation;
	String pixelRepresentation;

	String margin = "    ";

	public IndexEntry(DicomObject dob) {
		this.file = dob.getFile();
		this.transferSyntax = dob.getTransferSyntaxUID();
		this.transferSyntaxName = dob.getTransferSyntaxName();
		this.numberOfFrames = Integer.toString(dob.getNumberOfFrames());
		this.rows = Integer.toString(dob.getRows());
		this.columns = Integer.toString(dob.getColumns());
		this.photometricInterpretation = dob.getPhotometricInterpretation();
		this.planarConfiguration = Integer.toString(dob.getPlanarConfiguration());
		this.modality = dob.getModality();
		this.pixelPresentation = dob.getElementValue("PixelPresentation");
		this.pixelRepresentation = dob.getElementValue("PixelRepresentation");
	}

	public boolean matches(
						String fileName,
						String transferSyntax, 
						String photometricInterpretation, 
						String planarConfiguration, 
						String modality) {
							
		if (!fileName.equals("")) {
			String name = this.file.getAbsolutePath();
			if (!name.startsWith(fileName) && !name.endsWith(fileName)) return false;
		}
		
		if (!transferSyntax.equals("") && !this.transferSyntax.endsWith(transferSyntax)) return false;
		if (!photometricInterpretation.equals("") && !this.photometricInterpretation.startsWith(photometricInterpretation)) return false;
		if (!planarConfiguration.equals("") && !this.planarConfiguration.equals(planarConfiguration)) return false;
		if (!modality.equals("") && !this.modality.equals(modality)) return false;
		return true;
	}
	
    public void actionPerformed(ActionEvent event) {
		Object c = event.getSource();
		if (c instanceof JButton) {
			JButton jb = (JButton)c;
			String jbText = jb.getText();
			if (jbText.equals("Binary")) {
				Runner runner = new Runner(file.getAbsolutePath());
				runner.start();
			}
			else if (jbText.equals("Export")) {
				Configuration config = Configuration.getInstance();
				String scp = config.getProperty("scp", "dicom://DEST:SRC@192.168.0.225:104");
				DicomStorageSCU scu = new DicomStorageSCU(scp, 0, true, 0, 0, 0, 0);
				String result = scu.send(file).toString();
				StatusPane.getInstance().setText(file.getAbsolutePath() + " >" + result);
			}
		}
	}
	
	class Runner extends Thread {
		String name;
		String program;
		File programFile = null;;
		File programDir = null;
		public Runner(String name) {
			super("BinaryDump Runner");
			this.name = name;
			Configuration config = Configuration.getInstance();
			program = config.getProperty("binary", "D:/Development/Tools/BinaryDump/products/BinaryDump.jar");
			programFile = (new File(program)).getAbsoluteFile();
			if (programFile.exists()) programDir = programFile.getParentFile();
		}
		public void run() {
			try {
				Runtime rt = Runtime.getRuntime();
				ArrayList<String> command = new ArrayList<String>();
				command.add("java");
				command.add("-jar");
				command.add(program);
				command.add("\"" + name + "\"");
				String[] cmdarray = command.toArray( new String[command.size()] );

				StringBuffer sb = new StringBuffer();
				for (String s : cmdarray) {
					sb.append(s + " ");
				}
				StatusPane.getInstance().setText(sb.toString());

				Process proc = rt.exec(cmdarray, null, programDir);
				int exitVal = proc.waitFor();
			}
			catch (Exception ex) { ex.printStackTrace(); }
		}
	}
	
	public JPanel toPanel() {
		//make the button panel
		JPanel left = new JPanel();
		left.setBackground(Color.white);
		left.setLayout(new RowLayout());
		JButton export = new JButton("Export");
		export.addActionListener(this);
		left.add(export);
		left.add(RowLayout.crlf());
		JButton binary = new JButton("Binary");
		binary.addActionListener(this);
		left.add(binary);
		left.add(RowLayout.crlf());
		
		//make the text component
		JPanel right = new JPanel();
		right.setBackground(Color.white);
		right.setLayout(new RowLayout());
		JTextPane jtp = new JTextPane();
		jtp.setFont(new Font("Monospaced",Font.PLAIN,12));
		jtp.setText(toString());
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

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(file.getAbsolutePath() + "\n");
		sb.append(margin + "TransferSyntax:            " + transferSyntax + "\n");
		sb.append(margin + "TransferSyntaxName         " + transferSyntaxName + "\n");
		sb.append(margin + "NumberOfFrames:            " + numberOfFrames + "\n");
		sb.append(margin + "Rows:                      " + rows + "\n");
		sb.append(margin + "Columns:                   " + columns + "\n");
		sb.append(margin + "PixelPresentation:         " + pixelPresentation + "\n");
		sb.append(margin + "PixelRepresentation:       " + pixelRepresentation + "\n");
		sb.append(margin + "PhotometricInterpretation: " + photometricInterpretation + "\n");
		sb.append(margin + "PlanarConfiguration:       " + planarConfiguration + "\n");
		sb.append(margin + "Modality:                  " + modality + "\n");
		return sb.toString();
	}
}
