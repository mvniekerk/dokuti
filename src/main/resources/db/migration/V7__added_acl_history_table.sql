CREATE TABLE _documents.document_acl_history (
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 CACHE 1 ),
	document_id uuid NOT NULL,
	user_id uuid,
	team_id uuid,
	permission text NOT NULL,
	granted_by uuid NOT NULL,
	granted_on timestamp NOT NULL,
	revoked_by uuid,
	revoked_on timestamp,
	CONSTRAINT document_acl_history_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE _documents.document_acl_history OWNER TO postgres;


ALTER TABLE _documents.document_acl_history ADD CONSTRAINT "fk_document.id" FOREIGN KEY (document_id)
REFERENCES _documents.document (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;

