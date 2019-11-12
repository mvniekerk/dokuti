#!/bin/sh
#
echo " Starting Application "
#To reduce Tomcat startup time we added a system property pointing to "/dev/urandom" as a source of entropy.
exec java -Djava.security.egd=file:/dev/./urandom -jar /app.jar

