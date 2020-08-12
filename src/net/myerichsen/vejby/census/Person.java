package net.myerichsen.vejby.census;

/**
 * Person data available from a census
 * 
 * @author Michael Erichsen
 * @version 13. aug 2020
 *
 */
public class Person {
	private final int id;
	private String name;
	private String sex;
	private String trades;
	private String address;
	private String birthDate;
	private String deathDate;
	private String birthPlace;
	private String deathPlace;

	/**
	 * Constructor
	 *
	 * @param id
	 */
	public Person(int id) {
		super();
		this.id = id;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the birthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}

	/**
	 * @return the birthPlace
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * @return the deathDate
	 */
	public String getDeathDate() {
		return deathDate;
	}

	/**
	 * @return the deathPlace
	 */
	public String getDeathPlace() {
		return deathPlace;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @return the trades
	 */
	public String getTrades() {
		return trades;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @param birthDate
	 *            the birthDate to set
	 */
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @param birthPlace
	 *            the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * @param deathDate
	 *            the deathDate to set
	 */
	public void setDeathDate(String deathDate) {
		this.deathDate = deathDate;
	}

	/**
	 * @param deathPlace
	 *            the deathPlace to set
	 */
	public void setDeathPlace(String deathPlace) {
		this.deathPlace = deathPlace;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @param trades
	 *            the trades to set
	 */
	public void setTrades(String trades) {
		this.trades = trades;
	}

}
