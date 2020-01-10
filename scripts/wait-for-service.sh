#!/bin/sh
SERVICE=$1
TIMEOUT=$2
SEARCH_PATTERN=$3

echo "Giving ${SERVICE} ${TIMEOUT} seconds to start:"
#designed to be called from quickstart/docker-compose
timeout -s SIGINT $TIMEOUT scripts/wait-for-logline.sh $SERVICE   "${SEARCH_PATTERN}"
RESULT=$?


if [ $RESULT != 0 ]; then
    echo "Failed to start service ${SERVICE}"
    echo "Pattern '${SEARCH_PATTERN}' not found in ${TIMEOUT} seconds."
    return 1
fi
