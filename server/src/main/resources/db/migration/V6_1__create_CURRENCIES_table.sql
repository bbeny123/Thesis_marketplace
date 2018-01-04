--------------------------------------------------------
--  DDL for Table CURRENCIES
--------------------------------------------------------

CREATE TABLE CURRENCIES
(
  CUR_ID      NUMBER(10)      CONSTRAINT PK_CURRENCIES PRIMARY KEY,
  CUR_SYMBOL  NVARCHAR2(10)   CONSTRAINT NN_UN_CUR_SYMBOL NOT NULL UNIQUE
);

--------------------------------------------------------
--  Sequence for Table CURRENCIES
--------------------------------------------------------

CREATE SEQUENCE SEQ_CUR_ID
START WITH 1
INCREMENT BY 1;
