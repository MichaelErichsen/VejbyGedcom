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
 * @version 09-09-2020
 *
 */

public class VejbyGedcom {
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

	private JFrame frmVejbyGedcomIndtastning;
	private JTabbedPane tabbedPane;

	private WelcomePanel welcomePanel;
	private CensusPanel censusJPanel;
	private CensusMappingPanel censusMappingJPanel;
	private HouseholdPanel householdJPanel;
	private BirthPanel birthPanel;
	private MarriagePanel marriagePanel;
	private BurialPanel burialPanel;

	/**
	 * Create the application.
	 */
	public VejbyGedcom() {
		initialize();
	}

	/**
	 * @return the censusJPanel
	 */
	public CensusPanel getCensusJPanel() {
		return censusJPanel;
	}

	/**
	 * @return the censusMappingJPanel
	 */
	public CensusMappingPanel getCensusMappingJPanel() {
		return censusMappingJPanel;
	}

	/**
	 * @return the householdJPanel
	 */
	public HouseholdPanel getHouseholdJPanel() {
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

		welcomePanel = new WelcomePanel();
		getTabbedPane().addTab("Vejby GEDCOM", null, welcomePanel, null);

		censusJPanel = new CensusPanel(VejbyGedcom.this);
		getTabbedPane().addTab("Folket\u00E6llinger", null, censusJPanel, null);

		censusMappingJPanel = new CensusMappingPanel(VejbyGedcom.this);
		getTabbedPane().addTab("Mapning af felter", null, censusMappingJPanel, null);
		getTabbedPane().setEnabledAt(2, false);

		householdJPanel = new HouseholdPanel(VejbyGedcom.this);
		getTabbedPane().addTab("Husholdninger", null, householdJPanel, null);
		getTabbedPane().setEnabledAt(3, false);

		birthPanel = new BirthPanel();
		getTabbedPane().addTab("Fødsler", null, birthPanel, null);

		marriagePanel = new MarriagePanel();
		getTabbedPane().addTab("Vielser", null, marriagePanel, null);

		burialPanel = new BurialPanel();
		getTabbedPane().addTab("Dødsfald", null, burialPanel, null);
	}

	/**
	 * @param tabbedPane the tabbedPane to set
	 */
	public void setTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

}
