package org.rsna.indexer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.nio.charset.Charset;

public class StatusPane extends JPanel {

	static StatusPane pane = null;
	JLabel status;

    public static StatusPane getInstance() {
		return pane;
	}

	public static StatusPane getInstance(String s, Color c) {
		if (pane == null) pane = new StatusPane(s, c);
		return pane;
	}

	protected StatusPane(String s, Color c) {
		super();
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBackground(c);
		status = new JLabel(s);
		this.add(status);
	}

	public void setText(String s) {
		if (SwingUtilities.isEventDispatchThread()) {
			status.setText(s);
		}
		else {
			final String ss = s;
			Runnable display = new Runnable() {
				public void run() {
					status.setText(ss);
				}
			};
			SwingUtilities.invokeLater(display);
		}
	}

}
