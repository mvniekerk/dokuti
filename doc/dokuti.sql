-- Database generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler  version: 0.9.2-beta
-- PostgreSQL version: 11.0
-- Project Site: pgmodeler.io
-- Model Author: ---


-- Database creation must be done outside a multicommand file.
-- These commands were put in this file only as a convenience.
-- -- object: dokuti | type: DATABASE --
-- -- DROP DATABASE IF EXISTS dokuti;
-- CREATE DATABASE dokuti
-- 	ENCODING = 'UTF8'
-- 	TABLESPACE = pg_default
-- 	OWNER = postgres;
-- -- ddl-end --
-- 

-- object: _documents | type: SCHEMA --
-- DROP SCHEMA IF EXISTS _documents CASCADE;
CREATE SCHEMA _documents;
-- ddl-end --
ALTER SCHEMA _documents OWNER TO postgres;
-- ddl-end --

SET search_path TO pg_catalog,public,_documents;
-- ddl-end --

-- object: _documents.document | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document CASCADE;
CREATE TABLE _documents.document (
	id uuid NOT NULL,
	name text NOT NULL,
	description text,
	content_type text,
	__updated_on timestamp NOT NULL,
	__updated_by uuid NOT NULL,
	is_archived boolean NOT NULL DEFAULT false,
	CONSTRAINT pk_document PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON TABLE _documents.document IS 'All documents';
-- ddl-end --
COMMENT ON COLUMN _documents.document.name IS 'Document name';
-- ddl-end --
COMMENT ON COLUMN _documents.document.description IS 'Optional description for the document';
-- ddl-end --
COMMENT ON COLUMN _documents.document.content_type IS 'mime/type ppt, pdf, csv, docx, odf etc';
-- ddl-end --
ALTER TABLE _documents.document OWNER TO postgres;
-- ddl-end --

-- object: _documents.document_attribute | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_attribute CASCADE;
CREATE TABLE _documents.document_attribute (
	id bigint NOT NULL,
	document_id uuid,
	attribute_value text,
	attribute_label smallint NOT NULL,
	__updated_on timestamp NOT NULL,
	__updated_by uuid NOT NULL,
	CONSTRAINT pk_document_attribute PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON TABLE _documents.document_attribute IS 'for custom attributes added at run time by users';
-- ddl-end --
COMMENT ON COLUMN _documents.document_attribute.attribute_value IS 'eg. contract expiry date : 21/09/2023';
-- ddl-end --
ALTER TABLE _documents.document_attribute OWNER TO postgres;
-- ddl-end --

-- object: _documents.attribute_label | type: TABLE --
-- DROP TABLE IF EXISTS _documents.attribute_label CASCADE;
CREATE TABLE _documents.attribute_label (
	id smallint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 32767 START WITH 1 CACHE 1 ),
	name text NOT NULL,
	attribute_validaton text,
	__updated_on timestamp NOT NULL,
	__updated_by uuid NOT NULL,
	CONSTRAINT pk_document_attribute_label PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON TABLE _documents.attribute_label IS 'eg. Contract Renewal Date, and the validation regex will be dd-mm-yyyy would be:
^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\d\d$.';
-- ddl-end --
COMMENT ON COLUMN _documents.attribute_label.name IS 'human readable label';
-- ddl-end --
COMMENT ON COLUMN _documents.attribute_label.attribute_validaton IS 'regular expression to validate the attribute';
-- ddl-end --
ALTER TABLE _documents.attribute_label OWNER TO postgres;
-- ddl-end --

-- -- object: _documents.document_attribute_label_id_seq | type: SEQUENCE --
-- -- DROP SEQUENCE IF EXISTS _documents.document_attribute_label_id_seq CASCADE;
-- CREATE SEQUENCE _documents.document_attribute_label_id_seq
-- 	INCREMENT BY 1
-- 	MINVALUE 1
-- 	MAXVALUE 32767
-- 	START WITH 1
-- 	CACHE 1
-- 	NO CYCLE
-- 	OWNED BY NONE;
-- -- ddl-end --
-- ALTER SEQUENCE _documents.document_attribute_label_id_seq OWNER TO postgres;
-- -- ddl-end --
-- 
-- object: _documents.document_tag | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_tag CASCADE;
CREATE TABLE _documents.document_tag (
	document_id uuid,
	tag text
);
-- ddl-end --
COMMENT ON TABLE _documents.document_tag IS 'DENORMALISED for performance';
-- ddl-end --
ALTER TABLE _documents.document_tag OWNER TO postgres;
-- ddl-end --

-- object: idx_tag | type: INDEX --
-- DROP INDEX IF EXISTS _documents.idx_tag CASCADE;
CREATE INDEX idx_tag ON _documents.document_tag
	USING gin
	(
	  (to_tsvector('english', tag))
	);
-- ddl-end --

-- object: _documents.document_status | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_status CASCADE;
CREATE TABLE _documents.document_status (
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 CACHE 1 ),
	document_id uuid,
	updated_on date NOT NULL,
	status text NOT NULL,
	CONSTRAINT pk_document_status PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON COLUMN _documents.document_status.status IS 'CREATED, UPDATED, EDITED, DELETED, DESTROYED, ROLLBACK
Suggest using a databasetype for this';
-- ddl-end --
ALTER TABLE _documents.document_status OWNER TO postgres;
-- ddl-end --

-- -- object: _documents.document_status_id_seq | type: SEQUENCE --
-- -- DROP SEQUENCE IF EXISTS _documents.document_status_id_seq CASCADE;
-- CREATE SEQUENCE _documents.document_status_id_seq
-- 	INCREMENT BY 1
-- 	MINVALUE 1
-- 	MAXVALUE 9223372036854775807
-- 	START WITH 1
-- 	CACHE 1
-- 	NO CYCLE
-- 	OWNED BY NONE;
-- -- ddl-end --
-- ALTER SEQUENCE _documents.document_status_id_seq OWNER TO postgres;
-- -- ddl-end --
-- 
-- object: _documents.document_version | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_version CASCADE;
CREATE TABLE _documents.document_version (
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 CACHE 1 ),
	document_id uuid,
	version_id uuid NOT NULL,
	checksum text NOT NULL,
	__uploaded_on timestamp NOT NULL,
	__uploaded_by uuid NOT NULL,
	document_type text,
	checksum_algo text,
	CONSTRAINT pk_document_version PRIMARY KEY (id),
	CONSTRAINT un_version_id UNIQUE (version_id)

);
-- ddl-end --
COMMENT ON TABLE _documents.document_version IS 'this table holds the various instance meta-data of a particular document';
-- ddl-end --
COMMENT ON COLUMN _documents.document_version.version_id IS 'The uuid of the version instance that gets saved to the data store';
-- ddl-end --
COMMENT ON COLUMN _documents.document_version.checksum IS 'an hash digest to ensure file integrity';
-- ddl-end --
COMMENT ON COLUMN _documents.document_version.checksum_algo IS 'hashing algorithm  used to calculate checksum';
-- ddl-end --
ALTER TABLE _documents.document_version OWNER TO postgres;
-- ddl-end --

-- -- object: _documents.document_version_id_seq | type: SEQUENCE --
-- -- DROP SEQUENCE IF EXISTS _documents.document_version_id_seq CASCADE;
-- CREATE SEQUENCE _documents.document_version_id_seq
-- 	INCREMENT BY 1
-- 	MINVALUE 1
-- 	MAXVALUE 9223372036854775807
-- 	START WITH 1
-- 	CACHE 1
-- 	NO CYCLE
-- 	OWNED BY NONE;
-- -- ddl-end --
-- ALTER SEQUENCE _documents.document_version_id_seq OWNER TO postgres;
-- -- ddl-end --
-- 
-- object: _documents.document_acl | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_acl CASCADE;
CREATE TABLE _documents.document_acl (
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 CACHE 1 ),
	document_id uuid NOT NULL,
	user_uuid uuid NOT NULL,
	permission text NOT NULL DEFAULT 'read',
	may_assign boolean NOT NULL DEFAULT false,
	_granted_by uuid NOT NULL,
	_granted_on timestamp NOT NULL,
	team_uuid uuid NOT NULL,
	CONSTRAINT document_share_acl_pk PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON COLUMN _documents.document_acl.user_uuid IS 'the user_uuid as stored in A3S (or other identity storage) ';
-- ddl-end --
COMMENT ON COLUMN _documents.document_acl.permission IS 'READ < WRITE (and modify meta-data), < ROLLBACK < UN/ARCHIVE';
-- ddl-end --
COMMENT ON COLUMN _documents.document_acl.may_assign IS 'able to assign document rights to others';
-- ddl-end --
COMMENT ON COLUMN _documents.document_acl._granted_by IS 'uuid of the user who granted these acl rights';
-- ddl-end --
COMMENT ON COLUMN _documents.document_acl.team_uuid IS 'the team_uuid as stored in A3S (or other identity storage) ';
-- ddl-end --
ALTER TABLE _documents.document_acl OWNER TO postgres;
-- ddl-end --

-- -- object: _documents.document_share_acl_id_seq | type: SEQUENCE --
-- -- DROP SEQUENCE IF EXISTS _documents.document_share_acl_id_seq CASCADE;
-- CREATE SEQUENCE _documents.document_share_acl_id_seq
-- 	INCREMENT BY 1
-- 	MINVALUE 1
-- 	MAXVALUE 9223372036854775807
-- 	START WITH 1
-- 	CACHE 1
-- 	NO CYCLE
-- 	OWNED BY NONE;
-- -- ddl-end --
-- ALTER SEQUENCE _documents.document_share_acl_id_seq OWNER TO postgres;
-- -- ddl-end --
-- 
-- -- object: _documents.group_acl_id_seq | type: SEQUENCE --
-- -- DROP SEQUENCE IF EXISTS _documents.group_acl_id_seq CASCADE;
-- CREATE SEQUENCE _documents.group_acl_id_seq
-- 	INCREMENT BY 1
-- 	MINVALUE 1
-- 	MAXVALUE 9223372036854775807
-- 	START WITH 1
-- 	CACHE 1
-- 	NO CYCLE
-- 	OWNED BY NONE;
-- -- ddl-end --
-- ALTER SEQUENCE _documents.group_acl_id_seq OWNER TO postgres;
-- -- ddl-end --
-- 
-- object: _documents.document_version_parts | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_version_parts CASCADE;
CREATE TABLE _documents.document_version_parts (
	document_version_id uuid NOT NULL,
	sequence smallint NOT NULL,
	part_uuid uuid NOT NULL,
	checksum text NOT NULL,
	CONSTRAINT uq_document_version_id UNIQUE (document_version_id)

);
-- ddl-end --
COMMENT ON COLUMN _documents.document_version_parts.sequence IS 'sequence number of the document blob';
-- ddl-end --
COMMENT ON COLUMN _documents.document_version_parts.checksum IS 'an MD5 digest to ensure part integrity';
-- ddl-end --
ALTER TABLE _documents.document_version_parts OWNER TO postgres;
-- ddl-end --

-- object: _documents.user_favourite | type: TABLE --
-- DROP TABLE IF EXISTS _documents.user_favourite CASCADE;
CREATE TABLE _documents.user_favourite (
	user_uuid uuid NOT NULL,
	document_id uuid NOT NULL,
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ,
	CONSTRAINT document_favourite_pk PRIMARY KEY (id),
	CONSTRAINT uniq_user_doc UNIQUE (user_uuid,document_id)

);
-- ddl-end --
ALTER TABLE _documents.user_favourite OWNER TO postgres;
-- ddl-end --

-- object: _documents.document_acl_history | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_acl_history CASCADE;
CREATE TABLE _documents.document_acl_history (
	id bigint NOT NULL,
	document_id uuid NOT NULL,
	"userId" uuid,
	"teamId" uuid,
	permission text NOT NULL,
	granted_by uuid NOT NULL,
	granted_on timestamp NOT NULL,
	revoked_by uuid,
	revoked_on timestamp,
	CONSTRAINT document_acl_history_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE _documents.document_acl_history OWNER TO postgres;
-- ddl-end --

-- object: "fk_document.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.document_attribute DROP CONSTRAINT IF EXISTS "fk_document.id" CASCADE;
ALTER TABLE _documents.document_attribute ADD CONSTRAINT "fk_document.id" FOREIGN KEY (document_id)
REFERENCES _documents.document (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: "fk_attribute.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.document_attribute DROP CONSTRAINT IF EXISTS "fk_attribute.id" CASCADE;
ALTER TABLE _documents.document_attribute ADD CONSTRAINT "fk_attribute.id" FOREIGN KEY (attribute_label)
REFERENCES _documents.attribute_label (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: "fk_document.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.document_tag DROP CONSTRAINT IF EXISTS "fk_document.id" CASCADE;
ALTER TABLE _documents.document_tag ADD CONSTRAINT "fk_document.id" FOREIGN KEY (document_id)
REFERENCES _documents.document (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: "fk_document.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.document_status DROP CONSTRAINT IF EXISTS "fk_document.id" CASCADE;
ALTER TABLE _documents.document_status ADD CONSTRAINT "fk_document.id" FOREIGN KEY (document_id)
REFERENCES _documents.document (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: "fk_document.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.document_version DROP CONSTRAINT IF EXISTS "fk_document.id" CASCADE;
ALTER TABLE _documents.document_version ADD CONSTRAINT "fk_document.id" FOREIGN KEY (document_id)
REFERENCES _documents.document (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: "fk_document.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.document_acl DROP CONSTRAINT IF EXISTS "fk_document.id" CASCADE;
ALTER TABLE _documents.document_acl ADD CONSTRAINT "fk_document.id" FOREIGN KEY (document_id)
REFERENCES _documents.document (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: "fk_document_version_version.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.document_version_parts DROP CONSTRAINT IF EXISTS "fk_document_version_version.id" CASCADE;
ALTER TABLE _documents.document_version_parts ADD CONSTRAINT "fk_document_version_version.id" FOREIGN KEY (document_version_id)
REFERENCES _documents.document_version (version_id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: "fk_document.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.user_favourite DROP CONSTRAINT IF EXISTS "fk_document.id" CASCADE;
ALTER TABLE _documents.user_favourite ADD CONSTRAINT "fk_document.id" FOREIGN KEY (document_id)
REFERENCES _documents.document (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: "fk_document.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.document_acl_history DROP CONSTRAINT IF EXISTS "fk_document.id" CASCADE;
ALTER TABLE _documents.document_acl_history ADD CONSTRAINT "fk_document.id" FOREIGN KEY (document_id)
REFERENCES _documents.document (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


