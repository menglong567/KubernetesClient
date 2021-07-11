FROM azul/zulu-openjdk:8u252
VOLUME /tmp
COPY ./target/Kubernetes-Client-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources resources
COPY ./target/lib lib
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xmx512m","-jar","/app.jar"]
