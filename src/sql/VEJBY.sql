--<ScriptOptions statementTerminator=";"/>

CREATE TABLE VEJBY.FAMILY (
    ID CHAR(12) NOT NULL,
    HUSBAND CHAR(12) DEFAULT ' ',
    WIFE CHAR(12)
  );

CREATE TABLE VEJBY.EVENT (
    ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY,
    TYPE CHAR(12),
    SUBTYPE VARCHAR(32),
    DATE DATE,
    INDIVIDUAL CHAR(12),
    FAMILY CHAR(12),
    PLACE VARCHAR(256),
    NOTE VARCHAR(16000),
    SOURCEDETAIL VARCHAR(16000)
  );

CREATE TABLE VEJBY.INDIVIDUAL (
    ID CHAR(12) NOT NULL,
    GIVENNAME CHAR(64),
    SURNAME CHAR(64),
    SEX CHAR(1),
    FAMC CHAR(12),
    PHONNAME CHAR(64),
    BIRTHYEAR INTEGER,
    BIRTHPLACE VARCHAR(256),
    DEATHYEAR INTEGER,
    DEATHPLACE VARCHAR(256),
    PARENTS VARCHAR(512)
  );

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

CREATE INDEX VEJBY.SQL230124103427890
  ON VEJBY.INDIVIDUAL (FAMC ASC);

CREATE INDEX VEJBY.CENSUS_FON_IX
  ON VEJBY.CENSUS (FONNAVN ASC);

CREATE UNIQUE INDEX VEJBY.SQL230124103427450
  ON VEJBY.EVENT (ID ASC);

CREATE UNIQUE INDEX VEJBY.SQL230124103426740
  ON VEJBY.INDIVIDUAL (ID ASC);

CREATE UNIQUE INDEX VEJBY.CENSUS_UI
  ON VEJBY.CENSUS (KIPNR ASC, LOEBENR ASC);

CREATE UNIQUE INDEX VEJBY.SQL230124103427660
  ON VEJBY.FAMILY (ID ASC);

CREATE UNIQUE INDEX VEJBY.SQL230228113222930
  ON VEJBY.CENSUS (KIPNR ASC, LOEBENR ASC);

CREATE INDEX VEJBY.SQL230124103428330
  ON VEJBY.EVENT (INDIVIDUAL ASC);

CREATE INDEX VEJBY.SQL230124103429560
  ON VEJBY.FAMILY (HUSBAND ASC);

CREATE INDEX VEJBY.SQL230124103429780
  ON VEJBY.EVENT (FAMILY ASC);

CREATE INDEX VEJBY.SQL230124103428540
  ON VEJBY.FAMILY (WIFE ASC);

ALTER TABLE VEJBY.EVENT
  ADD CONSTRAINT Event_PK PRIMARY KEY (ID);

ALTER TABLE VEJBY.INDIVIDUAL
  ADD CONSTRAINT Individual_PK PRIMARY KEY (ID);

ALTER TABLE VEJBY.FAMILY
  ADD CONSTRAINT Family_PK PRIMARY KEY (ID);

ALTER TABLE VEJBY.CENSUS
  ADD CONSTRAINT CENSUS_PK PRIMARY KEY (
    KIPNR, LOEBENR);

ALTER TABLE VEJBY.EVENT
  ADD CONSTRAINT EVENT_FAMILY_FK
    FOREIGN KEY (FAMILY)
            	REFERENCES VEJBY.FAMILY (ID)
            	ON DELETE CASCADE;

ALTER TABLE VEJBY.FAMILY
  ADD CONSTRAINT Famil_Individua_F1
    FOREIGN KEY (HUSBAND)
            	REFERENCES VEJBY.INDIVIDUAL (ID)
            	ON DELETE CASCADE;

ALTER TABLE VEJBY.INDIVIDUAL
  ADD CONSTRAINT INDIVIDUA_FAMIL_FK
    FOREIGN KEY (FAMC)
            	REFERENCES VEJBY.FAMILY (ID)
            	ON DELETE CASCADE;

ALTER TABLE VEJBY.EVENT
  ADD CONSTRAINT EVEN_INDIVIDUA_FK
    FOREIGN KEY (INDIVIDUAL)
            	REFERENCES VEJBY.INDIVIDUAL (ID)
            	ON DELETE CASCADE;

ALTER TABLE VEJBY.FAMILY
  ADD CONSTRAINT Famil_Individua_F2
    FOREIGN KEY (WIFE)
            	REFERENCES VEJBY.INDIVIDUAL (ID)
            	ON DELETE CASCADE;
            	
CREATE INDEX VEJBY.CENSUS_HUSH_IX ON VEJBY.CENSUS
(
   KIPNR ASC,
   HUSSTANDS_FAMILIENR ASC
)
;