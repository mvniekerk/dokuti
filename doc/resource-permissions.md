# Resource Permissions

A first implementation of ACL and resource ownership has been implemented for the initial release. Although and extensive ACL model is planned for Dokuti, the initial
release only applies some permissions to Documents on creation. Currently, when a user creates a document, the following permissions are applied to the user that created
the document:

* `Read` - Allows reading the document. Document appears within lists requested by the user.
* `Write`- Allows modification of the Document, and any associated entities.
* `Rollback` - Allows rolling back a document to a previous version. Note: Document rollback is not yet implemented.
* `Archive` - Allows archiving of documents. Note: archiving is not yet implemented.

No other resource permissions are applied, or can be applied to resources other than Documents in the current release. An API for managing and applying resource 
permissions is planned for future releases.