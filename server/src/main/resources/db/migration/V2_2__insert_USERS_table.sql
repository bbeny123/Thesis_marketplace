--------------------------------------------------------
--  Example data for Table USERS
--------------------------------------------------------

INSERT INTO USERS (USR_ID, USR_EMAIL, USR_PASSWORD, USR_FIRST_NAME, USR_LAST_NAME, USR_CITY, USR_PRV_ID, USR_ADMIN)
VALUES
  (SEQ_USR_ID.nextval, 'admin@admin', '6603afb786702904bef53500aedc122fdfadd323', 'Admin', 'Admin', 'Łódź', '5', '1');
INSERT INTO USERS (USR_ID, USR_EMAIL, USR_PASSWORD, USR_FIRST_NAME, USR_LAST_NAME, USR_CITY, USR_PRV_ID, USR_ADMIN)
VALUES
  (SEQ_USR_ID.nextval, 'm@dalton', 'a1b1eff4f972e5477d51a3396211810490cd7f7d', 'Mike', 'Dalton', 'Warszawa', '7', '1');
COMMIT;
