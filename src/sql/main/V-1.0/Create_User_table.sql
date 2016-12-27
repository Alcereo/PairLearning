CREATE TABLE Users (
  uid VARCHAR(36)           NOT NULL PRIMARY KEY,
  login VARCHAR(30)         NOT NULL,
  passwordhash VARCHAR(64)  NOT NULL,
  email VARCHAR(60),
  activae BOOLEAN           DEFAULT false,
  name VARCHAR(50)          NOT NULL
);
CREATE UNIQUE INDEX users_login_uindex ON users (login);