#!/bin/sh
SERVICE=$1
SEARCH_PATTERN=$2
(docker-compose  logs -f $SERVICE &) | grep -o -a -h -m 1 "${SEARCH_PATTERN}"