package net.myerichsen.vejby.gedcom;

import java.util.ArrayList;
import java.util.List;

public class Family {
	private Individual father;
	private Individual mother;
	private List<Individual> children;

	/**
	 * 
	 */
	public Family() {
		super();
		children = new ArrayList<Individual>();
	}

	/**
	 * @return the children
	 */
	public List<Individual> getChildren() {
		return children;
	}

	/**
	 * @return the father
	 */
	public Individual getFather() {
		return father;
	}

	/**
	 * @return the mother
	 */
	public Individual getMother() {
		return mother;
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
	 * @param father
	 *            the father to set
	 */
	public void setFather(Individual father) {
		this.father = father;
	}

	/**
	 * @param mother
	 *            the mother to set
	 */
	public void setMother(Individual mother) {
		this.mother = mother;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (father != null) {
			sb.append(father);
				
		}
		
		if (mother != null) {
			sb.append(mother);
		}
		
		for (Individual child : children) {
			sb.append(child);
		}
		
		return sb.toString();
		
	}
}
