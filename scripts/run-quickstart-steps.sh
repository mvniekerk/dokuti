docker version
apk update
apk add curl jq
apk add docker-compose

cd quickstart/docker-compose
docker-compose up -d

../../scripts/wait-for-service.sh dokuti 90 'Tomcat started on port(s): 8080 (http) with context path '

TOKEN=`curl \
-s -v \
-X POST http://localhost:80/connect/token \
-H 'Content-Type: application/x-www-form-urlencoded' \
-H 'cache-control: no-cache' \
-d 'grant_type=password&username=dokuti-admin&password=Password1#&client_id=dokuti-test-client&client_secret=secret&scope=dokuti' \
| jq '.access_token' -r` \
&& echo "TOKEN is :$TOKEN"

curl -s -v  \
-H "Accept: application/json"  \
-X POST http://localhost:8181/documents \
-H "Authorization: Bearer $TOKEN" \
-H "cache-control: no-cache" \
-H "Content-Type: multipart/form-data" \
-H "Transfer-Encoding: chunked" \
-F "description=test initial description." \
-F file=@sample.file.txt | jq


docker-compose down