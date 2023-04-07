package net.myerichsen.gedcom.db.models;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Michael Erichsen
 * @version 7. apr. 2023
 *
 */
public class ASSettings {
	private String vejbyPath;
	private String probatePath;
	private String probateSource;
	private String cphDbPath;
	private String outputDirectory;
	private String gedcomFilePath;
	private String kipTextFilename;
	private String csvFileDirectory;
	private String vejbySchema;
	private String probateSchema;
	private String cphSchema;
	private String relocationSearch;
	private String censusSearch;
	private String probateSearch;
	private String polregSearch;
	private String burregSearch;
	private String siblingSearch;

	/**
	 * @return the vejbyPath
	 */
	public String getVejbyPath() {
		return vejbyPath;
	}

	public ASSettings(Properties props) {
		super();
		vejbyPath = props.getProperty("vejbyPath");
		probatePath = props.getProperty("probatePath");
		probateSource = props.getProperty("probateSource");
		cphDbPath = props.getProperty("cphDbPath");
		outputDirectory = props.getProperty("outputDirectory");
		gedcomFilePath = props.getProperty("gedcomFilePath");
		kipTextFilename = props.getProperty("kipTextFilename");
		csvFileDirectory = props.getProperty("csvFileDirectory");
		vejbySchema = props.getProperty("vejbySchema");
		probateSchema = props.getProperty("probateSchema");
		cphSchema = props.getProperty("cphSchema");
		relocationSearch = props.getProperty("relocationSearch");
		censusSearch = props.getProperty("censusSearch");
		probateSearch = props.getProperty("probateSearch");
		polregSearch = props.getProperty("polregSearch");
		burregSearch = props.getProperty("burregSearch");
		siblingSearch = props.getProperty("siblingSearch");
	}

	public void storeProperties(Properties props) {
		props.setProperty("vejbyPath", vejbyPath);
		props.setProperty("probatePath", probatePath);
		props.setProperty("probateSource", probateSource);
		props.setProperty("cphDbPath", cphDbPath);
		props.setProperty("outputDirectory", outputDirectory);
		props.setProperty("gedcomFilePath", gedcomFilePath);
		props.setProperty("kipTextFilename", kipTextFilename);
		props.setProperty("csvFileDirectory", csvFileDirectory);
		props.setProperty("vejbySchema", vejbySchema);
		props.setProperty("probateSchema", probateSchema);
		props.setProperty("cphSchema", cphSchema);
		props.setProperty("relocationSearch", relocationSearch);
		props.setProperty("censusSearch", censusSearch);
		props.setProperty("probateSearch", probateSearch);
		props.setProperty("polregSearch", polregSearch);
		props.setProperty("burregSearch", burregSearch);
		props.setProperty("siblingSearch", siblingSearch);

		String path = System.getProperty("user.home") + "/ArchiveSearcher.properties";

		try {
			final OutputStream output = new FileOutputStream(path);
			props.store(output, "Archive searcher properties");
		} catch (final Exception e2) {
			System.out.println("Kan ikke gemme egenskaber i " + path);
			e2.printStackTrace();
		}
	}

	/**
	 * @param vejbyPath the vejbyPath to set
	 */
	public void setVejbyPath(String vejbyPath) {
		this.vejbyPath = vejbyPath;
	}

	/**
	 * @return the probatePath
	 */
	public String getProbatePath() {
		return probatePath;
	}

	/**
	 * @param probatePath the probatePath to set
	 */
	public void setProbatePath(String probatePath) {
		this.probatePath = probatePath;
	}

	/**
	 * @return the probateSource
	 */
	public String getProbateSource() {
		return probateSource;
	}

	/**
	 * @param probateSource the probateSource to set
	 */
	public void setProbateSource(String probateSource) {
		this.probateSource = probateSource;
	}

	/**
	 * @return the cphDbPath
	 */
	public String getCphDbPath() {
		return cphDbPath;
	}

	/**
	 * @param cphDbPath the cphDbPath to set
	 */
	public void setCphDbPath(String cphDbPath) {
		this.cphDbPath = cphDbPath;
	}

	/**
	 * @return the outputDirectory
	 */
	public String getOutputDirectory() {
		return outputDirectory;
	}

	/**
	 * @param outputDirectory the outputDirectory to set
	 */
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	/**
	 * @return the gedcomFilePath
	 */
	public String getGedcomFilePath() {
		return gedcomFilePath;
	}

	/**
	 * @param gedcomFilePath the gedcomFilePath to set
	 */
	public void setGedcomFilePath(String gedcomFilePath) {
		this.gedcomFilePath = gedcomFilePath;
	}

	/**
	 * @return the kipTextFilename
	 */
	public String getKipTextFilename() {
		return kipTextFilename;
	}

	/**
	 * @param kipTextFilename the kipTextFilename to set
	 */
	public void setKipTextFilename(String kipTextFilename) {
		this.kipTextFilename = kipTextFilename;
	}

	/**
	 * @return the csvFileDirectory
	 */
	public String getCsvFileDirectory() {
		return csvFileDirectory;
	}

	/**
	 * @param csvFileDirectory the csvFileDirectory to set
	 */
	public void setCsvFileDirectory(String csvFileDirectory) {
		this.csvFileDirectory = csvFileDirectory;
	}

	/**
	 * @return the vejbySchema
	 */
	public String getVejbySchema() {
		return vejbySchema;
	}

	/**
	 * @param vejbySchema the vejbySchema to set
	 */
	public void setVejbySchema(String vejbySchema) {
		this.vejbySchema = vejbySchema;
	}

	/**
	 * @return the probateSchema
	 */
	public String getProbateSchema() {
		return probateSchema;
	}

	/**
	 * @param probateSchema the probateSchema to set
	 */
	public void setProbateSchema(String probateSchema) {
		this.probateSchema = probateSchema;
	}

	/**
	 * @return the cphSchema
	 */
	public String getCphSchema() {
		return cphSchema;
	}

	/**
	 * @param cphSchema the cphSchema to set
	 */
	public void setCphSchema(String cphSchema) {
		this.cphSchema = cphSchema;
	}

	/**
	 * @return the relocationSearch
	 */
	public String getRelocationSearch() {
		return relocationSearch;
	}

	/**
	 * @param relocationSearch the relocationSearch to set
	 */
	public void setRelocationSearch(String relocationSearch) {
		this.relocationSearch = relocationSearch;
	}

	/**
	 * @return the censusSearch
	 */
	public String getCensusSearch() {
		return censusSearch;
	}

	/**
	 * @param censusSearch the censusSearch to set
	 */
	public void setCensusSearch(String censusSearch) {
		this.censusSearch = censusSearch;
	}

	/**
	 * @return the probateSearch
	 */
	public String getProbateSearch() {
		return probateSearch;
	}

	/**
	 * @param probateSearch the probateSearch to set
	 */
	public void setProbateSearch(String probateSearch) {
		this.probateSearch = probateSearch;
	}

	/**
	 * @return the polregSearch
	 */
	public String getPolregSearch() {
		return polregSearch;
	}

	/**
	 * @param polregSearch the polregSearch to set
	 */
	public void setPolregSearch(String polregSearch) {
		this.polregSearch = polregSearch;
	}

	/**
	 * @return the burregSearch
	 */
	public String getBurregSearch() {
		return burregSearch;
	}

	/**
	 * @param burregSearch the burregSearch to set
	 */
	public void setBurregSearch(String burregSearch) {
		this.burregSearch = burregSearch;
	}

	/**
	 * @return the siblingSearch
	 */
	public String getSiblingSearch() {
		return siblingSearch;
	}

	/**
	 * @param siblingSearch the siblingSearch to set
	 */
	public void setSiblingSearch(String siblingSearch) {
		this.siblingSearch = siblingSearch;
	}

}
