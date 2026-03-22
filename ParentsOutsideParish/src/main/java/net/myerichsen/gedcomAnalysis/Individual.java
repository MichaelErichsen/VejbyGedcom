package net.myerichsen.gedcomAnalysis;

/**
 * An individual in a GEDCOM record
 *
 * @author Michael Erichsen, 2026
 */
public class Individual {
	private Birth birth;

	private CensusRecord censusRecord;
	private String id;
	private String name;
	private String sex;
	private String father;
	private String mother;

	/**
	 * @return the birth
	 */
	public Birth getBirth() {
		return birth;
	}

	/**
	 * @return the censusRecord
	 */
	public CensusRecord getCensusRecord() {
		return censusRecord;
	}

	/**
	 * @return the father
	 */
	public String getFather() {
		return father;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the mother
	 */
	public String getMother() {
		return mother;
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
	 * @param birth the birth to set
	 */
	public void setBirth(Birth birth) {
		this.birth = birth;
	}

	/**
	 * @param censusRecord the censusRecord to set
	 */
	public void setCensusRecord(CensusRecord censusRecord) {
		this.censusRecord = censusRecord;
	}

	/**
	 * @param father the father to set
	 */
	public void setFather(String father) {
		this.father = father;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param mother the mother to set
	 */
	public void setMother(String mother) {
		this.mother = mother;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param string the sex to set
	 */
	public void setSex(String string) {
		this.sex = string;
	}

	@Override
	public String toString() {
		return "Individual [" + (birth != null ? "birth=" + birth + ", " : "")
				+ (censusRecord != null ? "censusRecord=" + censusRecord + ", " : "")
				+ (id != null ? "id=" + id + ", " : "") + (name != null ? "name=" + name + ", " : "")
				+ (sex != null ? "sex=" + sex + ", " : "") + (father != null ? "father=" + father + ", " : "")
				+ (mother != null ? "mother=" + mother : "") + "]";
	}
}
