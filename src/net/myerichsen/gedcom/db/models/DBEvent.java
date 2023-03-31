package net.myerichsen.gedcom.db.models;

/**
 * Class representing an event in the derby database
 *
 * @author Michael Erichsen
 * @version 31. mar. 2023
 *
 */
public class DBEvent extends ASModel {
	private final int id;
	private final String individual;
	private final String place;
	private final String subType;
	private final String note;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param individual
	 * @param place
	 * @param subType
	 * @param note
	 */
	public DBEvent(int id, String individual, String place, String subType, String note) {
		this.id = id;
		this.individual = individual;
		this.place = place;
		this.subType = subType;
		this.note = note;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the individual
	 */
	public String getIndividual() {
		return individual;
	}

	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @return the subType
	 */
	public String getSubType() {
		return subType;
	}

	/**
	 * @return true if place or relocation note is in the location
	 */
	public boolean isInLocation(String location) {
		final String l = location.toLowerCase().trim();

		if (((place != null) && (place.toLowerCase().contains(l))) || ((subType != null) && (subType.equals("Flytning"))
				&& (note != null) && (note.toLowerCase().contains(l)))) {
			return true;
		}

		return false;
	}

	@Override
	public String[] toStringArray() {
		// TODO Auto-generated method stub
		return null;
	}
}
