package net.myerichsen.gedcom.util;

import java.util.Properties;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

/**
 * Content assist for ophold field in military roll entry dialog
 *
 * @author Michael Erichsen
 * @version 7. maj 2023
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
		final String proposal = props.getProperty("milrollOphold");
		final IContentProposal[] proposals = new IContentProposal[1];
		proposals[0] = new IContentProposal() {

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
		return proposals;
	}

}
