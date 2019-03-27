package org.rsna.indexer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.nio.charset.Charset;

public class IndexButton extends JButton {

	public IndexButton(String s) {
		super(s);
		setPreferredSize(new Dimension(80,25));
	}

}
