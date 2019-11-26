-- Database generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler  version: 0.9.2-alpha
-- PostgreSQL version: 10.0
-- Project Site: pgmodeler.io
-- Model Author: ---


-- Database creation must be done outside a multicommand file.
-- These commands were put in this file only as a convenience.
-- -- object: "dokuti" | type: DATABASE --
-- -- DROP DATABASE IF EXISTS "dokuti";
-- CREATE DATABASE "dokuti"
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
CREATE TABLE _documents.document(
	id uuid NOT NULL,
	name text NOT NULL,
	description text,
	content_type text,
	__updated_on timestamp NOT NULL,
	__updated_by uuid NOT NULL,
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
CREATE TABLE _documents.document_attribute(
	document_id uuid,
	attribute_value text,
	attribute_label smallint NOT NULL,
	__updated_on timestamp NOT NULL,
	__updated_by uuid NOT NULL,
	CONSTRAINT pk_document_attribute PRIMARY KEY (document_id, attribute_label)

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
CREATE TABLE _documents.attribute_label(
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
CREATE TABLE _documents.document_tag(
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
CREATE TABLE _documents.document_status(
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
CREATE TABLE _documents.document_version(
	id uuid NOT NULL,
	document_id uuid,
	checksum text NOT NULL,
	__uploaded_on timestamp NOT NULL,
	__uploaded_by uuid NOT NULL,
	CONSTRAINT pk_document_version PRIMARY KEY (id)
);
-- ddl-end --
COMMENT ON TABLE _documents.document_version IS 'this table holds the various instance meta-data of a particular document';
-- ddl-end --
COMMENT ON COLUMN _documents.document_version.checksum IS 'an MD5 digest to ensure file integrity';
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
CREATE TABLE _documents.document_acl(
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 CACHE 1 ),
	document_id uuid NOT NULL,
	user_uuid uuid NOT NULL,
	permission text NOT NULL DEFAULT 'read',
	may_assign boolean NOT NULL DEFAULT false,
	_granted_by uuid NOT NULL,
	_granted_on timestamp NOT NULL,
	CONSTRAINT document_share_acl_pk PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON COLUMN _documents.document_acl.user_uuid IS 'the user_uuid as stored in A3S (or other identity storage) or
the group_uuid as stored in the _documents schema';
-- ddl-end --
COMMENT ON COLUMN _documents.document_acl.permission IS 'READ < WRITE (and modify meta-data), < ROLLBACK < UN/ARCHIVE';
-- ddl-end --
COMMENT ON COLUMN _documents.document_acl.may_assign IS 'able to assign document rights to others';
-- ddl-end --
COMMENT ON COLUMN _documents.document_acl._granted_by IS 'uuid of the user who granted these acl rights';
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
CREATE TABLE _documents.document_version_parts(
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

-- object: _documents.document_group | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_group CASCADE;
CREATE TABLE _documents.document_group(
	id uuid NOT NULL,
	name text NOT NULL,
	__updated_on timestamp NOT NULL,
	__updated_by uuid NOT NULL,
	CONSTRAINT document_usergroup_pk PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON TABLE _documents.document_group IS 'group of users to which acl rights may be asigned';
-- ddl-end --
ALTER TABLE _documents.document_group OWNER TO postgres;
-- ddl-end --

-- object: _documents.document_document_group | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_document_group CASCADE;
CREATE TABLE _documents.document_document_group(
	document_id uuid NOT NULL,
	document_group_id UUID NOT NULL
	--__updated_on timestamp NOT NULL,
	--__updated_by uuid NOT NULL
);

-- object: _documents.document_group_user | type: TABLE --
-- DROP TABLE IF EXISTS _documents.document_group_user CASCADE;
CREATE TABLE _documents.document_group_user(
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 CACHE 1 ),
	group_uuid uuid NOT NULL,
	user_uuid uuid,
	__updated_by uuid NOT NULL,
	__updated_on timestamp NOT NULL,
	CONSTRAINT document_group_user_pk PRIMARY KEY (id)

);
-- ddl-end --
COMMENT ON COLUMN _documents.document_group_user.user_uuid IS 'the user uuid as stored in A3S (or other identity storage)';
-- ddl-end --
COMMENT ON COLUMN _documents.document_group_user.__updated_by IS 'uuid of the user who granted these acl rights';
-- ddl-end --
ALTER TABLE _documents.document_group_user OWNER TO postgres;
-- ddl-end --


-- Create the required Hibernate Sequence --
CREATE SEQUENCE _documents.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

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
REFERENCES _documents.document_version (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: "fk_usergroup.id" | type: CONSTRAINT --
-- ALTER TABLE _documents.document_group_user DROP CONSTRAINT IF EXISTS "fk_usergroup.id" CASCADE;
ALTER TABLE _documents.document_group_user ADD CONSTRAINT "fk_usergroup.id" FOREIGN KEY (group_uuid)
REFERENCES _documents.document_group (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --


