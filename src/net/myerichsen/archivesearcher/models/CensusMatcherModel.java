package net.myerichsen.archivesearcher.models;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.myerichsen.archivesearcher.util.Constants;

/**
 * Class representing a household in a census event for finding persons with
 * identical census records that have not yet been merged
 *
 * @author Michael Erichsen
 * @version 10. aug. 2023
 *
 */
public class CensusMatcherModel extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT1 = "SELECT DISTINCT SOURCEDETAIL FROM EVENT WHERE TYPE = 'Census' FETCH FIRST 50 ROWS ONLY";
	private static final String SELECT2 = "SELECT * FROM EVENT WHERE SOURCEDETAIL = ?";
	private static final String SELECT3 = "SELECT * FROM FAMILY WHERE HUSBAND = ? OR WIFE = ?";
//	private static final String SELECT4 = "SELECT * FROM INDIVIDUAL WHERE FAMC = ?";
	private static Connection conn;

	/**
	 * Worker method
	 *
	 * @param dbPath
	 * @param schema
	 * @param place
	 * @param date
	 * @throws SQLException
	 */
	public static CensusMatcherModel[] load(String dbPath, String schema, String date, String place)
			throws SQLException {
		final List<CensusMatcherModel> list = new ArrayList<>();
		ResultSet rs2, rs3;
		String sourceDetail = "";
		String individual = "";
		int counter = 0;
		final List<String> individualList = new ArrayList<>();
		String husband = "";
		String wife = "";

		conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement1 = conn.prepareStatement(SET_SCHEMA);
		statement1.setString(1, schema);
		statement1.execute();

		statement1 = conn.prepareStatement(SELECT1);
		final PreparedStatement statement2 = conn.prepareStatement(SELECT2);
		final PreparedStatement statement3 = conn.prepareStatement(SELECT3);
//		final PreparedStatement statement4 = conn.prepareStatement(SELECT4);
		final ResultSet rs1 = statement1.executeQuery();

		eachCensusEvent: while (rs1.next()) {
			sourceDetail = rs1.getString("SOURCEDETAIL");

			if (sourceDetail == null || sourceDetail.isBlank()) {
				continue eachCensusEvent;
			}

			counter = 0;
			individualList.clear();
			statement2.setString(1, sourceDetail);
			rs2 = statement2.executeQuery();

			eachHousehold: while (rs2.next()) {
//				System.out.println("Date: " + rs2.getString("DATE"));

				individual = rs2.getString("INDIVIDUAL");

				if (individual == null || individual.isBlank()) {
//					System.out.println(
//							"Individual " + rs2.getString("INDIVIDUAL") + ", " + sourceDetail.substring(0, 63) + "...");
					continue eachHousehold;
				}

				counter++;
				individualList.add(individual);

				// System.out.println(individual);

//
//				model = new CensusMatcherModel();
//				model.setFamilyId(family);
//
//				statement3.setString(1, family);
//				rs3 = statement3.executeQuery();
//
//				if (rs3.next()) {
//					model.setHusbandId(rs3.getString("HUSBAND").trim());
//					model.setWifeId(rs3.getString("WIFE").trim());
//				}
//
//				model.setSourceDetail(sourceDetail);
//				list.add(model);
//				break eachCensusEvent; // For debug
				if (counter > 1) {
					System.out.println("Counter: " + counter + ", " + individualList);

					statement3.setString(1, individualList.get(0));
					statement3.setString(2, individualList.get(0));
					rs3 = statement3.executeQuery();

					while (rs3.next()) {
						husband = rs3.getString("HUSBAND");
						wife = rs3.getString("WIFE");

						if (individualList.contains(husband) && individualList.contains(wife)) {
							continue eachCensusEvent;
						}
					}
				}
			}

		}

		final CensusMatcherModel[] array = new CensusMatcherModel[list.size()];

		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}

		return array;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Properties props = new Properties();

		try {
			final InputStream input = new FileInputStream(Constants.PROPERTIES_PATH);
			props.load(input);
			final String dbPath = props.getProperty("parishPath");
			final String schema = props.getProperty("parishSchema");
			final String date = "1860-02-01";
			final String place = "%Vejby%";
			final CensusMatcherModel[] array = CensusMatcherModel.load(dbPath, schema, date, place);

			for (final CensusMatcherModel censusMatcherModel : array) {
				System.out.println(censusMatcherModel);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	private String familyId = "";
	private String husbandId = "";
	private String wifeId = "";
	private List<String> children = new ArrayList<>();
	private List<String> witnesses = new ArrayList<>();
	private String sourceDetail = "";

	/**
	 * @return the children
	 */
	public List<String> getChildren() {
		return children;
	}

	/**
	 * @return the familyId
	 */
	public String getFamilyId() {
		return familyId;
	}

	/**
	 * @return the husbandId
	 */
	public String getHusbandId() {
		return husbandId;
	}

	/**
	 * @return the sourceDetail
	 */
	public String getSourceDetail() {
		return sourceDetail;
	}

	/**
	 * @return the wifeId
	 */
	public String getWifeId() {
		return wifeId;
	}

	/**
	 * @return the witnesses
	 */
	public List<String> getWitnesses() {
		return witnesses;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<String> children) {
		this.children = children;
	}

	/**
	 * @param familyId the familyId to set
	 */
	public void setFamilyId(String familyId) {
		this.familyId = familyId;
	}

	/**
	 * @param husbandId the husbandId to set
	 */
	public void setHusbandId(String husbandId) {
		this.husbandId = husbandId;
	}

	/**
	 * @param sourceDetail the sourceDetail to set
	 */
	public void setSourceDetail(String sourceDetail) {
		this.sourceDetail = sourceDetail;
	}

	/**
	 * @param wifeId the wifeId to set
	 */
	public void setWifeId(String wifeId) {
		this.wifeId = wifeId;
	}

	/**
	 * @param witnesses the witnesses to set
	 */
	public void setWitnesses(List<String> witnesses) {
		this.witnesses = witnesses;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();

		if (familyId != null) {
			builder.append(familyId);
			builder.append(", ");
		}
		if (husbandId != null) {
			builder.append(husbandId);
			builder.append(", ");
		}
		if (wifeId != null) {
			builder.append(wifeId);
			builder.append(", ");
		}
		if (children != null) {
			builder.append(children);
			builder.append(", ");
		}
		if (witnesses != null) {
			builder.append(witnesses);
			builder.append(", ");
		}
		if (sourceDetail != null) {
			if (sourceDetail.length() > 64) {
				sourceDetail = sourceDetail.substring(0, 64) + "...";
			}

			builder.append(sourceDetail);
		}
		return builder.toString();
	}
}
