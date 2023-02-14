package net.myerichsen.gedcom.db;

/**
 * Class representing an event in the derby database
 * 
 * @author Michael Erichsen
 * @version 14. feb. 2023
 *
 */
public class DBEvent {
	private final int id;
	private final String individual;
	private final String place;

	/**
	 * Constructor
	 *
	 * @param id
	 * @param individual
	 * @param place
	 */
	public DBEvent(int id, String individual, String place) {
		this.id = id;
		this.individual = individual;
		this.place = place;
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
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @return true if place is in the shire
	 */
	public boolean isInLocation(String location) {
		if (place == null) {
			return false;
		}

		if (place.toLowerCase().contains(location.toLowerCase())) {
			return true;
		}
		return false;
	}
}
