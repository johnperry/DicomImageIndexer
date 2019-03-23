package org.rsna.indexer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.rsna.ctp.pipeline.Status;
import org.rsna.ctp.objects.DicomObject;
import org.rsna.ctp.stdstages.dicom.DicomStorageSCU;
import org.rsna.ui.*;
import org.rsna.util.*;

import jdbm.btree.BTree;
import jdbm.helper.FastIterator;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.htree.HTree;
import jdbm.RecordManager;

public class IndexPane extends JPanel implements ActionListener {

	static IndexPane instance = null;
	static final Color bgColor = new Color(0xc6d8f9);
	
	LinkedList<IndexEntry> index = new LinkedList<IndexEntry>();
	Hashtable<IndexEntryType,Integer> types = new Hashtable<IndexEntryType,Integer>();
	private RecordManager recman;
	static final String databaseName = "IndexDB";
	private BTree indexDB = null;
	
	JPanel center;
	boolean running = false;
	JButton startStop;
	JButton exportTypes;
	ColorPane cp;

	public static IndexPane getInstance() {
		if (instance == null) {
			instance = new IndexPane();
		}
		return instance;
	}
	
	protected IndexPane() {
		super();
		setLayout( new BorderLayout() );

		center = new JPanel();
		center.setLayout( new BorderLayout() );
		center.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		cp = new ColorPane();
		center.add(cp);
		JScrollPane jsp = new JScrollPane();
		jsp.setViewportView(center);
		jsp.getVerticalScrollBar().setUnitIncrement(30);
		add(jsp, BorderLayout.CENTER);

		JPanel footer = new JPanel();
		startStop = new JButton("Start");
		startStop.addActionListener(this);
		exportTypes = new JButton("Export Types");
		exportTypes.addActionListener(this);
		footer.add(startStop);
		footer.add(Box.createHorizontalStrut(20));
		footer.add(exportTypes);
		add(footer, BorderLayout.SOUTH);
		
		open();
	}
	
	private void open() {
		try {
			File dbFile = new File(databaseName);
			recman = JdbmUtil.getRecordManager(dbFile.getAbsolutePath());
			indexDB = JdbmUtil.getBTree(recman, "indexDB");
		}
		catch (Exception unable) { unable.printStackTrace(); }
	}
	
	public void close() {
		if (recman != null) {
			try { recman.commit(); recman.close(); recman = null; }
			catch (Exception ignore) { }
		}
	}
	
	public void delete() {
		close();
		File db = new File(databaseName + "db");
		File lg = new File(databaseName + "lg");
		db.delete();
		lg.delete();
	}
	
	public LinkedList<IndexEntry> getIndex() {
		return index;
	}
	
	
	public IndexEntryType[] getTypes() {
		IndexEntryType[] ta = types.keySet().toArray(new IndexEntryType[types.size()]);
		Arrays.sort(ta);
		return ta;
	}
		
	public LinkedList<IndexEntry> search(IndexEntryType iet) {
		return search(
				"",
				iet.transferSyntax, 
				iet.photometricInterpretation, 
				iet.planarConfiguration, 
				iet.modality);
	}
		
	public LinkedList<IndexEntry> search(
				String fileName,
				String transferSyntax,
				String photometricInterpretation,
				String planarConfiguration,
				String modality) {
		StatusPane.getInstance().setText("Searching...");
		LinkedList<IndexEntry> results = new LinkedList<IndexEntry>();
		for (IndexEntry entry : index) {
			if (entry.matches(fileName, transferSyntax, photometricInterpretation, planarConfiguration, modality)) {
				results.add(entry);
			}
		}
		StatusPane.getInstance().setText(results.size() + " images found.");
		return results;
	}

    public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(startStop)) {
			if (startStop.getText().equals("Start")) {
				buildIndex();
			}
			else running = false;
		}
		else if (event.getSource().equals(exportTypes)) {
			Runnable r = new Runnable() {
				public void run() {
					int count = 0;
					int ok = 0;
					int retry = 0;
					int fail = 0;
					StatusPane sp = StatusPane.getInstance();
					Configuration config = Configuration.getInstance();
					String scp = config.getProperty("scp", "dicom://DEST:SRC@192.168.0.225:104");
					DicomStorageSCU scu = new DicomStorageSCU(scp, 0, true, 0, 0, 0, 0);
					for (IndexEntryType iet : getTypes()) {
						LinkedList<IndexEntry> list = search(iet);
						int n = list.size()/2;
						File file = list.get(n).file;
						Status result = scu.send(file);
						sp.setText(file.getAbsolutePath() + " > " + result.toString());
						count++;
						if (result.equals(Status.OK)) ok++;
						else if (result.equals(Status.FAIL)) {
							fail++;
							cp.println(result.toString() + ": " + file);
						}
						else if (result.equals(Status.RETRY)) {
							retry++;
							cp.println(result.toString() + ": " + file);
						}
					}
					sp.setText(ok + " of " + count + " files sent successfully (fail = "+fail+"; retry = "+retry+")");
				}
			};
			(new Thread(r)).start();
		}
	}
	
	public void load() {
		if (indexDB.size() == 0) {
			buildIndex();
		}
		else {
			int count = 0;
			index = new LinkedList<IndexEntry>();
			types = new Hashtable<IndexEntryType,Integer>();
			try {
				TupleBrowser tb = indexDB.browse();
				Tuple tuple = new Tuple();
				while (tb.getNext(tuple)) {
					IndexEntry entry = (IndexEntry)tuple.getValue();
					index.add(entry);
					updateTypes(entry);
					count++;
				}
			}
			catch (Exception unable) { unable.printStackTrace(); }
			cp.print(listTypes());
			cp.println(count + " images loaded");
			StatusPane.getInstance().setText(count + " images loaded");
		}
	}			
	
	public void buildIndex() {
		close();
		delete();
		open();
		cp.clear();
		Walker walker = new Walker();
		walker.start();
	}
	
	class Walker extends Thread {
		Configuration config = Configuration.getInstance();
		String dirname = config.getProperty("dir", "/Data");
		File dir = new File(dirname);
		int count = 0;
		StringBuffer sb = new StringBuffer();
		
		public Walker() {
			super();
		}
		
		public void run() {
			index = new LinkedList<IndexEntry>();
			types = new Hashtable<IndexEntryType,Integer>();
			running = true;
			startStop.setText("Stop");
			walk(dir);
			sb.append(listTypes());
			sb.append(count + " files.\n");
			sb.append("Done.\n");
			cp.setText(sb.toString());
			running = false;
			startStop.setText("Start");
		}
		
		private void walk(File file) {
			if (!running) return;
			if (file.isFile()) {
				try {
					DicomObject dob = new DicomObject(file);
					IndexEntry entry = new IndexEntry(dob);
					index.add(entry);
					indexDB.insert(entry.file.getAbsolutePath(), entry, true);
					updateTypes(entry);
					count++;
					StatusPane.getInstance().setText(count+": "+file.getAbsolutePath());
				}
				catch (Exception skip) { }
				yield();
			}
			else if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File f : files) {
					if (f.isFile()) {
						walk(f);
					}
				}
				for (File f : files) {
					if (f.isDirectory()) {
						walk(f);
					}
				}
			}
		}
	}

	private void updateTypes(IndexEntry entry) {	
		IndexEntryType iet = new IndexEntryType(entry);
		Integer i = types.get(iet);
		if (i == null) i = new Integer(1);
		else i = new Integer( i.intValue() + 1 );
		types.put(iet, i);
	}
	
	private String listTypes() {
		StringBuffer sb = new StringBuffer();
		IndexEntryType[] ta = types.keySet().toArray(new IndexEntryType[types.size()]);
		Arrays.sort(ta);
		for (IndexEntryType iet : ta) {
			int n = types.get(iet).intValue();
			sb.append(n + ":\n"+iet.toString());
		}
		sb.append("\nIndexEntryTypes table size = "+types.size()+"\n");
		return sb.toString();
	}
}
