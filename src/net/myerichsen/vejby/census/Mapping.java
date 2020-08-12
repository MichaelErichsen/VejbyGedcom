package net.myerichsen.vejby.census;

/**
 * Mapping between census fields and individual, census event, birth event, and
 * trade event.
 * 
 * @author Michael Erichsen
 * @version 13. aug. 2020
 *
 */
public class Mapping {
	private String[][] mappingMatrix;

	/**
	 * 
	 */
	public Mapping(int i, int j) {
		super();
		mappingMatrix = new String[i][j];
	}

	/**
	 * @return the mappingMatrix
	 */
	public String[][] getMappingMatrix() {
		return mappingMatrix;
	}

	public void setMappingMatrix(int i, int j, String string) {
		mappingMatrix[i][j] = string;
	}

	/**
	 * @param mappingMatrix
	 *            the mappingMatrix to set
	 */
	public void setMappingMatrix(String[][] mappingMatrix) {
		this.mappingMatrix = mappingMatrix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < mappingMatrix.length; i++) {
			sb.append(mappingMatrix[i][0] + ":\t" + mappingMatrix[i][1] + ",\t " + mappingMatrix[i][2] + ",\t "
					+ mappingMatrix[i][3] + ",\t" + mappingMatrix[i][4] + ",\t" + mappingMatrix[i][5] + "\n");
		}
		return sb.toString();
	}

}
