package net.myerichsen.vejby.gedcom;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a family in GEDCOM as extracted from a household in a
 * census file.
 * 
 * @version 05-09-2020
 * @author Michael Erichsen
 */
public class Family {
	private Individual father;
	private Individual mother;
	// Used by real faailies
	private List<Individual> children = new ArrayList<>();
	// Used by dummy family 0
	private List<Individual> singles = new ArrayList<>();
	private int householdId;
	private int familyId;
	private String marriageDate = "";
	private String marriagePlace = "";

	/**
	 * Constructor
	 *
	 * @param householdId
	 * @param familyId
	 */
	public Family(int householdId, int familyId) {
		super();
		this.setHouseholdId(householdId);
		this.setFamilyId(familyId);
	}

	/**
	 * @return the children
	 */
	public List<Individual> getChildren() {
		return children;
	}

	/**
	 * @return
	 */
	public int getFamilyId() {
		return familyId;
	}

	/**
	 * @return the father
	 */
	public Individual getFather() {
		return father;
	}

	/**
	 * @return
	 */
	public int getHouseholdId() {
		return householdId;
	}

	/**
	 * @return the marriageDate
	 */
	public String getMarriageDate() {
		return marriageDate;
	}

	/**
	 * @return the marriagePlace
	 */
	public String getMarriagePlace() {
		return marriagePlace;
	}

	/**
	 * @return A matrix of family member data
	 */
	public String[][] getMembers() {
		String[][] members = new String[getSize()][5];
		int index = 0;

		if (father != null) {
			members[index][0] = String.valueOf(father.getId());
			members[index][1] = father.getName();
			members[index][2] = father.getMaritalStatus();
			members[index][3] = father.getTrade();
			members[index++][4] = "Fader";
		}

		if (mother != null) {
			members[index][0] = String.valueOf(mother.getId());
			members[index][1] = mother.getName();
			members[index][2] = mother.getMaritalStatus();
			members[index][3] = mother.getTrade();
			members[index++][4] = "Moder";
		}

		for (Individual child : children) {
			members[index][0] = String.valueOf(child.getId());
			members[index][1] = child.getName();
			members[index][2] = child.getMaritalStatus();
			members[index][3] = child.getTrade();
			members[index++][4] = "Barn";
		}

		for (Individual single : singles) {
			members[index][0] = String.valueOf(single.getId());
			members[index][1] = single.getName();
			members[index][2] = single.getMaritalStatus();
			members[index][3] = single.getTrade();
			members[index++][4] = "";
		}

		return members;
	}

	/**
	 * @return the mother
	 */
	public Individual getMother() {
		return mother;
	}

	/**
	 * @return the singles
	 */
	public List<Individual> getSingles() {
		return singles;
	}

	/**
	 * @return int The size of the family
	 */
	public int getSize() {
		int size = 0;

		if (father != null) {
			size++;
		}
		if (mother != null) {
			size++;
		}
		size += children.size();
		size += singles.size();
		return size;
	}

	/**
	 * Remove an indvidual from a family
	 * 
	 * @param individual The individual to be removed
	 */
	public void removeIndividual(Individual individual) {
		if ((getFather() != null) && getFather().equals(individual)) {
			setFather(null);
		}

		if ((getMother() != null) && getMother().equals(individual)) {
			setMother(null);
		}

		for (Individual child : children) {
			if (child.equals(individual)) {
				children.remove(individual);
			}
		}

		for (Individual single : singles) {
			if (single.equals(individual)) {
				singles.remove(individual);
			}
		}
	}

	/**
	 * @param child
	 */
	public void setChild(Individual child) {
		children.add(child);

	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<Individual> children) {
		this.children = children;
	}

	/**
	 * @param familyId the familyId to set
	 */
	public void setFamilyId(int familyId) {
		this.familyId = familyId;
	}

	/**
	 * @param father the father to set
	 */
	public void setFather(Individual father) {
		this.father = father;
	}

	/**
	 * @param householdId the householdId to set
	 */
	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
	}

	/**
	 * @param marriageDate the marriageDate to set
	 */
	public void setMarriageDate(String marriageDate) {
		this.marriageDate = marriageDate;
	}

	/**
	 * @param marriagePlace the marriagePlace to set
	 */
	public void setMarriagePlace(String marriagePlace) {
		this.marriagePlace = marriagePlace;
	}

	/**
	 * @param mother the mother to set
	 */
	public void setMother(Individual mother) {
		this.mother = mother;
	}

	/**
	 * @param singles the singles to set
	 */
	public void setSingles(List<Individual> singles) {
		this.singles = singles;
	}

	/**
	 * @return
	 */
	public String toGedcom(int familyId) {
		StringBuilder sb = new StringBuilder();

		// Individual tags
		if (father != null) {
			sb.append(father.toGedcom());
			sb.append("1 FAMS @F" + familyId + "@\n");
		}

		if (mother != null) {
			sb.append(mother.toGedcom());
			sb.append("1 FAMS @F" + familyId + "@\n");
		}

		for (Individual child : children) {
			sb.append(child.toGedcom());
			sb.append("1 FAMC @F" + familyId + "@\n");
		}

		// Family tag
		sb.append("0 @F" + familyId + "@ FAM\n");

		if (father != null) {
			sb.append("1 HUSB @I" + father.getId() + "@\n");
		}

		if (mother != null) {
			sb.append("1 WIFE @I" + mother.getId() + "@\n");

		}

		for (Individual child : children) {
			sb.append("1 CHIL @I" + child.getId() + "@\n");
		}

		sb.append("1 MARR\n");

		if (!marriageDate.equals("")) {
			sb.append("2 DATE " + marriageDate + "\n");
		}

		if (!marriagePlace.equals("")) {
			sb.append("2 PLAC " + marriagePlace + "\n");
		}

		sb.append("2 SOUR @S1@\n");

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (familyId == 0) {
			return "Uden for familien";
		}
		if (father != null) {
			return father.toString();
		}

		if (mother != null) {
			return mother.toString();
		}

		return "";
	}
}
