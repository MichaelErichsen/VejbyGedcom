package net.myerichsen.gedcom.db;

/**
 * Class representing some individual data
 *
 * @author Michael Erichsen
 * @version 2023-01-26
 *
 */
public class DBIndividual {
	private String name;
	private String birthDate;

	// TODO Add death date
	
	/**
	 * No arg c:tor
	 */
	public DBIndividual() {
		super();
	}

	/**
	 * @return the birthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param birthDate
	 *            the birthDate to set
	 */
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
