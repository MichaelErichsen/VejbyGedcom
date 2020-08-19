package net.myerichsen.vejby.ui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * Main user interface for the Vejby Gedcom application. Implements tabbed
 * panels to do the work.
 * 
 * @author Michael Erichsen
 * @version 19. aug. 2020
 *
 */

public class VejbyGedcom {
	private JFrame frmVejbyGedcomIndtastning;
	private JTabbedPane tabbedPane;
	private CensusMappingJPanel censusMappingJPanel;
	private HouseholdJPanel householdJPanel;
	private CensusJPanel censusJPanel;

	/**
	 * @return the censusJPanel
	 */
	public CensusJPanel getCensusJPanel() {
		return censusJPanel;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
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
	 * @return the censusMappingJPanel
	 */
	public CensusMappingJPanel getCensusMappingJPanel() {
		return censusMappingJPanel;
	}

	/**
	 * @return the householdJPanel
	 */
	public HouseholdJPanel getHouseholdJPanel() {
		return householdJPanel;
	}

	/**
	 * @return the tabbedPane
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmVejbyGedcomIndtastning = new JFrame();
		frmVejbyGedcomIndtastning.setTitle("Vejby GEDCOM indtastning");
		frmVejbyGedcomIndtastning.setBounds(100, 100, 1080, 500);
		frmVejbyGedcomIndtastning.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmVejbyGedcomIndtastning.setJMenuBar(menuBar);

		JMenu mnFiler = new JMenu("Filer");
		menuBar.add(mnFiler);

		JMenuItem mntmLuk = new JMenuItem("Luk");
		mntmLuk.addActionListener(new ActionListener() {
			@Override
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

		setTabbedPane(new JTabbedPane(SwingConstants.TOP));
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		frmVejbyGedcomIndtastning.getContentPane().add(getTabbedPane(), gbc_tabbedPane);

		censusJPanel = new CensusJPanel(VejbyGedcom.this);
		getTabbedPane().addTab("Folket\u00E6llinger", null, censusJPanel, null);

		censusMappingJPanel = new CensusMappingJPanel(VejbyGedcom.this);
		getTabbedPane().addTab("Mapning af felter", null, censusMappingJPanel, null);
		getTabbedPane().setEnabledAt(1, false);

		householdJPanel = new HouseholdJPanel(VejbyGedcom.this);
		getTabbedPane().addTab("Husholdninger", null, householdJPanel, null);
		getTabbedPane().setEnabledAt(2, false);

		BaptismJpanel baptismJpanel = new BaptismJpanel();
		getTabbedPane().addTab("Kirkeb\u00F8ger (D\u00E5b)", null, baptismJpanel, null);
	}

	/**
	 * @param tabbedPane the tabbedPane to set
	 */
	public void setTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

}
