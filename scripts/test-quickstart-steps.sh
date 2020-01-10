
docker rm -f some-docker
docker rm -f some-client-name
docker network rm some-network

docker network create some-network

docker run --privileged --name some-docker -d \
    --network some-network --network-alias docker \
    -e DOCKER_TLS_CERTDIR=/certs \
    -v some-docker-certs-ca:/certs/ca \
    -v some-docker-certs-client:/certs/client \
    -v $PWD:/repo \
    docker:dind

# docker run --rm --network some-network --name some-client-name \
#     -e DOCKER_TLS_CERTDIR=/certs \
#     -v some-docker-certs-client:/certs/client:ro \
#     docker:latest version

docker run --rm --network some-network --name some-client-name \
    -e DOCKER_TLS_CERTDIR=/certs \
    -v some-docker-certs-client:/certs/client:ro \
    -v $PWD:/repo \
    -w /repo \
    docker:latest sh -c 'pwd; /repo/scripts/run-quickstart-steps.sh;'


docker rm -f some-docker
docker rm -f some-client-name
docker network rm some-network

# docker network create dind-net

# docker run  --privileged --name dind -d \
#     --network dind-net --network-alias docker \
#     -e DOCKER_TLS_CERTDIR=/certs \
#     -v $PWD/quickstart/docker/compose/docker-certs-ca:/certs/ca \
#     -v $PWD/quickstart/docker/compose/docker-certs-client:/certs/client \
#     docker:dind
# docker run --rm \
#     --network dind-net  \
#     -e DOCKER_TLS_CERTDIR=/certs \
#     -v $PWD/quickstart/docker/compose/docker-certs-client:/certs/client:ro \
#         -v $PWD:/repo  \
#         -w /repo \
#         --entrypoint /bin/sh \
#         docker:latest  /repo/scripts/run-quickstart-steps.sh

# docker network rm dind-net

