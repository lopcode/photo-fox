FROM eclipse-temurin:23@sha256:c7be9a5e17d3640e4a868a973c8afe391b0e33e98052e54eb563a5136813a2d8

ARG jar
ARG version="test"
ARG static_assets
ARG template_assets

RUN mkdir /opt/app
COPY $jar /opt/app/app.jar
COPY $static_assets /opt/app/static/
COPY $template_assets /opt/app/templates/

ENV JAVA_OPTS=""
ENV APP_PORT=8080
ENV APP_HOST=127.0.0.1

LABEL org.opencontainers.image.source="https://github.com/lopcode/photo-fox"
LABEL org.opencontainers.image.revision="$version"

USER nobody
WORKDIR /opt/app

# sense checks
RUN ls -al /opt/app
RUN java -version

ENTRYPOINT exec java ${JAVA_OPTS} -jar /opt/app/app.jar