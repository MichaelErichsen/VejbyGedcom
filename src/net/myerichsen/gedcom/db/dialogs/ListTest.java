package net.myerichsen.gedcom.db.dialogs;

import java.util.LinkedList;

import net.myerichsen.gedcom.db.models.MilRollEntryModel;

/**
 * @author Michael Erichsen
 * @version 10. maj 2023
 *
 */
public class ListTest {
// TODO Convert into a dialog with a tree containing the linked list
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final ListTest lt = new ListTest();
		lt.execute();

	}

	/**
	 *
	 */
	private void execute() {

		final LinkedList<MilRollEntryModel> list = new LinkedList<>();

		final MilRollEntryModel model = new MilRollEntryModel();

		list.add(model);

		// Find all previous entries
		int prevlaegdid = model.getPrevLaegdId();
		int glLoebeNr = model.getPrevLoebeNr();

		while (prevlaegdid != 0 && glLoebeNr != 0) {
			final MilRollEntryModel previous = MilRollEntryModel.select(prevlaegdid, glLoebeNr);
			list.addFirst(model);
			prevlaegdid = previous.getPrevLaegdId();
			glLoebeNr = previous.getPrevLoebeNr();
		}

		// Find all later entries
		// SELECT * from laegd, rulle where gl == new and gl == new
		// if rs.next contibue recursively
		final MilRollEntryModel selectOld = MilRollEntryModel.selectPrev(prevlaegdid, glLoebeNr);
		list.addLast(selectOld);

	}

}
