package net.myerichsen.gedcom.db.providers;

import java.util.Properties;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

/**
 * Content assist for current location field in military roll entry dialog
 *
 * @author Michael Erichsen
 * @version 8. maj 2023
 *
 */
public class OpholdProvider implements IContentProposalProvider {
	private final Properties props;

	/**
	 * Constructor
	 *
	 * @param props
	 */
	public OpholdProvider(Properties props) {
		this.props = props;
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		final String[] items = props.getProperty("milrollOphold").split(";");

		if (items.length == 0) {
			return new IContentProposal[0];
		}

		final IContentProposal[] proposals = new IContentProposal[items.length];

		for (int i = 0; i < items.length; i++) {
			final String proposal = items[i];

			proposals[i] = new IContentProposal() {

				@Override
				public String getContent() {
					return proposal;
				}

				@Override
				public int getCursorPosition() {
					return proposal.length();
				}

				@Override
				public String getDescription() {
					return null;
				}

				@Override
				public String getLabel() {
					return null;
				}
			};
		}
		return proposals;
	}
}