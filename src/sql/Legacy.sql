--<ScriptOptions statementTerminator=";"/>

CREATE TABLE LBASE (
		SYSTEM VARCHAR(8),
		DBTYPE VARCHAR(3),
		DBD VARCHAR(16),
		PCBLIST VARCHAR(128)
	);

CREATE TABLE LPROGRAM (
		SYSTEM VARCHAR(64),
		FILENAME VARCHAR(16),
		PROGRAMNAME VARCHAR(8),
		MODULETYPE VARCHAR(32),
		METACOBOL VARCHAR(3),
		LANGUAGE VARCHAR(8),
		IMSBASES VARCHAR(16777216),
		DB2TABLES VARCHAR(16777216),
		MQQUEUES VARCHAR(16777216),
		SUBPROGRAMTREE VARCHAR(16777216),
		CYCLOMATIC VARCHAR(32),
		LOC SMALLINT,
		DBCOUNT SMALLINT,
		SUBPROGCOUNT SMALLINT,
		CALLERS VARCHAR(16777216)
	);

CREATE TABLE LSTATEMENT (
		PROGRAMNAME VARCHAR(8),
		STATEMENTTYPE VARCHAR(8),
		KEYWORD VARCHAR(16),
		SUBJECT VARCHAR(64),
		TEXT VARCHAR(16777216)
	);
