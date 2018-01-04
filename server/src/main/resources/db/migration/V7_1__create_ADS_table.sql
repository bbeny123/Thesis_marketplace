--------------------------------------------------------
--  DDL for Table ADS
--------------------------------------------------------

CREATE TABLE ADS
(
  ADS_ID                    NUMBER(10) 		            CONSTRAINT PK_ADS_ID PRIMARY KEY,
  ADS_USR_ID                NUMBER(10)                CONSTRAINT NN_ADS_USR_ID NOT NULL,
  ADS_DATE                  DATE DEFAULT SYSDATE      CONSTRAINT NN_ADS_DATE NOT NULL,
  ADS_CAT_ID                NUMBER(10) 		            CONSTRAINT NN_ADS_CAT_ID NOT NULL,
  ADS_TITLE                 NVARCHAR2(60) 		        CONSTRAINT NN_ADS_TITLE NOT NULL,
  ADS_DESCRIPTION           NVARCHAR2(1000),
  ADS_PRICE                 NUMBER(10)                CONSTRAINT NN_ADS_PRICE NOT NULL,
  ADS_CUR_ID                NUMBER(10) DEFAULT 1      CONSTRAINT NN_ADS_CUR_ID NOT NULL,
  ADS_PRV_ID                NUMBER(10)                CONSTRAINT NN_ADS_PRV_ID NOT NULL,
  ADS_CITY                  NVARCHAR2(20)             CONSTRAINT NN_ADS_CITY NOT NULL,
  ADS_PHONE                 NVARCHAR2(20)             CONSTRAINT NN_ADS_PHONE NOT NULL,
  ADS_VIEWS                 NUMBER(10) DEFAULT 0      CONSTRAINT NN_ADS_VIEWS NOT NULL,
  CONSTRAINT FK_ADS_USR_ID  FOREIGN KEY (ADS_USR_ID)  REFERENCES USERS (USR_ID),
  CONSTRAINT FK_ADS_CAT_ID  FOREIGN KEY (ADS_CAT_ID)  REFERENCES SUBCATEGORIES (SCT_ID),
  CONSTRAINT FK_ADS_CUR_ID  FOREIGN KEY (ADS_CUR_ID)  REFERENCES CURRENCIES (CUR_ID),
  CONSTRAINT FK_ADS_PRV_ID  FOREIGN KEY (ADS_PRV_ID)  REFERENCES PROVINCES (PRV_ID)
);

--------------------------------------------------------
--  Sequence for Table ADS
--------------------------------------------------------

CREATE SEQUENCE SEQ_ADS_ID
START WITH 1
INCREMENT BY 1;
