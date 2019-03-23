package org.rsna.indexer;

public class IndexEntryType implements Comparable<IndexEntryType> {
	
	static String margin = "    ";

	String transferSyntax;
	String transferSyntaxName;
	String photometricInterpretation;
	String planarConfiguration;
	String modality;
	String pixelPresentation;
	String pixelRepresentation;
	String key;
	int hash = 0;

	public IndexEntryType(IndexEntry e) {
		this.transferSyntax = e.transferSyntax;
		this.transferSyntaxName = e.transferSyntaxName;
		this.photometricInterpretation = e.photometricInterpretation;
		this.planarConfiguration = e.planarConfiguration;
		this.modality = e.modality;
		this.pixelPresentation = e.pixelPresentation;
		this.pixelRepresentation = e.pixelRepresentation;
		this.key = getKey();
		this.hash = key.hashCode();
	}

	public String getKey() {
		return (transferSyntax 
					+ photometricInterpretation 
						+ planarConfiguration 
							+ pixelPresentation 
								+ pixelRepresentation
									+ modality);
	}
	
	public String getIndexKey() {
		return key;
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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
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
