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
		ListTest lt = new ListTest();
		lt.execute();

	}

	/**
	 * 
	 */
	private void execute() {

		LinkedList<MilRollEntryModel> list = new LinkedList<MilRollEntryModel>();

		MilRollEntryModel model = new MilRollEntryModel();

		list.add(model);

		// Find all previous entries
		int glLaegdId = model.getPrevLaegdId();
		int glLoebeNr = model.getPrevLoebeNr();

		while (glLaegdId != 0 && glLoebeNr != 0) {
			MilRollEntryModel previous = MilRollEntryModel.select(glLaegdId, glLoebeNr);
			list.addFirst(model);
			glLaegdId = previous.getPrevLaegdId();
			glLoebeNr = previous.getPrevLoebeNr();
		}

		// Find all later entries
		// SELECT * from laegd, rulle where gl == new and gl == new
		// if rs.next contibue recursively
		MilRollEntryModel selectOld = MilRollEntryModel.selectPrev(glLaegdId, glLoebeNr);
		list.addLast(selectOld);

	}

}
