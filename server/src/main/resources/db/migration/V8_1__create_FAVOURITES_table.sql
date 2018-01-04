--------------------------------------------------------
--  DDL for Table FAVOURITES
--------------------------------------------------------

CREATE TABLE FAVOURITES
(
  FAV_ID                        NUMBER(10)                      CONSTRAINT PK_FAV_ID PRIMARY KEY,
  FAV_USR_ID                    NUMBER(10)                      CONSTRAINT NN_FAV_USR_ID NOT NULL,
  FAV_ADS_ID                    NUMBER(10)                      CONSTRAINT NN_FAV_ADS_ID NOT NULL,
  CONSTRAINT FK_FAV_USR_ID      FOREIGN KEY (FAV_USR_ID)        REFERENCES USERS (USR_ID),
  CONSTRAINT FK_FAV_ADS_ID      FOREIGN KEY (FAV_ADS_ID)        REFERENCES ADS (ADS_ID),
  CONSTRAINT UN_FAV_USR_ADS_ID  UNIQUE (FAV_USR_ID, FAV_ADS_ID)
);

--------------------------------------------------------
--  Sequence for Table FAVOURITES
--------------------------------------------------------

CREATE SEQUENCE SEQ_FAV_ID
START WITH 1
INCREMENT BY 1;
