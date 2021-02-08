create user instantsharing with password '$instant.sharing!';
create database instantsharing with encoding='UTF-8' owner instantsharing;

--
-- Create role table to manage access rights
--
drop table if exists isrole cascade;

CREATE TABLE isrole (
    id serial PRIMARY KEY,
    designation character varying(50) NOT NULL
);


-- 
-- Create users table to control access
--
drop table if exists isuser cascade;

CREATE TABLE isuser (
    id serial PRIMARY KEY,
    logon character varying(100) NOT NULL,
    firstname character varying(100) NOT NULL,
    lastname character varying(100) NOT NULL,
    uid character varying(100) NOT NULL,
    companylogo character varying(500),
    password character varying(100) NOT NULL,
	lang character varying(2) NOT NULL,
    email character varying(100) NOT NULL,
    id_isrole integer NOT NULL,
    active boolean DEFAULT true NOT NULL
);

alter table isuser add constraint logon_index UNIQUE (logon);

ALTER TABLE ONLY isuser ADD CONSTRAINT isuser_isroleidfk_fkey FOREIGN KEY (id_isrole) REFERENCES isrole(id);

-- alter table only isuser add column companylogo bytea;


--
-- Create history table to follow users activities
--
drop table if exists isuserhistory cascade;

CREATE TABLE isuserhistory (
    id serial PRIMARY KEY,
	action character varying(100) NOT NULL,
	id_url character varying(100) NOT NULL,
	url character varying(200) NOT NULL,
	actiondate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_isuser integer NOT NULL
);


ALTER TABLE ONLY isuserhistory ADD CONSTRAINT isuserhistory_isuseridfk_fkey FOREIGN KEY (id_isuser) REFERENCES isuser(id);

--
-- Create preferences table
--
drop table if exists ispreference cascade;

CREATE TABLE ispreference (
    id serial PRIMARY KEY,
	stored_time integer NOT NULL,
    id_isuser integer NOT NULL
);

ALTER TABLE ONLY ispreference ADD CONSTRAINT ispreference_isuseridfk_fkey FOREIGN KEY (id_isuser) REFERENCES isuser(id);





--
-- Create recipients list table
--
drop table if exists islist cascade;

CREATE TABLE islist (
    id serial PRIMARY KEY,
	listname character varying(200) NOT NULL,
	created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_isuser integer NOT NULL
);

ALTER TABLE ONLY islist ADD CONSTRAINT islist_isuseridfk_fkey FOREIGN KEY (id_isuser) REFERENCES isuser(id);


--
-- Create sharing list table
--
drop table if exists issharing cascade;

CREATE TABLE issharing (
    id serial PRIMARY KEY,
    uid integer NOT NULL,
    stored_time integer NOT NULL,
	message character varying(2000) NOT NULL,
	created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	sent boolean DEFAULT false NOT NULL, 
	sent_date TIMESTAMP,
    id_isuser integer NOT NULL
);

ALTER TABLE ONLY issharing ADD CONSTRAINT issharing_isuseridfk_fkey FOREIGN KEY (id_isuser) REFERENCES isuser(id);




--
-- Create files list table
--
drop table if exists isfile cascade;

CREATE TABLE isfile (
    id serial PRIMARY KEY,
	filename character varying(1000) NOT NULL,
	directory character varying(500) NOT NULL,
	size integer NOT NULL,
	id_issharing integer NOT NULL,
	created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE ONLY isfile ADD CONSTRAINT isfile_issharingidfk_fkey FOREIGN KEY (id_issharing) REFERENCES issharing(id);


--
-- Create sharing to user link table
--
drop table if exists islisttoisuser cascade;

CREATE TABLE islisttoisuser (
    id serial PRIMARY KEY,
	id_islist integer NOT NULL,
    id_isuser integer NOT NULL,
	created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP    
);


ALTER TABLE ONLY islisttoisuser ADD CONSTRAINT islisttoisuser_issharingidfk_fkey FOREIGN KEY (id_islist) REFERENCES islist(id);
ALTER TABLE ONLY islisttoisuser ADD CONSTRAINT islisttoisuser_isuseridfk_fkey FOREIGN KEY (id_isuser) REFERENCES isuser(id);





--
-- Create sharing to user link table
--
drop table if exists issharingtoisuser cascade;

CREATE TABLE issharingtoisuser (
    id serial PRIMARY KEY,
	id_issharing integer NOT NULL,
    id_isuser integer NOT NULL,
	created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP    
);


ALTER TABLE ONLY issharingtoisuser ADD CONSTRAINT issharingtoisuser_issharingidfk_fkey FOREIGN KEY (id_issharing) REFERENCES issharing(id);
ALTER TABLE ONLY issharingtoisuser ADD CONSTRAINT issharingtoisuser_isuseridfk_fkey FOREIGN KEY (id_isuser) REFERENCES isuser(id);


--
-- Create sharing to user link table
--
drop table if exists isdownload cascade;

CREATE TABLE isdownload (
    id serial PRIMARY KEY,
	id_issharingtoisuser integer NOT NULL,
    id_isfile integer NOT NULL,
    downloaded boolean DEFAULT false NOT NULL,
	download_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP    
);


ALTER TABLE ONLY isdownload ADD CONSTRAINT isdownload_issharingtoisuseridfk_fkey FOREIGN KEY (id_issharingtoisuser) REFERENCES issharingtoisuser(id);
ALTER TABLE ONLY isdownload ADD CONSTRAINT isdownload_isfileidfk_fkey FOREIGN KEY (id_isfile) REFERENCES isfile(id);





--
-- Insert Data
--
INSERT INTO isrole (id,designation) VALUES (1, 'Administrator');
INSERT INTO isrole (id,designation) VALUES (2, 'User');

INSERT INTO isuser (logon, firstname, lastname, uid, password, email, id_isrole, active, lang) VALUES ('administrateur', 'admin', 'admin', 'admin' , 'administrateur', 'admin@localhost', 1, true, 'fr');
INSERT INTO isuser (logon, firstname, lastname, uid, password, email, id_isrole, active, lang) VALUES ('vissol', 'Charles', 'Vissol', 'a094614', 'vissol', 'charles.vissol@ariane.group', 1, true, 'fr');
INSERT INTO isuser (logon, firstname, lastname, uid, password, email, id_isrole, active, lang) VALUES ('anonymous', 'anonymous', 'anonymous', 'a000000', 'anonymous', 'anonymous@ariane.group', 1, true, 'fr');
INSERT INTO isuser (logon, firstname, lastname, uid, password, email, id_isrole, active, lang) VALUES ('bourreau', 'Thierry', 'Bourreau', 'A012781', 'bourreau', 'thierry.bourreau@ariane.group', 1, true, 'fr');
INSERT INTO isuser (logon, firstname, lastname, uid, password, email, id_isrole, active, lang) VALUES ('morardet', 'Laetitia', 'Morardet', 'M012009', 'morardet', 'laetitia.morardet@ariane.group', 1, true, 'fr');

