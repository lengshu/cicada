/**
 * 
 */
package org.aquarius.cicada.workbench.extension.editor.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

/**
 * Use string list to give content proposal.<BR>
 * 
 * @author aquarius.github@hotmail.com
 *
 */
public class ListContentProposalProvider implements IContentProposalProvider {

	/*
	 * The proposals provided.
	 */
	private List<String> proposals;

	/*
	 * The proposals mapped to IContentProposal. Cached for speed in the case where
	 * filtering is not used.
	 */
	private IContentProposal[] contentProposals;

	/*
	 * Boolean that tracks whether filtering is used.
	 */
	private boolean filterProposals = false;

	/**
	 * Construct a SimpleContentProposalProvider whose content proposals are always
	 * the specified array of Objects.
	 *
	 * @param proposals the Strings to be returned whenever proposals are requested.
	 */
	public ListContentProposalProvider(List<String> proposals) {
		super();
		this.proposals = proposals;
	}

	/**
	 * Return an array of Objects representing the valid content proposals for a
	 * field.
	 *
	 * @param contents the current contents of the field (only consulted if
	 *                 filtering is set to <code>true</code>)
	 * @param position the current cursor position within the field (ignored)
	 * @return the array of Objects that represent valid proposals for the field
	 *         given its current content.
	 */
	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		if (this.filterProposals) {
			ArrayList<ContentProposal> list = new ArrayList<>();
			for (String proposal : this.proposals) {
				if (proposal.length() >= contents.length() && proposal.substring(0, contents.length()).equalsIgnoreCase(contents)) {
					list.add(new ContentProposal(proposal));
				}
			}
			return list.toArray(new IContentProposal[list.size()]);
		}
		if (this.contentProposals == null) {
			this.contentProposals = new IContentProposal[this.proposals.size()];
			for (int i = 0; i < this.proposals.size(); i++) {
				this.contentProposals[i] = new ContentProposal(this.proposals.get(i));
			}
		}
		return this.contentProposals;
	}

	/**
	 * Set the boolean that controls whether proposals are filtered according to the
	 * current field content.
	 *
	 * @param filterProposals <code>true</code> if the proposals should be filtered
	 *                        to show only those that match the current contents of
	 *                        the field, and <code>false</code> if the proposals
	 *                        should remain the same, ignoring the field content.
	 * @since 3.3
	 */
	public void setFiltering(boolean filterProposals) {
		this.filterProposals = filterProposals;
		// Clear any cached proposals.
		this.contentProposals = null;
	}
}
