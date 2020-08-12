package net.myerichsen.vejby.census;

import java.util.ArrayList;
import java.util.List;

/**
 * A family as extracted from a household in a census file
 * 
 * @author Michael Erichsen
 * @version 13. aug. 2020
 *
 */
public class Family {
	private Person father;
	private Person mother;
	private List<Person> children = new ArrayList<Person>();
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
		this.householdId = householdId;
		this.familyId = familyId;
	}

	/**
	 * @return the children
	 */
	public List<Person> getChildren() {
		return children;
	}

	/**
	 * @return the familyId
	 */
	public int getFamilyId() {
		return familyId;
	}

	/**
	 * @return the father
	 */
	public Person getFather() {
		return father;
	}

	/**
	 * @return the householdId
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
		for (Person child : children) {
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
	public Person getMother() {
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
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<Person> children) {
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
	public void setFather(Person father) {
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
	public void setMother(Person mother) {
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
			return father.getName();
		} else if (mother != null) {
			return mother.getName();
		}
		return "?";
	}

}
