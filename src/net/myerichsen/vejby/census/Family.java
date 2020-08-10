package net.myerichsen.vejby.census;

import java.util.List;

/**
 * A family as extracted from a household in a census file
 * 
 * @author michael
 *
 */
public class Family {
	private Person father;
	private Person mother;
	private List<Person> children;

	/**
	 * @return the children
	 */
	public List<Person> getChildren() {
		return children;
	}

	/**
	 * @return the father
	 */
	public Person getFather() {
		return father;
	}

	/**
	 * @return the mother
	 */
	public Person getMother() {
		return mother;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<Person> children) {
		this.children = children;
	}

	/**
	 * @param father
	 *            the father to set
	 */
	public void setFather(Person father) {
		this.father = father;
	}

	/**
	 * @param mother
	 *            the mother to set
	 */
	public void setMother(Person mother) {
		this.mother = mother;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (father != null) {
			sb.append("Fader: " + father.getName() + ", ");
		}

		if (mother != null) {
			sb.append("Moder: " + mother.getName() + ", ");
		}
		
//		for (Person person : children) {
//			sb.append("Barn: " + person.getName() + ", ");			
//		}
	
		return sb.toString();
	}

}
