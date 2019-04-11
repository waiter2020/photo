FROM frolvlad/alpine-oraclejdk8-temporary
VOLUME /data
COPY photo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
