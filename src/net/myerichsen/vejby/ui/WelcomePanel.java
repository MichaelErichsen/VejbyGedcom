package net.myerichsen.vejby.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Michael Erichsen
 * @version 04-09-2020
 *
 */
public class WelcomePanel extends JPanel {
	private static final long serialVersionUID = -4440644507603142180L;

	/**
	 * Create the panel.
	 */
	public WelcomePanel() {
		setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel = new JLabel("Vejby GEDCOM");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 48));
		add(lblNewLabel, BorderLayout.CENTER);

	}

}
