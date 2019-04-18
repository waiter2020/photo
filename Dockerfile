FROM java:8
VOLUME /data
# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /photo/app.jar
EXPOSE 8086
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/photo/app.jar","--spring.profiles.active=dev"]
