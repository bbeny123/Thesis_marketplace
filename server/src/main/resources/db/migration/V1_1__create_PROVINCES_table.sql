--------------------------------------------------------
--  DDL for Table PROVINCES
--------------------------------------------------------

CREATE TABLE PROVINCES
(
  PRV_ID   NUMBER(10) CONSTRAINT PK_PROVINCES PRIMARY KEY,
  PRV_NAME NVARCHAR2(60) CONSTRAINT NN_UN_PRV_NAME NOT NULL UNIQUE
);
