--<ScriptOptions statementTerminator=";"/>

CREATE TABLE VEJBY.CENSUS (
    KIPNR CHAR(8) NOT NULL,
    LOEBENR INTEGER NOT NULL,
    AMT VARCHAR(256),
    HERRED VARCHAR(256),
    SOGN VARCHAR(256),
    KILDESTEDNAVN VARCHAR(256),
    HUSSTANDS_FAMILIENR VARCHAR(256),
    MATR_NR_ADRESSE VARCHAR(256),
    KILDENAVN VARCHAR(256),
    FONNAVN VARCHAR(256),
    KOEN VARCHAR(256),
    ALDER INTEGER,
    CIVILSTAND VARCHAR(256),
    KILDEERHVERV VARCHAR(4096),
    STILLING_I_HUSSTANDEN VARCHAR(256),
    KILDEFOEDESTED VARCHAR(256),
    FOEDT_KILDEDATO VARCHAR(256),
    FOEDEAAR INTEGER,
    ADRESSE VARCHAR(256),
    MATRIKEL VARCHAR(512),
    GADE_NR VARCHAR(256),
    FTAAR INTEGER,
    KILDEHENVISNING VARCHAR(256),
    KILDEKOMMENTAR VARCHAR(512)
  );
 
CREATE INDEX VEJBY.CENSUS_FTAAR
  ON VEJBY.CENSUS (FTAAR ASC);
  
CREATE INDEX VEJBY.CENSUS_FON_IX
  ON VEJBY.CENSUS (FONNAVN ASC);

CREATE UNIQUE INDEX VEJBY.CENSUS_UI
  ON VEJBY.CENSUS (KIPNR ASC, LOEBENR ASC);

ALTER TABLE VEJBY.CENSUS
  ADD CONSTRAINT CENSUS_PK PRIMARY KEY (
    KIPNR, LOEBENR);

            	
CREATE INDEX VEJBY.CENSUS_HUSH_IX ON VEJBY.CENSUS (
   KIPNR ASC,
   HUSSTANDS_FAMILIENR ASC);