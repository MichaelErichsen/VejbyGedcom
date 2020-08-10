/**
 * 
 */
package net.myerichsen.vejby.ui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import net.myerichsen.vejby.census.Table;

/**
 * @author michael
 *
 */
public class VejbyGedcom {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private JFrame frmVejbyGedcomIndtastning;
	private JTable censusJtable;

	// private String[] headers;

	private Table censusTable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VejbyGedcom window = new VejbyGedcom();
					window.frmVejbyGedcomIndtastning.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VejbyGedcom() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmVejbyGedcomIndtastning = new JFrame();
		frmVejbyGedcomIndtastning.setTitle("Vejby GEDCOM indtastning");
		frmVejbyGedcomIndtastning.setBounds(100, 100, 1080, 720);
		frmVejbyGedcomIndtastning.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmVejbyGedcomIndtastning.setJMenuBar(menuBar);

		JMenu mnFiler = new JMenu("Filer");
		menuBar.add(mnFiler);

		JMenuItem mntmbenKipFil = new JMenuItem("\u00C5ben KIP fil (.csv)...");
		mntmbenKipFil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openKipFile();
			}
		});
		mnFiler.add(mntmbenKipFil);

		JMenuItem mntmLuk = new JMenuItem("Luk");
		mntmLuk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFiler.add(mntmLuk);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		frmVejbyGedcomIndtastning.getContentPane().setLayout(gridBagLayout);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		frmVejbyGedcomIndtastning.getContentPane().add(tabbedPane, gbc_tabbedPane);

		JPanel censuspanel = new JPanel();
		tabbedPane.addTab("Folket\u00E6llinger", null, censuspanel, null);
		censuspanel.setLayout(new GridLayout(0, 1, 0, 0));

		JScrollPane censusscrollPane = new JScrollPane();
		censuspanel.add(censusscrollPane);

		censusJtable = new JTable();
		censusscrollPane.setViewportView(censusJtable);

		JPanel baptismpanel = new JPanel();
		tabbedPane.addTab("Kirkeb\u00F8ger (D\u00E5b)", null, baptismpanel, null);

	}

	/**
	 * 
	 */
	protected void openKipFile() {
		FileFilter ff = new FileNameExtensionFilter("KIP fil", "csv");
		JFileChooser kipChooser = new JFileChooser(
				"C://Users//michael//Documents//The Master Genealogist v9//Kilder//DDD");

		kipChooser.setFileFilter(ff);

		int returnValue = kipChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File kipFile = kipChooser.getSelectedFile();
			LOGGER.log(Level.INFO, kipFile.getAbsolutePath());

			String[] headers;
			try {
				FileInputStream fis = new FileInputStream(kipFile);
				Scanner sc = new Scanner(fis);

				// First line are headers
				headers = sc.nextLine().split(";");
				int index = -1;

				for (int i = 0; i < headers.length; i++) {
					if (headers[i].contains("FTår")) {
						index = i;
						break;
					}
				}

				if (index == -1) {
					LOGGER.log(Level.SEVERE, "FTår ikke fundet som kolonnenavn");
				}

				// Second line is data
				String[] ftData = sc.nextLine().split(";");
				String ftYear = ftData[index].substring(3);
				LOGGER.log(Level.INFO, ftYear);
				int year = Integer.parseInt(ftYear);

				String kipFileName = kipFile.getAbsolutePath();
				String message;
				censusTable = new Table(year, kipFileName);
				message = censusTable.readKipfile();
				LOGGER.log(Level.INFO, message);
				message = censusTable.removeEmptyColumns();
				LOGGER.log(Level.INFO, message);
				sc.close();
				fis.close();
			} catch (FileNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}

			String[][] censusArray = new String[censusTable.getPersons().size()][censusTable.getHeaders().size()];
			List<List<String>> lls = censusTable.getPersons();
			List<String> list;

			for (int i = 0; i < lls.size(); i++) {
				list = lls.get(i);

				for (int j = 0; j < list.size(); j++) {
					LOGGER.log(Level.FINE, "i " + i + " j " + j + " " + list.get(j));
					censusArray[i][j] = list.get(j);
				}
			}

			List<String> cth = censusTable.getHeaders();
			String[] headerArray = new String[cth.size()];
			for (int i = 0; i < cth.size(); i++) {
				headerArray[i] = cth.get(i);
			}

			DefaultTableModel defaultTableModel = new DefaultTableModel(censusArray, headerArray);
			censusJtable.setModel(defaultTableModel);
			censusJtable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		}
	}

}
