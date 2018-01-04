--------------------------------------------------------
--  DDL for Table PHOTOS
--------------------------------------------------------

CREATE TABLE PHOTOS
(
  PHT_ID        NUMBER(10) CONSTRAINT PK_PHT_ID PRIMARY KEY,
  PHT_ADS_ID    NUMBER(10)                          CONSTRAINT NN_PHT_ADS_ID NOT NULL,
  PHT_MINIATURE NUMBER(1) DEFAULT 0                 CONSTRAINT NN_PHT_MINIATURE NOT NULL,
  PHT_PHOTO     BLOB                                CONSTRAINT NN_PHT_PHOTO NOT NULL,
  CONSTRAINT FK_PHT_ADS_ID FOREIGN KEY (PHT_ADS_ID) REFERENCES ADS (ADS_ID),
  CONSTRAINT UN_PHT_MINIATURE_ADS_ID UNIQUE (PHT_ADS_ID, PHT_MINIATURE)
);

--------------------------------------------------------
--  Sequence for Table PHOTOS
--------------------------------------------------------

CREATE SEQUENCE SEQ_PHT_ID
  START WITH 1
  INCREMENT BY 1;
