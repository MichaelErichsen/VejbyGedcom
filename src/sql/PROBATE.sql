--<ScriptOptions statementTerminator=";"/>

CREATE TABLE GEDCOM.INDIVIDUAL (
    ID CHAR(8) NOT NULL,
    NAME CHAR(100),
    FONKOD CHAR(50),
    EVENT_ID CHAR(8),
    SOURCE CHAR(100) NOT NULL
  );

CREATE TABLE GEDCOM.EVENT (
    ID CHAR(8) NOT NULL,
    FROMDATE DATE,
    TODATE DATE,
    PLACE CHAR(50),
    EVENTTYPE CHAR(30),
    VITALTYPE CHAR(30),
    COVERED_DATA VARCHAR(3600),
    SOURCE CHAR(100) NOT NULL,
    CAPTION CHAR(50)
  );

CREATE INDEX GEDCOM.IX2
  ON GEDCOM.INDIVIDUAL (FONKOD ASC);

CREATE INDEX GEDCOM.IX1
  ON GEDCOM.EVENT (FROMDATE ASC);

CREATE INDEX GEDCOM.IX3
  ON GEDCOM.INDIVIDUAL (NAME ASC);

CREATE INDEX GEDCOM.SQL111222132042201
  ON GEDCOM.INDIVIDUAL (EVENT_ID ASC, SOURCE ASC);

CREATE UNIQUE INDEX GEDCOM.SQL111222132041940
  ON GEDCOM.EVENT (ID ASC, SOURCE ASC);

CREATE INDEX GEDCOM.IX4
  ON GEDCOM.EVENT (PLACE ASC);

CREATE UNIQUE INDEX GEDCOM.SQL111222132042200
  ON GEDCOM.INDIVIDUAL (ID ASC, SOURCE ASC);

ALTER TABLE GEDCOM.EVENT
  ADD CONSTRAINT SQL111222132041940 PRIMARY KEY (
    ID, SOURCE);

ALTER TABLE GEDCOM.INDIVIDUAL
  ADD CONSTRAINT SQL111222132042200 PRIMARY KEY (
    ID, SOURCE);

ALTER TABLE GEDCOM.INDIVIDUAL
  ADD CONSTRAINT SQL111222132042201
    FOREIGN KEY (EVENT_ID, SOURCE)
    	REFERENCES GEDCOM.EVENT (ID, SOURCE);