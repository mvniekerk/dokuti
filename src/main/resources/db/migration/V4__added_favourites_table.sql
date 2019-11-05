CREATE TABLE _documents.document_favourite(
	id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 CACHE 1 ),
	document_id uuid NOT NULL,
	user_uuid uuid NOT NULL,
	CONSTRAINT document_share_favourite_pk PRIMARY KEY (id)

);

ALTER TABLE _documents.document_favourite OWNER TO postgres;

ALTER TABLE _documents.document_favourite ADD CONSTRAINT "fk_document.id" FOREIGN KEY (document_id)
REFERENCES _documents.document (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;


