package net.myerichsen.vejby.census;

/**
 * @author Michael Erichsen
 * @version 15. aug. 2020
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

	String location;
	String date;

}
