CREATE SEQUENCE article_seq INCREMENT BY 50;

CREATE TABLE article (
  id INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  summary VARCHAR(2000) NOT NULL,
  content VARCHAR(10000) NOT NULL,
  date TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);