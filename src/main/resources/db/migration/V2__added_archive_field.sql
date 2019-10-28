
-- add new columnt to support archive/unarchive documents

ALTER TABLE _documents.document
ADD is_archive boolean NOT NULL DEFAULT false; 