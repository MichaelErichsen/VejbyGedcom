package net.myerichsen.gedcom.db.models;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Class representing all application settings
 *
 * @author Michael Erichsen
 * @version 25. apr. 2023
 *
 */
public class SettingsModel {
	private String burialPersonComplete;
	private String burregSearch;
	private String censusCsvFileDirectory;
	private String censusPath;
	private String censusSchema;
	private String censusSearch;
	private String cphCsvFileDirectory;
	private String cphDbPath;
	private String cphSchema;
	private String gedcomFilePath;
	private String headSearch;
	private String kipTextFilename;
	private String policeAddress;
	private String policePerson;
	private String policePosition;
	private String polregSearch;
	private String probatePath;
	private String probateSchema;
	private String probateSearch;
	private String probateSource;
	private String relocationSearch;
	private String siblingSearch;
	private String vejbyPath;
	private String vejbySchema;
	private String msgLogLen;

	/**
	 * Constructor
	 *
	 * @param props
	 */
	public SettingsModel(Properties props) {
		burialPersonComplete = props.getProperty("burialPersonComplete");
		burregSearch = props.getProperty("burregSearch");
		censusCsvFileDirectory = props.getProperty("censusCsvFileDirectory");
		censusPath = props.getProperty("censusPath");
		censusSchema = props.getProperty("censusSchema");
		censusSearch = props.getProperty("censusSearch");
		cphCsvFileDirectory = props.getProperty("cphCsvFileDirectory");
		cphDbPath = props.getProperty("cphDbPath");
		cphSchema = props.getProperty("cphSchema");
		gedcomFilePath = props.getProperty("gedcomFilePath");
		headSearch = props.getProperty("headSearch");
		kipTextFilename = props.getProperty("kipTextFilename");
		policeAddress = props.getProperty("policeAddress");
		policePerson = props.getProperty("policePerson");
		policePosition = props.getProperty("policePosition");
		polregSearch = props.getProperty("polregSearch");
		probatePath = props.getProperty("probatePath");
		probateSchema = props.getProperty("probateSchema");
		probateSearch = props.getProperty("probateSearch");
		probateSource = props.getProperty("probateSource");
		relocationSearch = props.getProperty("relocationSearch");
		siblingSearch = props.getProperty("siblingSearch");
		vejbyPath = props.getProperty("vejbyPath");
		vejbySchema = props.getProperty("vejbySchema");
		msgLogLen = props.getProperty("msgLogLen");
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
	 * @return
	 */
	public String getCensusPath() {
		return censusPath;
	}

	/**
	 * @return
	 */
	public String getCensusSchema() {
		return censusSchema;
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
	 * @return the headSearch
	 */
	public String getHeadSearch() {
		return headSearch;
	}

	/**
	 * @return the kipTextFilename
	 */
	public String getKipTextFilename() {
		return kipTextFilename;
	}

	/**
	 * @return the msgLogLen
	 */
	public String getMsgLogLen() {
		return msgLogLen;
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
	 * @param text
	 */
	public void setCensusPath(String text) {
		censusPath = text;

	}

	/**
	 * @param text
	 */
	public void setCensusSchema(String text) {
		censusSchema = text;
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
	 * @param headSearch the headSearch to set
	 */
	public void setHeadSearch(String headSearch) {
		this.headSearch = headSearch;
	}

	/**
	 * @param kipTextFilename the kipTextFilename to set
	 */
	public void setKipTextFilename(String kipTextFilename) {
		this.kipTextFilename = kipTextFilename;
	}

	/**
	 * @param msgLogLen the msgLogLen to set
	 */
	public void setMsgLogLen(String msgLogLen) {
		this.msgLogLen = msgLogLen;
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
	public String storeProperties(Properties props) {
		props.setProperty("burialPersonComplete", burialPersonComplete);
		props.setProperty("burregSearch", burregSearch);
		props.setProperty("censusCsvFileDirectory", censusCsvFileDirectory);
		props.setProperty("censusPath", censusPath);
		props.setProperty("censusSchema", censusSchema);
		props.setProperty("censusSearch", censusSearch);
		props.setProperty("cphCsvFileDirectory", cphCsvFileDirectory);
		props.setProperty("cphDbPath", cphDbPath);
		props.setProperty("cphSchema", cphSchema);
		props.setProperty("gedcomFilePath", gedcomFilePath);
		props.setProperty("headSearch", headSearch);
		props.setProperty("kipTextFilename", kipTextFilename);
		props.setProperty("policeAddress", policeAddress);
		props.setProperty("policePerson", policePerson);
		props.setProperty("policePosition", policePosition);
		props.setProperty("polregSearch", polregSearch);
		props.setProperty("probatePath", probatePath);
		props.setProperty("probateSchema", probateSchema);
		props.setProperty("probateSearch", probateSearch);
		props.setProperty("probateSource", probateSource);
		props.setProperty("relocationSearch", relocationSearch);
		props.setProperty("siblingSearch", siblingSearch);
		props.setProperty("vejbyPath", vejbyPath);
		props.setProperty("vejbySchema", vejbySchema);
		props.setProperty("msgLogLen", msgLogLen);

		final String path = System.getProperty("user.home") + "/ArchiveSearcher.properties";

		try {
			final OutputStream output = new FileOutputStream(path);
			props.store(output, "Archive searcher properties");
			return "Indstillinger er gemt i " + path;
		} catch (final Exception e2) {
			return "Kan ikke gemme indstillinger i " + path;
		}
	}

}
