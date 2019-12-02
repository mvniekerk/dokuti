
ALTER TABLE _documents.document
ADD shorten_key text; 

ALTER TABLE _documents.document
ADD CONSTRAINT uniq_shorten_key UNIQUE (shorten_key);

ALTER TABLE _documents.document_acl 
ADD team_uuid uuid;