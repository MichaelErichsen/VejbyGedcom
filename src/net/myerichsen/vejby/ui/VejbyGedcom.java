package net.myerichsen.vejby.ui;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * User interface for the Vejby Gedcom application. Implements tabbed panels to
 * do the work.
 *
 * @author Michael Erichsen
 * @version 11-09-2020
 *
 */

public class VejbyGedcom {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				final VejbyGedcom window = new VejbyGedcom();
				window.frmVejbyGedcomIndtastning.setVisible(true);
			} catch (final Exception e) {
				e.printStackTrace();
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
	private ConfirmationPanel confirmationPanel;
	private ConscriptsPanel conscriptsPanel;
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
		frmVejbyGedcomIndtastning.setBounds(100, 100, 1080, 624);
		frmVejbyGedcomIndtastning.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final JMenuBar menuBar = new JMenuBar();
		frmVejbyGedcomIndtastning.setJMenuBar(menuBar);

		final JMenu mnFiler = new JMenu("Filer");
		menuBar.add(mnFiler);

		final JMenuItem mntmLuk = new JMenuItem("Luk");
		mntmLuk.addActionListener(e -> System.exit(0));
		mnFiler.add(mntmLuk);

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		frmVejbyGedcomIndtastning.getContentPane().setLayout(gridBagLayout);

		setTabbedPane(new JTabbedPane(SwingConstants.TOP));
		final GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
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

		confirmationPanel = new ConfirmationPanel();
		getTabbedPane().addTab("Konfirmationer", null, confirmationPanel, null);

		conscriptsPanel = new ConscriptsPanel();
		getTabbedPane().addTab("Lægdsruller", null, conscriptsPanel, null);

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
