FROM alpine:edge
MAINTAINER coarsehorse
COPY . /img-gallery
RUN apk add --no-cache \
    openjdk11 \
    maven
WORKDIR /img-gallery
RUN mvn clean install
WORKDIR /img-gallery/target
ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "img-gallery-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080
