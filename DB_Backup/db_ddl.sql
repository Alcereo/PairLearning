CREATE TABLE sessions
(
  id VARCHAR(100) PRIMARY KEY NOT NULL,
  user_uid UUID NOT NULL,
  CONSTRAINT sessions_users_uid_fk FOREIGN KEY (user_uid) REFERENCES users (uid)
);
CREATE TABLE topic_rows
(
  topic_uid UUID NOT NULL,
  user_uid UUID NOT NULL,
  learn BOOLEAN,
  teach BOOLEAN,
  CONSTRAINT topic_rows_topics_uid_fk FOREIGN KEY (topic_uid) REFERENCES topics (uid),
  CONSTRAINT topic_rows_users_uid_fk FOREIGN KEY (user_uid) REFERENCES users (uid)
);
CREATE UNIQUE INDEX topic_rows_topic_uid_user_uid_pk ON topic_rows (topic_uid, user_uid);
CREATE TABLE topics
(
  uid UUID PRIMARY KEY NOT NULL,
  id INTEGER DEFAULT nextval('topics_id_seq'::regclass) NOT NULL,
  title TEXT
);
CREATE UNIQUE INDEX topics_id_uindex ON topics (id);
CREATE TABLE users
(
  uid UUID PRIMARY KEY NOT NULL,
  login VARCHAR(30) NOT NULL,
  passwordhash TEXT NOT NULL,
  email VARCHAR(60),
  activae BOOLEAN DEFAULT false,
  name TEXT NOT NULL
);
CREATE UNIQUE INDEX users_login_uindex ON users (login);