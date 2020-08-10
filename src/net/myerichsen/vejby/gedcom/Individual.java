package net.myerichsen.vejby.gedcom;

import java.util.Date;

public class Individual {
	/**
	 * "INDI" tag
	 */
	private final static String tag = "INDI";
	/**
	 * @return the tag
	 */
	public static String getTag() {
		return tag;
	}
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
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
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
	public Date getDeathDate() {
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
	public void setBirthDate(Date birthDate) {
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
	public void setDeathDate(Date deathDate) {
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
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
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
