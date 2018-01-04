--------------------------------------------------------
--  DDL for Table TOKENS
--------------------------------------------------------

CREATE TABLE TOKENS
(
  TKN_ID                    NUMBER(10) 			          CONSTRAINT PK_TOKENS PRIMARY KEY,
  TKN_USR_ID                NUMBER(10) 		            CONSTRAINT NN_TKN_USR_ID NOT NULL,
  TKN_CREATED               DATE DEFAULT SYSDATE      CONSTRAINT NN_TKN_CRATED NOT NULL,
  CONSTRAINT FK_TKN_USR_ID  FOREIGN KEY (TKN_USR_ID)  REFERENCES USERS (USR_ID)
);

--------------------------------------------------------
--  Sequence for Table TOKENS
--------------------------------------------------------

CREATE SEQUENCE SEQ_TKN_ID
START WITH 1
INCREMENT BY 1;
