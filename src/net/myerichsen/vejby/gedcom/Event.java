package net.myerichsen.vejby.gedcom;

/**
 * A census event as extracted from a KIP file.
 * 
 * @author Michael Erichsen
 * @version 16. aug. 2020
 *
 */
public class Event {
	// EVENT_DETAIL:=
	// n TYPE <EVENT_DESCRIPTOR> {0:1} p.43
	// n DATE <DATE_VALUE> {0:1} p.42/41
	// n <<PLACE_STRUCTURE>> {0:1} p.34
	// n <<ADDRESS_STRUCTURE>> {0:1} p.29
	// n AGE <AGE_AT_EVENT> {0:1} p.37
	// n AGNC <RESPONSIBLE_AGENCY> {0:1} p.52
	// n CAUS <CAUSE_OF_EVENT> {0:1} p.38
	// n <<SOURCE_CITATION>> {0:M} p.34
	// n <<MULTIMEDIA_LINK>> {0:M} p.33,26
	// 30
	// n <<NOTE_STRUCTURE>> {0:M} p.33

	private int censusYear;
	private String place;
	private String sourceId;

	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @param place
	 *            the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @return the censusYear
	 */
	public int getCensusYear() {
		return censusYear;
	}

	/**
	 * @param censusYear
	 *            the censusYear to set
	 */
	public void setCensusYear(int censusYear) {
		this.censusYear = censusYear;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("1 CENS\n");
		sb.append("2 DATE " + censusYear + "\n");
		sb.append("2 PLAC " + place + "\n");
		sb.append("3 ROLE Witness\n");
		sb.append("2 SOUR @S" + sourceId + "@\n");
		// 3 PAGE Husstand: Frederiksborg, Holbo, Vejby, 1845, 1, Weiby,
		// Præstegaarden
		// 4 CONT
		// 4 CONT Kildenavn, Alder, Civilstand, Kildeerhverv, Kildefødested,
		// Antal
		// 4 CONC familier/hus, Køn, Løbenr i indtastning, Matr.nr./Adresse,
		// 4 CONC Kildestednavn
		// 4 CONT Jeremias Müller, 62, Enkemand, Dr. phil. og Sognepræst,
		// Randers, 2, M,
		// 4 CONC 1, Præstegaarden, Weiby
		// 4 CONT Jens Müller, 13, Ugift, hans Børn, Barrit Weile Amt, 2, M, 2,
		// 4 CONC Præstegaarden, Weiby
		// 4 CONT Mathæus Müller, 10, Ugift, hans Børn, Her i Sognet [Vejby], 2,
		// M, 3,
		// 4 CONC Præstegaarden, Weiby
		// 4 CONT Christen Aastrup, 12, Ugift, hans Pleiesøn, Greenaa, 2, M, 4,
		// 4 CONC Præstegaarden, Weiby
		// 4 CONT Niels Gjølby, 35, Ugift, Huuslærer og Cand. Theol., Thorup
		// Wib. Amt, 2,
		// 4 CONC M, 5, Præstegaarden, Weiby
		// 4 CONT Frederikke Eskilsen, 24, Ugift, Huusjomfrue, Allerød Sogn, 2,
		// K, 6,
		// 4 CONC Præstegaarden, Weiby
		// 4 CONT Anders Jensen, 22, Ugift, Tjenestekarl, Weiby Sogn Frederb.
		// Amt, 2, M,
		// 4 CONC 7, Præstegaarden, Weiby
		// 4 CONT Mette Hans Datter, 28, Ugift, Tjenestepige, Weiby Sogn
		// Frederb. Amt, 2,
		// 4 CONC K, 8, Præstegaarden, Weiby
		// 4 CONT Peter Müller, 29, Ugift, Forpagter af Præsteg. Jordene,
		// Barrith Weile
		// 4 CONC Amt, 2, M, 9, Præstegaarden, Weiby
		// 4 CONT Ida Marie Sønnichsen, 32, Ugift, Huusjomfrue, Horslev Weile
		// Amt, 2, K,
		// 4 CONC 10, Præstegaarden, Weiby
		// 4 CONT Lars Nielsen, 25, Ugift, Tjenestefolk, Her i Sognet [Vejby],
		// 2, M, 11,
		// 4 CONC Præstegaarden, Weiby
		// 4 CONT Søren Jensen, 23, Ugift, Tjenestefolk, Her i Sognet [Vejby],
		// 2, M, 12,
		// 4 CONC Præstegaarden, Weiby
		// 4 CONT Anders Mortensen, 17, Ugift, Tjenestefolk, Her i Sognet
		// [Vejby], 2, M,
		// 4 CONC 13, Præstegaarden, Weiby
		// 4 CONT Bodil Sørens Dat, 25, Ugift, Tjenestefolk, Her i Sognet
		// [Vejby], 2, K,
		// 4 CONC 14, Præstegaarden, Weiby
		// 4 CONT Johannes Andersen, 25, Ugift, Tjenestefolk, Her i Sognet
		// [Vejby], 2, M,
		// 4 CONC 15, Præstegaarden, Weiby

		return sb.toString();
	}

}
