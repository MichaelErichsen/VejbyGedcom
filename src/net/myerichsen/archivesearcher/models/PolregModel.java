package net.myerichsen.archivesearcher.models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a police registry event
 *
 * @author Michael Erichsen
 * @version 18. maj 2023
 *
 */
public class PolregModel extends ASModel {
	/**
	 * Constants
	 */
	private static final String SET_SCHEMA = "SET SCHEMA = ?";
	private static final String SELECT_POLICE_ADDRESS = "SELECT * FROM POLICE_ADDRESS WHERE POLICE_ADDRESS.PERSON_ID = ?";
	private static final String SELECT_POLICE_PERSON = "SELECT * FROM POLICE_PERSON WHERE POLICE_PERSON.PHONNAME = ?";
	private static final String SELECT_POLICE_POSITION = "SELECT * FROM POLICE_POSITION WHERE POLICE_POSITION.PERSON_ID = ?";

	/**
	 * Get a list of objects from the database
	 *
	 * @param dbPath
	 * @param phonName
	 * @param birthDate
	 * @param deathDate
	 * @return
	 * @throws SQLException
	 */
	public static PolregModel[] load(String schema, String dbPath, String phonName, String birthDate, String deathDate)
			throws SQLException {
		final Date bd = birthDate.equals("") ? Date.valueOf("0001-01-01") : Date.valueOf(birthDate);
		int calcYear = 0;

		final Connection conn = DriverManager.getConnection("jdbc:derby:" + dbPath);
		PreparedStatement statement = conn.prepareStatement(SET_SCHEMA);
		statement.setString(1, schema);
		statement.execute();
		statement = conn.prepareStatement(SELECT_POLICE_PERSON);
		statement.setString(1, phonName);
		final ResultSet rs = statement.executeQuery();
		ResultSet rs2, rs3;
		Date prBirthDate = null;
		int birthday = 0;
		int birthmonth = 0;
		int birthyear = 0;
		String rsbd;

		PolregModel pr;
		final List<PolregModel> lpr = new ArrayList<>();

		while (rs.next()) {
			pr = new PolregModel();
			pr.setId(rs.getInt("ID"));
			pr.setName(getField(rs, "FIRSTNAMES") + " " + getField(rs, "LASTNAME"));

			birthday = rs.getInt("BIRTHDAY");
			birthmonth = rs.getInt("BIRTHMONTH");
			birthyear = rs.getInt("BIRTHYEAR");

			if (birthyear == 0) {
				continue;
			}

			birthday = birthday == 0 ? 1 : birthday;
			birthmonth = birthmonth == 0 ? 1 : birthmonth;
			rsbd = String.format("%04d", birthyear) + "-" + String.format("%02d", birthmonth) + "-"
					+ String.format("%02d", birthday);

			prBirthDate = Date.valueOf(rsbd);

			if (bd.before(Date.valueOf("1000-01-01"))) {
				calcYear = 0;
			} else {
				try {
					calcYear = bd.toLocalDate().getYear() - prBirthDate.toLocalDate().getYear();
				} catch (final Exception e) {
					calcYear = 0;
				}
			}

			if (calcYear > 2 || calcYear < -2) {
				continue;
			}

			pr.setBirthDate(prBirthDate);
			lpr.add(pr);
		}

		statement = conn.prepareStatement(SELECT_POLICE_POSITION);

		for (final PolregModel pr2 : lpr) {
			statement.setInt(1, pr2.getId());
			rs2 = statement.executeQuery();

			if (rs2.next()) {
				pr2.setOccupation(getField(rs2, "POSITION_DANISH"));
			}
		}

		statement = conn.prepareStatement(SELECT_POLICE_ADDRESS);

		for (final PolregModel pr3 : lpr) {
			statement.setInt(1, pr3.getId());
			rs3 = statement.executeQuery();

			if (rs3.next()) {
				try {
					pr3.setStreet(rs3.getString("STREET"));
				} catch (final SQLException e2) {
					pr3.setStreet("");
				}
				try {
					pr3.setNumber(rs3.getInt("NUMBER"));
				} catch (final SQLException e2) {
					pr3.setNumber(0);
				}
				try {
					pr3.setLetter(rs3.getString("LETTER"));
				} catch (final SQLException e1) {
					pr3.setLetter("");
				}
				try {
					pr3.setFloor(rs.getString("FLOOR"));
				} catch (final SQLException e) {
					pr3.setFloor("");
				}
				try {
					pr3.setPlace(rs.getString("PLACE"));
				} catch (final SQLException e) {
					pr3.setPlace("");
				}
				try {
					pr3.setHost(rs3.getString("HOST"));
				} catch (final SQLException e1) {
					pr3.setHost("");
				}
				try {
					pr3.setDay(rs3.getInt("DAY"));
				} catch (final SQLException e1) {
					pr3.setDay(1);
				}
				try {
					pr3.setMonth(rs3.getInt("MONTH"));
				} catch (final SQLException e) {
					pr3.setMonth(1);
				}
				try {
					pr3.setYear(rs3.getInt("XYEAR"));
				} catch (final SQLException e) {
					pr3.setYear(1);
				}
				try {
					pr3.setFullAddress(rs3.getString("FULL_ADDRESS"));
				} catch (final SQLException e) {
					pr3.setFullAddress("");
				}
			}
		}

		statement.close();

		final PolregModel[] pra = new PolregModel[lpr.size()];

		for (int i = 0; i < lpr.size(); i++) {
			pra[i] = lpr.get(i);
		}

		return pra;
	}

	private int id;
	private String name = "";
	private Date birthDate;
	private String occupation = "";
	private String street = "";
	private int number = 0;
	private String letter = "";
	private String floor = "";
	private String place = "";
	private String host = "";
	private int day = 0;
	private int month = 0;
	private int year = 0;
	private String fullAddress = "";

	/**
	 * Constructor
	 *
	 */
	public PolregModel() {
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * @return the day
	 */
	public int getDay() {
		return day;
	}

	/**
	 * @return the floor
	 */
	public String getFloor() {
		return floor;
	}

	/**
	 * @return the fullAddress
	 */
	public String getFullAddress() {
		return fullAddress;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the letter
	 */
	public String getLetter() {
		return letter;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @return the occupation
	 */
	public String getOccupation() {
		return occupation;
	}

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @param day the day to set
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * @param floor the floor to set
	 */
	public void setFloor(String floor) {
		this.floor = floor;
	}

	/**
	 * @param fullAddress the fullAddress to set
	 */
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param letter the letter to set
	 */
	public void setLetter(String letter) {
		this.letter = letter;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @param occupation the occupation to set
	 */
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return id + ", " + (name != null ? name + ", " : "") + (birthDate != null ? birthDate + ", " : "")
				+ (occupation != null ? occupation + ", " : "") + (street != null ? street + ", " : "") + number + ", "
				+ (letter != null ? letter + ", " : "") + (floor != null ? floor + ", " : "")
				+ (place != null ? place + ", " : "") + (host != null ? host + ", " : "") + day + ", " + month + ", "
				+ year + ", " + (fullAddress != null ? fullAddress : "");
	}

}
