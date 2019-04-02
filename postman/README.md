# Running Postman tests

Dokuti can be spun up in several ways for testing purposes. The most common is using `docker-compose`. A `Dokuti.postman_collection.json` Postman collection has
been provided within the `postman` folder. The below all-caps variables can be set as Postman environment variables or in the Postman collection directly.

## Setting environment variables

When using `docker-compose` the values should be set as follows:

```
base-url = localhost:8181
DOKUTI_BASE_URL = localhost:8181
keycloak-host = localhost:9191
KEYCLOAK_BASE_URL = localhost:9191
```

Note: The `Dokuti-Quickstart.postman.environment.json` file is a Postman environment intended for use with the `docker-compose` environment (It sets all the required environment variables when used).