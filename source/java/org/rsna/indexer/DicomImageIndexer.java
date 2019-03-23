package org.rsna.indexer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.nio.charset.Charset;
import org.apache.log4j.*;
import org.rsna.ui.*;
import org.rsna.util.*;

public class DicomImageIndexer extends JFrame {

    static final String windowTitle = "DICOM Image Indexer";
	static final Color bgColor = new Color(0xc6d8f9);

	JTabbedPane jtp;
	JScrollPane jsp;
	IndexPane indexPane;
    SearchPane searchPane;
    ResultsPane resultsPane;
    PropertiesPane propertiesPane;
    ColorPane text;
    StatusPane status;

    public static void main(String args[]) {
		Logger.getRootLogger().addAppender(
				new ConsoleAppender(
					new PatternLayout("%d{HH:mm:ss} %-5p [%c{1}] %m%n")));
		Logger.getRootLogger().setLevel(Level.INFO);
        new DicomImageIndexer();
    }

    public DicomImageIndexer() {
		super();
		setTitle(windowTitle);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(bgColor);
		getContentPane().add(panel,BorderLayout.CENTER);

		indexPane = IndexPane.getInstance();
		searchPane = SearchPane.getInstance();
		resultsPane = ResultsPane.getInstance();
		propertiesPane = PropertiesPane.getInstance();

		//Make a footer bar to display status.
		status = StatusPane.getInstance(" ", bgColor);
		panel.add(status, BorderLayout.SOUTH);

		//Make the tabbed pane for the center
		jtp = new JTabbedPane();
		panel.add(jtp, BorderLayout.CENTER);
		
		//Put in the index pane
		jtp.add("Index", indexPane);

		//Put in the search pane
		jtp.add("Search", searchPane);

		//Put in the results pane
		jtp.add("Results", resultsPane);
		
		//Put in the props pane
		jtp.add("Properties", propertiesPane);
		
		//Select the search pane
		jtp.setSelectedComponent(searchPane);

        addWindowListener(new WindowCloser(this));

        pack();
        positionFrame();
        setVisible(true);
        
        //Start building the index
        indexPane.load();
	}

    class WindowCloser extends WindowAdapter {
		JFrame parent;
		public WindowCloser(JFrame parent) {
			this.parent = parent;
		}
		public void windowClosing(WindowEvent evt) {
			indexPane.close();
			Configuration config = Configuration.getInstance();
			Point p = getLocation();
			config.setProperty("x", Integer.toString(p.x));
			config.setProperty("y", Integer.toString(p.y));
			Toolkit t = getToolkit();
			Dimension d = parent.getSize ();
			config.setProperty("w", Integer.toString(d.width));
			config.setProperty("h", Integer.toString(d.height));
			config.store();
			System.exit(0);
		}
    }

	private void positionFrame() {
		Configuration config = Configuration.getInstance();
		int x = StringUtil.getInt( config.getProperty("x"), 0 );
		int y = StringUtil.getInt( config.getProperty("y"), 0 );
		int w = StringUtil.getInt( config.getProperty("w"), 0 );
		int h = StringUtil.getInt( config.getProperty("h"), 0 );
		boolean noProps = ((w == 0) || (h == 0));
		int wmin = 550;
		int hmin = 600;
		if ((w < wmin) || (h < hmin)) {
			w = wmin;
			h = hmin;
		}
		if ( noProps || !screensCanShow(x, y) || !screensCanShow(x+w-1, y+h-1) ) {
			Toolkit t = getToolkit();
			Dimension scr = t.getScreenSize ();
			x = (scr.width - wmin)/2;
			y = (scr.height - hmin)/2;
			w = wmin;
			h = hmin;
		}
		setSize( w, h );
		setLocation( x, y );
	}

	private boolean screensCanShow(int x, int y) {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = env.getScreenDevices();
		for (GraphicsDevice screen : screens) {
			GraphicsConfiguration[] configs = screen.getConfigurations();
			for (GraphicsConfiguration gc : configs) {
				if (gc.getBounds().contains(x, y)) return true;
			}
		}
		return false;
	}

}
