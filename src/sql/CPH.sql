--<ScriptOptions statementTerminator=";"/>

CREATE TABLE CPH.BURIAL_PERSON_COMPLETE (
    ID INTEGER NOT NULL,
    NUMBER INTEGER DEFAULT NULL,
    FIRSTNAMES CHAR(100) DEFAULT 'NULL',
    LASTNAME CHAR(100) DEFAULT 'NULL',
    BIRTHNAME CHAR(100) DEFAULT 'NULL',
    AGEYEARS INTEGER DEFAULT NULL,
    AGEMONTH DECIMAL(4 , 2) DEFAULT NULL,
    AGEWEEKS DECIMAL(4 , 2) DEFAULT NULL,
    AGEDAYS DECIMAL(4 , 2) DEFAULT NULL,
    AGEHOURS INTEGER DEFAULT NULL,
    DATEOFBIRTH DATE DEFAULT NULL,
    DATEOFDEATH DATE DEFAULT NULL,
    YEAROFBIRTH INTEGER DEFAULT NULL,
    DEATHPLACE VARCHAR(4096) DEFAULT 'NULL',
    CIVILSTATUS CHAR(25) DEFAULT 'NULL',
    ADRESSOUTSIDECPH VARCHAR(255) DEFAULT 'NULL',
    SEX CHAR(10) DEFAULT 'NULL',
    COMMENT VARCHAR(16384),
    CEMETARY CHAR(100) DEFAULT 'NULL',
    CHAPEL CHAR(100) DEFAULT 'NULL',
    PARISH CHAR(100) DEFAULT 'NULL',
    STREET CHAR(75) DEFAULT 'NULL',
    HOOD CHAR(25) DEFAULT 'NULL',
    STREET_NUMBER INTEGER DEFAULT NULL,
    LETTER CHAR(1) DEFAULT 'NULL',
    FLOOR CHAR(15) DEFAULT 'NULL',
    INSTITUTION CHAR(100) DEFAULT 'NULL',
    INSTITUTION_STREET CHAR(75) DEFAULT 'NULL',
    INSTITUTION_HOOD CHAR(25) DEFAULT 'NULL',
    INSTITUTION_STREET_NUMBER INTEGER DEFAULT NULL,
    OCCUPTATIONS VARCHAR(4096) DEFAULT 'NULL',
    OCCUPATION_RELATION_TYPES VARCHAR(4096) DEFAULT 'NULL',
    DEATHCAUSES VARCHAR(4096) DEFAULT 'NULL',
    DEATHCAUSES_DANISH VARCHAR(4096) DEFAULT 'NULL',
    PHONNAME CHAR(64)
  );

CREATE TABLE CPH.BURIAL_ADDRESS (
    ID INTEGER NOT NULL,
    PERSON_ID INTEGER NOT NULL,
    STREET CHAR(75) DEFAULT 'NULL',
    HOOD CHAR(25) DEFAULT 'NULL',
    STREET_UNIQUE CHAR(75) DEFAULT 'NULL',
    STREET_NUMBER INTEGER DEFAULT NULL,
    LETTER CHAR(1) DEFAULT 'NULL',
    FLOOR CHAR(15) DEFAULT 'NULL',
    INSTITUTION CHAR(100) DEFAULT 'NULL',
    INSTITUTION_STREET CHAR(75) DEFAULT 'NULL',
    INSTITUTION_HOOD CHAR(25) DEFAULT 'NULL',
    INSTITUTION_STREET_UNIQUE CHAR(75) DEFAULT 'NULL',
    INSTITUTION_STREET_NUMBER INTEGER DEFAULT NULL
  );

CREATE TABLE CPH.POLICE_PERSON_W_OCCUPATIONS (
    ID INTEGER NOT NULL,
    FIRSTNAMES CHAR(80) DEFAULT 'NULL',
    LASTNAME CHAR(50) DEFAULT 'NULL',
    MAIDENNAME CHAR(50) DEFAULT 'NULL',
    MARRIED INTEGER DEFAULT NULL,
    TYPE CHAR(6) DEFAULT 'NULL',
    GENDER INTEGER DEFAULT NULL,
    BIRTHPLACE CHAR(100) DEFAULT 'NULL',
    BIRTHDAY INTEGER DEFAULT NULL,
    BIRTHMONTH INTEGER DEFAULT NULL,
    BIRTHYEAR INTEGER DEFAULT NULL,
    DEATHDAY INTEGER DEFAULT NULL,
    DEATHMONTH INTEGER DEFAULT NULL,
    DEATHYEAR INTEGER DEFAULT NULL,
    OCCUPATIONS VARCHAR(255) DEFAULT 'NULL',
    OCCUPATIONS_ISCO_MAJOR_GROUPS CHAR(64) DEFAULT 'NULL',
    OCCUPATIONS_ISCO_SUBMAJOR_GROUPS CHAR(64) DEFAULT 'NULL',
    OCCUPATIONS_ISCO_MINOR_GROUPS CHAR(64) DEFAULT 'NULL'
  );

CREATE TABLE CPH.POLICE_POSITION (
    ID INTEGER NOT NULL,
    PERSON_ID INTEGER NOT NULL,
    POSITION_DANISH VARCHAR(100) DEFAULT 'NULL',
    POSITION_ENGLISH VARCHAR(100) DEFAULT 'NULL',
    ISCO_MAJOR_GROUP CHAR(5) DEFAULT 'NULL',
    ISCO_SUBMAJOR_GROUP CHAR(5) DEFAULT 'NULL',
    ISCO_MINOR_GROUP CHAR(5) DEFAULT 'NULL',
    ISCO_UNIT CHAR(5) DEFAULT 'NULL'
  );

CREATE TABLE CPH.BURIAL_PERSON_W_DEATHCAUSES (
    ID INTEGER NOT NULL,
    NUMBER INTEGER DEFAULT NULL,
    FIRSTNAMES CHAR(100) DEFAULT 'NULL',
    LASTNAME CHAR(100) DEFAULT 'NULL',
    BIRTHNAME CHAR(100) DEFAULT 'NULL',
    AGEYEARS INTEGER DEFAULT NULL,
    AGEMONTH DECIMAL(4 , 2) DEFAULT NULL,
    AGEWEEKS DECIMAL(4 , 2) DEFAULT NULL,
    AGEDAYS DECIMAL(4 , 2) DEFAULT NULL,
    AGEHOURS INTEGER DEFAULT NULL,
    DATEOFBIRTH DATE DEFAULT NULL,
    DATEOFDEATH DATE DEFAULT NULL,
    YEAROFBIRTH INTEGER DEFAULT NULL,
    DEATHPLACE VARCHAR(4096) DEFAULT 'NULL',
    CIVILSTATUS CHAR(25) DEFAULT 'NULL',
    ADRESSOUTSIDECPH VARCHAR(255) DEFAULT 'NULL',
    SEX CHAR(10) DEFAULT 'NULL',
    COMMENT VARCHAR(16384),
    CEMETARY CHAR(100) DEFAULT 'NULL',
    CHAPEL CHAR(100) DEFAULT 'NULL',
    PARISH CHAR(100) DEFAULT 'NULL',
    DEATHCAUSES VARCHAR(4096) DEFAULT 'NULL',
    DEATHCAUSES_DANISH VARCHAR(4096) DEFAULT 'NULL'
  );

CREATE TABLE CPH.BURIAL_POSITION (
    ID INTEGER NOT NULL,
    PERSON_ID INTEGER DEFAULT NULL,
    POSITION CHAR(128) DEFAULT 'NULL',
    RELATIONTYPE CHAR(128) DEFAULT 'NULL',
    WORKPLACE CHAR(128) DEFAULT 'NULL'
  );

CREATE TABLE CPH.POLICE_RELATION (
    PERSON_MAIN_ID INTEGER NOT NULL,
    PERSON_RELATED_ID INTEGER NOT NULL,
    RELATION_TYPE INTEGER NOT NULL
  );

CREATE TABLE CPH.POLICE_PERSON (
    ID INTEGER NOT NULL,
    FIRSTNAMES CHAR(80) DEFAULT 'NULL',
    LASTNAME CHAR(50) DEFAULT 'NULL',
    MAIDENNAME CHAR(50) DEFAULT 'NULL',
    MARRIED null,
    TYPE CHAR(6) DEFAULT 'NULL',
    GENDER INTEGER DEFAULT NULL,
    BIRTHPLACE CHAR(100) DEFAULT 'NULL',
    BIRTHDAY INTEGER DEFAULT NULL,
    BIRTHMONTH INTEGER DEFAULT NULL,
    BIRTHYEAR INTEGER DEFAULT NULL,
    DEATHDAY INTEGER DEFAULT NULL,
    DEATHMONTH INTEGER DEFAULT NULL,
    DEATHYEAR INTEGER DEFAULT NULL,
    PHONNAME CHAR(64) DEFAULT 'NULL'
  );

CREATE TABLE CPH.BURIAL_PERSON (
    ID INTEGER NOT NULL,
    NUMBER INTEGER DEFAULT NULL,
    FIRSTNAMES CHAR(100) DEFAULT 'NULL',
    LASTNAME CHAR(100) DEFAULT 'NULL',
    BIRTHNAME CHAR(100) DEFAULT 'NULL',
    AGEYEARS INTEGER DEFAULT NULL,
    AGEMONTH DECIMAL(4 , 2) DEFAULT NULL,
    AGEWEEKS DECIMAL(4 , 2) DEFAULT NULL,
    AGEDAYS DECIMAL(4 , 2) DEFAULT NULL,
    AGEHOURS INTEGER DEFAULT NULL,
    DATEOFBIRTH DATE DEFAULT NULL,
    DATEOFDEATH DATE DEFAULT NULL,
    YEAROFBIRTH INTEGER DEFAULT NULL,
    DEATHPLACE VARCHAR(255) DEFAULT 'NULL',
    CIVILSTATUS CHAR(25) DEFAULT 'NULL',
    ADRESSOUTSIDECPH VARCHAR(255) DEFAULT 'NULL',
    SEX CHAR(10) DEFAULT 'NULL',
    COMMENT VARCHAR(16384),
    CEMETARY CHAR(100) DEFAULT 'NULL',
    CHAPEL CHAR(100) DEFAULT 'NULL',
    PARISH CHAR(100) DEFAULT 'NULL'
  );

CREATE TABLE CPH.POLICE_ADDRESS (
    ID INTEGER NOT NULL,
    PERSON_ID INTEGER NOT NULL,
    STREET VARCHAR(65) DEFAULT 'NULL',
    NUMBER VARCHAR(10) DEFAULT 'NULL',
    LETTER VARCHAR(3) DEFAULT 'NULL',
    FLOOR VARCHAR(10) DEFAULT 'NULL',
    SIDE VARCHAR(3) DEFAULT 'NULL',
    PLACE VARCHAR(90) DEFAULT 'NULL',
    HOST VARCHAR(255) DEFAULT 'NULL',
    LATITUDE DECIMAL(18 , 12) DEFAULT NULL,
    LONGITUDE DECIMAL(18 , 12) DEFAULT NULL,
    DAY INTEGER DEFAULT NULL,
    MONTH INTEGER DEFAULT NULL,
    XYEAR INTEGER DEFAULT NULL,
    FULL_ADDRESS VARCHAR(512) DEFAULT 'NULL'
  );

CREATE TABLE CPH.BURIAL_DEATHCAUSE (
    ID INTEGER NOT NULL,
    PERSON_ID INTEGER DEFAULT NULL,
    DEATHCAUSE VARCHAR(125) DEFAULT 'NULL',
    DEATHCAUSE_DANISH VARCHAR(125) DEFAULT 'NULL',
    XORDER INTEGER DEFAULT 0,
    PRIORITY INTEGER DEFAULT 0
  );

CREATE UNIQUE INDEX CPH.SQL230130210151780
  ON CPH.POLICE_ADDRESS (ID ASC);

CREATE UNIQUE INDEX CPH.SQL230129182252050
  ON CPH.BURIAL_DEATHCAUSE (ID ASC);

CREATE UNIQUE INDEX CPH.SQL230130140616560
  ON CPH.BURIAL_PERSON (ID ASC);

CREATE UNIQUE INDEX CPH.SQL230130143806320
  ON CPH.BURIAL_PERSON (ID ASC);

CREATE UNIQUE INDEX CPH.SQL230130190816350
  ON CPH.BURIAL_PERSON_W_DEATHCAUSES (ID ASC);

CREATE INDEX CPH.SQL230131235911960
  ON CPH.BURIAL_POSITION (PERSON_ID ASC);

CREATE UNIQUE INDEX CPH.SQL230130190752240
  ON CPH.BURIAL_PERSON_COMPLETE (ID ASC);

CREATE UNIQUE INDEX CPH.SQL230129182253090	
  ON CPH.POLICE_POSITION (ID ASC);

CREATE INDEX CPH.SQL230131235849940
  ON CPH.POLICE_POSITION (PERSON_ID ASC);

CREATE UNIQUE INDEX CPH.SQL230129182252970
  ON CPH.POLICE_PERSON_W_OCCUPATIONS (ID ASC);

CREATE UNIQUE INDEX CPH.SQL230130200549550
  ON CPH.BURIAL_POSITION (ID ASC);

CREATE UNIQUE INDEX CPH.SQL230130121859790
  ON CPH.BURIAL_DEATHCAUSE (ID ASC);

CREATE UNIQUE INDEX CPH.SQL230130191754520
  ON CPH.BURIAL_PERSON_W_DEATHCAUSES (ID ASC);

CREATE INDEX CPH.POLICE_BURIAL_NAME_INDEX
  ON CPH.BURIAL_PERSON (FIRSTNAMES ASC, LASTNAME ASC);

CREATE UNIQUE INDEX CPH.SQL230129182251900
  ON CPH.BURIAL_ADDRESS (ID ASC);

CREATE INDEX CPH.SQL230131235918140
  ON CPH.POLICE_ADDRESS (PERSON_ID ASC);

CREATE UNIQUE INDEX CPH.SQL230130200332010
  ON CPH.BURIAL_POSITION (ID ASC);

CREATE INDEX CPH.SQL230131235909600
  ON CPH.BURIAL_DEATHCAUSE (PERSON_ID ASC);

CREATE UNIQUE INDEX CPH.SQL230129182252820
  ON CPH.POLICE_PERSON (ID ASC);

CREATE UNIQUE INDEX CPH.SQL230130192800720
  ON CPH.BURIAL_PERSON_COMPLETE (ID ASC);

CREATE INDEX CPH.SQL230131235958090
  ON CPH.BURIAL_ADDRESS (PERSON_ID ASC);

ALTER TABLE CPH.BURIAL_PERSON_W_DEATHCAUSES
  ADD CONSTRAINT SQL230129182252450 PRIMARY KEY (ID);

ALTER TABLE CPH.POLICE_ADDRESS
  ADD CONSTRAINT SQL230129182252700 PRIMARY KEY (ID);

ALTER TABLE CPH.BURIAL_PERSON_COMPLETE
  ADD CONSTRAINT SQL230129182252320 PRIMARY KEY (ID);

ALTER TABLE CPH.POLICE_POSITION
  ADD CONSTRAINT SQL230129182253090 PRIMARY KEY (ID);

ALTER TABLE CPH.BURIAL_ADDRESS
  ADD CONSTRAINT SQL230129182251900 PRIMARY KEY (ID);

ALTER TABLE CPH.POLICE_PERSON
  ADD CONSTRAINT SQL230129182252820 PRIMARY KEY (ID);

ALTER TABLE CPH.POLICE_PERSON_W_OCCUPATIONS
  ADD CONSTRAINT SQL230129182252970 PRIMARY KEY (ID);

ALTER TABLE CPH.BURIAL_PERSON
  ADD CONSTRAINT SQL230129182252190 PRIMARY KEY (ID);

ALTER TABLE CPH.BURIAL_DEATHCAUSE
  ADD CONSTRAINT SQL230129182252050 PRIMARY KEY (ID);

ALTER TABLE CPH.BURIAL_POSITION
  ADD CONSTRAINT SQL230129182252580 PRIMARY KEY (ID);

ALTER TABLE CPH.BURIAL_ADDRESS
  ADD CONSTRAINT BURIAL__BURIAL_FK
    FOREIGN KEY (PERSON_ID)
    	REFERENCES CPH.BURIAL_PERSON (ID)
    	ON DELETE CASCADE;

ALTER TABLE CPH.BURIAL_DEATHCAUSE
  ADD CONSTRAINT BURIAL_DE_BURIA_FK
    FOREIGN KEY (PERSON_ID)
    	REFERENCES CPH.BURIAL_PERSON (ID)
    	ON DELETE CASCADE;

ALTER TABLE CPH.POLICE_POSITION
  ADD CONSTRAINT POLICE_P_POLICE_FK
    FOREIGN KEY (PERSON_ID)
    	REFERENCES CPH.POLICE_PERSON (ID)
    	ON DELETE CASCADE;

ALTER TABLE CPH.BURIAL_POSITION
  ADD CONSTRAINT BURIAL_P_BURIAL_FK
    FOREIGN KEY (PERSON_ID)
    	REFERENCES CPH.BURIAL_PERSON (ID)
    	ON DELETE CASCADE;

ALTER TABLE CPH.POLICE_ADDRESS
  ADD CONSTRAINT POLICE__POLICE_FK
    FOREIGN KEY (PERSON_ID)
    	REFERENCES CPH.POLICE_PERSON (ID)
    	ON DELETE CASCADE;
    	
CREATE INDEX CPH.BURIAL_PHON_IX ON CPH.BURIAL_PERSON_COMPLETE (PHONNAME ASC);
    	
CREATE INDEX CPH.POLICE_PHON_IX ON CPH.POLICE_PERSON (PHONNAME ASC);