CREATE TABLE publisher (
  contract_address VARCHAR(42) NOT NULL,
  feed_url VARCHAR(500) NOT NULL,
  PRIMARY KEY (contract_address)
);

CREATE TABLE subscriber (
  contract_address VARCHAR(42) NOT NULL,
  PRIMARY KEY (contract_address)
);

CREATE TABLE subscription (
  sub_contract_address VARCHAR(42) NOT NULL,
  pub_contract_address VARCHAR(42) NOT NULL,
  PRIMARY KEY (sub_contract_address, pub_contract_address),
  CONSTRAINT fk_subscription_subscriber FOREIGN KEY (sub_contract_address)
    REFERENCES subscriber (contract_address) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_subscription_publisher FOREIGN KEY (pub_contract_address)
    REFERENCES publisher (contract_address) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE entry (
  pub_contract_address VARCHAR(42) NOT NULL,
  number INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  description VARCHAR(2000) NOT NULL,
  date TIMESTAMP NOT NULL,
  PRIMARY KEY (pub_contract_address, number),
  CONSTRAINT fk_entry_publisher FOREIGN KEY (pub_contract_address)
    REFERENCES publisher (contract_address) ON DELETE CASCADE ON UPDATE CASCADE
);