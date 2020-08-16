package net.myerichsen.vejby.gedcom;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a family in GEDCOM as extracted from a household in a
 * census file
 * 
 * @author Michael Erichsen
 * @version 16. aug. 2020
 *
 */
public class Family {
	private Individual father;
	private Individual mother;
	private List<Individual> children = new ArrayList<Individual>();
	private int householdId;
	private int familyId;

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
	 * @return A matrix of family member data
	 */
	public String[][] getMembers() {
		String[][] members = new String[getSize()][4];
		int index = 0;

		if (father != null) {
			members[index][0] = father.getName();
			members[index][1] = father.getSex();
			members[index][2] = father.getBirthDate();
			members[index++][3] = "Fader";
		}
		if (mother != null) {
			members[index][0] = mother.getName();
			members[index][1] = mother.getSex();
			members[index][2] = mother.getBirthDate();
			members[index++][3] = "Moder";
		}
		for (Individual child : children) {
			members[index][0] = child.getName();
			members[index][1] = child.getSex();
			members[index][2] = child.getBirthDate();
			members[index++][3] = "Barn";
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
		return size;
	}

	/**
	 * @param child
	 */
	public void setChild(Individual child) {
		children.add(child);

	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<Individual> children) {
		this.children = children;
	}

	/**
	 * @param familyId
	 *            the familyId to set
	 */
	public void setFamilyId(int familyId) {
		this.familyId = familyId;
	}

	/**
	 * @param father
	 *            the father to set
	 */
	public void setFather(Individual father) {
		this.father = father;
	}

	/**
	 * @param householdId
	 *            the householdId to set
	 */
	public void setHouseholdId(int householdId) {
		this.householdId = householdId;
	}

	/**
	 * @param mother
	 *            the mother to set
	 */
	public void setMother(Individual mother) {
		this.mother = mother;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (father != null) {
			return father.toString();
		}

		if (mother != null) {
			return mother.toString();
		}

		return "";
	}

	/**
	 * @return
	 */
	public String toGedcom() {
		StringBuilder sb = new StringBuilder();
		if (father != null) {
			sb.append(father.toGedcom());

		}

		if (mother != null) {
			sb.append(mother.toGedcom());
		}

		for (Individual child : children) {
			sb.append(child.toGedcom());
		}

		return sb.toString();
	}
}
