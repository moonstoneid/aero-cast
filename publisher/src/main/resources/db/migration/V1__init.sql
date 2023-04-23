CREATE SEQUENCE entry_seq INCREMENT BY 50;

CREATE TABLE entry (
  id INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  description VARCHAR(2000) NOT NULL,
  date TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);