FROM eclipse-temurin:18-jre

RUN apt-get update \
        && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends postgresql-client-14

USER 1000

ENTRYPOINT ["/server-1.0-SNAPSHOT/bin/server"]

ADD ./server/build/distributions/server-1.0-SNAPSHOT.tar /
ADD ./server/trends /trends
ADD ./server/trends.yaml /
ADD ./server/application.yaml /
