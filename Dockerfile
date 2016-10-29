FROM openjdk:8-alpine
MAINTAINER Igor Laborie <igor@monkeypatch.io>

COPY target/cat-microservice-1.0.jar ./cat-microservice-1.0.jar

EXPOSE 8080

CMD java -jar cat-microservice-1.0.jar