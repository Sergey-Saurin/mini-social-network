CREATE sequence hibernate_sequence start 1 increment 1;

CREATE TYPE perm_message AS ENUM ('ALL', 'FRIENDS');

--CREATE CAST (varchar AS perm_message) WITH INOUT AS IMPLICIT;

CREATE TABLE person (
	id bigserial NOT NULL,
	first_name varchar(255) NOT NULL,
	last_name varchar(255),
	reg_date timestamp NOT NULL,
	birth_date date,
	email varchar(255),
	phone varchar(255),
	password varchar(255) NOT NULL,
	photo varchar(255),
	about varchar(2048),
	country varchar(255),
	city varchar(255),
	is_approved boolean NOT NULL,
	messages_permission perm_message NOT NULL,
	last_online_time timestamp NOT NULL,
	is_blocked boolean NOT NULL,
	PRIMARY KEY (id));
