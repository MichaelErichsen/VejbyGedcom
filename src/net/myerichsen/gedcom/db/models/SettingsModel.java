package net.myerichsen.gedcom.db.models;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Class representing all application settings
 *
 * @author Michael Erichsen
 * @version 10. apr. 2023
 *
 */
public class SettingsModel {
	private String vejbyPath;
	private String probatePath;
	private String probateSource;
	private String cphDbPath;
	private String gedcomFilePath;
	private String kipTextFilename;
	private String censusCsvFileDirectory;
	private String vejbySchema;
	private String probateSchema;
	private String cphSchema;
	private String relocationSearch;
	private String censusSearch;
	private String probateSearch;
	private String polregSearch;
	private String burregSearch;
	private String siblingSearch;
	private String cphCsvFileDirectory;
	private String burialPersonComplete;
	private String policeAddress;
	private String policePerson;
	private String policePosition;

	/**
	 * Constructor
	 *
	 * @param props
	 */
	public SettingsModel(Properties props) {
		super();
		vejbyPath = props.getProperty("vejbyPath");
		probatePath = props.getProperty("probatePath");
		probateSource = props.getProperty("probateSource");
		cphDbPath = props.getProperty("cphDbPath");
		gedcomFilePath = props.getProperty("gedcomFilePath");
		kipTextFilename = props.getProperty("kipTextFilename");
		censusCsvFileDirectory = props.getProperty("censusCsvFileDirectory");
		vejbySchema = props.getProperty("vejbySchema");
		probateSchema = props.getProperty("probateSchema");
		cphSchema = props.getProperty("cphSchema");
		relocationSearch = props.getProperty("relocationSearch");
		censusSearch = props.getProperty("censusSearch");
		probateSearch = props.getProperty("probateSearch");
		polregSearch = props.getProperty("polregSearch");
		burregSearch = props.getProperty("burregSearch");
		siblingSearch = props.getProperty("siblingSearch");
		cphCsvFileDirectory = props.getProperty("cphCsvFileDirectory");
		burialPersonComplete = props.getProperty("burialPersonComplete");
		policeAddress = props.getProperty("policeAddress");
		policePerson = props.getProperty("policePerson");
		policePosition = props.getProperty("policePosition");
	}

	/**
	 * @return the burialPersonComplete
	 */
	public String getBurialPersonComplete() {
		return burialPersonComplete;
	}

	/**
	 * @return the burregSearch
	 */
	public String getBurregSearch() {
		return burregSearch;
	}

	/**
	 * @return the censusCsvFileDirectory
	 */
	public String getCensusCsvFileDirectory() {
		return censusCsvFileDirectory;
	}

	/**
	 * @return the censusSearch
	 */
	public String getCensusSearch() {
		return censusSearch;
	}

	/**
	 * @return the cphCsvFileDirectory
	 */
	public String getCphCsvFileDirectory() {
		return cphCsvFileDirectory;
	}

	/**
	 * @return the cphDbPath
	 */
	public String getCphDbPath() {
		return cphDbPath;
	}

	/**
	 * @return the cphSchema
	 */
	public String getCphSchema() {
		return cphSchema;
	}

	/**
	 * @return the csvFileDirectory
	 */
	public String getCsvFileDirectory() {
		return censusCsvFileDirectory;
	}

	/**
	 * @return the gedcomFilePath
	 */
	public String getGedcomFilePath() {
		return gedcomFilePath;
	}

	/**
	 * @return the kipTextFilename
	 */
	public String getKipTextFilename() {
		return kipTextFilename;
	}

	/**
	 * @return the policeAddress
	 */
	public String getPoliceAddress() {
		return policeAddress;
	}

	/**
	 * @return the policePerson
	 */
	public String getPolicePerson() {
		return policePerson;
	}

	/**
	 * @return the policePosition
	 */
	public String getPolicePosition() {
		return policePosition;
	}

	/**
	 * @return the polregSearch
	 */
	public String getPolregSearch() {
		return polregSearch;
	}

	/**
	 * @return the probatePath
	 */
	public String getProbatePath() {
		return probatePath;
	}

	/**
	 * @return the probateSchema
	 */
	public String getProbateSchema() {
		return probateSchema;
	}

	/**
	 * @return the probateSearch
	 */
	public String getProbateSearch() {
		return probateSearch;
	}

	/**
	 * @return the probateSource
	 */
	public String getProbateSource() {
		return probateSource;
	}

	/**
	 * @return the relocationSearch
	 */
	public String getRelocationSearch() {
		return relocationSearch;
	}

	/**
	 * @return the siblingSearch
	 */
	public String getSiblingSearch() {
		return siblingSearch;
	}

	/**
	 * @return the vejbyPath
	 */
	public String getVejbyPath() {
		return vejbyPath;
	}

	/**
	 * @return the vejbySchema
	 */
	public String getVejbySchema() {
		return vejbySchema;
	}

	/**
	 * @param burialPersonComplete the burialPersonComplete to set
	 */
	public void setBurialPersonComplete(String burialPersonComplete) {
		this.burialPersonComplete = burialPersonComplete;
	}

	/**
	 * @param burregSearch the burregSearch to set
	 */
	public void setBurregSearch(String burregSearch) {
		this.burregSearch = burregSearch;
	}

	/**
	 * @param censusCsvFileDirectory the censusCsvFileDirectory to set
	 */
	public void setCensusCsvFileDirectory(String censusCsvFileDirectory) {
		this.censusCsvFileDirectory = censusCsvFileDirectory;
	}

	/**
	 * @param censusSearch the censusSearch to set
	 */
	public void setCensusSearch(String censusSearch) {
		this.censusSearch = censusSearch;
	}

	/**
	 * @param cphCsvFileDirectory the cphCsvFileDirectory to set
	 */
	public void setCphCsvFileDirectory(String cphCsvFileDirectory) {
		this.cphCsvFileDirectory = cphCsvFileDirectory;
	}

	/**
	 * @param cphDbPath the cphDbPath to set
	 */
	public void setCphDbPath(String cphDbPath) {
		this.cphDbPath = cphDbPath;
	}

	/**
	 * @param cphSchema the cphSchema to set
	 */
	public void setCphSchema(String cphSchema) {
		this.cphSchema = cphSchema;
	}

	/**
	 * @param csvFileDirectory the csvFileDirectory to set
	 */
	public void setCsvFileDirectory(String csvFileDirectory) {
		this.censusCsvFileDirectory = csvFileDirectory;
	}

	/**
	 * @param gedcomFilePath the gedcomFilePath to set
	 */
	public void setGedcomFilePath(String gedcomFilePath) {
		this.gedcomFilePath = gedcomFilePath;
	}

	/**
	 * @param kipTextFilename the kipTextFilename to set
	 */
	public void setKipTextFilename(String kipTextFilename) {
		this.kipTextFilename = kipTextFilename;
	}

	/**
	 * @param policeAddress the policeAddress to set
	 */
	public void setPoliceAddress(String policeAddress) {
		this.policeAddress = policeAddress;
	}

	/**
	 * @param policePerson the policePerson to set
	 */
	public void setPolicePerson(String policePerson) {
		this.policePerson = policePerson;
	}

	/**
	 * @param policePosition the policePosition to set
	 */
	public void setPolicePosition(String policePosition) {
		this.policePosition = policePosition;
	}

	/**
	 * @param polregSearch the polregSearch to set
	 */
	public void setPolregSearch(String polregSearch) {
		this.polregSearch = polregSearch;
	}

	/**
	 * @param probatePath the probatePath to set
	 */
	public void setProbatePath(String probatePath) {
		this.probatePath = probatePath;
	}

	/**
	 * @param probateSchema the probateSchema to set
	 */
	public void setProbateSchema(String probateSchema) {
		this.probateSchema = probateSchema;
	}

	/**
	 * @param probateSearch the probateSearch to set
	 */
	public void setProbateSearch(String probateSearch) {
		this.probateSearch = probateSearch;
	}

	/**
	 * @param probateSource the probateSource to set
	 */
	public void setProbateSource(String probateSource) {
		this.probateSource = probateSource;
	}

	/**
	 * @param relocationSearch the relocationSearch to set
	 */
	public void setRelocationSearch(String relocationSearch) {
		this.relocationSearch = relocationSearch;
	}

	/**
	 * @param siblingSearch the siblingSearch to set
	 */
	public void setSiblingSearch(String siblingSearch) {
		this.siblingSearch = siblingSearch;
	}

	/**
	 * @param vejbyPath the vejbyPath to set
	 */
	public void setVejbyPath(String vejbyPath) {
		this.vejbyPath = vejbyPath;
	}

	/**
	 * @param vejbySchema the vejbySchema to set
	 */
	public void setVejbySchema(String vejbySchema) {
		this.vejbySchema = vejbySchema;
	}

	/**
	 * @param props
	 */
	public void storeProperties(Properties props) {
		props.setProperty("vejbyPath", vejbyPath);
		props.setProperty("probatePath", probatePath);
		props.setProperty("probateSource", probateSource);
		props.setProperty("cphDbPath", cphDbPath);
		props.setProperty("gedcomFilePath", gedcomFilePath);
		props.setProperty("kipTextFilename", kipTextFilename);
		props.setProperty("censusCsvFileDirectory", censusCsvFileDirectory);
		props.setProperty("vejbySchema", vejbySchema);
		props.setProperty("probateSchema", probateSchema);
		props.setProperty("cphSchema", cphSchema);
		props.setProperty("relocationSearch", relocationSearch);
		props.setProperty("censusSearch", censusSearch);
		props.setProperty("probateSearch", probateSearch);
		props.setProperty("polregSearch", polregSearch);
		props.setProperty("burregSearch", burregSearch);
		props.setProperty("siblingSearch", siblingSearch);
		props.setProperty("cphCsvFileDirectory", cphCsvFileDirectory);
		props.setProperty("burialPersonComplete", burialPersonComplete);
		props.setProperty("policeAddress", policeAddress);
		props.setProperty("policePerson", policePerson);
		props.setProperty("policePosition", policePosition);

		final String path = System.getProperty("user.home") + "/ArchiveSearcher.properties";

		try {
			final OutputStream output = new FileOutputStream(path);
			props.store(output, "Archive searcher properties");
		} catch (final Exception e2) {
			System.out.println("Kan ikke gemme egenskaber i " + path);
			e2.printStackTrace();
		}
	}

}
