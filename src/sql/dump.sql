--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

DROP DATABASE "PairLearning";
--
-- Name: PairLearning; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE "PairLearning" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'ru_RU.UTF-8' LC_CTYPE = 'ru_RU.UTF-8';


ALTER DATABASE "PairLearning" OWNER TO postgres;

\connect "PairLearning"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: sessions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE sessions (
    id character varying(100) NOT NULL,
    user_uid uuid NOT NULL
);


ALTER TABLE sessions OWNER TO postgres;

--
-- Name: topic_rows; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topic_rows (
    topic_uid uuid NOT NULL,
    user_uid uuid NOT NULL,
    learn boolean,
    teach boolean
);


ALTER TABLE topic_rows OWNER TO postgres;

--
-- Name: topics; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE topics (
    uid uuid NOT NULL,
    id integer NOT NULL,
    title text
);


ALTER TABLE topics OWNER TO postgres;

--
-- Name: topics_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE topics_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE topics_id_seq OWNER TO postgres;

--
-- Name: topics_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE topics_id_seq OWNED BY topics.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE users (
    uid uuid NOT NULL,
    login character varying(30) NOT NULL,
    passwordhash character varying(64) NOT NULL,
    email character varying(60),
    activae boolean DEFAULT false,
    name text NOT NULL
);


ALTER TABLE users OWNER TO postgres;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topics ALTER COLUMN id SET DEFAULT nextval('topics_id_seq'::regclass);


--
-- Data for Name: sessions; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO sessions (id, user_uid) VALUES ('C6FB687C3FB32787797B9D42D2AFBE10', '5859e9a1-f5f8-4291-95f9-6e216a94c819');


--
-- Data for Name: topic_rows; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO topic_rows (topic_uid, user_uid, learn, teach) VALUES ('615f504f-13de-4685-bb6a-278b22f5f021', '3c350dd7-e96c-4126-a4f5-daa7fcb4f227', false, true);
INSERT INTO topic_rows (topic_uid, user_uid, learn, teach) VALUES ('dc367c98-534e-4771-b1d6-e772504cc70b', '3c350dd7-e96c-4126-a4f5-daa7fcb4f227', true, false);
INSERT INTO topic_rows (topic_uid, user_uid, learn, teach) VALUES ('615f504f-13de-4685-bb6a-278b22f5f021', '7d29c12a-cc44-40d4-ada1-132260ff6cee', true, false);
INSERT INTO topic_rows (topic_uid, user_uid, learn, teach) VALUES ('1750d770-4d45-4d1f-9144-ce3cbccaa508', '3c350dd7-e96c-4126-a4f5-daa7fcb4f227', false, false);
INSERT INTO topic_rows (topic_uid, user_uid, learn, teach) VALUES ('dc367c98-534e-4771-b1d6-e772504cc70b', '7d29c12a-cc44-40d4-ada1-132260ff6cee', false, true);
INSERT INTO topic_rows (topic_uid, user_uid, learn, teach) VALUES ('dc367c98-534e-4771-b1d6-e772504cc70b', '5859e9a1-f5f8-4291-95f9-6e216a94c819', true, false);
INSERT INTO topic_rows (topic_uid, user_uid, learn, teach) VALUES ('615f504f-13de-4685-bb6a-278b22f5f021', '5859e9a1-f5f8-4291-95f9-6e216a94c819', false, true);
INSERT INTO topic_rows (topic_uid, user_uid, learn, teach) VALUES ('1750d770-4d45-4d1f-9144-ce3cbccaa508', '5859e9a1-f5f8-4291-95f9-6e216a94c819', true, false);
INSERT INTO topic_rows (topic_uid, user_uid, learn, teach) VALUES ('1750d770-4d45-4d1f-9144-ce3cbccaa508', '7d29c12a-cc44-40d4-ada1-132260ff6cee', false, false);


--
-- Data for Name: topics; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO topics (uid, id, title) VALUES ('dc367c98-534e-4771-b1d6-e772504cc70b', 1, 'Тема 1');
INSERT INTO topics (uid, id, title) VALUES ('615f504f-13de-4685-bb6a-278b22f5f021', 2, 'Тема 2');
INSERT INTO topics (uid, id, title) VALUES ('1750d770-4d45-4d1f-9144-ce3cbccaa508', 3, 'Тема 3');


--
-- Name: topics_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('topics_id_seq', 1, false);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO users (uid, login, passwordhash, email, activae, name) VALUES ('3c350dd7-e96c-4126-a4f5-daa7fcb4f227', 'Ivan', '40bd001563085fc35165329ea1ff5c5ecbdbbeef', '', true, 'Иван');
INSERT INTO users (uid, login, passwordhash, email, activae, name) VALUES ('7d29c12a-cc44-40d4-ada1-132260ff6cee', 'Alcereo', '40bd001563085fc35165329ea1ff5c5ecbdbbeef', 'sdffe', true, 'Александр');
INSERT INTO users (uid, login, passwordhash, email, activae, name) VALUES ('5859e9a1-f5f8-4291-95f9-6e216a94c819', 'qwe', '36d4ee617227f68f2177a7830da8699e11a47e811967acad4938c138562452d5', 'qwe12', true, 'qwe');


--
-- Name: sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sessions
    ADD CONSTRAINT sessions_pkey PRIMARY KEY (id);


--
-- Name: topic_rows_topic_uid_user_uid_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topic_rows
    ADD CONSTRAINT topic_rows_topic_uid_user_uid_pk UNIQUE (topic_uid, user_uid);


--
-- Name: topics_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY topics
    ADD CONSTRAINT topics_pkey PRIMARY KEY (uid);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (uid);


--
-- Name: topics_id_uindex; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX topics_id_uindex ON topics USING btree (id);


--
-- Name: users_login_uindex; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX users_login_uindex ON users USING btree (login);


--
-- Name: sessions_users_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sessions
    ADD CONSTRAINT sessions_users_uid_fk FOREIGN KEY (user_uid) REFERENCES users(uid) ON UPDATE SET NULL ON DELETE SET NULL;


--
-- Name: topic_rows_topics_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topic_rows
    ADD CONSTRAINT topic_rows_topics_uid_fk FOREIGN KEY (topic_uid) REFERENCES topics(uid);


--
-- Name: topic_rows_users_uid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY topic_rows
    ADD CONSTRAINT topic_rows_users_uid_fk FOREIGN KEY (user_uid) REFERENCES users(uid);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

