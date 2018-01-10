--------------------------------------------------------
--  DDL for Table USERS
--------------------------------------------------------

CREATE TABLE USERS
(
  USR_ID         NUMBER(10)                 CONSTRAINT PK_USERS PRIMARY KEY,
  USR_EMAIL      NVARCHAR2(60)              CONSTRAINT NN_UN_USR_EMAIL NOT NULL UNIQUE,
  USR_PASSWORD   NVARCHAR2(60)              CONSTRAINT NN_USR_PASSWORD NOT NULL,
  USR_FIRST_NAME NVARCHAR2(20)              CONSTRAINT NN_USR_FIRST_NAME NOT NULL,
  USR_LAST_NAME  NVARCHAR2(20),
  USR_CITY       NVARCHAR2(20)              CONSTRAINT NN_USR_CITY NOT NULL,
  USR_PRV_ID     NUMBER(10)                 CONSTRAINT NN_USR_PROVINCE_ID NOT NULL,
  USR_PHONE      NVARCHAR2(20),
  USR_ADMIN      NUMBER(1) DEFAULT 0        CONSTRAINT NN_USR_ADMIN NOT NULL,
  CONSTRAINT FK_USR_PRV_ID FOREIGN KEY (USR_PRV_ID) REFERENCES PROVINCES (PRV_ID)
);

--------------------------------------------------------
--  Sequence for Table USERS
--------------------------------------------------------

CREATE SEQUENCE SEQ_USR_ID
  START WITH 3
  INCREMENT BY 1;
