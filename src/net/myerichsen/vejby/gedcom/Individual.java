package net.myerichsen.vejby.gedcom;

import java.util.Date;

public class Individual {
	/**
	 * "INDI" tag
	 */
	private final static String tag = "INDI";
	private int id;
	private String name;
	private String sex;
	private String trades;
	private String address;
	private Date birthDate;
	private Date deathDate;
	private String birthPlace;
	private String deathPlace;

	/**
	 * @return the birthPlace
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * @param birthPlace
	 *            the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * @return the deathPlace
	 */
	public String getDeathPlace() {
		return deathPlace;
	}

	/**
	 * @param deathPlace
	 *            the deathPlace to set
	 */
	public void setDeathPlace(String deathPlace) {
		this.deathPlace = deathPlace;
	}

	/**
	 * @return the trades
	 */
	public String getTrades() {
		return trades;
	}

	/**
	 * @param trades
	 *            the trades to set
	 */
	public void setTrades(String trades) {
		this.trades = trades;
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate
	 *            the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the deathDate
	 */
	public Date getDeathDate() {
		return deathDate;
	}

	/**
	 * @param deathDate
	 *            the deathDate to set
	 */
	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the tag
	 */
	public static String getTag() {
		return tag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();

		sb.append("0 @I" + getId() + "@ " + getTag() + "\n");
		sb.append("1 NAME " + getName() + "\n");
		if (getSex() == Sex.M) {
			sb.append("1 SEX M\n");
		} else if (getSex() == Sex.F) {
			sb.append("1 SEX F\n");
		}

		sb.append("1 BIRT\n");
		sb.append("2 DATE " + getBirthDate() + "\n");
		if (getBirthPlace() != null) {
			sb.append("2 PLAC " + getBirthPlace() + "\n");
		}

		if (getDeathDate() != null) {
			sb.append("1 DEAT\n");
			sb.append("2 DATE " + getDeathDate() + "\n");

		}

		if (getTrades() != null) {
			sb.append("1 OCCU " + getTrades() + "\n");
		}

		// final Iterator<String> itr = getFamilieListe().iterator();
		//
		// while (itr.hasNext()) {
		// sb.append("1 " + itr.next() + "\n");
		// }

		return sb.toString();
	}

}
