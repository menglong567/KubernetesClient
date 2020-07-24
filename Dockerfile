FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./target/Kubernetes-Client-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xmx512m","-jar","/app.jar"]