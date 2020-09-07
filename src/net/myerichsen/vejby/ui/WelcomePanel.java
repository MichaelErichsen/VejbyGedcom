package net.myerichsen.vejby.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Michael Erichsen
 * @version 07-09-2020
 *
 */
public class WelcomePanel extends JPanel {
	private static final long serialVersionUID = -4440644507603142180L;

	/**
	 * Create the panel.
	 */
	public WelcomePanel() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0 };
		gridBagLayout.rowHeights = new int[] { 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0 };
		setLayout(gridBagLayout);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 1.0;
		gbc_scrollPane.weightx = 1.0;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);

		JPanel panel = new JPanel();
		scrollPane.setViewportView(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JTextArea lblNewLabel = new JTextArea("Vejby Gedcom");
		lblNewLabel.setEditable(false);
		lblNewLabel.setLineWrap(true);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		panel.add(lblNewLabel);

		JTextArea lblNewLabel_1 = new JTextArea(

				"Dette program er beregnet til at overs\u00E6tte udtr\u00E6k af folket\u00E6llinger og Family Search s\u00F8gninger til GEDCOM (Genealogical Data Communication) formatet, som kan importeres ind i sl\u00E6gtsforskningsprogrammer.\r\nDet er udviklet til anvendelse i Vejby Sogn, Holbo Herred, Frederiksborg Amt, men burde kunne bruges alle vegne.");
		lblNewLabel_1.setEditable(false);
		lblNewLabel_1.setWrapStyleWord(true);
		lblNewLabel_1.setLineWrap(true);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(lblNewLabel_1);

		JTextArea lblNewLabel_2 = new JTextArea(

				"Folket\u00E6llinger kan hentes i csv (Comma Separated Values) format fra Statens Arkiver, f. eks. ved hj\u00E6lp af oversigten p\u00E5 http://ao.salldata.dk/index.php?type=ft");
		lblNewLabel_2.setEditable(false);
		lblNewLabel_2.setWrapStyleWord(true);
		lblNewLabel_2.setLineWrap(true);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel.add(lblNewLabel_2);

		JTextArea lblNewLabel_3 = new JTextArea(

				"Family Search udtr\u00E6k laves ved f\u00F8rst at foretage en foresp\u00F8rgsel p\u00E5 https://www.familysearch.org/search/ og derefter eksportere resultatet som tsv (Tab Separated Values).\r\nProgrammet underst\u00F8tter lige nu kun f\u00F8dsler og vielser.\r\nProgrammet kan \u00E5bne flere tsv filer p\u00E5 \u00E9n gang for f\u00F8dsler.\r\nFjernelse af dubletter fungerer ikke ordentligt endnu.");
		lblNewLabel_3.setEditable(false);
		lblNewLabel_3.setWrapStyleWord(true);
		lblNewLabel_3.setLineWrap(true);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 16));
		panel.add(lblNewLabel_3);
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 0;
		gbc_textPane.gridy = 1;

	}

}
