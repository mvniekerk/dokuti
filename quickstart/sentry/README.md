## Introduction
This is a minimal setup to get a local Sentry server up and running. For the
full local setup please refer to: [Sentry: On-premise
setup](https://github.com/getsentry/onpremise).

The Sentry server will be listening on `localhost:9000`.

## Usage

### Preparing PostgreSQL

To start using Sentry, there are 2 pre-requisite steps: run database migration
and create a superuser. Follow the steps outlined below.

```bash
docker volume create --name=sentry-postgres
docker-compose run --rm web upgrade --noinput
docker-compose run --rm web createuser
# follow the prompts to create a user that is used to login the webui
```

### Run Sentry

```bash
docker-compose up -d
```

### Test

This section outlines running a Python script in Docker to test if the Sentry
server is receiving stack traces correctly.

1. Add a new Python project from the Sentry UI: `localhost:9000`.

2. Make a note of the _DSN_.

3. If you are on Mac or Windows (and installed docker via _Docker Desktop_), you
   can replace `localhost` in the _DSN_ with `host.docker.internal`.

4. Update `example.py` with the _DSN_:

    ```python
    # update line 2 in example.py with the project DSN:
    sentry_sdk.init("http://fce66e08ca364acc90aac5e21a1cad74@host.docker.internal:9000/3")
    ```

5. Test if Sentry is receiving logs:

    ```bash
    docker run -it --rm -v ${PWD}/example.py:/opt/app/example.py -w /opt/app python:3.7-slim-stretch bash
    # from within the container:
    pip install --upgrade sentry-sdk==0.10.2
    python example.py
    ```
