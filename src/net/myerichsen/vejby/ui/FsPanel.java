package net.myerichsen.vejby.ui;

import java.awt.LayoutManager;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @author Michael Erichsen
 * @version 11-09-2020
 *
 */
public class FsPanel extends JPanel {
	protected String[][] dataArray;
	protected String[] headerArray;
	protected JTable table;
	private static final long serialVersionUID = -4008194513738386085L;
	protected static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	protected Preferences prefs = Preferences.userRoot().node("net.myerichsen.vejby.gedcom");
	protected JButton saveButton;
	protected String fileNameStub;
	protected JButton eliminateButton;

	/**
	 * Constructor
	 *
	 */
	public FsPanel() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param layout
	 */
	public FsPanel(LayoutManager layout) {
		super(layout);
	}

	/**
	 * Constructor
	 *
	 * @param isDoubleBuffered
	 */
	public FsPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	/**
	 * Constructor
	 *
	 * @param layout
	 * @param isDoubleBuffered
	 */
	public FsPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	/**
	 * @param j
	 */
	private void clearRow(int i) {
		for (int j = 0; j < dataArray[i].length; j++) {
			dataArray[i][j] = "";
		}
	}

	/**
	 * Concatenate two arrays. Copied from
	 * https://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java
	 * 
	 * @param <T>
	 * @param a
	 * @param b
	 * @return
	 */
	protected <T> T[] concatenate(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	/**
	 * Eliminate duplicates in the data array.
	 * 
	 * For each row in the data array:
	 * 
	 * Compare all fields with all subsequent rows. If identical, then remove
	 * subsequent row.
	 */
	protected void eliminateDuplicates() {
		int deletions = 0;
		String stringI = "";
		String stringJ = "";

		for (int i = 0; i < dataArray.length; i++) {
			// LOGGER.log(Level.INFO, "R�kke: " + i + "; " + dataArray[i][0]);

			if ((dataArray[i][0] == null) || (dataArray[i][0].equals(""))) {
				continue;
			}
			stringI = listDataArray(i);

			for (int j = i + 1; j < dataArray.length; j++) {
				// LOGGER.log(Level.INFO, "R�kke: " + j + "; " + dataArray[j][0]);
				if ((dataArray[j][0] == null) || (dataArray[j][0].equals(""))) {
					continue;
				}
				stringJ = listDataArray(j);

				if (stringI.equals(stringJ)) {
					LOGGER.log(Level.FINE, "Fundet " + i + ": " + stringI);
					LOGGER.log(Level.FINE, "Fundet " + i + ": " + stringJ);
					clearRow(j);
					deletions++;
				}
			}

		}

		String[][] dataArray2 = new String[dataArray.length - deletions][8];

		int i2 = 0;
		for (String[] element : dataArray) {
			if ((element[0] != null) && (!element[0].equals(""))) {
				dataArray2[i2++] = element;
			}
		}

		LOGGER.log(Level.INFO, "Data array efter sletning af " + deletions + " r�kker: " + dataArray2.length);

		dataArray = dataArray2;

		DefaultTableModel model = new DefaultTableModel(dataArray, headerArray);
		table.setModel(model);
	}

	/**
	 * Fix some of the code page problems.
	 * 
	 * @param columns
	 * @param col
	 * @return
	 */
	protected String fixCodePage(String[] columns, int col) {
		String s;
		byte[] a;

		try {
			s = columns[col];
		} catch (Exception e1) {
			s = "";
		}

		try {
			a = s.getBytes("ISO-8859-1");
			s = new String(a, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return s;
	}

	/**
	 * @param i
	 * @return
	 */
	private String listDataArray(int i) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < dataArray[i].length; j++) {
			sb.append(dataArray[i][j] + ", ");
		}

		LOGGER.log(Level.FINE, "[" + i + "]: " + sb.toString());
		return sb.toString();
	}

}