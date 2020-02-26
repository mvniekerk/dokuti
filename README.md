[![CircleCI](https://circleci.com/gh/GrindrodBank/dokuti.svg?style=svg)](https://circleci.com/gh/GrindrodBank/dokuti)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FGrindrodBank%2Fdokuti.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FGrindrodBank%2Fdokuti?ref=badge_shield)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GrindrodBank_dokuti&metric=alert_status)](https://sonarcloud.io/dashboard?id=GrindrodBank_dokuti)

# Dokuti

Dokuti is a batteries-included, enterprise-grade, open-source, free-standing microservice document service suitable for use as a back-end for document storage and versioning needs.

This service is implemented in Java using [Spring Boot](https://spring.io/projects/spring-boot).

## Quickstart

Taking it out for a spin in 5-10 minutes:

* Clone Github repo:
```bash
git clone git@github.com:GrindrodBank/dokuti.git
```

* Run using docker-compose:
```bash
cd dokuti
cd quickstart/docker-compose
docker-compose up -d
```

* Inspecting it should show that it's up:
```bash
docker-compose ps
```

5 services are installed:
* dokuti - The actual document service installed on port 8181
* PostgreSQL Database installed on port 5432
* A3S and and A3S-identity-server services
* A3S PostgreSQL Database installed on port 5478


### Credentials for PostgreSQL database
* Password: postgres
* Database: dokuti

> This example uses:
> * [curl](https://github.com/curl/curl) (a handy command line client to do HTTP requests) and 
> * [jq](https://stedolan.github.io/jq/) ( a nice command line JSON processor)

* Get an authentication token in order to get API access:

```bash
TOKEN=`curl \
-s -v \
-X POST http://localhost:80/connect/token \
-H 'Content-Type: application/x-www-form-urlencoded' \
-H 'cache-control: no-cache' \
-d 'grant_type=password&username=dokuti-admin&password=Password1#&client_id=dokuti-test-client&client_secret=secret&scope=dokuti' \
| jq '.access_token' -r` \
&& echo "TOKEN is :$TOKEN"
```

* Create a `sample.file.txt` file to upload as a document:

```bash
echo "Hello there" >> sample.file.txt
```

* Then create a document:
```bash
curl -s -v  \
-H "Accept: application/json"  \
-X POST http://localhost:8181/documents \
-H "Authorization: Bearer $TOKEN" \
-H "cache-control: no-cache" \
-H "Content-Type: multipart/form-data" \
-H "Transfer-Encoding: chunked" \
-F "description=test initial description." \
-F file=@sample.file.txt | jq
```

* To undeploy everything:

```bash
docker-compose down
```

### Sentry

Setting up Sentry server:

```bash
cd quickstart/sentry
docker volume create --name=sentry-postgres
docker-compose run --rm web config generate-secret-key | tail -n 1 | tr -d '\r\n' | awk '{print "SENTRY_SECRET_KEY="$1}' > .env
docker-compose run --rm web upgrade --noinput
docker-compose run --rm web createuser
docker-compose up -d
```

Sentry is now listening on `localhost:9000`

# Postman Collection

*NOTE:* You need to be using the full installation of postman and not the Chrome Application  

A `Dokuti.postman_collection.json` Postman collection has been included within the `postman` folder. This collection is intended for exploring the Dokuti API. There is also a `Dokuti-Quickstart.postman_environment.json` Postman environment that can be used to interact with the
Dokuti instance deployed using the `docker-compose` method in the Quickstart above.

# Project Documentation

All project documentation is currently available within the `/doc` folder.

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FGrindrodBank%2Fdokuti.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FGrindrodBank%2Fdokuti?ref=badge_large)

