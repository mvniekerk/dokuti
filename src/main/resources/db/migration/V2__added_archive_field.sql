
-- add new columnt to support archive/unarchive documents

ALTER TABLE _documents.document
ADD is_archived boolean NOT NULL DEFAULT false; 