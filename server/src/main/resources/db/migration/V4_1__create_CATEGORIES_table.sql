--------------------------------------------------------
--  DDL for Table CATEGORIES
--------------------------------------------------------

CREATE TABLE CATEGORIES
(
  CAT_ID    NUMBER(10)      CONSTRAINT PK_CATEGORIES PRIMARY KEY,
  CAT_NAME  NVARCHAR2(20)   CONSTRAINT NN_UN_CAT_NAME NOT NULL UNIQUE
);

--------------------------------------------------------
--  Sequence for Table CATEGORIES
--------------------------------------------------------

CREATE SEQUENCE SEQ_CAT_ID
START WITH 1
INCREMENT BY 1;
